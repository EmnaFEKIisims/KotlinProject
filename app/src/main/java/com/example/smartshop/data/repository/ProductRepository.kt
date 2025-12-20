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
            // Save in Firebase first
            val productMap = hashMapOf(
                "name" to product.name,
                "quantity" to product.quantity,
                "price" to product.price,
                "imageURL" to product.imageURL
            )
            val docRef = collection.add(productMap).await()

            // Prepare product with firebaseId
            val productWithFirebaseId = product.copy(firebaseId = docRef.id)

            // Insert into Room
            productDao.insertProduct(productWithFirebaseId)

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
                    "price" to product.price ,
                    "imageURL" to product.imageURL
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
        Log.d("FIREBASE", "🔥 listenToFirebaseUpdates STARTED")
        collection.addSnapshotListener { snapshot, error ->

            if (error != null || snapshot == null) {
                Log.e("FIREBASE", "Listener error or snapshot null", error)
                return@addSnapshotListener
            }

            Log.d("FIREBASE", "Documents count = ${snapshot.documents.size}")

            CoroutineScope(Dispatchers.IO).launch {
                for (doc in snapshot.documents) {
                    val firebaseId = doc.id
                    val existingProduct = productDao.getProductByFirebaseId(firebaseId)

                    val product = Product(
                        id = existingProduct?.id ?: 0, // reuse Room id if exists
                        name = doc.getString("name") ?: "",
                        quantity = (doc.getLong("quantity") ?: 0L).toInt(),
                        price = doc.getDouble("price") ?: 0.0,
                        imageURL = doc.getString("imageURL") ?: "",
                        firebaseId = firebaseId
                    )

                    if (existingProduct != null) {
                        productDao.updateProduct(product)
                    } else {
                        productDao.insertProduct(product)
                    }
                }
                Log.d("ROOM", "Firebase sync finished")
            }
        }
    }


    suspend fun getProductByFirebaseId(firebaseId: String?): Product? {
        if (firebaseId == null) return null
        return productDao.getProductByFirebaseId(firebaseId)
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
