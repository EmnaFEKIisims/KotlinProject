package com.example.smartshop.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.local.entity.Product
import com.example.smartshop.data.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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
}
