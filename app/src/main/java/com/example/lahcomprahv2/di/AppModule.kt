package com.example.lahcomprahv2.di

import com.example.lahcomprahv2.analytics.ProductAnalytics
import com.example.lahcomprahv2.data.FirebaseProductRepository
import com.example.lahcomprahv2.data.ProductRepository
import com.example.lahcomprahv2.ui.screens.list.ProductListViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAnalytics.getInstance(get()) }
    single { ProductAnalytics(get()) }
    single<ProductRepository> { FirebaseProductRepository(get()) }
    viewModel { ProductListViewModel(get()) }
}
