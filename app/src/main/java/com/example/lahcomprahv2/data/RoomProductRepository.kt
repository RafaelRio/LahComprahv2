package com.example.lahcomprahv2.data

import com.example.lahcomprahv2.data.local.ProductDao
import com.example.lahcomprahv2.data.local.toDomain
import com.example.lahcomprahv2.data.local.toEntity
import com.example.lahcomprahv2.models.Product
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomProductRepository(
    private val productDao: ProductDao
) : ProductRepository {

    override fun observeProducts(): Flow<List<Product>> {
        return productDao.observeProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addProduct(name: String, quantity: Int) {
        productDao.upsert(
            Product(
                id = UUID.randomUUID().toString(),
                nombre = name,
                cantidad = quantity
            ).toEntity()
        )
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.deleteById(product.id)
    }

    override suspend fun updateProduct(product: Product) {
        productDao.upsert(product.toEntity())
    }
}
