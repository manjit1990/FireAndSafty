package com.yoga.firesafety.shared.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.yoga.firesafety.shared.domain.model.AssignedTechnician
import com.yoga.firesafety.shared.domain.model.EmergencyContact
import com.yoga.firesafety.shared.domain.model.Role
import com.yoga.firesafety.shared.domain.model.User
import com.yoga.firesafety.shared.presentation.MainViewModel
import com.yoga.firesafety.shared.presentation.AuthState
import com.yoga.firesafety.shared.presentation.dashboard.AssignmentState
import com.yoga.firesafety.shared.presentation.dashboard.WorkOrderViewModel
import com.yoga.firesafety.shared.presentation.dashboard.formatIsoDateTime
import com.yoga.firesafety.shared.presentation.dashboard.formatWorkOrderDateTimeRange
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.atStartOfDayIn
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.launch

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
    val adminId = (authState as? AuthState.Authenticated)?.session?.userId
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
    val technicians = allUsers.filter { it.role == Role.TECHNICIAN }
    val assignmentState by workOrderViewModel.assignmentState.collectAsState()
    
    val selectedTechnicians = remember { mutableStateListOf<User>() }
    var selectedDate by remember { mutableStateOf("") }
    var selectedStartTime by remember { mutableStateOf("") }
    var selectedEndTime by remember { mutableStateOf("") }
    
    val emergencyContacts = remember { mutableStateListOf<EmergencyContact>() }
    var newContactName by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val today = Clock.System.now().toEpochMilliseconds()
                return utcTimeMillis >= today - 86400000 
            }
        }
    )
    val startTimePickerState = rememberTimePickerState()
    val endTimePickerState = rememberTimePickerState()
    val isAssigning = assignmentState is AssignmentState.Loading
    val assignmentError = (assignmentState as? AssignmentState.Error)?.message
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(order?.id, order?.technicianId, order?.assignedTechnicians, order?.scheduledAt, order?.scheduledEnd, order?.emergencyContacts, technicians) {
        if (order == null) return@LaunchedEffect

        selectedDate = order.scheduledAt?.substringBefore("T").orEmpty()
        selectedStartTime = order.scheduledAt?.substringAfter("T", "")?.take(5).orEmpty()
        selectedEndTime = order.scheduledEnd?.substringAfter("T", "")?.take(5).orEmpty()
        
        if (selectedTechnicians.isEmpty() && technicians.isNotEmpty()) {
            if (!order.assignedTechnicians.isNullOrEmpty()) {
                val assigned = technicians.filter { tech -> order.assignedTechnicians.any { it.id == tech.id } }
                selectedTechnicians.addAll(assigned)
            } else if (!order.technicianId.isNullOrEmpty()) {
                technicians.find { it.id == order.technicianId }?.let { selectedTechnicians.add(it) }
            }
        }

        if (emergencyContacts.isEmpty() && !order.emergencyContacts.isNullOrEmpty()) {
            emergencyContacts.clear()
            emergencyContacts.addAll(order.emergencyContacts)
        }
    }

    LaunchedEffect(assignmentState) {
        if (assignmentState is AssignmentState.Success) {
            workOrderViewModel.clearAssignmentState()
            onAssigned()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Schedule Task", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Order not found", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Admin Info Header
                Card(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Dispatcher Profile", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                            Text(adminName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                // Task Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    order.type.replace("_", " "),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text("#${order.id}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(order.buildingName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(order.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Column(modifier = Modifier.padding(horizontal = 28.dp)) {
                    Text("Scheduling Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (order.technicianName != null || order.assignedAt != null) {
                        CurrentAssignmentCard(order = order)
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    
                    // Multi-Select Technicians Section
                    Text("Assign Technicians (Select 1 or more)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (technicians.isEmpty()) {
                        Text("No technicians found in system", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            technicians.forEach { tech ->
                                val techName = "${tech.firstName} ${tech.lastName}".trim().ifEmpty { tech.email }
                                val isSelected = selectedTechnicians.any { it.id == tech.id }
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isSelected) {
                                                selectedTechnicians.removeAll { it.id == tech.id }
                                            } else {
                                                selectedTechnicians.add(tech)
                                            }
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Checkbox(
                                                checked = isSelected,
                                                onCheckedChange = { checked ->
                                                    if (checked) {
                                                        if (!selectedTechnicians.any { it.id == tech.id }) selectedTechnicians.add(tech)
                                                    } else {
                                                        selectedTechnicians.removeAll { it.id == tech.id }
                                                    }
                                                }
                                            )
                                            Text(techName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                        Text(tech.phoneNumber, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Date Selection
                    SchedulingPickerCard(
                        label = "Visit Date",
                        value = if (selectedDate.isEmpty()) "Choose Date" else selectedDate,
                        icon = Icons.Default.CalendarMonth,
                        onClick = { showDatePicker = true }
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Time Selection Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SchedulingPickerCard(
                            modifier = Modifier.weight(1f),
                            label = "Arrival",
                            value = if (selectedStartTime.isEmpty()) "Start" else selectedStartTime,
                            icon = Icons.Default.AccessTime,
                            onClick = { showStartTimePicker = true }
                        )
                        SchedulingPickerCard(
                            modifier = Modifier.weight(1f),
                            label = "Departure",
                            value = if (selectedEndTime.isEmpty()) "End" else selectedEndTime,
                            icon = Icons.Default.AccessTime,
                            onClick = { showEndTimePicker = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                    Text("Emergency Contacts (Optional)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))

                    emergencyContacts.forEachIndexed { index, contact ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(contact.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text(contact.phoneNumber, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { emergencyContacts.removeAt(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newContactName,
                            onValueChange = { newContactName = it },
                            label = { Text("Contact Name") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newContactPhone,
                            onValueChange = { newContactPhone = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            if (newContactName.isNotBlank() && newContactPhone.isNotBlank()) {
                                emergencyContacts.add(EmergencyContact(name = newContactName, phoneNumber = newContactPhone))
                                newContactName = ""
                                newContactPhone = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Emergency Contact", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Button(
                        onClick = {
                            val now = kotlinx.datetime.Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds()).toLocalDateTime(TimeZone.currentSystemDefault())
                            val isToday = selectedDate == now.date.toString()
                            
                            val startParts = selectedStartTime.split(":")
                            val endParts = selectedEndTime.split(":")
                            
                            if (startParts.size == 2 && endParts.size == 2) {
                                val startHour = startParts[0].toInt()
                                val startMin = startParts[1].toInt()
                                val endHour = endParts[0].toInt()
                                val endMin = endParts[1].toInt()
                                
                                if (isToday && (startHour < now.hour || (startHour == now.hour && startMin < now.minute))) {
                                    scope.launch { snackbarHostState.showSnackbar("Arrival time cannot be in the past") }
                                    return@Button
                                }
                                
                                if (endHour < startHour || (endHour == startHour && endMin <= startMin)) {
                                    scope.launch { snackbarHostState.showSnackbar("Departure must be after Arrival") }
                                    return@Button
                                }
                            }

                            if (selectedTechnicians.isNotEmpty()) {
                                val primaryTech = selectedTechnicians.first()
                                val techNames = selectedTechnicians.joinToString(", ") { "${it.firstName} ${it.lastName}".trim() }
                                val assignedTechsModels = selectedTechnicians.map { 
                                    AssignedTechnician(
                                        id = it.id,
                                        name = "${it.firstName} ${it.lastName}".trim().ifEmpty { it.email },
                                        phoneNumber = it.phoneNumber
                                    )
                                }
                                val isoStart = "${selectedDate}T${selectedStartTime}:00"
                                val isoEnd = "${selectedDate}T${selectedEndTime}:00"
                                workOrderViewModel.assignOrder(
                                    orderId = orderId,
                                    technicianId = primaryTech.id,
                                    technicianName = techNames,
                                    technicianPhoneNumber = primaryTech.phoneNumber,
                                    scheduledAt = isoStart,
                                    scheduledEnd = isoEnd,
                                    assignedAt = kotlinx.datetime.Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds()).toString(),
                                    assignedById = adminId,
                                    assignedByName = adminName,
                                    emergencyContacts = emergencyContacts.toList(),
                                    assignedTechnicians = assignedTechsModels
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        enabled = !isAssigning && selectedTechnicians.isNotEmpty() && selectedDate.isNotEmpty() && selectedStartTime.isNotEmpty() && selectedEndTime.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        if (isAssigning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Text(if (isAssigning) "DISPATCHING" else "DISPATCH TECHNICIAN", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.25.sp)
                    }
                    if (assignmentError != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Assignment failed: $assignmentError",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
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
                }) { Text("OK", fontWeight = FontWeight.ExtraBold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    headlineContentColor = MaterialTheme.colorScheme.primary,
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary
                )
            )
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
private fun CurrentAssignmentCard(order: com.yoga.firesafety.shared.domain.model.WorkOrder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.16f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                "Current Assignment",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            AssignmentRow(Icons.Default.Engineering, "Assigned to", order.technicianName ?: "Not assigned")
            AssignmentRow(Icons.Default.Event, "Visit", formatWorkOrderDateTimeRange(order.scheduledAt, order.scheduledEnd))
            AssignmentRow(Icons.Default.Person, "Assigned by", order.assignedByName ?: "Admin")
            AssignmentRow(Icons.Default.Schedule, "Assigned on", formatIsoDateTime(order.assignedAt))
        }
    }
}

@Composable
private fun AssignmentRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SchedulingPickerCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        OutlinedCard(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = if (value.contains("Choose") || value.contains("Start") || value.contains("End")) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface)
            }
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
        containerColor = Color.White,
        confirmButton = confirmButton,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        },
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 6.dp
    )
}
