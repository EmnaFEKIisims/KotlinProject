package com.example.smartshop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartshop.ui.auth.SignInScreen
import com.example.smartshop.ui.auth.SignUpScreen
import com.example.smartshop.ui.product.AddProductScreen
import com.example.smartshop.ui.product.ProductListScreen
import com.example.smartshop.ui.product.UpdateProductScreen
import com.example.smartshop.data.repository.ProductRepository
import com.example.smartshop.data.local.SmartShopDatabase
import com.example.smartshop.data.viewmodel.ProductViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartshop.data.local.entity.Product

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

        composable(Routes.PRODUCT_LIST) {
            ProductListScreen(navController)
        }

        composable(Routes.ADD_PRODUCT) {
            AddProductScreen(navController)
        }

        composable("update_product/{firebaseId}") { backStackEntry ->
            val firebaseId = backStackEntry.arguments?.getString("firebaseId")
            val context = LocalContext.current

            if (firebaseId != null) {
                // Initialize database and repository
                val productDao = SmartShopDatabase.getDatabase(context).productDao()
                val repository = ProductRepository(productDao)
                val viewModel: ProductViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ProductViewModel(repository) as T
                        }
                    }
                )

                // --- FIX: Declare mutable state with proper imports ---
                val productState = remember { mutableStateOf<Product?>(null) }

                // Load product from ViewModel
                LaunchedEffect(firebaseId) {
                    productState.value = viewModel.getProductByFirebaseId(firebaseId)
                }

                // Show UpdateProductScreen when product is loaded
                productState.value?.let { product ->
                    UpdateProductScreen(navController, product)
                }
            }
        }





    }
}