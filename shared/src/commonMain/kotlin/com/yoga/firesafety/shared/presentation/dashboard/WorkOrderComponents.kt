package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yoga.firesafety.shared.domain.model.WorkOrder

@Composable
fun WorkOrderScheduleItem(order: WorkOrder, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Priority Bar
            val barColor = when(order.priority.uppercase()) {
                "HIGH" -> Color(0xFFEF4444)
                "MEDIUM" -> Color(0xFFF59E0B)
                else -> MaterialTheme.colorScheme.primary
            }
            Box(modifier = Modifier.width(6.dp).fillMaxHeight().background(barColor))
            
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        order.type.replace("_", " "), 
                        style = MaterialTheme.typography.labelLarge, 
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        color = when(order.status.name) {
                            "NEW" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            "ASSIGNED" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            order.status.name,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = when(order.status.name) {
                                "NEW" -> MaterialTheme.colorScheme.secondary
                                "ASSIGNED" -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    order.buildingName, 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime, 
                        contentDescription = null, 
                        modifier = Modifier.size(16.dp), 
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (order.scheduledAt != null && order.scheduledEnd != null) {
                            val start = order.scheduledAt.substringAfter("T").take(5)
                            val end = order.scheduledEnd.substringAfter("T").take(5)
                            "$start - $end"
                        } else "Unscheduled", 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = if (order.scheduledAt != null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn, 
                        contentDescription = null, 
                        modifier = Modifier.size(16.dp), 
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        order.address, 
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
