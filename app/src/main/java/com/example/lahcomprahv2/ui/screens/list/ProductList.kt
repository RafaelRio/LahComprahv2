package com.example.lahcomprahv2.ui.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lahcomprahv2.R
import com.example.lahcomprahv2.models.Product
import com.example.lahcomprahv2.ui.theme.OnSecondaryColor
import com.example.lahcomprahv2.ui.theme.SecondaryColor
import com.example.lahcomprahv2.ui.theme.SurfaceColor
import java.util.Locale
import org.koin.androidx.compose.koinViewModel

private const val PRODUCT_NAME_FIELD_TAG = "product_name_field"
private const val DELETE_CONFIRM_DIALOG_TAG = "delete_confirm_dialog"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsListScreen(viewModel: ProductListViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var productPendingDelete by remember { mutableStateOf<Product?>(null) }
    var dismissSheetOnCompletedOperation by remember { mutableIntStateOf(-1) }

    LaunchedEffect(uiState.completedOperationCount, dismissSheetOnCompletedOperation) {
        if (dismissSheetOnCompletedOperation == -1) return@LaunchedEffect
        if (uiState.completedOperationCount < dismissSheetOnCompletedOperation) return@LaunchedEffect
        dismissSheetOnCompletedOperation = -1
        showBottomSheet = false
        selectedProduct = null
    }

    LaunchedEffect(uiState.errorMessage) {
        val errorMessage = uiState.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(errorMessage)
        viewModel.clearError()
        dismissSheetOnCompletedOperation = -1
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Bold)
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SecondaryColor,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ), actions = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.add_product),
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .clip(CircleShape)
                            .size(35.dp)
                            .clickable {
                                selectedProduct = null // Limpiar producto seleccionado para agregar nuevo
                                showBottomSheet = true
                            },
                        tint = Color.White
                    )
                }
            )

        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            ProductList(
                products = uiState.products,
                onDelete = { product ->
                    productPendingDelete = product
                },
                onEdit = { product ->
                    selectedProduct = product // Establecer el producto a editar
                    showBottomSheet = true
                }
            )
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        if (!uiState.isSaving) {
                            showBottomSheet = false
                            selectedProduct = null
                        }
                    },
                    sheetState = sheetState,
                    contentWindowInsets = { WindowInsets.ime }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.ime)
                    ) {
                        BottomSheetAddProduct(
                            viewModel = viewModel,
                            isSaving = uiState.isSaving,
                            onSaveSucceeded = {
                                dismissSheetOnCompletedOperation = uiState.completedOperationCount + 1
                            },
                            productToEdit = selectedProduct,
                            onDismiss = {
                                dismissSheetOnCompletedOperation = -1
                                showBottomSheet = false
                                selectedProduct = null
                            }
                        )
                    }
                }
            }

            val productToDelete = productPendingDelete
            if (productToDelete != null) {
                AlertDialog(
                    onDismissRequest = {
                        if (!uiState.isSaving) {
                            productPendingDelete = null
                        }
                    },
                    modifier = Modifier.testTag(DELETE_CONFIRM_DIALOG_TAG),
                    title = {
                        Text(stringResource(R.string.delete_product_confirmation_title))
                    },
                    text = {
                        Text(
                            stringResource(
                                R.string.delete_product_confirmation_message,
                                productToDelete.nombre
                            )
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteProduct(productToDelete)
                                productPendingDelete = null
                            },
                            enabled = !uiState.isSaving
                        ) {
                            Text(stringResource(R.string.confirm))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { productPendingDelete = null },
                            enabled = !uiState.isSaving
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProductList(
    products: List<Product>,
    onDelete: (Product) -> Unit,
    modifier: Modifier = Modifier,
    onEdit: (Product) -> Unit
) {
    if (products.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.empty_products),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    LazyColumn(modifier = modifier) {
        items(items = products, key = { it.id }) { product ->
            ProductItem(
                product = product,
                onDelete = { onDelete(product) },
                onEdit = { onEdit(product) }
            )
        }
    }
}

@Composable
fun ProductItem(product: Product, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        colors = CardDefaults.cardColors(containerColor = OnSecondaryColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.nombre.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = stringResource(R.string.product_quantity, product.cantidad),
                    color = SurfaceColor,
                    fontSize = 16.sp
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_product),
                    tint = SurfaceColor
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_product),
                    tint = SurfaceColor
                )
            }
        }
    }
}


@Composable
fun BottomSheetAddProduct(
    viewModel: ProductListViewModel,
    isSaving: Boolean,
    onSaveSucceeded: () -> Unit,
    productToEdit: Product? = null,
    onDismiss: () -> Unit
) {
    var name by remember(productToEdit?.id) { mutableStateOf(productToEdit?.nombre.orEmpty()) }
    var quantity by remember(productToEdit?.id) { mutableIntStateOf(productToEdit?.cantidad ?: 1) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val isEditing = productToEdit != null
    val buttonLabel = if (isEditing) {
        stringResource(R.string.update_product)
    } else {
        stringResource(R.string.add_product)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (isEditing) {
                    stringResource(R.string.edit_product_title)
                } else {
                    stringResource(R.string.add_product_title)
                },
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.product_name)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(PRODUCT_NAME_FIELD_TAG)
                    .focusRequester(focusRequester)
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SecondaryColor)
                        .size(48.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.decrease_quantity),
                        tint = Color.White
                    )
                }

                Text(
                    text = quantity.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { quantity++ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SecondaryColor)
                        .size(48.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.increase_quantity),
                        tint = Color.White
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        keyboardController?.hide()
                        onSaveSucceeded()
                        if (isEditing) {
                            val updatedProduct = productToEdit.copy(
                                nombre = name.trim(),
                                cantidad = quantity
                            )
                            viewModel.updateProduct(updatedProduct)
                        } else {
                            viewModel.addProduct(name, quantity)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("save_product_button"),
                enabled = name.isNotBlank() && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.saving))
                } else {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(buttonLabel)
                }
            }
        }
    }

    LaunchedEffect(productToEdit?.id) {
        focusRequester.requestFocus()
    }
}
