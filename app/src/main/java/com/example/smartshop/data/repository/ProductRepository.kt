package com.example.smartshop.data.repository

import android.util.Log
import com.example.smartshop.data.local.dao.ProductDao
import com.example.smartshop.data.local.entity.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class ProductRepository(private val productDao: ProductDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("products")

    // Add product to Room and Firebase
    suspend fun addProduct(product: Product) {
        if (!validateProduct(product)) return
        try {
            // Save locally
            val roomId = productDao.insertProduct(product)

            // Save in Firebase
            val productMap = hashMapOf(
                "name" to product.name,
                "quantity" to product.quantity,
                "price" to product.price
            )
            val docRef = collection.add(productMap).await()

            // Update Room with firebaseId
            productDao.updateProduct(product.copy(id = roomId.toInt(), firebaseId = docRef.id))
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error adding product", e)
        }
    }

    // Update product
    suspend fun updateProduct(product: Product) {
        if (!validateProduct(product)) return
        try {
            // Update locally
            productDao.updateProduct(product)

            // Update in Firebase
            product.firebaseId?.let { id ->
                val productMap = hashMapOf(
                    "name" to product.name,
                    "quantity" to product.quantity,
                    "price" to product.price
                )
                collection.document(id).set(productMap).await()
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error updating product", e)
        }
    }

    // Delete product
    suspend fun deleteProduct(product: Product) {
        try {
            // Delete locally
            productDao.deleteProduct(product)

            // Delete from Firebase
            product.firebaseId?.let { id ->
                collection.document(id).delete().await()
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error deleting product", e)
        }
    }

    // Read products from Room
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

    // Real-time sync from Firebase to Room
    fun listenToFirebaseUpdates() {
        collection.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener

            val products = snapshot.documents.map { doc ->
                Product(
                    id = 0, // Room will generate a local ID
                    name = doc.getString("name") ?: "",
                    quantity = (doc.getLong("quantity") ?: 0L).toInt(),
                    price = doc.getDouble("price") ?: 0.0,
                    firebaseId = doc.id
                )
            }

            // Update Room with Firebase data
            CoroutineScope(Dispatchers.IO).launch {
                productDao.insertProducts(products)
            }
        }
    }

    // Calculate total number of products
    fun getTotalProducts(): Flow<Int> =
        productDao.getAllProducts().map { list -> list.size }

    // Calculate total stock value
    fun getTotalStockValue(): Flow<Double> =
        productDao.getAllProducts().map { list ->
            list.sumOf { it.quantity * it.price }
        }

    private fun validateProduct(product: Product): Boolean {
        return product.price > 0 && product.quantity >= 0
    }
}
