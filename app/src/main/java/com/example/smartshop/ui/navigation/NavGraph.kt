package com.example.smartshop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartshop.ui.auth.SignInScreen
import com.example.smartshop.ui.auth.SignUpScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SIGN_IN
    ) {
        composable(Routes.SIGN_IN) {
            SignInScreen(navController)
        }

        composable(Routes.SIGN_UP) {
            SignUpScreen(navController)

        }
    }
}