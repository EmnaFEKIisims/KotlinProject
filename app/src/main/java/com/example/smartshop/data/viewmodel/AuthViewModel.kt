package com.example.smartshop.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.local.entity.User
import com.example.smartshop.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    fun signIn(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val (success, errorMsg) = userRepository.signIn(email, password)
            onResult(success, errorMsg)
        }
    }


    fun signUp(
        email: String,
        password: String,
        fullName: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = userRepository.signUp(email, password, fullName)
            onResult(success)
        }
    }

    fun logout(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                userRepository.logout()
                onResult(true)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Logout failed: ${e.message}", e)
                onResult(false)
            }
        }
    }

    fun getCurrentUser(onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getCachedUser()
            onResult(user)
        }
    }


}
