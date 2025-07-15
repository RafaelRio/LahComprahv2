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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.lahcomprahv2.R
import com.example.lahcomprahv2.models.Product
import com.example.lahcomprahv2.ui.theme.OnSecondaryColor
import com.example.lahcomprahv2.ui.theme.SecondaryColor
import com.example.lahcomprahv2.ui.theme.SurfaceColor
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaProductosScreen(viewModel: ProductListViewModel = ProductListViewModel()) {
    val productos by viewModel.productos.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Bold)
                }, colors = TopAppBarColors(
                    containerColor = SecondaryColor,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.Unspecified
                ), actions = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        "Add product",
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
            ProductList(productos, viewModel, onEdit = { product ->
                selectedProduct = product // Establecer el producto a editar
                showBottomSheet = true
            })
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                        selectedProduct = null
                    },
                    sheetState = sheetState,
                    windowInsets = WindowInsets.ime
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.ime)
                    ) {
                        BottomSheetAddProduct(
                            viewModel = viewModel,
                            productToEdit = selectedProduct,
                            onDismiss = {
                                showBottomSheet = false
                                selectedProduct = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductList(
    productos: List<Product>,
    viewModel: ProductListViewModel,
    modifier: Modifier = Modifier,
    onEdit: (Product) -> Unit
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(productos) { _, producto ->
            ProductItem(
                producto = producto,
                onDelete = { viewModel.eliminarProducto(producto) },
                onEdit = { onEdit(producto) }
            )
        }
    }
}

@Composable
fun ProductItem(producto: Product, onDelete: () -> Unit, onEdit: () -> Unit) {
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
                    text = producto.nombre.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Cantidad: ${producto.cantidad}",
                    color = SurfaceColor,
                    fontSize = 16.sp
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar producto",
                    tint = SurfaceColor
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar producto",
                    tint = SurfaceColor
                )
            }
        }
    }
}


@Composable
fun BottomSheetAddProduct(
    viewModel: ProductListViewModel,
    productToEdit: Product? = null,
    onDismiss: () -> Unit
) {
    var nombre by remember { mutableStateOf(productToEdit?.nombre ?: "") }
    var cantidad by remember { mutableIntStateOf(productToEdit?.cantidad ?: 1) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val isEditing = productToEdit != null

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (isEditing) "Editar Producto" else "Agregar Producto",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        item {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                modifier = Modifier
                    .fillMaxWidth()
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
                    onClick = { if (cantidad > 1) cantidad-- },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SecondaryColor)
                        .size(48.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Disminuir cantidad",
                        tint = Color.White
                    )
                }

                Text(
                    text = cantidad.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { cantidad++ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SecondaryColor)
                        .size(48.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Aumentar cantidad",
                        tint = Color.White
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        keyboardController?.hide()
                        if (isEditing) {
                            // Editar producto existente
                            val updatedProduct = productToEdit!!.copy(
                                nombre = nombre,
                                cantidad = cantidad
                            )
                            viewModel.editarProducto(updatedProduct)
                        } else {
                            // Agregar nuevo producto
                            viewModel.agregarProductoConImagen(nombre, cantidad)
                        }
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = nombre.isNotBlank()
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isEditing) "Actualizar Producto" else "Agregar Producto")
            }
        }
    }

    // Auto-focus en el campo de texto cuando se abre el bottom sheet
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}