package com.example.smartshop.data.repository

import android.util.Log
import com.example.smartshop.data.local.dao.UserDao
import com.example.smartshop.data.local.entity.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val userDao: UserDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("users")

    // Sign-up user
    suspend fun signUp(user: User): Boolean {
        try {
            // 1️⃣ Save locally
            val localId = userDao.insertUser(user)

            // 2️⃣ Save in Firebase
            val userMap = hashMapOf(
                "name" to user.fullName,
                "email" to user.email,
                "password" to user.password
            )
            val docRef = collection.add(userMap).await()

            Log.d("UserRepository", "User signed up: ${docRef.id}")
            return true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error signing up user", e)
            return false
        }
    }

    // Sign-in user
    suspend fun signIn(email: String, password: String): Boolean {
        try {
            // 1️⃣ Check locally
            val localUser = userDao.getUserByEmail(email)
            if (localUser != null && localUser.password == password) return true

            // 2️⃣ If not found locally, check Firebase
            val querySnapshot = collection.whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val doc = querySnapshot.documents[0]
                val user = User(
                    fullName = doc.getString("fullName") ?: "",
                    email = doc.getString("email") ?: "",
                    password = doc.getString("password") ?: ""
                )
                // Save locally for future offline sign-in
                userDao.insertUser(user)
                return true
            }

            return false
        } catch (e: Exception) {
            Log.e("UserRepository", "Error signing in user", e)
            return false
        }
    }

    fun listenToFirebaseUpdates(onUpdate: (List<User>) -> Unit) {
        collection.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener

            val users = snapshot.documents.map { doc ->
                User(
                    fullName = doc.getString("fullName") ?: "",
                    email = doc.getString("email") ?: "",
                    password = doc.getString("password") ?: ""
                )
            }
            onUpdate(users)
        }
    }

}