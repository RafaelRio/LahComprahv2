package com.example.lahcomprahv2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LahComprahDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
