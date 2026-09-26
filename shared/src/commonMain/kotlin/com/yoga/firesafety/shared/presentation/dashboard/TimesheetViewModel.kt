package com.yoga.firesafety.shared.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoga.firesafety.shared.domain.model.TimeEntry
import com.yoga.firesafety.shared.domain.model.TimeTrackType
import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.domain.repository.SessionRepository
import com.yoga.firesafety.shared.domain.repository.TimesheetRepository
import com.yoga.firesafety.shared.domain.repository.WorkOrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class TimesheetViewModel(
    private val timesheetRepository: TimesheetRepository,
    private val sessionRepository: SessionRepository,
    private val workOrderRepository: WorkOrderRepository
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)

    private val _selectedSubTab = MutableStateFlow(0) // 0 = Track Time, 1 = Time Log
    val selectedSubTab: StateFlow<Int> = _selectedSubTab

    private val _selectedType = MutableStateFlow(TimeTrackType.GENERAL)
    val selectedType: StateFlow<TimeTrackType> = _selectedType

    private val _selectedWorkOrder = MutableStateFlow<WorkOrder?>(null)
    val selectedWorkOrder: StateFlow<WorkOrder?> = _selectedWorkOrder

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds

    private val _totalTodayMinutes = MutableStateFlow(0L)
    val totalTodayMinutes: StateFlow<Long> = _totalTodayMinutes

    private val _selectedLogDate = MutableStateFlow(
        Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
    )
    val selectedLogDate: StateFlow<LocalDate> = _selectedLogDate

    private var timerJob: Job? = null

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val activeEntry: StateFlow<TimeEntry?> = _userId
        .filterNotNull()
        .flatMapLatest { id -> timesheetRepository.observeActiveEntry(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val timeLogs: StateFlow<List<TimeEntry>> = _userId
        .filterNotNull()
        .flatMapLatest { id -> timesheetRepository.observeTimeEntries(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val technicianWorkOrders: StateFlow<List<WorkOrder>> = _userId
        .filterNotNull()
        .flatMapLatest { id -> workOrderRepository.getWorkOrdersForTechnician(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayWorkOrders: StateFlow<List<WorkOrder>> = technicianWorkOrders
        .map { orders ->
            val todayStr = getTodayDateString(Clock.System.now().toEpochMilliseconds())
            orders.filter { order ->
                val isToday = order.scheduledAt?.startsWith(todayStr) == true
                val isUpcoming = order.status != com.yoga.firesafety.shared.domain.model.WorkOrderStatus.COMPLETED &&
                        order.status != com.yoga.firesafety.shared.domain.model.WorkOrderStatus.CANCELLED
                isToday && isUpcoming
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedDateLogs: StateFlow<List<TimeEntry>> = combine(timeLogs, _selectedLogDate) { logs, date ->
        val dateStr = date.toString()
        logs.filter { it.date == dateStr }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedDateTotalMinutes: StateFlow<Long> = combine(selectedDateLogs, activeEntry, _selectedLogDate) { logs, active, date ->
        val dateStr = date.toString()
        val completedMinutes = logs.filter { it.clockOutTime != null }.sumOf { it.durationMinutes }
        val activeMinutes = if (active != null && active.date == dateStr) {
            val durationMs = Clock.System.now().toEpochMilliseconds() - active.clockInTime
            if (durationMs > 0) durationMs / (1000 * 60) else 0L
        } else 0L
        completedMinutes + activeMinutes
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    init {
        viewModelScope.launch {
            val session = sessionRepository.getSession()
            if (session != null) {
                _userId.value = session.userId
            }
        }

        // Monitor activeEntry to handle live timer and today's total duration
        viewModelScope.launch {
            activeEntry.collect { entry ->
                if (entry != null && entry.clockOutTime == null) {
                    startTimer(entry.clockInTime)
                } else {
                    stopTimer()
                }
            }
        }

        viewModelScope.launch {
            combine(_userId.filterNotNull(), timeLogs, activeEntry) { userId, logs, active ->
                val todayStr = getTodayDateString(Clock.System.now().toEpochMilliseconds())
                val completedTodayMinutes = logs
                    .filter { it.date == todayStr && it.clockOutTime != null }
                    .sumOf { it.durationMinutes }

                val activeMinutes = if (active != null && active.date == todayStr) {
                    val durationMs = Clock.System.now().toEpochMilliseconds() - active.clockInTime
                    if (durationMs > 0) durationMs / (1000 * 60) else 0L
                } else 0L

                completedTodayMinutes + activeMinutes
            }.collect { totalMinutes ->
                _totalTodayMinutes.value = totalMinutes
            }
        }
    }

    fun setSubTab(index: Int) {
        _selectedSubTab.value = index
    }

    fun setType(type: TimeTrackType) {
        _selectedType.value = type
    }

    fun setSelectedWorkOrder(order: WorkOrder?) {
        _selectedWorkOrder.value = order
    }

    fun previousDay() {
        _selectedLogDate.value = _selectedLogDate.value.minus(DatePeriod(days = 1))
    }

    fun nextDay() {
        _selectedLogDate.value = _selectedLogDate.value.plus(DatePeriod(days = 1))
    }

    fun formatLogDateHeader(date: LocalDate): String {
        val dayOfWeek = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        val month = date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        return "$dayOfWeek, $month ${date.dayOfMonth}"
    }

    fun clockIn() {
        val userId = _userId.value ?: return
        viewModelScope.launch {
            val currentActive = timesheetRepository.getActiveEntry(userId)
            if (currentActive == null) {
                val order = _selectedWorkOrder.value
                val title = if (_selectedType.value == TimeTrackType.VISIT && order != null) {
                    "${order.buildingName} - #${order.id.takeLast(6)}"
                } else null

                timesheetRepository.clockIn(
                    userId = userId,
                    type = _selectedType.value,
                    workOrderId = order?.id,
                    workOrderTitle = title
                )
            }
        }
    }

    fun clockOut(notes: String? = null) {
        val entry = activeEntry.value ?: return
        val now = Clock.System.now().toEpochMilliseconds()
        viewModelScope.launch {
            timesheetRepository.clockOut(
                entryId = entry.id,
                clockOutTime = now,
                notes = notes
            )
            stopTimer()
        }
    }

    private fun startTimer(clockInTimeMs: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                val nowMs = Clock.System.now().toEpochMilliseconds()
                val diffSeconds = (nowMs - clockInTimeMs) / 1000
                _elapsedSeconds.value = if (diffSeconds >= 0) diffSeconds else 0L
                delay(1000)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _elapsedSeconds.value = 0L
    }

    private fun getTodayDateString(epochMs: Long): String {
        val instant = Instant.fromEpochMilliseconds(epochMs)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
    }

    fun formatSecondsToHHMMSS(seconds: Long): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return "${hrs.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }

    fun formatMinutesToHoursMinutes(minutes: Long): String {
        if (minutes <= 0) return "0m"
        val hrs = minutes / 60
        val mins = minutes % 60
        return when {
            hrs > 0 && mins > 0 -> "${hrs}h ${mins}m"
            hrs > 0 -> "${hrs}h"
            else -> "${mins}m"
        }
    }

    fun formatEpochToTime(epochMs: Long): String {
        val instant = Instant.fromEpochMilliseconds(epochMs)
        val ldt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = ldt.hour
        val minute = ldt.minute.toString().padStart(2, '0')
        val amPm = if (hour >= 12) "PM" else "AM"
        val formattedHour = if (hour % 12 == 0) 12 else hour % 12
        return "$formattedHour:$minute $amPm"
    }
}
