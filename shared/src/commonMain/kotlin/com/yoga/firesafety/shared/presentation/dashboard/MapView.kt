package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.yoga.firesafety.shared.domain.model.WorkOrder

@Composable
expect fun MapView(modifier: Modifier, workOrders: List<WorkOrder>)
