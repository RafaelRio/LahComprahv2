package com.example.lahcomprahv2.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lahcomprahv2.data.ProductRepository
import com.example.lahcomprahv2.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        observeProducts()
    }

    fun addProduct(name: String, quantity: Int) {
        saveAction {
            repository.addProduct(name = name.trim(), quantity = quantity)
        }
    }

    fun deleteProduct(product: Product) {
        saveAction {
            repository.deleteProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        saveAction {
            repository.updateProduct(product.copy(nombre = product.nombre.trim()))
        }
    }

    fun clearError() {
        _uiState.update { current -> current.copy(errorMessage = null) }
    }

    private fun observeProducts() {
        viewModelScope.launch {
            repository.observeProducts()
                .catch { throwable ->
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unknown error"
                        )
                    }
                }
                .collect { products ->
                    _uiState.update { current ->
                        current.copy(
                            products = products.sortedBy { it.nombre.lowercase() },
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    private fun saveAction(block: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { current ->
                current.copy(isSaving = true)
            }
            try {
                block()
                _uiState.update { current ->
                    current.copy(
                        isSaving = false,
                        completedOperationCount = current.completedOperationCount + 1,
                        errorMessage = null
                    )
                }
            } catch (throwable: Throwable) {
                _uiState.update { current ->
                    current.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Unknown error"
                    )
                }
            }
        }
    }
}
