package com.example.lahcomprahv2.ui.screens.list

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.lahcomprahv2.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductListViewModel: ViewModel() {

    private val _productos = MutableStateFlow<List<Product>>(emptyList())
    val productos: StateFlow<List<Product>> = _productos

    private val database = FirebaseDatabase.getInstance().getReference("products")

    init {
        obtenerProductos()
    }

    private fun obtenerProductos() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listaProductos = snapshot.children.mapNotNull { it.getValue(Product::class.java) }
                _productos.value = listaProductos
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al obtener datos", error.toException())
            }
        })
    }

    fun agregarProductoConImagen(nombre: String, cantidad: Int) {
        val id = database.push().key ?: return
        val producto = Product(id, nombre, cantidad)
        database.child(id).setValue(producto)
    }

    fun eliminarProducto(producto: Product) {
        database.child(producto.id).removeValue()
    }

    fun editarProducto(producto: Product) {
        database.child(producto.id).setValue(producto)
    }

}