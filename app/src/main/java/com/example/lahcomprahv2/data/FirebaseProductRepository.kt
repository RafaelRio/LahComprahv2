package com.example.lahcomprahv2.data

import com.example.lahcomprahv2.analytics.ProductAnalytics
import com.example.lahcomprahv2.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

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
                cancel(CancellationException(error.message, error.toException()))
            }
        }

        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    override suspend fun addProduct(name: String, quantity: Int) {
        val id = database.push().key ?: error("Unable to generate Firebase key")
        val product = Product(id = id, nombre = name, cantidad = quantity)
        database.child(id).setValue(product).await()
        productAnalytics.logProductCreated(product)
    }

    override suspend fun deleteProduct(product: Product) {
        database.child(product.id).removeValue().await()
        productAnalytics.logProductDeleted(product)
    }

    override suspend fun updateProduct(product: Product) {
        database.child(product.id).setValue(product).await()
        productAnalytics.logProductUpdated(product)
    }

    private companion object {
        const val PRODUCTS_PATH = "products"
    }
}
