package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoga.firesafety.shared.domain.model.WorkOrder

import androidx.compose.foundation.border
import com.yoga.firesafety.shared.presentation.theme.AppColors

import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.CameraAlt

@Composable
fun WorkOrderScheduleItem(order: WorkOrder, onClick: () -> Unit) {
    val isLive = order.status.name == "STARTED" || order.status.name == "IN_PROGRESS" || order.status.name == "LIVE"
    val priorityColor = when(order.priority.uppercase()) {
        "HIGH" -> Color(0xFFFF4B66)
        "MEDIUM" -> Color(0xFFFFB03B)
        else -> Color(0xFF00D2FF)
    }

    if (isLive) {
        // Expansive Ultra-Premium Live Card Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1629)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E294B))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Tag Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFFFB03B).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(100.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB03B).copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Engineering,
                                contentDescription = null,
                                tint = Color(0xFFFFB03B),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = order.type.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFB03B),
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(100.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(Color(0xFF10B981), CircleShape))
                            Column {
                                Text("LIVE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color(0xFF10B981), fontSize = 9.sp)
                                Text("NOW", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), fontSize = 8.sp)
                            }
                        }
                    }
                }

                // Title Section
                Text(
                    text = order.buildingName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 28.sp
                )

                // Location Segment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFFF4B66), modifier = Modifier.size(18.dp))
                        Column {
                            Text(order.address, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f))
                            Text("Plot 104, Active Field Region", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.4f))
                        }
                    }
                    Text(
                        text = "Map",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.clickable { /* Map Action */ }
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                // Scheduled Window Segment
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.05f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(18.dp))
                        }
                    }
                    Column {
                        Text("SCHEDULED WINDOW", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.4f), letterSpacing = 1.sp)
                        Text(
                            text = "Today, " + formatWorkOrderDateTimeRange(order.scheduledAt, order.scheduledEnd).substringAfter(", "),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Assignee Profile Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val nameStr = order.technicianName ?: "Unassigned"
                        val initial = nameStr.take(2).uppercase()
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = Color(0xFF3B82F6)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(initial, fontWeight = FontWeight.ExtraBold, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(nameStr, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                Surface(color = Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(4.dp)) {
                                    Text("Lead", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                }
                            }
                            Text("+91 98765 43210", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.4f))
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape).size(40.dp)) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape).size(40.dp)) {
                            Icon(Icons.AutoMirrored.Filled.Comment, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Footer Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onClick,
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("Continue Checklist", fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = {},
                        modifier = Modifier.background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)).size(54.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }
    } else {
        // Luxury Standard/Upcoming Card Design
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B0F19)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.03f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(6.dp).background(priorityColor, CircleShape))
                        Text(order.type.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = priorityColor.copy(alpha = 0.8f))
                    }
                    Surface(color = Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(6.dp)) {
                        Text(order.status.name, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                    }
                }

                Text(order.buildingName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(16.dp))
                    Text(order.address, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.4f), maxLines = 1)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
                        Text(formatWorkOrderDateTimeRange(order.scheduledAt, order.scheduledEnd).substringAfter(", "), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.7f))
                    }

                    if (!order.technicianName.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val initials = order.technicianName.take(2).uppercase()
                            Surface(modifier = Modifier.size(24.dp), shape = CircleShape, color = Color.White.copy(alpha = 0.1f)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(initials, style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(order.technicianName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(status: String) {
    val isLive = status == "STARTED" || status == "IN PROGRESS" || status == "LIVE"
    val color = if (isLive) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isLive) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = if (isLive) "LIVE NOW" else status,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            letterSpacing = 1.0.sp
        )
    }
}

fun formatWorkOrderDateTimeRange(scheduledAt: String?, scheduledEnd: String?): String {
    if (scheduledAt.isNullOrBlank()) return "Unscheduled"

    val date = scheduledAt.substringBefore("T")
    val start = scheduledAt.substringAfter("T", "").take(5)
    val end = scheduledEnd?.substringAfter("T", "")?.take(5).orEmpty()

    return when {
        start.isNotBlank() && end.isNotBlank() -> "$date, $start - $end"
        start.isNotBlank() -> "$date, $start"
        else -> date
    }
}

fun formatIsoDateTime(value: String?): String {
    if (value.isNullOrBlank()) return "Not available"

    val date = value.substringBefore("T")
    val time = value.substringAfter("T", "").take(5)

    return if (time.isBlank()) date else "$date $time"
}
