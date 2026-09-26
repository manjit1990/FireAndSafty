package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onLogout: () -> Unit,
    onProfileClick: () -> Unit = {},
    timesheetViewModel: TimesheetViewModel? = null
) {
    val scrollState = rememberScrollState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    val activeEntry = timesheetViewModel?.activeEntry?.collectAsState()?.value
    val elapsedSeconds = timesheetViewModel?.elapsedSeconds?.collectAsState()?.value ?: 0L
    val isOnline = activeEntry != null && activeEntry.clockOutTime == null

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = "More",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = Color(0xFF131A30),
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Company Name
        Text(
            text = "Torbram Fire Protection",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF131A30).copy(alpha = 0.5f),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Status Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF6F5F2),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF131A30)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) Color(0xFF2E7D32) else Color(0xFF94A3B8))
                        )
                        Text(
                            text = if (isOnline) "Online" else "Offline",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) Color(0xFF2E7D32) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.Black.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Up to date",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF131A30),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Online for",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = if (isOnline) timesheetViewModel.formatSecondsToHHMMSS(elapsedSeconds) else "00:00:00",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF131A30)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Navigation Items
        MoreNavItem(
            icon = Icons.Default.GridView,
            label = "Apps & integrations",
            onClick = {}
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.Black.copy(alpha = 0.05f))
        Spacer(modifier = Modifier.height(8.dp))

        MoreNavItem(
            icon = Icons.Default.ChatBubbleOutline,
            label = "Support",
            onClick = {}
        )
        MoreNavItem(
            icon = Icons.Default.AutoAwesome,
            label = "Product updates",
            onClick = {}
        )
        MoreNavItem(
            icon = Icons.Default.Redeem,
            label = "Refer a friend",
            onClick = {}
        )
        MoreNavItem(
            icon = Icons.Default.HelpOutline,
            label = "About",
            onClick = {}
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.Black.copy(alpha = 0.05f))
        Spacer(modifier = Modifier.height(8.dp))

        MoreNavItem(
            icon = Icons.Default.AccountCircle,
            label = "Profile",
            onClick = onProfileClick
        )
        MoreNavItem(
            icon = Icons.Default.Tune,
            label = "Preferences",
            onClick = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        MoreNavItem(
            icon = Icons.Default.Logout,
            label = "Logout",
            iconColor = Color(0xFFE53935),
            labelColor = Color(0xFFE53935),
            onClick = { showLogoutDialog = true }
        )

        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
fun MoreNavItem(
    icon: ImageVector,
    label: String,
    iconColor: Color = Color(0xFF131A30).copy(alpha = 0.7f),
    labelColor: Color = Color(0xFF131A30),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = labelColor,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}
