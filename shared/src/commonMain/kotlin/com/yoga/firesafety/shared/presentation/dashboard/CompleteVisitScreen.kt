package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteVisitScreen(
    orderId: String,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
    viewModel: WorkOrderViewModel = koinViewModel()
) {
    val workOrders by viewModel.workOrders.collectAsState()
    val order = workOrders.find { it.id == orderId }
    val today = remember { 
        Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date 
    }

    var notes by remember { mutableStateOf("") }
    var resolvedIssue by remember { mutableStateOf<Boolean?>(null) }
    var safetyFollowed by remember { mutableStateOf<Boolean?>(null) }
    var clientSatisfied by remember { mutableStateOf<Boolean?>(null) }
    
    var selectedMedia by remember { mutableStateOf(listOf<MediaItem>()) }
    var showSourceDialog by remember { mutableStateOf<MediaType?>(null) }
    
    val mediaPicker = rememberMediaPicker { newItems ->
        selectedMedia = selectedMedia + newItems
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Complete Visit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color(0xFF131A30)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF131A30))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF8F9FB),
                    titleContentColor = Color(0xFF131A30)
                )
            )
        },
        containerColor = Color(0xFFF8F9FB)
    ) { padding ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF3B82F6))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    "Job Report",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF10B981)
                )
                Text(
                    "Please answer these final questions for ${order.buildingName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF131A30).copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                CompletionQuestion(
                    question = "Was the issue fully resolved?",
                    selected = resolvedIssue,
                    onSelected = { resolvedIssue = it }
                )

                CompletionQuestion(
                    question = "Did you follow all safety protocols?",
                    selected = safetyFollowed,
                    onSelected = { safetyFollowed = it }
                )

                CompletionQuestion(
                    question = "Is the client satisfied with the work?",
                    selected = clientSatisfied,
                    onSelected = { clientSatisfied = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("Final Technician Notes", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = Color(0xFF131A30))
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    placeholder = { Text("Add any extra details here...", color = Color(0xFF131A30).copy(alpha = 0.2f)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color.Black.copy(alpha = 0.1f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color(0xFF131A30),
                        unfocusedTextColor = Color(0xFF131A30)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text("Photos & Videos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = Color(0xFF131A30))
                Spacer(modifier = Modifier.height(16.dp))

                if (showSourceDialog != null) {
                    AlertDialog(
                        onDismissRequest = { showSourceDialog = null },
                        title = { Text("Select Source", fontWeight = FontWeight.Bold) },
                        text = { Text("Choose how you want to add the ${showSourceDialog?.name?.lowercase()}") },
                        confirmButton = {
                            TextButton(onClick = { 
                                if (showSourceDialog == MediaType.PHOTO) mediaPicker.pickPhoto(true)
                                else mediaPicker.pickVideo(true)
                                showSourceDialog = null 
                            }) {
                                Text("Camera", fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { 
                                if (showSourceDialog == MediaType.PHOTO) mediaPicker.pickPhoto(false)
                                else mediaPicker.pickVideo(false)
                                showSourceDialog = null 
                            }) {
                                Text("Gallery")
                            }
                        }
                    )
                }
                
                Text("Media Attachments", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = Color(0xFF131A30))
                Spacer(modifier = Modifier.height(16.dp))

                // Horizontal list of selected media
                if (selectedMedia.isNotEmpty()) {
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(selectedMedia.size) { index ->
                            val item = selectedMedia[index]
                            Box(modifier = Modifier.padding(top = 8.dp)) {
                                Surface(
                                    modifier = Modifier.size(110.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.Black.copy(alpha = 0.05f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f)),
                                    shadowElevation = 2.dp
                                ) {
                                    if (item.thumbnail != null) {
                                        androidx.compose.foundation.Image(
                                            bitmap = item.thumbnail,
                                            contentDescription = null,
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                            Icon(
                                                if (item.type == MediaType.PHOTO) Icons.Default.Image else Icons.Default.Videocam,
                                                contentDescription = null,
                                                tint = Color.Black.copy(alpha = 0.2f),
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }
                                
                                // Remove button
                                IconButton(
                                    onClick = { selectedMedia = selectedMedia.filterIndexed { i, _ -> i != index } },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(28.dp)
                                        .offset(x = 10.dp, y = (-10).dp)
                                        .background(Color.White, CircleShape)
                                        .border(1.dp, Color.Black.copy(alpha = 0.1f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp), tint = Color.Red)
                                }
                                
                                // Media type indicator
                                Surface(
                                    modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
                                    color = Color.Black.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Icon(
                                        if (item.type == MediaType.PHOTO) Icons.Default.PhotoCamera else Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.padding(4.dp).size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MediaPlaceholderCard(
                        icon = Icons.Default.AddAPhoto, 
                        label = "Add Photo",
                        onClick = { showSourceDialog = MediaType.PHOTO }
                    )
                    MediaPlaceholderCard(
                        icon = Icons.Default.VideoCall, 
                        label = "Add Video",
                        onClick = { showSourceDialog = MediaType.VIDEO }
                    )
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
                    shadowElevation = 2.dp
                ) {
                    Button(
                        onClick = {
                            val questions = mapOf(
                                "Issue Resolved" to (resolvedIssue?.toString() ?: "N/A"),
                                "Safety Followed" to (safetyFollowed?.toString() ?: "N/A"),
                                "Client Satisfied" to (clientSatisfied?.toString() ?: "N/A")
                            )
                            viewModel.completeVisit(
                                orderId = orderId,
                                notes = notes,
                                photos = selectedMedia.filter { it.type == MediaType.PHOTO }.map { it.uri },
                                videos = selectedMedia.filter { it.type == MediaType.VIDEO }.map { it.uri },
                                questions = questions,
                                completedAt = today.toString()
                            )
                            onCompleted()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        enabled = resolvedIssue != null && safetyFollowed != null
                    ) {
                        Text("FINISH & SUBMIT", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun CompletionQuestion(
    question: String,
    selected: Boolean?,
    onSelected: (Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(question, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF131A30))
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(
                selected = selected == true,
                onClick = { onSelected(true) },
                label = { Text("Yes", fontWeight = FontWeight.Bold) },
                leadingIcon = if (selected == true) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF10B981).copy(alpha = 0.1f),
                    selectedLabelColor = Color(0xFF10B981)
                )
            )
            FilterChip(
                selected = selected == false,
                onClick = { onSelected(false) },
                label = { Text("No", fontWeight = FontWeight.Bold) },
                leadingIcon = if (selected == false) {
                    { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFF4B66).copy(alpha = 0.1f),
                    selectedLabelColor = Color(0xFFFF4B66)
                )
            )
        }
    }
}

@Composable
fun MediaPlaceholderCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f)),
        shadowElevation = 1.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF3B82F6).copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = Color(0xFF131A30).copy(alpha = 0.4f))
        }
    }
}
