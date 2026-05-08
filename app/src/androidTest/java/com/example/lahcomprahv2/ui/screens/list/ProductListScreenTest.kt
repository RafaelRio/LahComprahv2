package com.example.lahcomprahv2.ui.screens.list

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lahcomprahv2.data.ProductRepository
import com.example.lahcomprahv2.models.Product
import com.example.lahcomprahv2.ui.theme.LahComprahV2Theme
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductListScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun deleteRequiresConfirmationBeforeRemovingProduct() {
        val repository = FakeUiProductRepository(
            initialProducts = listOf(Product(id = "1", nombre = "pan", cantidad = 2))
        )
        val viewModel = ProductListViewModel(repository)
        val deleteDescription = composeRule.activity.getString(com.example.lahcomprahv2.R.string.delete_product)
        val dialogTitle = composeRule.activity.getString(com.example.lahcomprahv2.R.string.delete_product_confirmation_title)
        val cancelLabel = composeRule.activity.getString(com.example.lahcomprahv2.R.string.cancel)
        val confirmLabel = composeRule.activity.getString(com.example.lahcomprahv2.R.string.confirm)

        composeRule.setContent {
            LahComprahV2Theme {
                ProductsListScreen(viewModel = viewModel)
            }
        }

        composeRule.onNodeWithText("Pan").assertIsDisplayed()
        composeRule.onNodeWithContentDescription(deleteDescription).performClick()
        composeRule.onNodeWithText(dialogTitle).assertIsDisplayed()

        composeRule.onNodeWithText(cancelLabel).performClick()
        assertTrue(composeRule.onAllNodesWithText(dialogTitle).fetchSemanticsNodes().isEmpty())
        composeRule.onNodeWithText("Pan").assertIsDisplayed()

        composeRule.onNodeWithContentDescription(deleteDescription).performClick()
        composeRule.onNodeWithText(confirmLabel).performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            repository.products.value.isEmpty()
        }
        assertTrue(composeRule.onAllNodesWithText("Pan").fetchSemanticsNodes().isEmpty())
    }

    @Test
    fun saveKeepsSheetOpenUntilRepositoryCompletes() {
        val repository = FakeUiProductRepository()
        repository.addGate = CompletableDeferred()
        val viewModel = ProductListViewModel(repository)
        val addDescription = composeRule.activity.getString(com.example.lahcomprahv2.R.string.add_product)
        val savingLabel = composeRule.activity.getString(com.example.lahcomprahv2.R.string.saving)

        composeRule.setContent {
            LahComprahV2Theme {
                ProductsListScreen(viewModel = viewModel)
            }
        }

        composeRule.onNodeWithContentDescription(addDescription).performClick()
        composeRule.onNodeWithTag("product_name_field").performTextInput("leche")
        composeRule.onNodeWithTag("save_product_button").performClick()

        composeRule.onNodeWithText(savingLabel).assertIsDisplayed()
        composeRule.onNodeWithTag("save_product_button").assertIsDisplayed()

        repository.addGate?.complete(Unit)

        composeRule.waitUntil(timeoutMillis = 5_000) {
            repository.products.value.any { it.nombre == "leche" }
        }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag("save_product_button").fetchSemanticsNodes().isEmpty()
        }
        composeRule.onNodeWithText("Leche").assertIsDisplayed()
    }
}

private class FakeUiProductRepository(
    initialProducts: List<Product> = emptyList()
) : ProductRepository {
    val products = MutableStateFlow(initialProducts)
    var addGate: CompletableDeferred<Unit>? = null

    override fun observeProducts(): Flow<List<Product>> = products

    override suspend fun addProduct(name: String, quantity: Int) {
        addGate?.await()
        val id = "product-${products.value.size + 1}"
        products.value = products.value + Product(id = id, nombre = name, cantidad = quantity)
    }

    override suspend fun deleteProduct(product: Product) {
        products.value = products.value.filterNot { it.id == product.id }
    }

    override suspend fun updateProduct(product: Product) {
        products.value = products.value.map { current ->
            if (current.id == product.id) product else current
        }
    }
}
