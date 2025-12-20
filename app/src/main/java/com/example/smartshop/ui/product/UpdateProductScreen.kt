package com.example.smartshop.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smartshop.data.local.SmartShopDatabase
import com.example.smartshop.data.local.entity.Product
import com.example.smartshop.data.repository.ProductRepository
import com.example.smartshop.data.viewmodel.ProductViewModel
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProductScreen(navController: NavHostController, product: Product) {
    // Same color palette as other pages
    val primaryPink = Color(0xFFFF6B8B) // Soft pink
    val secondaryPink = Color(0xFFFF8FA3) // Lighter pink
    val pastelBlue = Color(0xFF6A9BFF) // Pastel blue
    val lightBackground = Color(0xFFF8F9FF) // Very light blue background
    val darkText = Color(0xFF2D2B55) // Dark text for contrast
    val cardColor = Color.White // White card
    val successGreen = Color(0xFF4CAF50) // Green for success
    val errorColor = Color(0xFFE53935) // Error color

    // Alert states
    var showSuccessAlert by remember { mutableStateOf(false) }
    var showErrorAlert by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val productDao = SmartShopDatabase.getDatabase(context).productDao()
    val repository = ProductRepository(productDao)

    val viewModel: ProductViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductViewModel(repository) as T
            }
        }
    )

    var name by remember { mutableStateOf(product.name) }
    var quantity by remember { mutableStateOf(product.quantity.toString()) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var imageURL by remember { mutableStateOf(product.imageURL ?: "") }

    // Show success alert function
    LaunchedEffect(showSuccessAlert) {
        if (showSuccessAlert) {
            delay(1500) // Show for 1.5 seconds
            showSuccessAlert = false
            navController.popBackStack()
        }
    }

    // Show error alert function
    LaunchedEffect(showErrorAlert) {
        if (showErrorAlert) {
            delay(3000) // Show for 3 seconds
            showErrorAlert = false
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
                                "Update Product",
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
                        navigationIcon = {
                            IconButton(
                                onClick = { navController.popBackStack() }
                            ) {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Top
                ) {
                    // Welcome message
                    Text(
                        text = "Edit Product",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = darkText,
                            fontSize = 24.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    )

                    Text(
                        text = "Update the details of your product",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = darkText.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 24.dp)
                    )

                    // Card container for form
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                clip = true
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = cardColor
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            // Product Name field
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = {
                                    Text(
                                        "Product Name",
                                        color = darkText.copy(alpha = 0.6f)
                                    )
                                },
                                leadingIcon = {
                                    Text(
                                        "📦",
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = darkText,
                                    unfocusedTextColor = darkText,
                                    focusedBorderColor = primaryPink,
                                    unfocusedBorderColor = darkText.copy(alpha = 0.3f),
                                    focusedLabelColor = primaryPink,
                                    unfocusedLabelColor = darkText.copy(alpha = 0.6f),
                                    cursorColor = primaryPink,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                singleLine = true
                            )

                            // Quantity field
                            OutlinedTextField(
                                value = quantity,
                                onValueChange = { quantity = it },
                                label = {
                                    Text(
                                        "Quantity",
                                        color = darkText.copy(alpha = 0.6f)
                                    )
                                },
                                leadingIcon = {
                                    Text(
                                        "🔢",
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = darkText,
                                    unfocusedTextColor = darkText,
                                    focusedBorderColor = primaryPink,
                                    unfocusedBorderColor = darkText.copy(alpha = 0.3f),
                                    focusedLabelColor = primaryPink,
                                    unfocusedLabelColor = darkText.copy(alpha = 0.6f),
                                    cursorColor = primaryPink,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            // Price field
                            OutlinedTextField(
                                value = price,
                                onValueChange = { price = it },
                                label = {
                                    Text(
                                        "Price (DT)",
                                        color = darkText.copy(alpha = 0.6f)
                                    )
                                },
                                leadingIcon = {
                                    Text(
                                        "💰",
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = darkText,
                                    unfocusedTextColor = darkText,
                                    focusedBorderColor = primaryPink,
                                    unfocusedBorderColor = darkText.copy(alpha = 0.3f),
                                    focusedLabelColor = primaryPink,
                                    unfocusedLabelColor = darkText.copy(alpha = 0.6f),
                                    cursorColor = primaryPink,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            // Image URL field
                            OutlinedTextField(
                                value = imageURL,
                                onValueChange = { imageURL = it },
                                label = {
                                    Text(
                                        "Image URL",
                                        color = darkText.copy(alpha = 0.6f)
                                    )
                                },
                                leadingIcon = {
                                    Text(
                                        "🖼️",
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 32.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = darkText,
                                    unfocusedTextColor = darkText,
                                    focusedBorderColor = primaryPink,
                                    unfocusedBorderColor = darkText.copy(alpha = 0.3f),
                                    focusedLabelColor = primaryPink,
                                    unfocusedLabelColor = darkText.copy(alpha = 0.6f),
                                    cursorColor = primaryPink,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                singleLine = true
                            )

                            // Update Product button with gradient
                            Button(
                                onClick = {
                                    try {
                                        val q = quantity.toIntOrNull()
                                        val p = price.toDoubleOrNull()

                                        if (name.isBlank() || q == null || p == null || imageURL.isBlank()) {
                                            alertMessage = "Please fill all fields correctly"
                                            showErrorAlert = true
                                            return@Button
                                        }

                                        if (q <= 0) {
                                            alertMessage = "Quantity must be greater than 0"
                                            showErrorAlert = true
                                            return@Button
                                        }

                                        if (p <= 0) {
                                            alertMessage = "Price must be greater than 0"
                                            showErrorAlert = true
                                            return@Button
                                        }

                                        isLoading = true

                                        val updatedProduct = product.copy(
                                            name = name,
                                            quantity = q,
                                            price = p,
                                            imageURL = imageURL
                                        )

                                        viewModel.updateProduct(updatedProduct)

                                        isLoading = false
                                        alertMessage = "Product updated successfully!"
                                        showSuccessAlert = true
                                    } catch (e: Exception) {
                                        isLoading = false
                                        alertMessage = "Invalid input: ${e.message}"
                                        showErrorAlert = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent
                                ),
                                enabled = !isLoading,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(pastelBlue, Color(0xFF8FA3FF))
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp,
                                            color = Color.White
                                        )
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                "✏️",
                                                color = Color.White,
                                                fontSize = 20.sp
                                            )
                                            Text(
                                                text = "Update Product",
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Success Alert Dialog
            if (showSuccessAlert) {
                AlertDialog(
                    onDismissRequest = {
                        showSuccessAlert = false
                        navController.popBackStack()
                    },
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
                            onClick = {
                                showSuccessAlert = false
                                navController.popBackStack()
                            },
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

            // Error Alert Dialog
            if (showErrorAlert) {
                AlertDialog(
                    onDismissRequest = { showErrorAlert = false },
                    title = {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = errorColor
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
                            onClick = { showErrorAlert = false },
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
        }
    }
}