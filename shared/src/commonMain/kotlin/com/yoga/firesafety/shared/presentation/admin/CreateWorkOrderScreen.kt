package com.yoga.firesafety.shared.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.domain.model.WorkOrderStatus
import com.yoga.firesafety.shared.presentation.dashboard.WorkOrderViewModel
import com.yoga.firesafety.shared.presentation.inspection.FormField
import com.yoga.firesafety.shared.presentation.inspection.DropdownField
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkOrderScreen(
    onBackClick: () -> Unit,
    onOrderCreated: () -> Unit,
    viewModel: WorkOrderViewModel = koinViewModel()
) {
    var buildingName by remember { mutableStateOf("") }
    var streetAddress by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("ON") }
    var postalCode by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("INSPECTION") }
    var priority by remember { mutableStateOf("MEDIUM") }
    var notes by remember { mutableStateOf("") }

    val provinces = listOf("AB", "BC", "MB", "NB", "NL", "NS", "NT", "NU", "ON", "PE", "QC", "SK", "YT")


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Work Order", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface, 
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        val fullAddress = "$streetAddress, $city, $province $postalCode".trim()
                        val newOrder = WorkOrder(
                            id = Random.nextInt(1000, 9999).toString(),
                            buildingName = buildingName,
                            address = fullAddress,
                            type = type,
                            status = WorkOrderStatus.NEW,
                            priority = priority,
                            scheduledAt = null,
                            scheduledEnd = null,
                            dispatcherNotes = notes
                        )
                        viewModel.createOrder(newOrder)
                        onOrderCreated()
                    },
                    modifier = Modifier.fillMaxWidth().padding(20.dp).height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = buildingName.isNotBlank() && streetAddress.isNotBlank() && city.isNotBlank() && postalCode.isNotBlank(),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text("CREATE WORK ORDER", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.padding(20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Building Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    FormField("Building Name", buildingName) { buildingName = it }
                    FormField("Street Address", streetAddress) { streetAddress = it }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1.5f)) {
                            FormField("City", city) { city = it }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DropdownField(
                                label = "Province",
                                selectedValue = province,
                                options = provinces,
                                onOptionSelected = { province = it }
                            )
                        }
                    }
                    
                    FormField("Postal Code", postalCode) { postalCode = it }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Service Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    DropdownField(
                        label = "Service Type",
                        selectedValue = type,
                        options = listOf("INSPECTION", "REPAIR", "MAINTENANCE", "EMERGENCY"),
                        onOptionSelected = { type = it }
                    )
                    
                    DropdownField(
                        label = "Priority Level",
                        selectedValue = priority,
                        options = listOf("LOW", "MEDIUM", "HIGH", "CRITICAL"),
                        onOptionSelected = { priority = it }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    FormField("Dispatcher Notes (Optional)", notes) { notes = it }
                }
            }
        }
    }
}
