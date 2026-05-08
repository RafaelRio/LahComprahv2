package com.example.lahcomprahv2.ui.screens.list

import com.example.lahcomprahv2.models.Product

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val completedOperationCount: Int = 0,
    val errorMessage: String? = null
)
