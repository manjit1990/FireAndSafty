package com.yoga.firesafety.shared.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yoga.firesafety.shared.domain.model.Role
import com.yoga.firesafety.shared.presentation.auth.LoginScreen
import com.yoga.firesafety.shared.presentation.auth.SignupScreen
import com.yoga.firesafety.shared.presentation.dashboard.WorkOrderListScreen
import com.yoga.firesafety.shared.presentation.dashboard.WorkOrderDetailsScreen
import com.yoga.firesafety.shared.presentation.dashboard.WorkOrderViewModel
import com.yoga.firesafety.shared.presentation.inspection.InspectionFormScreen
import com.yoga.firesafety.shared.presentation.admin.AdminDashboardScreen
import com.yoga.firesafety.shared.presentation.admin.CreateWorkOrderScreen
import com.yoga.firesafety.shared.presentation.admin.ScheduleWorkOrderScreen
import com.yoga.firesafety.shared.presentation.admin.UserManagementScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainApp(viewModel: MainViewModel = koinViewModel()) {
    val authState by viewModel.authState.collectAsState()

    when (val state = authState) {
        is AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is AuthState.Unauthenticated -> {
            MainNavigation("login", viewModel)
        }
        is AuthState.Authenticated -> {
            val startDest = if (state.session.role == Role.ADMIN) "admin_dashboard" else "dashboard"
            MainNavigation(startDest, viewModel)
        }
    }
}

@Composable
fun MainNavigation(
    startDestination: String,
    mainViewModel: MainViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = if (role == Role.ADMIN) "admin_dashboard" else "dashboard"
                    navController.navigate(destination) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onCreateAccountClick = {
                    navController.navigate("signup")
                }
            )
        }
        composable("signup") {
            SignupScreen(
                onSignupSuccess = { role ->
                    val destination = if (role == Role.ADMIN) "admin_dashboard" else "dashboard"
                    navController.navigate(destination) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable("dashboard") {
            val viewModel: WorkOrderViewModel = koinViewModel()
            WorkOrderListScreen(
                onWorkOrderClick = { order ->
                    navController.navigate("work_order_details/${order.id}")
                },
                onLogout = { 
                    mainViewModel.logout()
                },
                viewModel = viewModel
            )
        }
        composable("admin_dashboard") {
            val viewModel: WorkOrderViewModel = koinViewModel()
            AdminDashboardScreen(
                onLogout = { 
                    mainViewModel.logout()
                },
                onCreateOrderClick = { navController.navigate("create_work_order") },
                onManageUsersClick = { navController.navigate("user_management") },
                onWorkOrderClick = { orderId -> 
                    navController.navigate("schedule_work_order/$orderId")
                },
                viewModel = viewModel
            )
        }
        composable("user_management") {
            UserManagementScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("create_work_order") {
            val viewModel: WorkOrderViewModel = koinViewModel()
            CreateWorkOrderScreen(
                onBackClick = { navController.popBackStack() },
                onOrderCreated = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable(
            "schedule_work_order/{orderId}",
            arguments = listOf(navArgument("orderId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            ScheduleWorkOrderScreen(
                orderId = orderId,
                onBackClick = { navController.popBackStack() },
                onAssigned = { navController.popBackStack() }
            )
        }
        composable(
            "work_order_details/{orderId}",
            arguments = listOf(navArgument("orderId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            val viewModel: WorkOrderViewModel = koinViewModel()
            val workOrders by viewModel.workOrders.collectAsState()
            val order = workOrders.find { it.id == orderId }
            
            if (order != null) {
                WorkOrderDetailsScreen(
                    order = order,
                    onBackClick = { navController.popBackStack() },
                    onStartVisit = { /* Logic to start visit */ },
                    onCompleteVisit = { /* Logic to complete visit */ },
                    onChecklistClick = { type: String ->
                        navController.navigate("inspection_form/$type")
                    }
                )
            }
        }
        composable(
            "inspection_form/{formType}",
            arguments = listOf(navArgument("formType") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val formType = backStackEntry.arguments?.getString("formType") ?: "inspection"
            InspectionFormScreen(
                formType = formType,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }
    }
}
