package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.sp
import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.presentation.MainViewModel
import com.yoga.firesafety.shared.presentation.AuthState
import com.yoga.firesafety.shared.presentation.dashboard.ProfileScreen
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkOrderListScreen(
    onWorkOrderClick: (WorkOrder) -> Unit,
    onLogout: () -> Unit,
    viewModel: WorkOrderViewModel = koinViewModel(),
    mainViewModel: MainViewModel = koinViewModel()
) {
    val authState by mainViewModel.authState.collectAsState()
    val userId = (authState as? AuthState.Authenticated)?.session?.userId ?: ""
    val workOrders by viewModel.technicianWorkOrders.collectAsState()
    
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.setTechnicianFilter(userId)
        }
    }
    
    val today = remember { kotlinx.datetime.Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    var selectedDate by remember { mutableStateOf(today) }
    var selectedTabIndex by remember { mutableStateOf(1) } // Default to "Schedule"
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // Luxury custom inner tab filter state: 0 = All Tasks, 1 = Live, 2 = Upcoming
    var selectedTaskFilterIndex by remember { mutableStateOf(0) }

    // Calendar Scrolling State
    val dateRange = remember { 
        (-60..60).map { today.plus(DatePeriod(days = it)) } 
    }
    val calendarScrollState = rememberLazyListState(initialFirstVisibleItemIndex = 57) // Near center (today is index 60)
    
    val visibleMonthDate by remember {
        derivedStateOf {
            val index = calendarScrollState.firstVisibleItemIndex
            if (index in dateRange.indices) dateRange[index] else today
        }
    }

    LaunchedEffect(Unit) {
        val todayIndex = dateRange.indexOf(today)
        if (todayIndex != -1) {
            calendarScrollState.scrollToItem(todayIndex - 2)
        }
    }

    val filteredList = remember(workOrders, selectedDate, selectedTabIndex, selectedTaskFilterIndex) {
        if (selectedTabIndex == 2) emptyList() 
        else {
            workOrders.filter { order ->
                val isCorrectStatus = if (selectedTabIndex == 0) {
                    order.status == com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED
                } else {
                    order.status != com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED
                }
                
                val matchesDate = order.scheduledAt?.startsWith(selectedDate.toString()) ?: false
                
                // Fine-tuned Premium sub-filter mapping
                val matchesFilterChip = when (selectedTaskFilterIndex) {
                    1 -> order.status.name == "STARTED" || order.status.name == "IN_PROGRESS" || order.status.name == "LIVE"
                    2 -> order.status.name != "STARTED" && order.status.name != "IN_PROGRESS" && order.status.name != "LIVE" && order.status != com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED
                    else -> true
                }
                
                isCorrectStatus && (selectedTabIndex == 0 || matchesDate) && matchesFilterChip
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to logout from the FireSafety Portal?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Logout", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
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
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "SCHEDULED PERIOD", 
                            style = MaterialTheme.typography.labelSmall, 
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.4f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${visibleMonthDate.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${visibleMonthDate.year}", 
                                style = MaterialTheme.typography.titleLarge, 
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White.copy(alpha = 0.4f))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = Color.White.copy(alpha = 0.05f)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
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
        containerColor = Color(0xFF070A13)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF070A13))) {
            Column(modifier = Modifier.padding(padding)) {
                if (selectedTabIndex != 2) {
                    CalendarRibbon(
                        selectedDate = selectedDate, 
                        dateRange = dateRange,
                        scrollState = calendarScrollState,
                        onDateSelected = { selectedDate = it }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    // Premium Filter Segment Row Header
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = when(selectedTabIndex) {
                                    0 -> "ARCHIVE"
                                    else -> "TODAY'S TASKS"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Surface(color = Color(0xFF1E294B), shape = RoundedCornerShape(100.dp)) {
                                Text(
                                    text = "${filteredList.size} Total",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF3B82F6),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Filter", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
                            Icon(Icons.Default.FilterList, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                        }
                    }

                    // Luxury Horizontal Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val liveCount = workOrders.count { it.status.name == "STARTED" || it.status.name == "IN_PROGRESS" || it.status.name == "LIVE" }
                        val upcomingCount = workOrders.count { it.status.name != "STARTED" && it.status.name != "IN_PROGRESS" && it.status.name != "LIVE" && it.status != com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED }

                        FilterCapsule("All Tasks", selectedTaskFilterIndex == 0) { selectedTaskFilterIndex = 0 }
                        FilterCapsule("Live ($liveCount)", selectedTaskFilterIndex == 1) { selectedTaskFilterIndex = 1 }
                        FilterCapsule("Upcoming ($upcomingCount)", selectedTaskFilterIndex == 2) { selectedTaskFilterIndex = 2 }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
                
                Box(modifier = Modifier.weight(1f)) {
                    when(selectedTabIndex) {
                        0, 1 -> {
                            if (filteredList.isEmpty()) {
                                EmptyStateView(message = "No assignments found")
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 120.dp, top = 4.dp)
                                ) {
                                    items(filteredList) { order ->
                                        WorkOrderScheduleItem(order = order, onClick = { onWorkOrderClick(order) })
                                    }
                                }
                            }
                        }
                        2 -> ProfileScreen()
                    }
                }
            }

            FloatingBottomNav(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                selectedIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )
        }
    }
}

@Composable
fun FilterCapsule(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (isSelected) Color.White else Color(0xFF0F1424),
        shape = RoundedCornerShape(100.dp),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White.copy(alpha = 0.3f),
            letterSpacing = 1.5.sp
        )
        if (count > 0) {
            Text(
                "$count TOTAL",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmptyStateView(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = Color.White.copy(alpha = 0.2f), style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun FloatingBottomNav(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = modifier
            .width(260.dp)
            .height(64.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.9f),
        shape = RoundedCornerShape(100.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f)),
        shadowElevation = 24.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabIcon(Icons.Default.History, selectedIndex == 0) { onTabSelected(0) }
            NavTabIcon(Icons.Default.Dashboard, selectedIndex == 1) { onTabSelected(1) }
            NavTabIcon(Icons.Default.Person, selectedIndex == 2) { onTabSelected(2) }
        }
    }
}

@Composable
fun NavTabIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            icon, 
            contentDescription = null, 
            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun CalendarRibbon(
    selectedDate: LocalDate, 
    dateRange: List<LocalDate>,
    scrollState: LazyListState,
    onDateSelected: (LocalDate) -> Unit
) {
    LazyRow(
        state = scrollState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(dateRange) { date ->
            val isSelected = date == selectedDate
            val weekday = date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
            
            Surface(
                modifier = Modifier
                    .width(64.dp)
                    .height(90.dp)
                    .clickable { onDateSelected(date) },
                color = if (isSelected) Color.White else Color(0xFF0F1424),
                shape = RoundedCornerShape(20.dp),
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.03f))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = weekday,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.Black.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.3f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = if (isSelected) Color.Black else Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.width(16.dp).height(3.dp).background(Color(0xFF3B82F6), RoundedCornerShape(100.dp)))
                    }
                }
            }
        }
    }
}
