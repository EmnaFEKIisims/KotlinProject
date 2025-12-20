package com.example.smartshop.ui.product

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smartshop.data.local.SmartShopDatabase
import com.example.smartshop.data.local.entity.Product
import com.example.smartshop.data.repository.ProductRepository
import com.example.smartshop.data.repository.UserRepository
import com.example.smartshop.data.viewmodel.AuthViewModel
import com.example.smartshop.data.viewmodel.ProductViewModel
import com.example.smartshop.ui.navigation.Routes
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.shadow
import com.example.smartshop.data.local.entity.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(navController: NavHostController) { // Add navController parameter
    // Same color palette as sign pages
    val primaryPink = Color(0xFFFF6B8B) // Soft pink
    val secondaryPink = Color(0xFFFF8FA3) // Lighter pink
    val pastelBlue = Color(0xFF6A9BFF) // Pastel blue
    val lightBackground = Color(0xFFF8F9FF) // Very light blue background
    val darkText = Color(0xFF2D2B55) // Dark text for contrast
    val cardColor = Color.White // White card
    val successGreen = Color(0xFF4CAF50) // Green for success

    // Alert state
    var showDeleteAlert by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var alertMessage by remember { mutableStateOf("") }
    var showSuccessAlert by remember { mutableStateOf(false) }
    var showLogoutAlert by remember { mutableStateOf(false) }

    // 1️⃣ Database
    val context = LocalContext.current
    val productDao = SmartShopDatabase.getDatabase(context).productDao()
    val repository = ProductRepository(productDao)

    // 2️⃣ ViewModel
    val viewModel: ProductViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductViewModel(repository) as T
            }
        }
    )

    val userDao = SmartShopDatabase.getDatabase(context).userDao()
    val userRepository = UserRepository(userDao)


    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(userRepository) as T
            }
        }
    )

    // 3️⃣ Collect products from Flow
    val products by viewModel.products.collectAsState(initial = emptyList())
    val totalProducts by viewModel.totalProducts.collectAsState(initial = 0)
    val totalStockValue by viewModel.totalStockValue.collectAsState(initial = 0.0)
    val currentUser = remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        authViewModel.getCurrentUser { user ->
            currentUser.value = user
        }
    }



    // Show success alert function
    LaunchedEffect(showSuccessAlert) {
        if (showSuccessAlert) {
            delay(2000) // Show for 2 seconds
            showSuccessAlert = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = lightBackground
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "SmartShop",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = primaryPink,
                            titleContentColor = Color.White
                        ),
                        actions = {
                            IconButton(
                                onClick = { showLogoutAlert = true }
                            ) {
                                Icon(
                                    Icons.Filled.ExitToApp,
                                    contentDescription = "Logout",
                                    tint = Color.White
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            // Navigate to add product screen
                            navController.navigate(Routes.ADD_PRODUCT)
                        },
                        containerColor = primaryPink,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Product")
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Welcome message
                    Text(
                        text = "Welcome, ${currentUser.value?.fullName ?: "User"}!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = darkText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    )

                    // 🔹 DASHBOARD (TOP)
                    Dashboard(
                        totalProducts = totalProducts,
                        totalStockValue = totalStockValue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 🔹 PRODUCT LIST (BOTTOM)
                    if (products.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "No products available",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = darkText.copy(alpha = 0.6f)
                                    )
                                )
                                Text(
                                    "Tap + to add your first product",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = darkText.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(products) { product ->
                                ProductItem(
                                    product = product,
                                    onDelete = {
                                        productToDelete = it
                                        showDeleteAlert = true
                                    },
                                    onEdit = {
                                        // Navigate to edit product screen
                                        navController.navigate("${Routes.EDIT_PRODUCT}/${product.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Delete Confirmation Alert
            if (showDeleteAlert) {
                AlertDialog(
                    onDismissRequest = { showDeleteAlert = false },
                    title = {
                        Text(
                            text = "Confirm Delete",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = darkText
                            )
                        )
                    },
                    text = {
                        Text(
                            text = "Are you sure you want to delete this product?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = darkText.copy(alpha = 0.8f)
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                productToDelete?.let { viewModel.deleteProduct(it) }
                                showDeleteAlert = false
                                alertMessage = "Product deleted successfully!"
                                showSuccessAlert = true
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFFE53935) // Red color for delete
                            )
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteAlert = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = primaryPink
                            )
                        ) {
                            Text("Cancel")
                        }
                    },
                    containerColor = cardColor,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.shadow(16.dp, RoundedCornerShape(20.dp))
                )
            }

            // Success Alert
            if (showSuccessAlert) {
                AlertDialog(
                    onDismissRequest = { showSuccessAlert = false },
                    title = {
                        Text(
                            text = "Success!",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = successGreen
                            )
                        )
                    },
                    text = {
                        Text(
                            text = alertMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = darkText
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { showSuccessAlert = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = primaryPink
                            )
                        ) {
                            Text("OK")
                        }
                    },
                    containerColor = cardColor,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.shadow(16.dp, RoundedCornerShape(20.dp))
                )
            }

            // Logout Confirmation Alert
            if (showLogoutAlert) {
                AlertDialog(
                    onDismissRequest = { showLogoutAlert = false },
                    title = {
                        Text(
                            text = "Confirm Logout",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = darkText
                            )
                        )
                    },
                    text = {
                        Text(
                            text = "Are you sure you want to logout?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = darkText.copy(alpha = 0.8f)
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                authViewModel.logout { success ->
                                    if (success) {
                                        // Navigate back to SignIn screen
                                        navController.navigate(Routes.SIGN_IN) {
                                            popUpTo(0) { inclusive = true } // clears backstack
                                        }
                                    } else {
                                        Toast.makeText(context, "Logout failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                showLogoutAlert = false
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFFE53935) // Red color for logout
                            )
                        ) {
                            Text("Logout")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showLogoutAlert = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = primaryPink
                            )
                        ) {
                            Text("Cancel")
                        }
                    },
                    containerColor = cardColor,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.shadow(16.dp, RoundedCornerShape(20.dp))
                )
            }
        }
    }
}