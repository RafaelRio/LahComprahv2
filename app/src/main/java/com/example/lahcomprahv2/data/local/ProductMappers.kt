package com.example.lahcomprahv2.data.local

import com.example.lahcomprahv2.models.Product

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    nombre = nombre,
    cantidad = cantidad
)

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    nombre = nombre,
    cantidad = cantidad
)
