package com.yoga.firesafety.shared.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    var address by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("INSPECTION") }
    var priority by remember { mutableStateOf("MEDIUM") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Work Order", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        val newOrder = WorkOrder(
                            id = Random.nextInt(1000, 9999).toString(),
                            buildingName = buildingName,
                            address = address,
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
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = buildingName.isNotBlank() && address.isNotBlank()
                ) {
                    Text("CREATE ORDER", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Building Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            
            FormField("Building Name", buildingName) { buildingName = it }
            FormField("Full Address", address) { address = it }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Service Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            
            DropdownField(
                label = "Service Type (e.g. INSPECTION, REPAIR)",
                selectedValue = type,
                options = listOf("INSPECTION", "REPAIR", "MAINTENANCE", "EMERGENCY"),
                onOptionSelected = { type = it }
            )
            
            DropdownField(
                label = "Priority (HIGH, MEDIUM, LOW)",
                selectedValue = priority,
                options = listOf("LOW", "MEDIUM", "HIGH", "CRITICAL"),
                onOptionSelected = { priority = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FormField("Dispatcher Notes", notes) { notes = it }
        }
    }
}
