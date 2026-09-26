package com.yoga.firesafety.shared.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yoga.firesafety.shared.domain.model.WorkOrderStatus
import com.yoga.firesafety.shared.presentation.dashboard.WorkOrderViewModel
import com.yoga.firesafety.shared.presentation.dashboard.WorkOrderScheduleItem
import com.yoga.firesafety.shared.presentation.dashboard.CalendarRibbon
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import kotlinx.datetime.Instant as KotlinxInstant
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit,
    onCreateOrderClick: () -> Unit,
    onManageUsersClick: () -> Unit,
    onWorkOrderClick: (String) -> Unit,
    viewModel: WorkOrderViewModel = koinViewModel()
) {
    val workOrders by viewModel.workOrders.collectAsState()
    val activeEntries by viewModel.activeEntries.collectAsState()
    val onlineTechCount = activeEntries.map { it.userId }.distinct().size

    var showLogoutDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val today = remember { 
        KotlinxInstant.fromEpochMilliseconds(kotlinx.datetime.Clock.System.now().toEpochMilliseconds())
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date 
    }
    val scope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf(today) }

    val dateRange = remember { 
        (-60..60).map { today.plus(DatePeriod(days = it)) } 
    }
    val calendarScrollState = rememberLazyListState(initialFirstVisibleItemIndex = 57)

    LaunchedEffect(Unit) {
        val todayIndex = dateRange.indexOf(today)
        if (todayIndex != -1) {
            calendarScrollState.scrollToItem(todayIndex - 2)
        }
    }

    val dateFilteredOrders = remember(workOrders, selectedDate) {
        workOrders.filter { order ->
            order.scheduledAt?.startsWith(selectedDate.toString()) == true
        }
    }

    val unassignedOrders = dateFilteredOrders.filter { it.status == WorkOrderStatus.NEW }
    val assignedOrders = dateFilteredOrders.filter { 
        it.status == WorkOrderStatus.ASSIGNED || 
        it.status == WorkOrderStatus.STARTED || 
        it.status == WorkOrderStatus.IN_PROGRESS ||
        it.status == WorkOrderStatus.ACCEPTED ||
        it.status == WorkOrderStatus.EN_ROUTE ||
        it.status == WorkOrderStatus.ON_SITE
    }
    val completedOrders = dateFilteredOrders.filter { 
        it.status == WorkOrderStatus.COMPLETED || it.status == WorkOrderStatus.CANCELLED 
    }

    val filteredOrders = when (selectedTabIndex) {
        0 -> unassignedOrders
        1 -> assignedOrders
        else -> completedOrders
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to logout of the Admin Portal?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Confirm", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Portal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onManageUsersClick) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = "Manage Users", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateOrderClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("NEW WORK ORDER", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Summary Cards Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Active Orders",
                    value = "${workOrders.size}",
                    icon = Icons.Default.Assignment,
                    color = MaterialTheme.colorScheme.primary
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Techs Online",
                    value = "$onlineTechCount",
                    icon = Icons.Default.Person,
                    color = Color(0xFF2E7D32)
                )
            }

            // Calendar Ribbon & Go to Today
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 2.dp)) {
                Text(
                    "Go to Today",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B82F6),
                    modifier = Modifier.align(Alignment.CenterStart).clickable { 
                        selectedDate = today
                        val todayIndex = dateRange.indexOf(today)
                        if (todayIndex != -1) {
                            scope.launch {
                                calendarScrollState.animateScrollToItem(maxOf(0, todayIndex - 2))
                            }
                        }
                    }
                )
            }

            CalendarRibbon(
                selectedDate = selectedDate, 
                dateRange = dateRange,
                scrollState = calendarScrollState,
                workOrders = workOrders,
                onDateSelected = { selectedDate = it }
            )

            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Fleet Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            // Dynamic Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)) }
            ) {
                AdminTab(0, "Pending", unassignedOrders.size, selectedTabIndex == 0) { selectedTabIndex = 0 }
                AdminTab(1, "Assigned", assignedOrders.size, selectedTabIndex == 1) { selectedTabIndex = 1 }
                AdminTab(2, "Archived", completedOrders.size, selectedTabIndex == 2) { selectedTabIndex = 2 }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inbox, 
                            contentDescription = null, 
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = when(selectedTabIndex) {
                                0 -> "No pending work orders for this date"
                                1 -> "No active assignments for this date"
                                else -> "No archived tasks for this date"
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp)
                ) {
                    items(filteredOrders) { order ->
                        WorkOrderScheduleItem(
                            order = order, 
                            isAdmin = true, 
                            customButtonText = if (selectedTabIndex == 1) "Edit Task" else "Assign Task",
                            onClick = { onWorkOrderClick(order.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminTab(
    index: Int,
    title: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Tab(
        selected = isSelected,
        onClick = onClick,
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                )
                if (count > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = count.toString(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
