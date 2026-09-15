package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoga.firesafety.shared.domain.model.WorkOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkOrderDetailsScreen(
    order: WorkOrder,
    onBackClick: () -> Unit,
    onStartVisit: () -> Unit,
    onCompleteVisit: () -> Unit,
    onChecklistClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Visit", "Details", "Notes")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.1f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.1f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Cinematic Header
                Column(modifier = Modifier.padding(24.dp)) {
                    Surface(
                        color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(
                            "LIVE TASK", 
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall, 
                            fontWeight = FontWeight.ExtraBold, 
                            color = Color(0xFF3B82F6),
                            letterSpacing = 1.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        order.buildingName, 
                        style = MaterialTheme.typography.headlineLarge, 
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 38.sp
                    )
                    Text(
                        order.address, 
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Action Buttons Row (Directions & Call)
                    val uriHandler = LocalUriHandler.current
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        VisitSecondaryButton(
                            modifier = Modifier.weight(1f),
                            label = "Directions",
                            icon = Icons.Default.Directions,
                            onClick = {
                                val mapUri = "https://www.google.com/maps/search/?api=1&query=${order.address.replace(" ", "+")}"
                                uriHandler.openUri(mapUri)
                            }
                        )
                        VisitSecondaryButton(
                            modifier = Modifier.weight(1f),
                            label = "Call Client",
                            icon = Icons.Default.Call,
                            onClick = { /* Call logic */ }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Main Action Buttons
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        LuxuryButton(
                            modifier = Modifier.weight(1f),
                            label = "START VISIT",
                            icon = Icons.Default.PlayArrow,
                            containerColor = Color(0xFF3B82F6),
                            enabled = order.status != com.yoga.firesafety.shared.domain.model.WorkOrderStatus.STARTED && order.status != com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED,
                            onClick = onStartVisit
                        )
                        LuxuryButton(
                            modifier = Modifier.weight(1f),
                            label = "COMPLETE",
                            icon = Icons.Default.Check,
                            containerColor = Color(0xFF10B981),
                            enabled = order.status == com.yoga.firesafety.shared.domain.model.WorkOrderStatus.STARTED,
                            onClick = onCompleteVisit
                        )
                    }
                }

                // Seamless Tab Switcher
                LuxuryTabRow(
                    selectedIndex = selectedTab,
                    tabs = tabs,
                    onTabSelected = { selectedTab = it }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    when (selectedTab) {
                        0 -> VisitTabContent(order, onChecklistClick)
                        1 -> DetailsTabContent(order)
                        2 -> NotesTabContent(order)
                    }
                }
            }
        }
    }
}

@Composable
fun VisitSecondaryButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(56.dp),
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(label, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun LuxuryButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = Color.White.copy(alpha = 0.05f)
        ),
        enabled = enabled,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(label, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun LuxuryTabRow(
    selectedIndex: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedIndex == index
            Column(
                modifier = Modifier
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    title.uppercase(), 
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f),
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.size(4.dp).background(Color.White, CircleShape))
                }
            }
        }
    }
}

@Composable
fun VisitTabContent(order: WorkOrder, onChecklistClick: (String) -> Unit) {
    Column {
        Text("INSTRUCTIONS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = Color.White.copy(alpha = 0.3f), letterSpacing = 1.5.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Text(
                order.dispatcherNotes ?: "Proceed with the standard monthly safety protocols and inspection checklist.",
                modifier = Modifier.padding(20.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 26.sp
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("DIGITAL CHECKLISTS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = Color.White.copy(alpha = 0.3f), letterSpacing = 1.5.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        LuxuryChecklistItem("Deficiency Report", "Tap to open", onClick = { onChecklistClick("deficiency") })
        LuxuryChecklistItem("Monthly Fire Alarm Test", "Standard Protocol", onClick = { onChecklistClick("inspection") })
    }
}

@Composable
fun LuxuryChecklistItem(title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.padding(vertical = 6.dp),
        color = Color.White.copy(alpha = 0.03f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color(0xFF3B82F6).copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF3B82F6))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun DetailsTabContent(order: WorkOrder) {
    Column {
        LuxuryInfoRow(Icons.Default.Badge, "Technician", order.technicianName ?: "Unassigned")
        LuxuryInfoRow(Icons.Default.CalendarToday, "Assigned On", formatIsoDateTime(order.assignedAt))
        LuxuryInfoRow(Icons.Default.Flag, "Priority Level", order.priority)
        LuxuryInfoRow(Icons.Default.ConfirmationNumber, "Internal ID", "#${order.id.take(8)}")
    }
}

@Composable
fun LuxuryInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.3f), fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun NotesTabContent(order: WorkOrder) {
    Column {
        Text("ON-SITE OBSERVATIONS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = Color.White.copy(alpha = 0.3f), letterSpacing = 1.5.sp)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().height(200.dp),
            placeholder = { Text("Enter detailed technician notes here...", color = Color.White.copy(alpha = 0.2f)) },
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3B82F6),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedContainerColor = Color.White.copy(alpha = 0.03f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}
