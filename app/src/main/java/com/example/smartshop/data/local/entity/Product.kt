package com.example.smartshop.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "products"  , indices = [Index(value = ["firebaseId"], unique = true)])
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val quantity: Int,
    val price: Double,
    val imageURL: String? ,
    val firebaseId: String? = null
)