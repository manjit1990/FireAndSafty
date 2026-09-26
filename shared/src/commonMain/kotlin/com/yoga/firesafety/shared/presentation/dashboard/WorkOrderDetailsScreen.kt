package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.domain.model.WorkOrderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkOrderDetailsScreen(
    order: WorkOrder,
    onBackClick: () -> Unit,
    onStartVisit: () -> Unit,
    onCompleteVisit: () -> Unit,
    onChecklistClick: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("VISIT", "DETAILS", "NOTES")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Visit Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color(0xFF131A30), fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF131A30))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF8F9FB),
                    titleContentColor = Color(0xFF131A30)
                )
            )
        },
        containerColor = Color(0xFFF8F9FB)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // High-Fidelity Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(color = Color(0xFF3B82F6).copy(alpha = 0.1f), shape = RoundedCornerShape(100.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(6.dp).background(Color(0xFF3B82F6), CircleShape))
                                Text("Assigned", style = MaterialTheme.typography.labelSmall, color = Color(0xFF3B82F6), fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(100.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
                                Text("Inspection", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Visit for ${order.buildingName}", 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF131A30),
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF131A30).copy(alpha = 0.2f), modifier = Modifier.size(16.dp))
                        Text(
                            text = order.address, 
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF131A30).copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    // Schedule Row (Dynamic Date & Time)
                    val scheduleText = formatWorkOrderDateTimeRange(order.scheduledAt, order.scheduledEnd)
                    val datePart = if (scheduleText.contains(",")) scheduleText.substringBefore(",") else scheduleText
                    val timePart = if (scheduleText.contains(",")) scheduleText.substringAfter(", ") else "Unscheduled"

                    Surface(
                        color = Color(0xFFF8F9FB),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black.copy(alpha = 0.02f), RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(18.dp))
                            Text(
                                text = datePart,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF131A30),
                                fontSize = 15.sp
                            )
                            Box(modifier = Modifier.size(4.dp).background(Color.Black.copy(alpha = 0.1f), CircleShape))
                            Text(
                                text = timePart,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF131A30),
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Row 1: Directions + Emergency Calls
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            onClick = {
                                val mapUri = "https://www.google.com/maps/dir/?api=1&destination=${order.address.replace(" ", "+")}&travelmode=driving"
                                uriHandler.openUri(mapUri)
                            },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF131A30)
                        ) {
                            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Directions", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }
                        }
                        
                        if (order.emergencyContacts.isNotEmpty()) {
                            order.emergencyContacts.forEach { contact ->
                                Surface(
                                    onClick = {
                                        try {
                                            uriHandler.openUri("tel:${contact.phoneNumber}")
                                        } catch (e: Exception) {}
                                    },
                                    modifier = Modifier.height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF10B981),
                                    shadowElevation = 1.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = contact.name.ifEmpty { "Call" },
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            Surface(
                                onClick = {
                                    order.technicianPhoneNumber?.let { phone ->
                                        try {
                                            uriHandler.openUri("tel:$phone")
                                        } catch (e: Exception) {}
                                    }
                                },
                                modifier = Modifier.size(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f)),
                                shadowElevation = 1.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.PhoneEnabled, contentDescription = "Call", tint = Color(0xFF131A30).copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Start Visit vs Complete Visit Action Bar (Dynamic based on visit state)
            if (order.status != WorkOrderStatus.COMPLETED) {
                val isStarted = order.status.name == "STARTED" || order.status.name == "IN_PROGRESS" || order.status.name == "LIVE"
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (!isStarted) {
                        Surface(
                            onClick = onStartVisit,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF3B82F6),
                            shadowElevation = 2.dp
                        ) {
                            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Start Visit", fontWeight = FontWeight.Black, color = Color.White, fontSize = 15.sp, letterSpacing = 0.5.sp)
                            }
                        }
                    } else {
                        Surface(
                            onClick = onCompleteVisit,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF10B981),
                            shadowElevation = 2.dp
                        ) {
                            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Complete Visit", fontWeight = FontWeight.Black, color = Color.White, fontSize = 15.sp, letterSpacing = 0.5.sp)
                            }
                        }
                    }
                }
            }

            // Tab Row Redesign
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Column(
                        modifier = Modifier.clickable { selectedTab = index }.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color(0xFF131A30) else Color(0xFF131A30).copy(alpha = 0.3f),
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.labelLarge,
                            letterSpacing = 0.5.sp,
                            fontSize = 15.sp
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.width(24.dp).height(3.dp).background(Color(0xFF131A30), RoundedCornerShape(100.dp)))
                        }
                    }
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.Black.copy(alpha = 0.05f))

            Column(modifier = Modifier.padding(16.dp)) {
                when (selectedTab) {
                    0 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Instructions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color(0xFF131A30))
                                Text("Standard Operating Proc.", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                            }
                            
                            Surface(
                                color = Color(0xFFF1F5F9).copy(alpha = 0.4f),
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.04f))
                            ) {
                                Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                                    // Blue vertical bar
                                    Box(modifier = Modifier.width(5.dp).fillMaxHeight().background(Color(0xFF3B82F6), RoundedCornerShape(100.dp)))
                                    
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Check main riser pressure gauges, verify backflow preventers, and conduct sprinkler valve tamper switch testing.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF131A30).copy(alpha = 0.8f),
                                            lineHeight = 22.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Surface(
                                            color = Color.White,
                                            shape = RoundedCornerShape(10.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
                                            modifier = Modifier.width(180.dp)
                                        ) {
                                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFF131A30).copy(alpha = 0.3f), modifier = Modifier.size(18.dp))
                                                Column {
                                                    Text("Keybox Code:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.4f), fontSize = 10.sp)
                                                    Text("#4921", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Black, color = Color.Black)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Column {
                                Text("Checklists", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color(0xFF131A30))
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f))
                                ) {
                                    Column {
                                        MockupChecklistItem(
                                            title = "Monthly Deficiency Report", 
                                            subtitle = "Not filled", 
                                            hasSync = false, 
                                            onClick = { onChecklistClick("deficiency") }
                                        )
                                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.Black.copy(alpha = 0.05f))
                                        MockupChecklistItem(
                                            title = "Monthly Fire Alarm, Sprinkler, Extinguisher, Emergency Lighting Testing", 
                                            subtitle = "Fire Alarm Panel & Valves", 
                                            onClick = { onChecklistClick("inspection") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    1 -> DetailsTabContent(order)
                    2 -> NotesTabContent(order)
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun MockupChecklistItem(title: String, subtitle: String, hasSync: Boolean = false, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF131A30), fontWeight = FontWeight.Black, fontSize = 15.sp)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color(0xFF131A30).copy(alpha = 0.4f), fontWeight = FontWeight.Bold)
            }
            if (hasSync) {
                Icon(Icons.Default.Sync, contentDescription = null, tint = Color(0xFF131A30).copy(alpha = 0.3f), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(12.dp))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF131A30).copy(alpha = 0.2f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun DetailsTabContent(order: WorkOrder) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
        shadowElevation = 1.dp,
        modifier = Modifier.padding(bottom = 100.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            LuxuryInfoRow(Icons.Default.Badge, "Technician", order.technicianName ?: "Unassigned")
            LuxuryInfoRow(Icons.Default.CalendarToday, "Assigned On", formatIsoDateTime(order.assignedAt))
            LuxuryInfoRow(Icons.Default.Flag, "Priority Level", order.priority)
            LuxuryInfoRow(Icons.Default.ConfirmationNumber, "Internal ID", "#${order.id.take(8)}")
        }
    }
}

@Composable
fun LuxuryInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color.Black.copy(alpha = 0.03f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF131A30).copy(alpha = 0.4f))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF131A30).copy(alpha = 0.3f), fontWeight = FontWeight.Black)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF131A30), fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun NotesTabContent(order: WorkOrder) {
    Column {
        Text("ON-SITE OBSERVATIONS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = Color(0xFF131A30).copy(alpha = 0.3f), letterSpacing = 1.5.sp)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().height(200.dp),
            placeholder = { Text("Enter detailed technician notes here...", color = Color(0xFF131A30).copy(alpha = 0.2f)) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3B82F6),
                unfocusedBorderColor = Color.Black.copy(alpha = 0.05f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color(0xFF131A30),
                unfocusedTextColor = Color(0xFF131A30)
            )
        )
    }
}
