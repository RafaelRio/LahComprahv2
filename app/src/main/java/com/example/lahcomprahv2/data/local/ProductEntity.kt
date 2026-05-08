package com.example.lahcomprahv2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val cantidad: Int
)
