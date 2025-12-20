package com.example.smartshop.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smartshop.data.local.SmartShopDatabase
import com.example.smartshop.data.repository.UserRepository
import com.example.smartshop.data.viewmodel.AuthViewModel
import com.example.smartshop.ui.navigation.Routes
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavHostController) {
    // Same premium color palette as SignIn
    val primaryPink = Color(0xFFFF6B8B) // Soft pink
    val secondaryPink = Color(0xFFFF8FA3) // Lighter pink
    val pastelBlue = Color(0xFF6A9BFF) // Pastel blue
    val lightBackground = Color(0xFFF8F9FF) // Very light blue background
    val darkText = Color(0xFF2D2B55) // Dark text for contrast
    val cardColor = Color.White // White card
    val errorColor = Color(0xFFE53935) // Error color

    // Alert states
    var showSuccessAlert by remember { mutableStateOf(false) }
    var showErrorAlert by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userDao = SmartShopDatabase.getDatabase(context).userDao()
    val userRepository = UserRepository(userDao)

    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(userRepository) as T
            }
        }
    )

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Show success alert function
    LaunchedEffect(showSuccessAlert) {
        if (showSuccessAlert) {
            delay(2000) // Show for 2 seconds
            showSuccessAlert = false
            navController.navigate(Routes.SIGN_IN)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Title with pink gradient
                Text(
                    text = "SmartShop",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        color = primaryPink
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Create your account",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = darkText.copy(alpha = 0.6f),
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(bottom = 32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                // Card container for form
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sign Up",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = darkText,
                                fontSize = 24.sp
                            ),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Full Name field
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = {
                                Text(
                                    "Full Name",
                                    color = darkText.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = "Full Name",
                                    tint = primaryPink
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
                                focusedLeadingIconColor = primaryPink,
                                unfocusedLeadingIconColor = primaryPink.copy(alpha = 0.7f),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            singleLine = true
                        )

                        // Email field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = {
                                Text(
                                    "Email",
                                    color = darkText.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Email,
                                    contentDescription = "Email",
                                    tint = primaryPink
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
                                focusedLeadingIconColor = primaryPink,
                                unfocusedLeadingIconColor = primaryPink.copy(alpha = 0.7f),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            singleLine = true
                        )

                        // Password field with text toggle (SHOW/HIDE)
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = {
                                Text(
                                    "Password",
                                    color = darkText.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Lock,
                                    contentDescription = "Password",
                                    tint = primaryPink
                                )
                            },
                            trailingIcon = {
                                // Text toggle instead of icon
                                Text(
                                    text = if (passwordVisible) "HIDE" else "SHOW",
                                    color = primaryPink,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                        ) {
                                            passwordVisible = !passwordVisible
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
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
                                focusedLeadingIconColor = primaryPink,
                                unfocusedLeadingIconColor = primaryPink.copy(alpha = 0.7f),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTrailingIconColor = primaryPink,
                                unfocusedTrailingIconColor = primaryPink.copy(alpha = 0.7f)
                            ),
                            visualTransformation = if (passwordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            singleLine = true
                        )

                        // Confirm Password field (uses same passwordVisible state)
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = {
                                Text(
                                    "Confirm Password",
                                    color = darkText.copy(alpha = 0.6f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Lock,
                                    contentDescription = "Confirm Password",
                                    tint = primaryPink
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
                                focusedLeadingIconColor = primaryPink,
                                unfocusedLeadingIconColor = primaryPink.copy(alpha = 0.7f),
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            visualTransformation = if (passwordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            singleLine = true
                        )

                        // Sign Up button with pink gradient
                        Button(
                            onClick = {
                                if (password != confirmPassword) {
                                    alertMessage = "Passwords do not match"
                                    showErrorAlert = true
                                    return@Button
                                }

                                if (email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                                    alertMessage = "Please fill all fields"
                                    showErrorAlert = true
                                    return@Button
                                }

                                isLoading = true
                                authViewModel.signUp(email, password, fullName) { success ->
                                    isLoading = false
                                    if (success) {
                                        alertMessage = "Account created successfully!"
                                        showSuccessAlert = true
                                    } else {
                                        alertMessage = "Sign up failed. Please try again."
                                        showErrorAlert = true
                                    }
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
                                            colors = listOf(primaryPink, secondaryPink)
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
                                    Text(
                                        text = "Sign Up",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign in text with pastel blue accent
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Already have an account? ",
                        color = darkText.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Sign In",
                        color = pastelBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            navController.navigate(Routes.SIGN_IN)
                        }
                    )
                }

                // Subtle accent line at bottom
                Box(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .fillMaxWidth(0.3f)
                        .height(2.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(primaryPink.copy(alpha = 0.5f), pastelBlue.copy(alpha = 0.5f))
                            )
                        )
                )
            }

            // Success Alert Dialog
            if (showSuccessAlert) {
                AlertDialog(
                    onDismissRequest = {
                        showSuccessAlert = false
                        navController.navigate(Routes.SIGN_IN)
                    },
                    title = {
                        Text(
                            text = "Success!",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50) // Green color for success
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
                                navController.navigate(Routes.SIGN_IN)
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