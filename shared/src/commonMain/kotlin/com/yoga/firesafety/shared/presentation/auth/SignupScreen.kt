package com.yoga.firesafety.shared.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoga.firesafety.shared.domain.model.Role
import org.jetbrains.compose.resources.painterResource
import firesafetyservicemanagement.shared.generated.resources.Res
import firesafetyservicemanagement.shared.generated.resources.app_logo
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onSignupSuccess: (Role) -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: SignupViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val countries = listOf(
        Country("Canada", "+1", "🇨🇦"),
        Country("United States", "+1", "🇺🇸"),
        Country("India", "+91", "🇮🇳"),
        Country("United Kingdom", "+44", "🇬🇧"),
        Country("Australia", "+61", "🇦🇺")
    )
    var selectedCountry by remember { mutableStateOf(countries[0]) }
    var showCountryPicker by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        when (state) {
            is SignupState.Success -> {
                isLoading = false
                onSignupSuccess((state as SignupState.Success).role)
            }
            is SignupState.Error -> {
                isLoading = false
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo Section
            Image(
                painter = painterResource(Res.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF131A30),
                fontWeight = FontWeight.Black,
                fontSize = 28.sp
            )
            Text(
                text = "Join the professional Torbram team",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF131A30).copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ModernSignupField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = "First Name",
                        icon = Icons.Default.Badge
                    )
                    
                    ModernSignupField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = "Last Name",
                        icon = Icons.Default.Badge
                    )
                    
                    ModernSignupField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Email Address",
                        icon = Icons.Default.Email
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Password", color = Color(0xFF131A30).copy(alpha = 0.3f)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF131A30).copy(alpha = 0.3f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF131A30)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.05f),
                            unfocusedContainerColor = Color(0xFFF8F9FB),
                            focusedContainerColor = Color(0xFFF8F9FB)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        placeholder = { Text("Phone Number", color = Color(0xFF131A30).copy(alpha = 0.3f)) },
                        leadingIcon = {
                            Box {
                                TextButton(
                                    onClick = { showCountryPicker = true }, 
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = "${selectedCountry.flag} ${selectedCountry.code}", 
                                        color = Color(0xFF3B82F6), 
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                DropdownMenu(
                                    expanded = showCountryPicker,
                                    onDismissRequest = { showCountryPicker = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    countries.forEach { country ->
                                        DropdownMenuItem(
                                            text = { Text("${country.flag} ${country.name} (${country.code})", color = Color(0xFF131A30)) },
                                            onClick = {
                                                selectedCountry = country
                                                showCountryPicker = false
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF131A30)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.05f),
                            unfocusedContainerColor = Color(0xFFF8F9FB),
                            focusedContainerColor = Color(0xFFF8F9FB)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { 
                            isLoading = true
                            val fullPhoneNumber = "${selectedCountry.code}${phoneNumber}"
                            viewModel.signup(firstName, lastName, email, password, fullPhoneNumber)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        enabled = !isLoading && state !is SignupState.Loading && email.isNotBlank() && password.isNotBlank() && firstName.isNotBlank() && phoneNumber.isNotBlank()
                    ) {
                        if (isLoading || state is SignupState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("CREATE ACCOUNT", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        }
                    }
                    
                    if (state is SignupState.Error) {
                        Surface(
                            modifier = Modifier.padding(top = 16.dp),
                            color = Color(0xFFFF4B66).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = (state as SignupState.Error).message,
                                color = Color(0xFFFF4B66),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = onBackToLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Already have an account?",
                        color = Color(0xFF131A30).copy(alpha = 0.4f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sign In",
                        color = Color(0xFF3B82F6),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ModernSignupField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFF131A30).copy(alpha = 0.3f)) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(20.dp)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF131A30)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF3B82F6),
            unfocusedBorderColor = Color.Black.copy(alpha = 0.05f),
            unfocusedContainerColor = Color(0xFFF8F9FB),
            focusedContainerColor = Color(0xFFF8F9FB)
        )
    )
    Spacer(modifier = Modifier.height(8.dp))
}

data class Country(val name: String, val code: String, val flag: String)
