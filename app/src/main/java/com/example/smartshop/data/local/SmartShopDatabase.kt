package com.example.smartshop.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smartshop.data.local.dao.ProductDao
import com.example.smartshop.data.local.entity.Product

@Database(entities = [Product::class], version = 1)
abstract class SmartShopDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: SmartShopDatabase? = null

        fun getDatabase(context: Context): SmartShopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartShopDatabase::class.java,
                    "smartshop_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}