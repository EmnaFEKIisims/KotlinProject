package com.example.smartshop.data.local.dao

import androidx.room.*
import com.example.smartshop.data.local.entity.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long  // Returns Room ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>) // Bulk insert for Firebase sync

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE firebaseId = :firebaseId LIMIT 1")
    suspend fun getProductByFirebaseId(firebaseId: String?): Product?
}