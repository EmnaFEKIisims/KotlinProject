package com.example.smartshop.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.local.entity.Product
import com.example.smartshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import android.util.Log
import com.google.firebase.firestore.QuerySnapshot

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    // Flow of products from Room
    val products: Flow<List<Product>> = repository.getAllProducts()
    val totalProducts = repository.getTotalProducts()
    val totalStockValue = repository.getTotalStockValue()



    init {
        // Start listening to Firebase updates
        repository.listenToFirebaseUpdates()


    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            repository.addProduct(product)
            repository.listenToFirebaseUpdates()
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }
    fun getProductByFirebaseId(firebaseId: String): Product? {
        // This is a suspend function in Room
        var product: Product? = null
        runBlocking {
            product = repository.getProductByFirebaseId(firebaseId)
        }
        return product
    }

}
