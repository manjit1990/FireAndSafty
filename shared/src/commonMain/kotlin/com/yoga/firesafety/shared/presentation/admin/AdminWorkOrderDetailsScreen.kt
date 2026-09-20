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
    onEditClick: (String) -> Unit,
    viewModel: WorkOrderViewModel = koinViewModel()
) {
    val workOrders by viewModel.workOrders.collectAsState()
    val order = workOrders.find { it.id == orderId }
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Task Details", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
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
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Status Header Banner
                StatusBanner(status = order.status)

                // Main Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = order.type,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text("#${order.id}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text(
                            text = order.buildingName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Surface(
                            onClick = {
                                val url = "https://www.google.com/maps/search/?api=1&query=${order.buildingName}, ${order.address}".replace(" ", "%20")
                                try { uriHandler.openUri(url) } catch (e: Exception) {}
                            },
                            color = Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFFF4B66), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = order.address,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Assignment Detail Section
                AdminDetailSection("Technician Assignment") {
                    AdminInfoRow(Icons.Default.Person, "Assignee", order.technicianName ?: "Waiting for Assignment")
                    if (!order.technicianPhoneNumber.isNullOrBlank()) {
                        AdminInfoRow(Icons.Default.Phone, "Contact", order.technicianPhoneNumber)
                    }
                    AdminInfoRow(Icons.Default.CalendarMonth, "Scheduled", formatWorkOrderDateTimeRange(order.scheduledAt, order.scheduledEnd))
                    
                    if (order.status == WorkOrderStatus.ASSIGNED || order.status == WorkOrderStatus.NEW) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = { onEditClick(order.id) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("EDIT ASSIGNMENT", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (order.status == WorkOrderStatus.COMPLETED) {
                    AdminDetailSection("Job Completion Report") {
                        AdminInfoRow(Icons.Default.AssignmentTurnedIn, "Status", "Verified & Closed")
                        AdminInfoRow(Icons.Default.CheckCircle, "Finished On", formatIsoDateTime(order.completedAt))
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Questionnaire Results", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(12.dp))
                        order.completionQuestions.forEach { (q, a) ->
                            AdminQuestionRow(q, a)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("On-Site Notes", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = order.completionNotes ?: "No specific notes recorded by technician.",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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
        WorkOrderStatus.NEW -> Color(0xFF3B82F6) to "UNASSIGNED / NEW"
        WorkOrderStatus.ASSIGNED -> Color(0xFFF59E0B) to "TECHNICIAN ASSIGNED"
        WorkOrderStatus.STARTED, WorkOrderStatus.IN_PROGRESS -> Color(0xFF10B981) to "TECHNICIAN IS ON-SITE"
        WorkOrderStatus.COMPLETED -> Color(0xFF22C55E) to "TASK COMPLETED SUCCESSFULLY"
        WorkOrderStatus.CANCELLED -> Color(0xFFEF4444) to "TASK CANCELLED"
        else -> MaterialTheme.colorScheme.primary to status.name.replace("_", " ")
    }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(12.dp), shape = CircleShape, color = color) {}
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = color,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
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
