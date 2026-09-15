package com.yoga.firesafety.shared.presentation.dashboard

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
import com.yoga.firesafety.shared.domain.model.WorkOrder
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteVisitScreen(
    orderId: String,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
    viewModel: WorkOrderViewModel = koinViewModel()
) {
    val workOrders by viewModel.workOrders.collectAsState()
    val order = workOrders.find { it.id == orderId }
    val today = remember { LocalDate(2026, 9, 13) } // Placeholder since Clock.System is being difficult

    var notes by remember { mutableStateOf("") }
    var resolvedIssue by remember { mutableStateOf<Boolean?>(null) }
    var safetyFollowed by remember { mutableStateOf<Boolean?>(null) }
    var clientSatisfied by remember { mutableStateOf<Boolean?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Complete Visit", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        val questions = mapOf(
                            "Issue Resolved" to (resolvedIssue?.toString() ?: "N/A"),
                            "Safety Followed" to (safetyFollowed?.toString() ?: "N/A"),
                            "Client Satisfied" to (clientSatisfied?.toString() ?: "N/A")
                        )
                        viewModel.completeVisit(
                            orderId = orderId,
                            notes = notes,
                            photos = emptyList(), // Placeholder
                            videos = emptyList(), // Placeholder
                            questions = questions,
                            completedAt = today.toString()
                        )
                        onCompleted()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    enabled = resolvedIssue != null && safetyFollowed != null
                ) {
                    Text("FINISH & SUBMIT", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }
            }
        }
    ) { padding ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    "Job Report",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2E7D32)
                )
                Text(
                    "Please answer these final questions for ${order.buildingName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                CompletionQuestion(
                    question = "Was the issue fully resolved?",
                    selected = resolvedIssue,
                    onSelected = { resolvedIssue = it }
                )

                CompletionQuestion(
                    question = "Did you follow all safety protocols?",
                    selected = safetyFollowed,
                    onSelected = { safetyFollowed = it }
                )

                CompletionQuestion(
                    question = "Is the client satisfied with the work?",
                    selected = clientSatisfied,
                    onSelected = { clientSatisfied = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("Final Technician Notes", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    placeholder = { Text("Add any extra details here...") },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("Photos & Videos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MediaPlaceholderCard(icon = Icons.Default.AddAPhoto, label = "Add Photo")
                    MediaPlaceholderCard(icon = Icons.Default.VideoCall, label = "Add Video")
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun CompletionQuestion(
    question: String,
    selected: Boolean?,
    onSelected: (Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(question, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(
                selected = selected == true,
                onClick = { onSelected(true) },
                label = { Text("Yes") },
                leadingIcon = if (selected == true) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null
            )
            FilterChip(
                selected = selected == false,
                onClick = { onSelected(false) },
                label = { Text("No") },
                leadingIcon = if (selected == false) {
                    { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null
            )
        }
    }
}

@Composable
fun MediaPlaceholderCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Card(
        modifier = Modifier.size(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}
