package com.example.lahcomprahv2.data

import com.example.lahcomprahv2.models.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProducts(): Flow<List<Product>>

    suspend fun addProduct(name: String, quantity: Int)

    suspend fun deleteProduct(product: Product)

    suspend fun updateProduct(product: Product)
}
