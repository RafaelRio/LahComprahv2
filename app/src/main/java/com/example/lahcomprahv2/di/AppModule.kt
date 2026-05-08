package com.example.lahcomprahv2.di

import androidx.room.Room
import com.example.lahcomprahv2.data.ProductRepository
import com.example.lahcomprahv2.data.RoomProductRepository
import com.example.lahcomprahv2.data.local.LahComprahDatabase
import com.example.lahcomprahv2.ui.screens.list.ProductListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            LahComprahDatabase::class.java,
            "lahcomprah.db"
        ).fallbackToDestructiveMigration(dropAllTables = false).build()
    }
    single { get<LahComprahDatabase>().productDao() }
    single<ProductRepository> { RoomProductRepository(get()) }
    viewModel { ProductListViewModel(get()) }
}
