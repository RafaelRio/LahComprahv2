package com.example.lahcomprahv2.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lahcomprahv2.data.ProductRepository
import com.example.lahcomprahv2.models.Product
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class ProductListViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()
    private var observationJob: Job? = null

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
        if (observationJob?.isActive == true) return

        _uiState.update {
            it.copy(
                isLoading = true,
                syncErrorMessage = null
            )
        }

        observationJob = viewModelScope.launch {
            repository.observeProducts()
                .catch {
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            syncErrorMessage =
                                "No se pudo sincronizar la lista. Inténtalo de nuevo."
                        )
                    }
                }
                .collect { products ->
                    _uiState.update { current ->
                        current.copy(
                            products = products.sortedBy {
                                it.nombre.lowercase()
                            },
                            isLoading = false,
                            syncErrorMessage = null
                        )
                    }
                }
        }
    }

    fun retryObservation() {
        observeProducts()
    }

    private fun saveAction(block: suspend () -> Unit) {
        if (_uiState.value.isSaving) return

        _uiState.update { current ->
            current.copy(
                isSaving = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                block()

                _uiState.update { current ->
                    current.copy(
                        completedOperationCount =
                            current.completedOperationCount + 1,
                        errorMessage = null
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { current ->
                    current.copy(
                        errorMessage = exception.message
                            ?: "No se pudo completar la operación"
                    )
                }
            } finally {
                _uiState.update { current ->
                    current.copy(isSaving = false)
                }
            }
        }
    }
}

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val completedOperationCount: Int = 0,
    val errorMessage: String? = null,
    val syncErrorMessage: String? = null,
)
