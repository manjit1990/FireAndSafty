package com.yoga.firesafety.shared.presentation.admin

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
import com.yoga.firesafety.shared.domain.model.User
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
    workOrderViewModel: WorkOrderViewModel = koinViewModel(),
    userViewModel: UserManagementViewModel = koinViewModel()
) {
    val workOrders by workOrderViewModel.workOrders.collectAsState()
    val order = workOrders.find { it.id == orderId }
    
    val allUsers by userViewModel.users.collectAsState()
    val technicians = allUsers.filter { it.role.name == "TECHNICIAN" }
    
    var selectedTech by remember { mutableStateOf<User?>(null) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

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
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Task: ${order.type.replace("_", " ")}", 
                    style = MaterialTheme.typography.headlineSmall, 
                    fontWeight = FontWeight.Bold
                )
                Text(order.buildingName, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Technician Selection
                Text("Assign Technician", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                DropdownField(
                    label = "Select Technician",
                    selectedValue = selectedTech?.let { "${it.firstName} ${it.lastName}" } ?: "",
                    options = technicians.map { "${it.firstName} ${it.lastName}" },
                    onOptionSelected = { name ->
                        selectedTech = technicians.find { "${it.firstName} ${it.lastName}" == name }
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Date Selection
                Text("Schedule Date", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                OutlinedCard(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(if (selectedDate.isEmpty()) "Pick a date" else selectedDate)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Time Selection
                Text("Schedule Time", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                OutlinedCard(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(if (selectedTime.isEmpty()) "Pick a time" else selectedTime)
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = {
                        val isoDateTime = "${selectedDate}T${selectedTime}:00"
                        workOrderViewModel.assignOrder(orderId, selectedTech?.id ?: "", isoDateTime)
                        onAssigned()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = selectedTech != null && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()
                ) {
                    Text("CONFIRM ASSIGNMENT", fontWeight = FontWeight.Bold)
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
    
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour = if (timePickerState.hour < 10) "0${timePickerState.hour}" else "${timePickerState.hour}"
                    val minute = if (timePickerState.minute < 10) "0${timePickerState.minute}" else "${timePickerState.minute}"
                    selectedTime = "$hour:$minute"
                    showTimePicker = false
                }) { Text("OK") }
            }
        ) {
            TimePicker(state = timePickerState)
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
