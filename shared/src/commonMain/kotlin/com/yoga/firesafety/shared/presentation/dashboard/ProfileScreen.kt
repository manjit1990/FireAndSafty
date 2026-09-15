package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoga.firesafety.shared.domain.model.User
import com.yoga.firesafety.shared.domain.repository.UserRepository
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value = userRepository.getCurrentUser()
        }
    }

    fun setEditing(editing: Boolean) {
        _isEditing.value = editing
    }

    fun updateProfile(firstName: String, lastName: String, phoneNumber: String) {
        val currentUser = _user.value ?: return
        viewModelScope.launch {
            try {
                userRepository.updateUserProfile(currentUser.id, firstName, lastName, phoneNumber, currentUser.profileImageUrl)
                _isEditing.value = false
                loadUser()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = koinViewModel()) {
    val user by viewModel.user.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.let {
            firstName = it.firstName
            lastName = it.lastName
            phoneNumber = it.phoneNumber
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.yoga.firesafety.shared.presentation.theme.AppGradients.Midnight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Profile Image
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(2.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.White.copy(alpha = 0.2f)
                )
                
                IconButton(
                    onClick = { /* Image picker logic */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(44.dp)
                        .background(Color(0xFF3B82F6), CircleShape)
                        .border(3.dp, Color.Black, CircleShape)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (!isEditing) {
                Text(
                    "${user?.firstName ?: ""} ${user?.lastName ?: ""}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Surface(
                    modifier = Modifier.padding(top = 12.dp),
                    color = Color(0xFF3B82F6).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(100.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f))
                ) {
                    Text(
                        user?.role?.name ?: "TECHNICIAN",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF3B82F6),
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(56.dp))
                
                // Luxury Info List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ProfileInfoItem(Icons.Default.Badge, "EMPLOYEE ID", user?.id?.take(8) ?: "---")
                    ProfileInfoItem(Icons.Default.Phone, "CONTACT NUMBER", user?.phoneNumber ?: "")
                    ProfileInfoItem(Icons.Default.Email, "OFFICIAL EMAIL", user?.email ?: "")
                }

                Spacer(modifier = Modifier.height(64.dp))
                
                Button(
                    onClick = { viewModel.setEditing(true) },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("EDIT ACCOUNT DETAILS", fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = 1.sp)
                }
            } else {
                Text("EDIT PROFILE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = Color(0xFF3B82F6), letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(40.dp))
                
                ModernProfileField("FIRST NAME", firstName) { firstName = it }
                ModernProfileField("LAST NAME", lastName) { lastName = it }
                ModernProfileField("PHONE NUMBER", phoneNumber) { phoneNumber = it }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { viewModel.setEditing(false) },
                        modifier = Modifier.weight(1f).height(64.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Text("CANCEL", color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewModel.updateProfile(firstName, lastName, phoneNumber) },
                        modifier = Modifier.weight(1f).height(64.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("SAVE", color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ModernProfileField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 20.dp).fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF10B981),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

@Composable
fun ProfileInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.05f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF10B981))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f), fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}
