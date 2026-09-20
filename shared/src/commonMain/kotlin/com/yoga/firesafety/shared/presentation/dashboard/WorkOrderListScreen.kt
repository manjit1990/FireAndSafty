package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.presentation.MainViewModel
import com.yoga.firesafety.shared.presentation.AuthState
import com.yoga.firesafety.shared.presentation.dashboard.MoreScreen
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock as KtClock
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
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.setTechnicianFilter(userId)
            viewModel.refresh()
        }
    }
    
    val today = remember { KtClock.System.todayIn(TimeZone.currentSystemDefault()) }
    val scope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf(today) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // Luxury custom inner tab filter state: 0 = All Tasks, 1 = Live, 2 = Upcoming
    var selectedTaskFilterIndex by remember { mutableStateOf(0) }
    
    // Full-screen map state
    var isFullScreenMapOpen by remember { mutableStateOf(false) }
    
    // Schedule specific toggle: 0 = Day, 1 = List
    var scheduleViewType by remember { mutableStateOf(0) }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    )
    var showDatePicker by remember { mutableStateOf(false) }

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

    val hasLiveTask = remember(workOrders) {
        workOrders.any { it.status.name == "STARTED" || it.status.name == "IN_PROGRESS" || it.status.name == "LIVE" }
    }

    val currentViewWorkOrders = remember(workOrders, selectedDate, selectedTabIndex, scheduleViewType) {
        when (selectedTabIndex) {
            1 -> { // Schedule
                if (scheduleViewType == 0) {
                    // Day View: Filter by selected date
                    workOrders.filter { it.scheduledAt?.startsWith(selectedDate.toString()) == true }
                } else {
                    // List Agenda: All non-completed tasks
                    workOrders.filter { it.status != com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED }
                }
            }
            2 -> { // Timesheet/Archive
                workOrders.filter { it.status == com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED }
            }
            else -> emptyList()
        }
    }

    val filteredList = remember(currentViewWorkOrders, selectedTaskFilterIndex) {
        currentViewWorkOrders.filter { order ->
            when (selectedTaskFilterIndex) {
                1 -> order.status.name == "STARTED" || order.status.name == "IN_PROGRESS" || order.status.name == "LIVE"
                2 -> order.status.name != "STARTED" && order.status.name != "IN_PROGRESS" && order.status.name != "LIVE" && order.status != com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED
                3 -> order.status == com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED
                else -> true
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
            if (selectedTabIndex != 0 && selectedTabIndex != 3) {
                Surface(
                    color = if (selectedTabIndex == 3) Color.Transparent else Color(0xFFF8F9FB),
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            onClick = onLogout,
                            modifier = Modifier.size(38.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
                            shadowElevation = 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back", modifier = Modifier.size(22.dp), tint = Color.Black.copy(alpha = 0.6f))
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showDatePicker = true },
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${visibleMonthDate.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${visibleMonthDate.year}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF131A30),
                                fontSize = 16.sp
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF131A30).copy(alpha = 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Surface(
                            onClick = { isFullScreenMapOpen = true },
                            modifier = Modifier.size(38.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Map, contentDescription = "Map", tint = Color(0xFF3B82F6), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        },
        containerColor = if (selectedTabIndex == 3) Color(0xFF070A13) else Color(0xFFF8F9FB)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(if (selectedTabIndex == 3) Color(0xFF070A13) else Color(0xFFF8F9FB))) {
            Column(modifier = Modifier.padding(padding)) {
                if (selectedTabIndex == 1) {
                    // Schedule Specific Header Features
                    LuxuryDayListToggle(
                        selected = scheduleViewType,
                        onSelected = { scheduleViewType = it }
                    )
                    
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
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
                    
                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let {
                                        selectedDate = kotlinx.datetime.Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
                                    }
                                    showDatePicker = false
                                }) {
                                    Text("OK", fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Premium Technician Profile Card
                    val fullName = remember(authState) {
                        (authState as? AuthState.Authenticated)?.session?.let {
                            "${it.firstName ?: ""} ${it.lastName ?: ""}".trim().ifEmpty { it.email }
                        } ?: "Technician"
                    }
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(48.dp)) {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF59E0B) // Amber
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = fullName.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").take(2),
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                                // Online status dot
                                Surface(
                                    modifier = Modifier.size(14.dp).align(Alignment.BottomEnd).offset(x = 2.dp, y = 2.dp),
                                    shape = CircleShape,
                                    color = Color(0xFF10B981),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                                ) {}
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = fullName.lowercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF131A30),
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "Senior Field Inspector • Shift: Day",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF131A30).copy(alpha = 0.4f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                            
                            Surface(
                                color = Color(0xFFF8F9FB),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f))
                            ) {
                                Text(
                                    text = currentViewWorkOrders.size.toString(),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF131A30),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val liveCount = currentViewWorkOrders.count { it.status.name == "STARTED" || it.status.name == "IN_PROGRESS" || it.status.name == "LIVE" }
                        val upcomingCount = currentViewWorkOrders.count { it.status.name != "STARTED" && it.status.name != "IN_PROGRESS" && it.status.name != "LIVE" && it.status != com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED }
                        val completedCount = currentViewWorkOrders.count { it.status == com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED }

                        FilterChipModern("All Tasks", currentViewWorkOrders.size.toString(), selectedTaskFilterIndex == 0) { selectedTaskFilterIndex = 0 }
                        FilterChipModern("Live", liveCount.toString(), selectedTaskFilterIndex == 1, showDot = true) { selectedTaskFilterIndex = 1 }
                        FilterChipModern("Upcoming", upcomingCount.toString(), selectedTaskFilterIndex == 2) { selectedTaskFilterIndex = 2 }
                        FilterChipModern("Completed", completedCount.toString(), selectedTaskFilterIndex == 3) { selectedTaskFilterIndex = 3 }
                    }
                } else if (selectedTabIndex == 2) {
                    // Timesheet Header
                    SectionHeader(title = "TIMESHEET", count = filteredList.size)
                }
                
                Box(modifier = Modifier.weight(1f)) {
                    when(selectedTabIndex) {
                        0 -> TechnicianMapHomeTab(workOrders = workOrders)
                        1 -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                if (scheduleViewType == 0) {
                                    if (filteredList.isEmpty()) {
                                        EmptyStateView(message = "No tasks have been assigned to you for this date")
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxSize(),
                                            contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp)
                                        ) {
                                            items(filteredList) { order ->
                                                WorkOrderScheduleItem(order = order, onClick = { onWorkOrderClick(order) })
                                            }
                                        }
                                    }
                                } else {
                                    if (filteredList.isEmpty()) {
                                        EmptyStateView(message = "You have no active tasks assigned at the moment")
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxSize(),
                                            contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp)
                                        ) {
                                            items(filteredList) { order ->
                                                WorkOrderScheduleItem(order = order, onClick = { onWorkOrderClick(order) })
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            if (filteredList.isEmpty()) {
                                EmptyStateView(message = "No archived tasks found")
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp)
                                ) {
                                    items(filteredList) { order ->
                                        WorkOrderScheduleItem(
                                            order = order, 
                                            hasLiveTask = hasLiveTask,
                                            onClick = { onWorkOrderClick(order) }
                                        )
                                    }
                                }
                            }
                        }
                        3 -> MoreScreen(onLogout = onLogout)
                    }
                }
            }

            FloatingBottomNav(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                selectedIndex = selectedTabIndex,
                onTabSelected = { viewModel.updateSelectedTab(it) }
            )
        }
    }

    if (isFullScreenMapOpen) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            MapView(
                modifier = Modifier.fillMaxSize(),
                workOrders = workOrders
            )
            
            Surface(
                onClick = { isFullScreenMapOpen = false },
                modifier = Modifier.padding(24.dp).align(Alignment.TopEnd).size(44.dp),
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
            
            Text(
                text = "FLEET MAP VIEW",
                modifier = Modifier.padding(24.dp).align(Alignment.TopStart),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun FilterChipModern(text: String, count: String, isSelected: Boolean, showDot: Boolean = false, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (isSelected) Color(0xFF3B82F6) else Color.White,
        shape = RoundedCornerShape(100.dp),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (showDot) {
                Box(modifier = Modifier.size(6.dp).background(if (isSelected) Color.White else Color(0xFF10B981), CircleShape))
            }
            val displayText = if (count.isNotEmpty() && count != "0") "$text ($count)" else text
            Text(
                text = displayText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF131A30).copy(alpha = 0.6f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun LuxuryDayListToggle(selected: Int, onSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .height(38.dp)
            .background(Color(0xFFE2E8F0).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(3.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(if (selected == 0) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                .clickable { onSelected(0) },
            contentAlignment = Alignment.Center
        ) {
            Text("Day View", color = if (selected == 0) Color(0xFF131A30) else Color(0xFF131A30).copy(alpha = 0.4f), fontWeight = FontWeight.Black, style = MaterialTheme.typography.bodyLarge)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(if (selected == 1) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
                .clickable { onSelected(1) },
            contentAlignment = Alignment.Center
        ) {
            Text("List Agenda", color = if (selected == 1) Color(0xFF131A30) else Color(0xFF131A30).copy(alpha = 0.4f), fontWeight = FontWeight.Black, style = MaterialTheme.typography.bodyLarge)
        }
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
            color = Color(0xFF131A30).copy(alpha = 0.3f),
            letterSpacing = 1.5.sp
        )
        if (count > 0) {
            Text(
                "$count TOTAL",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF131A30).copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmptyStateView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = Color(0xFF3B82F6).copy(alpha = 0.05f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.AssignmentLate, 
                    contentDescription = null, 
                    tint = Color(0xFF3B82F6).copy(alpha = 0.3f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message, 
            color = Color(0xFF131A30).copy(alpha = 0.4f), 
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 14.sp
        )
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
            .width(320.dp)
            .height(50.dp)
            .shadow(16.dp, RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.03f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabIcon("Home", Icons.Default.Home, selectedIndex == 0) { onTabSelected(0) }
            NavTabIcon("Schedule", Icons.Default.DateRange, selectedIndex == 1) { onTabSelected(1) }
            NavTabIcon("Timesheet", Icons.Default.AccessTime, selectedIndex == 2) { onTabSelected(2) }
            NavTabIcon("More", Icons.Default.MoreHoriz, selectedIndex == 3) { onTabSelected(3) }
        }
    }
}

@Composable
fun NavTabIcon(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 2.dp, horizontal = 8.dp)
    ) {
        Icon(
            icon, 
            contentDescription = label, 
            tint = if (isSelected) Color(0xFF3B82F6) else Color.Black.copy(alpha = 0.3f),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
            color = if (isSelected) Color(0xFF3B82F6) else Color.Black.copy(alpha = 0.3f),
            fontSize = 8.sp
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 1.dp)
                    .width(12.dp)
                    .height(2.dp)
                    .background(Color(0xFF3B82F6), RoundedCornerShape(100.dp))
            )
        }
    }
}

@Composable
fun CalendarRibbon(
    selectedDate: LocalDate, 
    dateRange: List<LocalDate>,
    scrollState: LazyListState,
    workOrders: List<WorkOrder>,
    onDateSelected: (LocalDate) -> Unit
) {
    LazyRow(
        state = scrollState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 0.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(dateRange) { date ->
            val isSelected = date == selectedDate
            val weekday = date.dayOfWeek.name.take(3).uppercase()
            val hasAppointments = remember(workOrders, date) {
                workOrders.any { it.scheduledAt?.startsWith(date.toString()) == true }
            }
            
            Surface(
                modifier = Modifier
                    .width(54.dp)
                    .height(60.dp)
                    .clickable { onDateSelected(date) },
                color = if (isSelected) Color(0xFF3B82F6) else Color.White,
                shape = RoundedCornerShape(16.dp),
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.03f)),
                shadowElevation = if (isSelected) 4.dp else 1.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = weekday,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.2f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = if (isSelected) Color.White else Color(0xFF131A30),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(4.dp)
                                .background(Color.White, CircleShape)
                        )
                    }

                    if (hasAppointments) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else Color(0xFF10B981),
                                modifier = Modifier
                                    .size(14.dp)
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TechnicianMapHomeTab(workOrders: List<WorkOrder>) {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    val scrollState = rememberScrollState()
    
    // Dynamic Time and Date Logic
    val now = remember { KtClock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
    val greeting = remember(now.hour) {
        when (now.hour) {
            in 5..11 -> "Good morning,"
            in 12..16 -> "Good afternoon,"
            in 17..20 -> "Good evening,"
            else -> "Good night,"
        }
    }
    
    val dateString = remember(now) {
        val dayName = now.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        val monthName = now.month.name.lowercase().replaceFirstChar { it.uppercase() }
        val dayOfMonth = now.dayOfMonth
        val suffix = when {
            dayOfMonth in 11..13 -> "th"
            dayOfMonth % 10 == 1 -> "st"
            dayOfMonth % 10 == 2 -> "nd"
            dayOfMonth % 10 == 3 -> "rd"
            else -> "th"
        }
        "$dayName, $monthName $dayOfMonth$suffix"
    }

    val todayStr = now.date.toString()
    val todaysTasks = remember(workOrders, todayStr) {
        workOrders.filter { order ->
            order.scheduledAt?.startsWith(todayStr) == true &&
            order.status != com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED
        }
    }

    var selectedOrderOnMap by remember { mutableStateOf<WorkOrder?>(null) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Premium Client-Style Greeting Header Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF131A30).copy(alpha = 0.4f),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF131A30)
                    )
                    Text(
                        text = "Let's get started",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF131A30).copy(alpha = 0.6f)
                    )
                }
                
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Real Interactive Map Background
                    MapView(
                        modifier = Modifier.fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    change.consume()
                                }
                            },
                        workOrders = todaysTasks
                    )

                    // Stylized grid simulation overlay
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(5) {
                            HorizontalDivider(color = Color.Black.copy(alpha = 0.05f))
                        }
                    }
                    
                    // Plotting Task Pinpoints
                    todaysTasks.take(5).forEach { order ->
                        val hashValue = order.id.hashCode().let { if (it < 0) -it else it }
                        val revHashValue = order.id.reversed().hashCode().let { if (it < 0) -it else it }
                        val xOffset = (hashValue % 60 + 20) / 100f
                        val yOffset = (revHashValue % 50 + 25) / 100f
                        
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(
                                    x = (320 * xOffset).dp,
                                    y = (200 * yOffset).dp
                                )
                        ) {
                            MapPinpoint(
                                isSelected = selectedOrderOnMap?.id == order.id,
                                onClick = { selectedOrderOnMap = order }
                            )
                        }
                    }

                    // Task Detail Overlay on Map
                    selectedOrderOnMap?.let { order ->
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp)
                                .fillMaxWidth(0.9f),
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f)),
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFFF4B66), modifier = Modifier.size(20.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(order.buildingName, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color(0xFF131A30))
                                    Text(order.address, style = MaterialTheme.typography.labelSmall, color = Color(0xFF131A30).copy(alpha = 0.5f), maxLines = 1)
                                }
                                IconButton(
                                    onClick = {
                                        val url = "https://www.google.com/maps/search/?api=1&query=${order.buildingName}, ${order.address}".replace(" ", "%20")
                                        try { uriHandler.openUri(url) } catch (e: Exception) {}
                                    },
                                    modifier = Modifier.size(32.dp).background(Color(0xFF3B82F6), CircleShape)
                                ) {
                                    Icon(Icons.Default.Directions, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { selectedOrderOnMap = null }) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF131A30).copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    if (selectedOrderOnMap == null) {
                        Text(
                            text = "Tracking ${todaysTasks.size} today's tasks",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF131A30).copy(alpha = 0.4f),
                            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                        )
                    }
                }
            }

            // Up Next Card
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "UP NEXT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF131A30).copy(alpha = 0.3f),
                    letterSpacing = 1.5.sp
                )
                
                val todayStr = now.date.toString()
                val nextOrder = workOrders.find { order ->
                    val dateMatches = order.scheduledAt?.startsWith(todayStr) == true
                    val isLive = order.status.name == "STARTED" || order.status.name == "IN_PROGRESS" || order.status.name == "LIVE"
                    val isCompleted = order.status == com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED
                    dateMatches && !isLive && !isCompleted
                }
                
                if (nextOrder != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val url = "https://www.google.com/maps/search/?api=1&query=${nextOrder.buildingName}, ${nextOrder.address}".replace(" ", "%20")
                                try { uriHandler.openUri(url) } catch (e: Exception) {}
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(72.dp)
                                    .background(Color(0xFF10B981), RoundedCornerShape(100.dp))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(nextOrder.buildingName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Black, color = Color(0xFF131A30))
                                Text(
                                    text = formatWorkOrderDateTimeRange(nextOrder.scheduledAt, nextOrder.scheduledEnd).substringAfter(", "),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3B82F6)
                                )
                                Text(nextOrder.address, style = MaterialTheme.typography.bodySmall, color = Color(0xFF131A30).copy(alpha = 0.5f), maxLines = 1)
                                Text(nextOrder.type + " Inspection", style = MaterialTheme.typography.labelSmall, color = Color(0xFF131A30).copy(alpha = 0.3f), fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF131A30).copy(alpha = 0.2f))
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f))
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No upcoming tasks for today", color = Color(0xFF131A30).copy(alpha = 0.3f), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // This Week
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("This week", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Black, color = Color(0xFF131A30))
                            Text("Sep 13 - 19", style = MaterialTheme.typography.labelSmall, color = Color(0xFF131A30).copy(alpha = 0.4f))
                        }
                        Text(
                            text = "View timesheet",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3B82F6),
                            modifier = Modifier.clickable { }
                        )
                    }
                    HorizontalDivider(color = Color.Black.copy(alpha = 0.05f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total completed time", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF131A30).copy(alpha = 0.6f))
                        Text("00:00", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color(0xFF131A30))
                    }
                }
            }

            // Get Help
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "Get Help",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF10B981),
                    modifier = Modifier.clickable { }
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun MapPinpoint(isSelected: Boolean, onClick: () -> Unit) {
    val color = if (isSelected) Color(0xFF3B82F6) else Color(0xFFFF4B66)
    val size = if (isSelected) 24.dp else 18.dp
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(color.copy(alpha = 0.2f), CircleShape)
            )
        }
        Surface(
            modifier = Modifier.size(size),
            shape = CircleShape,
            color = color,
            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.8f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.PinDrop,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}
