package com.yoga.firesafety.shared.presentation.inspection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionFormScreen(
    formType: String,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val title = if (formType == "deficiency") "Deficiency Report" else "Monthly Inspection"
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.weight(1f).height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("SAVE REPORT", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Status Info
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Version", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("v2.4.10", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Status", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "IN PROGRESS", 
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall, 
                                color = MaterialTheme.colorScheme.primary, 
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
            
            Column(modifier = Modifier.padding(20.dp)) {
                if (formType == "deficiency") {
                    DeficiencyForm()
                } else {
                    MonthlyInspectionForm()
                }
            }
        }
    }
}

@Composable
fun DeficiencyForm() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("General Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(20.dp))
            
            var techName by remember { mutableStateOf("Vikram Singh") }
            FormField("Technician Name", techName) { techName = it }
            
            Spacer(modifier = Modifier.height(32.dp))
            Text("Deficiency Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(20.dp))

            var fireDef by remember { mutableStateOf("") }
            var sprinklerDef by remember { mutableStateOf("") }
            var extDef by remember { mutableStateOf("") }
            var lightDef by remember { mutableStateOf("") }

            FormField("Fire Alarm Deficiency", fireDef) { fireDef = it }
            FormField("Sprinkler Deficiency", sprinklerDef) { sprinklerDef = it }
            FormField("Fire Extinguishers & Hoses Deficiency", extDef) { extDef = it }
            FormField("Emergency Light Deficiency", lightDef) { lightDef = it }
        }
    }
}

@Composable
fun MonthlyInspectionForm() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Fire Alarm System", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(20.dp))
            
            var selectedMonth by remember { mutableStateOf("") }
            DropdownField(
                label = "Inspection Month",
                selectedValue = selectedMonth,
                options = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"),
                onOptionSelected = { selectedMonth = it }
            )
            
            var acPower by remember { mutableStateOf("") }
            DropdownField(
                label = "AC Power Light On Arrival?",
                selectedValue = acPower,
                options = listOf("Yes", "No"),
                onOptionSelected = { acPower = it }
            )
            
            var systemNormal by remember { mutableStateOf("") }
            DropdownField(
                label = "System Normal On Arrival?",
                selectedValue = systemNormal,
                options = listOf("Yes", "No"),
                onOptionSelected = { systemNormal = it }
            )
            
            var condition by remember { mutableStateOf("") }
            var deviceLoc by remember { mutableStateOf("") }
            
            FormField("Condition Explanation", condition) { condition = it }
            FormField("Initiating Device Location", deviceLoc) { deviceLoc = it }
            
            Spacer(modifier = Modifier.height(32.dp))
            Text("Equipment Status", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(20.dp))
            
            Text("Fire hose stations checked?", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            var hoseStation by remember { mutableStateOf("") }
            DropdownField(
                label = "Select Status",
                selectedValue = hoseStation,
                options = listOf("Yes", "No"),
                onOptionSelected = { hoseStation = it }
            )
            var hoseReason by remember { mutableStateOf("") }
            FormField("Reason (if no)", hoseReason) { hoseReason = it }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Emergency lighting tested?", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            var emergencyLight by remember { mutableStateOf("") }
            DropdownField(
                label = "Select Status",
                selectedValue = emergencyLight,
                options = listOf("Yes", "No"),
                onOptionSelected = { emergencyLight = it }
            )
            var lightReason by remember { mutableStateOf("") }
            FormField("Reason (if no)", lightReason) { lightReason = it }
            
            Spacer(modifier = Modifier.height(32.dp))
            Text("Compliance Verification", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(20.dp))
            
            var inspectorName by remember { mutableStateOf("") }
            var comments by remember { mutableStateOf("") }
            
            FormField("Inspector Full Name", inspectorName) { inspectorName = it }
            FormField("Final Observation Comments", comments) { comments = it }
        }
    }
}

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun DropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        Box {
            OutlinedCard(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedValue.isEmpty()) "Choose Option" else selectedValue,
                        color = if (selectedValue.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (selectedValue.isEmpty()) FontWeight.Normal else FontWeight.Medium
                    )
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.85f).background(MaterialTheme.colorScheme.surface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
