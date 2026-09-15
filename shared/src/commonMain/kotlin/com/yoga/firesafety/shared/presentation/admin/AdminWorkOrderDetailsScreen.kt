package com.yoga.firesafety.shared.presentation.admin

import androidx.compose.foundation.background
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
import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.domain.model.WorkOrderStatus
import com.yoga.firesafety.shared.presentation.dashboard.WorkOrderViewModel
import com.yoga.firesafety.shared.presentation.dashboard.formatIsoDateTime
import com.yoga.firesafety.shared.presentation.dashboard.formatWorkOrderDateTimeRange
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminWorkOrderDetailsScreen(
    orderId: String,
    onBackClick: () -> Unit,
    viewModel: WorkOrderViewModel = koinViewModel()
) {
    val workOrders by viewModel.workOrders.collectAsState()
    val order = workOrders.find { it.id == orderId }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Task Monitor", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Order not found", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Status Header
                StatusBanner(status = order.status)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    order.buildingName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    order.address,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Assignment Detail Card
                AdminDetailSection("Technician Assignment") {
                    AdminInfoRow(Icons.Default.Person, "Technician", order.technicianName ?: "Unassigned")
                    AdminInfoRow(Icons.Default.Badge, "ID", order.technicianId ?: "N/A")
                    AdminInfoRow(Icons.Default.Schedule, "Scheduled", formatWorkOrderDateTimeRange(order.scheduledAt, order.scheduledEnd))
                }

                if (order.status == WorkOrderStatus.COMPLETED) {
                    Spacer(modifier = Modifier.height(24.dp))
                    AdminDetailSection("Completion Report") {
                        AdminInfoRow(Icons.Default.CheckCircle, "Completed On", formatIsoDateTime(order.completedAt))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Questionnaire Results:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        order.completionQuestions.forEach { (q, a) ->
                            AdminQuestionRow(q, a)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Completion Notes:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text(order.completionNotes ?: "No notes provided", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun StatusBanner(status: WorkOrderStatus) {
    val (color, text) = when(status) {
        WorkOrderStatus.STARTED -> Color(0xFF60A5FA) to "TECHNICIAN IS ON-SITE"
        WorkOrderStatus.COMPLETED -> Color(0xFF2E7D32) to "TASK COMPLETED"
        else -> MaterialTheme.colorScheme.primary to status.name
    }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.labelLarge, color = color, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun AdminDetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                content()
            }
        }
    }
}

@Composable
fun AdminInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(12.dp))
        Text("$label: ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun AdminQuestionRow(question: String, answer: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(question, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(answer, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.ExtraBold, color = if (answer == "true") Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface)
    }
}
