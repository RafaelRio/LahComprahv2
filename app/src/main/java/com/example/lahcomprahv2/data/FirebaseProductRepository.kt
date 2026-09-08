package com.example.lahcomprahv2.data

import com.example.lahcomprahv2.analytics.ProductAnalytics
import com.example.lahcomprahv2.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseProductRepository(
    private val productAnalytics: ProductAnalytics,
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance().getReference(PRODUCTS_PATH)
) : ProductRepository {

    override fun observeProducts(): Flow<List<Product>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = snapshot.children.mapNotNull { child ->
                    child.getValue(Product::class.java)?.copy(id = child.key.orEmpty())
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    override suspend fun addProduct(name: String, quantity: Int) {
        val validatedName = validateProductData(name, quantity)
        val id = database.push().key
            ?: error("No se pudo generar el identificador del producto")

        val product = Product(
            id = id,
            nombre = validatedName,
            cantidad = quantity
        )

        database.child(id).setValue(product).await()
        productAnalytics.logProductCreated(product)
    }

    override suspend fun updateProduct(product: Product) {
        validateProductId(product.id)
        val validatedName = validateProductData(
            product.nombre,
            product.cantidad
        )
        val validatedProduct = product.copy(nombre = validatedName)

        database.child(validatedProduct.id).setValue(validatedProduct).await()
        productAnalytics.logProductUpdated(validatedProduct)
    }

    override suspend fun deleteProduct(product: Product) {
        validateProductId(product.id)

        database.child(product.id).removeValue().await()
        productAnalytics.logProductDeleted(product)
    }

    private fun validateProductData(name: String, quantity: Int): String {
        val normalizedName = name.trim()

        require(normalizedName.isNotEmpty()) {
            "El nombre del producto no puede estar vacío"
        }
        require(quantity > 0) {
            "La cantidad debe ser mayor que cero"
        }

        return normalizedName
    }

    private fun validateProductId(id: String) {
        require(id.isNotBlank()) {
            "El identificador del producto no puede estar vacío"
        }
        require(id.none { char ->
            char in ".#\$[]/" || char.code <= 31 || char.code == 127
        }) {
            "El identificador del producto contiene caracteres no válidos"
        }
    }

    private companion object {
        const val PRODUCTS_PATH = "products"
    }
}
