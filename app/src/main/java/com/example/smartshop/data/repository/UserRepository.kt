package com.example.smartshop.data.repository

import android.util.Log
import com.example.smartshop.data.local.dao.UserDao
import com.example.smartshop.data.local.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val userDao: UserDao
) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    // 🔐 SIGN UP
    suspend fun signUp(
        email: String,
        password: String,
        fullName: String
    ): Boolean {
        return try {
            // 1️⃣ Firebase Authentication
            val authResult = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val firebaseId = authResult.user?.uid
                ?: throw Exception("Firebase UID null")

            // 2️⃣ Firestore profile
            val userMap = mapOf(
                "email" to email,
                "fullName" to fullName
            )
            usersCollection.document(firebaseId).set(userMap).await()

            // 3️⃣ Room cache
            val user = User(
                email = email,
                fullName = fullName,
                firebaseId = firebaseId
            )
            userDao.insertUser(user)

            true
        } catch (e: Exception) {
            Log.e("UserRepository", "SignUp failed", e)
            false
        }
    }

    // 🔐 SIGN IN
    suspend fun signIn(email: String, password: String): Pair<Boolean, String?> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseId = authResult.user?.uid ?: throw Exception("Firebase UID null")
            val doc = usersCollection.document(firebaseId).get().await()
            val user = User(
                email = doc.getString("email") ?: "",
                fullName = doc.getString("fullName") ?: "",
                firebaseId = firebaseId
            )
            userDao.insertUser(user)
            Pair(true, null)
        } catch (e: Exception) {
            Log.e("UserRepository", "SignIn failed", e)
            Pair(false, e.message ?: "Unknown error")
        }
    }


    // 🔓 LOG OUT
    suspend fun logout() {
        auth.signOut()
        userDao.clearUser()
    }

    suspend fun getCachedUser(): User? {
        return userDao.getLoggedInUser()
    }

}
