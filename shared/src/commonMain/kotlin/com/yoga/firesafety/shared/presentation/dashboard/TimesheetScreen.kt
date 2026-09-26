package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoga.firesafety.shared.domain.model.TimeEntry
import com.yoga.firesafety.shared.domain.model.TimeTrackType
import com.yoga.firesafety.shared.domain.model.WorkOrder

@Composable
fun TimesheetScreen(
    viewModel: TimesheetViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSubTab by viewModel.selectedSubTab.collectAsState()
    val activeEntry by viewModel.activeEntry.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val totalTodayMinutes by viewModel.totalTodayMinutes.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedWorkOrder by viewModel.selectedWorkOrder.collectAsState()
    val timeLogs by viewModel.timeLogs.collectAsState()
    val technicianWorkOrders by viewModel.technicianWorkOrders.collectAsState()
    val todayWorkOrders by viewModel.todayWorkOrders.collectAsState()

    var showVisitPickerDialog by remember { mutableStateOf(false) }
    var showClockOutDialog by remember { mutableStateOf(false) }
    var showNoTaskDialog by remember { mutableStateOf(false) }

    val isClockedIn = activeEntry != null && activeEntry?.clockOutTime == null

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        Text(
            text = "Timesheet",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF0B1E36),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        )

        // Sub-Tabs Header ("Track Time" & "Time Log")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            SubTabButton(
                title = "Track Time",
                isSelected = selectedSubTab == 0,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.setSubTab(0) }
            )
            SubTabButton(
                title = "Time Log",
                isSelected = selectedSubTab == 1,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.setSubTab(1) }
            )
        }

        HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            if (selectedSubTab == 0) {
                TrackTimeTabContent(
                    isClockedIn = isClockedIn,
                    activeEntry = activeEntry,
                    elapsedSeconds = elapsedSeconds,
                    totalTodayMinutes = totalTodayMinutes,
                    selectedType = selectedType,
                    selectedWorkOrder = selectedWorkOrder,
                    viewModel = viewModel,
                    onSelectType = { type ->
                        viewModel.setType(type)
                        if (todayWorkOrders.isEmpty()) {
                            showNoTaskDialog = true
                        } else if (type == TimeTrackType.VISIT && selectedWorkOrder == null) {
                            showVisitPickerDialog = true
                        }
                    },
                    onClockInClick = {
                        if (todayWorkOrders.isEmpty()) {
                            showNoTaskDialog = true
                        } else if (selectedType == TimeTrackType.VISIT && selectedWorkOrder == null) {
                            showVisitPickerDialog = true
                        } else {
                            viewModel.clockIn()
                        }
                    },
                    onClockOutClick = {
                        showClockOutDialog = true
                    }
                )
            } else {
                TimeLogTabContent(
                    timeLogs = timeLogs,
                    viewModel = viewModel
                )
            }
        }
    }

    if (showVisitPickerDialog) {
        VisitPickerModalDialog(
            workOrders = todayWorkOrders,
            selectedOrder = selectedWorkOrder,
            onOrderSelected = { order ->
                viewModel.setSelectedWorkOrder(order)
                showVisitPickerDialog = false
            },
            onDismiss = { showVisitPickerDialog = false }
        )
    }

    if (showClockOutDialog) {
        ClockOutConfirmationDialog(
            onConfirm = { notes ->
                viewModel.clockOut(notes)
                showClockOutDialog = false
            },
            onDismiss = { showClockOutDialog = false }
        )
    }

    if (showNoTaskDialog) {
        NoTaskAssignedDialog(
            onDismiss = { showNoTaskDialog = false }
        )
    }
}

@Composable
private fun SubTabButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF0B1E36) else Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .height(3.dp)
                .fillMaxWidth(if (isSelected) 0.8f else 0f)
                .background(if (isSelected) Color(0xFF0B1E36) else Color.Transparent, RoundedCornerShape(2.dp))
        )
    }
}

@Composable
private fun TrackTimeTabContent(
    isClockedIn: Boolean,
    activeEntry: TimeEntry?,
    elapsedSeconds: Long,
    totalTodayMinutes: Long,
    selectedType: TimeTrackType,
    selectedWorkOrder: WorkOrder?,
    viewModel: TimesheetViewModel,
    onSelectType: (TimeTrackType) -> Unit,
    onClockInClick: () -> Unit,
    onClockOutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Status Badge
        ClockStatusPill(
            isClockedIn = isClockedIn,
            activeEntry = activeEntry,
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Timer Display (00:00:00)
        val displayTime = if (isClockedIn) {
            viewModel.formatSecondsToHHMMSS(elapsedSeconds)
        } else {
            "00:00:00"
        }

        Text(
            text = displayTime,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B1E36),
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.9f),
            color = Color(0xFFE2E8F0),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total Today Text
        Text(
            text = "Total Today: ${viewModel.formatMinutesToHoursMinutes(totalTodayMinutes)}",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Main Clock In / Clock Out Button
        Button(
            onClick = {
                if (isClockedIn) onClockOutClick() else onClockInClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isClockedIn) Color(0xFFD32F2F) else Color(0xFF2E7D32)
            )
        ) {
            Text(
                text = if (isClockedIn) "Clock Out" else "Clock In",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Select time to track section
        if (!isClockedIn) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select time to track",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0B1E36),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TrackTypeCard(
                        title = "Visit",
                        icon = Icons.Default.Build,
                        isSelected = selectedType == TimeTrackType.VISIT,
                        subtitle = selectedWorkOrder?.buildingName ?: "Select Visit",
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectType(TimeTrackType.VISIT) }
                    )

                    TrackTypeCard(
                        title = "General",
                        icon = Icons.Default.Work,
                        isSelected = selectedType == TimeTrackType.GENERAL,
                        subtitle = "Duty / Admin",
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectType(TimeTrackType.GENERAL) }
                    )
                }
            }
        } else {
            // Clocked in info card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "ACTIVE SESSION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (activeEntry?.type == TimeTrackType.VISIT) {
                            activeEntry.workOrderTitle ?: "Visit Session"
                        } else "General Duty",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B1E36)
                    )
                    if (activeEntry?.clockInTime != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Started at ${viewModel.formatEpochToTime(activeEntry.clockInTime)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClockStatusPill(
    isClockedIn: Boolean,
    activeEntry: TimeEntry?,
    viewModel: TimesheetViewModel
) {
    val bgColor = if (isClockedIn) Color(0xFFE8F5E9) else Color(0xFFF1F5F9)
    val textColor = if (isClockedIn) Color(0xFF1B5E20) else Color(0xFF64748B)
    val dotColor = if (isClockedIn) Color(0xFF2E7D32) else Color(0xFF94A3B8)

    val statusText = if (isClockedIn && activeEntry?.clockInTime != null) {
        "Clocked in since ${viewModel.formatEpochToTime(activeEntry.clockInTime)}"
    } else {
        "Not clocked in"
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(100.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
private fun TrackTypeCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFFF0F7FF) else Color.White,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) Color(0xFF2563EB) else Color(0xFF64748B),
                    modifier = Modifier.size(28.dp)
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B1E36)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun TimeLogTabContent(
    timeLogs: List<TimeEntry>,
    viewModel: TimesheetViewModel
) {
    val selectedDate by viewModel.selectedLogDate.collectAsState()
    val dateLogs by viewModel.selectedDateLogs.collectAsState()
    val totalMinutes by viewModel.selectedDateTotalMinutes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Date Navigator Row (< Friday, Sep 18 >)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousDay() }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Day", tint = Color(0xFF131A30))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val headerText = viewModel.formatLogDateHeader(selectedDate)
                Text(
                    text = headerText.substringBefore(","),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF131A30),
                    fontSize = 18.sp
                )
                Text(
                    text = headerText.substringAfter(", ").trim(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = { viewModel.nextDay() }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Day", tint = Color(0xFF131A30))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Total Summary
        Text(
            text = "Total: ${viewModel.formatMinutesToHoursMinutes(totalMinutes)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = Color(0xFF131A30),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (dateLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No time logs recorded for this date",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dateLogs) { log ->
                    TimelineLogItem(log = log, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun TimelineLogItem(
    log: TimeEntry,
    viewModel: TimesheetViewModel
) {
    val clockInStr = viewModel.formatEpochToTime(log.clockInTime)
    val clockOutStr = if (log.clockOutTime != null) viewModel.formatEpochToTime(log.clockOutTime) else "In Progress"
    val durationText = if (log.clockOutTime != null) viewModel.formatMinutesToHoursMinutes(log.durationMinutes) else "Active"
    val titleText = if (log.type == TimeTrackType.VISIT) (log.workOrderTitle ?: "Visit Session") else "General"
    val iconVector = if (log.type == TimeTrackType.VISIT) Icons.Default.Build else Icons.Default.Work

    Column(modifier = Modifier.fillMaxWidth()) {
        // Clocked in row
        TimelineEventRow(
            icon = Icons.Default.PlayArrow,
            iconTint = Color(0xFF3B82F6),
            title = "Clocked in",
            time = clockInStr
        )

        // Task / Visit / General row
        TimelineEventRow(
            icon = iconVector,
            iconTint = if (log.type == TimeTrackType.VISIT) Color(0xFF2563EB) else Color(0xFF475569),
            title = titleText,
            time = "$clockInStr - $clockOutStr",
            trailingText = durationText,
            hasLine = log.clockOutTime != null
        )

        // Clocked out row (if completed)
        if (log.clockOutTime != null) {
            TimelineEventRow(
                icon = Icons.Default.Stop,
                iconTint = Color(0xFFD32F2F),
                title = "Clocked out",
                time = clockOutStr,
                hasLine = false
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.Black.copy(alpha = 0.04f))
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun TimelineEventRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    time: String,
    trailingText: String? = null,
    hasLine: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timeline Column with icon and vertical line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            if (hasLine) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(Color(0xFFE2E8F0))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and Time
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B1E36)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
        }

        // Trailing duration (if present)
        if (trailingText != null) {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF131A30),
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun VisitPickerModalDialog(
    workOrders: List<WorkOrder>,
    selectedOrder: WorkOrder?,
    onOrderSelected: (WorkOrder) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Visit Work Order",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (workOrders.isEmpty()) {
                Text(
                    text = "No active work orders assigned to you currently.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(workOrders) { order ->
                        val isSelected = order.id == selectedOrder?.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOrderSelected(order) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = order.buildingName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0B1E36)
                                )
                                Text(
                                    text = order.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ClockOutConfirmationDialog(
    onConfirm: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var notesText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Clock Out Confirmation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B1E36)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Are you sure you want to clock out? You can optionally add work notes or a summary below.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF475569)
                )

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Work Notes / Summary (Optional)") },
                    placeholder = { Text("e.g. Completed inspection on Floor 2") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(notesText.ifBlank { null })
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Clock Out", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

@Composable
private fun NoTaskAssignedDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "No Task Assigned",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B1E36)
            )
        },
        text = {
            Text(
                text = "No tasks are assigned for today, so clock-in is not allowed.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF475569)
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("OK", fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}
