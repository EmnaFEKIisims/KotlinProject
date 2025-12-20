package com.example.smartshop.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.local.entity.User
import com.example.smartshop.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    fun signIn(
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = userRepository.signIn(email, password)
            onResult(success)
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
}
