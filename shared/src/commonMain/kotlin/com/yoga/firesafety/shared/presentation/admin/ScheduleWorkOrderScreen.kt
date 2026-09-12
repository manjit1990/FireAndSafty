package com.yoga.firesafety.shared.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoga.firesafety.shared.domain.model.User
import com.yoga.firesafety.shared.presentation.MainViewModel
import com.yoga.firesafety.shared.presentation.AuthState
import com.yoga.firesafety.shared.presentation.dashboard.WorkOrderViewModel
import com.yoga.firesafety.shared.presentation.inspection.DropdownField
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleWorkOrderScreen(
    orderId: String,
    onBackClick: () -> Unit,
    onAssigned: () -> Unit,
    mainViewModel: MainViewModel = koinViewModel(),
    workOrderViewModel: WorkOrderViewModel = koinViewModel(),
    userViewModel: UserManagementViewModel = koinViewModel()
) {
    val authState by mainViewModel.authState.collectAsState()
    val adminName = when(val state = authState) {
        is AuthState.Authenticated -> {
            val name = "${state.session.firstName ?: ""} ${state.session.lastName ?: ""}".trim()
            if (name.isNotEmpty()) name else state.session.email
        }
        else -> "Admin"
    }

    val workOrders by workOrderViewModel.workOrders.collectAsState()
    val order = workOrders.find { it.id == orderId }
    
    val allUsers by userViewModel.users.collectAsState()
    val technicians = allUsers.filter { it.role.name == "TECHNICIAN" }
    
    var selectedTech by remember { mutableStateOf<User?>(null) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedStartTime by remember { mutableStateOf("") }
    var selectedEndTime by remember { mutableStateOf("") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState()
    val startTimePickerState = rememberTimePickerState()
    val endTimePickerState = rememberTimePickerState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Schedule Task", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Order not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F7FA))
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Admin Info Header
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Assigning by", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                            Text(adminName, style = MaterialTheme.typography.bodyLarge, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Task Details Card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    order.type.replace("_", " "),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text("ID: #${order.id}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(order.buildingName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(order.address, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    // Technician Selection
                    Text("Technician", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    DropdownField(
                        label = "Assign a professional",
                        selectedValue = selectedTech?.let { "${it.firstName} ${it.lastName}" } ?: "",
                        options = technicians.map { "${it.firstName} ${it.lastName}" },
                        onOptionSelected = { name ->
                            selectedTech = technicians.find { "${it.firstName} ${it.lastName}" == name }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Date Selection
                    Text("Date", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    OutlinedCard(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(if (selectedDate.isEmpty()) "Select Date" else selectedDate, fontWeight = if (selectedDate.isEmpty()) FontWeight.Normal else FontWeight.Medium)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Time Selection Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Start Time", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            OutlinedCard(
                                onClick = { showStartTimePicker = true },
                                modifier = Modifier.padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (selectedStartTime.isEmpty()) "Start" else selectedStartTime, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("End Time", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            OutlinedCard(
                                onClick = { showEndTimePicker = true },
                                modifier = Modifier.padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (selectedEndTime.isEmpty()) "End" else selectedEndTime, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Button(
                        onClick = {
                            val isoStart = "${selectedDate}T${selectedStartTime}:00"
                            val isoEnd = "${selectedDate}T${selectedEndTime}:00"
                            workOrderViewModel.assignOrder(orderId, selectedTech?.id ?: "", isoStart, isoEnd)
                            onAssigned()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = selectedTech != null && selectedDate.isNotEmpty() && selectedStartTime.isNotEmpty() && selectedEndTime.isNotEmpty(),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("CONFIRM ASSIGNMENT", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
    
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.fromEpochMilliseconds(it)
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                        selectedDate = date.toString()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    if (showStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour = if (startTimePickerState.hour < 10) "0${startTimePickerState.hour}" else "${startTimePickerState.hour}"
                    val minute = if (startTimePickerState.minute < 10) "0${startTimePickerState.minute}" else "${startTimePickerState.minute}"
                    selectedStartTime = "$hour:$minute"
                    showStartTimePicker = false
                }) { Text("OK") }
            }
        ) {
            TimePicker(state = startTimePickerState)
        }
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour = if (endTimePickerState.hour < 10) "0${endTimePickerState.hour}" else "${endTimePickerState.hour}"
                    val minute = if (endTimePickerState.minute < 10) "0${endTimePickerState.minute}" else "${endTimePickerState.minute}"
                    selectedEndTime = "$hour:$minute"
                    showEndTimePicker = false
                }) { Text("OK") }
            }
        ) {
            TimePicker(state = endTimePickerState)
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        text = content
    )
}
