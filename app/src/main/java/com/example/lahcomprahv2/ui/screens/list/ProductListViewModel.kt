package com.example.lahcomprahv2.ui.screens.list

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.lahcomprahv2.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class ProductListViewModel: ViewModel() {

    private val _productos = MutableStateFlow<List<Product>>(emptyList())
    val productos: StateFlow<List<Product>> = _productos
    private val storage = FirebaseStorage.getInstance().getReference("imagenes_productos")

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
        val producto = Product(id, nombre, cantidad, null)
        database.child(id).setValue(producto)
    }

    fun eliminarProducto(producto: Product) {
        // 1. Eliminar imagen si existe
        producto.imagenUrl?.let { url ->
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.getReferenceFromUrl(url)
            storageRef.delete()
                .addOnSuccessListener {
                    Log.d("Firebase", "Imagen eliminada correctamente")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error al eliminar la imagen: ${e.message}", e)
                }
        }

        // 2. Eliminar producto de la base de datos
        database.child(producto.id).removeValue()
    }

}