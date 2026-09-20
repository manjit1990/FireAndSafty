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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.domain.model.WorkOrderStatus
import com.yoga.firesafety.shared.domain.model.checkIfOverdue
import com.yoga.firesafety.shared.domain.model.checkIfCompletionOverdue
import kotlinx.datetime.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

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
fun WorkOrderScheduleItem(
    order: WorkOrder, 
    hasLiveTask: Boolean = false,
    onClick: () -> Unit
) {
    val isLive = order.status.name == "STARTED" || order.status.name == "IN_PROGRESS" || order.status.name == "LIVE"
    val isCompleted = order.status == com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED
    
    var currentTime by remember { mutableStateOf<kotlinx.datetime.Instant>(Clock.System.now()) }
    LaunchedEffect(Unit) {
        while(true) {
            delay(30.seconds) // Update every 30 seconds
            currentTime = Clock.System.now()
        }
    }

    val isOverdue = remember(order, currentTime) {
        order.checkIfOverdue(currentTime)
    }

    val isCompletionOverdue = remember(order, currentTime) {
        order.checkIfCompletionOverdue(currentTime)
    }

    val priorityColor = when(order.priority.uppercase()) {
        "HIGH" -> Color(0xFFFF4B66)
        "MEDIUM" -> Color(0xFFF59E0B) // Amber
        else -> Color(0xFF3B82F6) // Blue
    }

    if (isLive) {
        // Expansive Ultra-Premium Live Card Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
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

                    if (isCompletionOverdue) {
                        Surface(
                            color = Color(0xFFFF4B66).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(100.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4B66).copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF4B66), modifier = Modifier.size(14.dp))
                                Text(
                                    text = "COMPLETION OVERDUE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFF4B66),
                                    letterSpacing = 0.5.sp
                                )
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
            }
        }
    } else {
        // Luxury Standard/Agenda Card Design matching Mockup
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
            ) {
                // Mockup high-contrast vertical accent bar
                Box(
                    modifier = Modifier
                        .width(7.dp)
                        .fillMaxHeight()
                        .background(
                            when {
                                isCompleted -> Color(0xFF94A3B8)
                                isOverdue -> Color(0xFFFF4B66)
                                else -> priorityColor
                            }
                        )
                )
                
                Column(
                    modifier = Modifier.padding(10.dp).weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.08f),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(modifier = Modifier.size(6.dp).background(Color(0xFF10B981), CircleShape))
                                    Text(
                                        text = order.type.lowercase().replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (isOverdue) {
                                Surface(
                                    color = Color(0xFFFF4B66).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(100.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4B66).copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF4B66), modifier = Modifier.size(12.dp))
                                        Text(
                                            text = "OVERDUE - START NOW",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFFF4B66),
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                            
                            if (isCompleted) {
                                Surface(
                                    color = Color(0xFF64748B).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
                                        Text(
                                            text = "Completed",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF64748B),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                        
                        if (!isCompleted) {
                            Surface(
                                color = Color(0xFFF59E0B).copy(alpha = 0.05f),
                                shape = RoundedCornerShape(100.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "High Priority",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        val titleParts = order.address.split(",").take(2)
                        val mainTitle = if (titleParts.size >= 2) "${titleParts[0]} • ${titleParts[1]}" else order.buildingName
                        
                        Text(
                            text = mainTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = if (isCompleted) Color(0xFF131A30).copy(alpha = 0.6f) else Color(0xFF131A30),
                            fontSize = 17.sp,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = "Commercial Structural & Electrical Survey",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF131A30).copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = Color(0xFF3B82F6).copy(alpha = 0.05f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.border(1.dp, Color.Black.copy(alpha = 0.02f), RoundedCornerShape(10.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                                val startStr = order.scheduledAt?.substringAfter("T")?.take(5) ?: "00:00"
                                val endStr = order.scheduledEnd?.substringAfter("T")?.take(5) ?: "00:00"
                                Text(
                                    text = "$startStr - $endStr",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF131A30),
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "(2 hrs window)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF131A30).copy(alpha = 0.3f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        // "Starts in..." badge logic
                        if (!isCompleted) {
                            val timeText = remember<String?>(order.scheduledAt) {
                                calculateStartsIn(order.scheduledAt)
                            }
                            
                            timeText?.let { text ->
                                Surface(
                                    color = Color(0xFF10B981).copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = text,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF065F46),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color.Black.copy(alpha = 0.04f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                modifier = Modifier.size(34.dp),
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.05f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.Black.copy(alpha = 0.2f), modifier = Modifier.size(18.dp))
                                }
                            }
                            Column {
                                Text(
                                    text = order.technicianName ?: "Unassigned",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isCompleted) Color(0xFF131A30).copy(alpha = 0.6f) else Color(0xFF131A30),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Assigned Lead",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF131A30).copy(alpha = 0.3f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                onClick = {},
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.04f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.06f))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF131A30).copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                                }
                            }
                            
                            if (!isCompleted) {
                                Button(
                                    onClick = onClick,
                                    modifier = Modifier.height(40.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isOverdue) Color(0xFFFF4B66) else Color(0xFF3B82F6)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                                    enabled = !hasLiveTask
                                ) {
                                    Text(
                                        text = if (isOverdue) "START APPOINTMENT" else "Start Task",
                                        fontWeight = FontWeight.Black,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
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

fun calculateStartsIn(scheduledAt: String?): String? {
    if (scheduledAt.isNullOrBlank()) return null
    return try {
        val scheduledInstant = LocalDateTime.parse(scheduledAt).toInstant(TimeZone.currentSystemDefault())
        val now = kotlinx.datetime.Clock.System.now()
        val diffMillis: Long = scheduledInstant.toEpochMilliseconds() - now.toEpochMilliseconds()
        
        val totalMinutes: Long = diffMillis / 60000L
        if (totalMinutes <= 0L) return null
        
        val days = totalMinutes / (24L * 60L)
        val remainingMinutesAfterDays = totalMinutes % (24L * 60L)
        val hours = remainingMinutesAfterDays / 60L
        val minutes = remainingMinutesAfterDays % 60L
        
        when {
            days > 0L -> "Starts in ${days}d ${hours}h"
            hours > 0L -> "Starts in ${hours}h ${minutes}m"
            else -> "Starts in ${minutes}m"
        }
    } catch (e: Exception) {
        null
    }
}

