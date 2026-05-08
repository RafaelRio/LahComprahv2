package com.example.lahcomprahv2.ui.screens.list

import com.example.lahcomprahv2.MainDispatcherRule
import com.example.lahcomprahv2.data.ProductRepository
import com.example.lahcomprahv2.models.Product
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads and sorts products from repository`() = runTest {
        val repository = FakeProductRepository(
            products = listOf(
                Product(id = "2", nombre = "zanahorias", cantidad = 1),
                Product(id = "1", nombre = "aceite", cantidad = 2)
            )
        )

        val viewModel = ProductListViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(listOf("aceite", "zanahorias"), viewModel.uiState.value.products.map { it.nombre })
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `exposes error when repository stream fails`() = runTest {
        val viewModel = ProductListViewModel(FailingProductRepository("firebase down"))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("firebase down", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `trims name before adding product`() = runTest {
        val repository = FakeProductRepository()
        val viewModel = ProductListViewModel(repository)

        advanceUntilIdle()
        viewModel.addProduct("  leche  ", 3)
        advanceUntilIdle()

        assertEquals("leche", repository.lastAddedName)
        assertEquals(3, repository.lastAddedQuantity)
    }

    @Test
    fun `clear error resets visible error message`() = runTest {
        val viewModel = ProductListViewModel(FailingProductRepository("boom"))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.errorMessage != null)
        viewModel.clearError()

        assertNull(viewModel.uiState.value.errorMessage)
    }
}

private class FakeProductRepository(
    products: List<Product> = emptyList()
) : ProductRepository {
    private val productFlow = MutableStateFlow(products)

    var lastAddedName: String? = null
    var lastAddedQuantity: Int? = null

    override fun observeProducts(): Flow<List<Product>> = productFlow

    override suspend fun addProduct(name: String, quantity: Int) {
        lastAddedName = name
        lastAddedQuantity = quantity
        productFlow.value = productFlow.value + Product(
            id = "generated",
            nombre = name,
            cantidad = quantity
        )
    }

    override suspend fun deleteProduct(product: Product) {
        productFlow.value = productFlow.value.filterNot { it.id == product.id }
    }

    override suspend fun updateProduct(product: Product) {
        productFlow.value = productFlow.value.map {
            if (it.id == product.id) product else it
        }
    }
}

private class FailingProductRepository(
    private val message: String
) : ProductRepository {
    override fun observeProducts(): Flow<List<Product>> = flow {
        error(message)
    }

    override suspend fun addProduct(name: String, quantity: Int) = Unit

    override suspend fun deleteProduct(product: Product) = Unit

    override suspend fun updateProduct(product: Product) = Unit
}
