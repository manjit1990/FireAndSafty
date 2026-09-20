package com.yoga.firesafety.shared.presentation.inspection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionFormScreen(
    formType: String,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val title = if (formType == "deficiency") "Monthly Deficiency" else "Monthly Fire Alarm"
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = title, 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF131A30),
                        fontSize = 17.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF131A30))
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color(0xFF131A30))
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Status Header Info
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Last updated", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        Text("-", style = MaterialTheme.typography.bodySmall, color = Color(0xFF131A30), fontWeight = FontWeight.Black)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Sent on", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        Text("-", style = MaterialTheme.typography.bodySmall, color = Color(0xFF131A30), fontWeight = FontWeight.Black)
                    }
                }
            }
            
            HorizontalDivider(color = Color.Black.copy(alpha = 0.05f))
            
            if (formType == "deficiency") {
                DeficiencyForm()
            } else {
                MonthlyInspectionForm()
            }

            // Scrollable Save Button Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Save", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
                
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.03f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = Color(0xFF131A30).copy(alpha = 0.4f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun MonthlyInspectionForm() {
    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Monthly Fire Alarm Test and Inspection", 
            style = MaterialTheme.typography.titleMedium, 
            color = Color(0xFF131A30), 
            fontWeight = FontWeight.Black
        )
        
        var selectedMonth by remember { mutableStateOf("") }
        DropdownField(
            label = "Month of Inspection:",
            selectedValue = selectedMonth,
            options = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"),
            onOptionSelected = { selectedMonth = it }
        )
        
        var acPower by remember { mutableStateOf("") }
        DropdownField(
            label = "AC Power Light On Upon Arrival?",
            selectedValue = acPower,
            options = listOf("Yes", "No", "N/A"),
            onOptionSelected = { acPower = it }
        )
        
        var systemNormal by remember { mutableStateOf("") }
        DropdownField(
            label = "System Normal Upon Arrival?",
            selectedValue = systemNormal,
            options = listOf("Yes", "No", "N/A"),
            onOptionSelected = { systemNormal = it }
        )
        
        var condition by remember { mutableStateOf("") }
        FormField("If No, Explain Condition of system", condition) { condition = it }
        
        var alarmDeviceLoc by remember { mutableStateOf("") }
        FormField("Location of Alarm Initiating Device Tested:", alarmDeviceLoc) { alarmDeviceLoc = it }
        
        var deviceFunctioned by remember { mutableStateOf("") }
        DropdownField(
            label = "Tested device functioned correctly?",
            selectedValue = deviceFunctioned,
            options = listOf("Yes", "No", "N/A"),
            onOptionSelected = { deviceFunctioned = it }
        )
        
        var audibleSignal by remember { mutableStateOf("") }
        DropdownField(
            label = "Operation of common audible signal confirmed:",
            selectedValue = audibleSignal,
            options = listOf("Yes", "No", "N/A"),
            onOptionSelected = { audibleSignal = it }
        )
        
        var standbyPower by remember { mutableStateOf("") }
        DropdownField(
            label = "Inspection completed on standby power?",
            selectedValue = standbyPower,
            options = listOf("Yes", "No", "N/A"),
            onOptionSelected = { standbyPower = it }
        )
        
        var voiceTested by remember { mutableStateOf("") }
        DropdownField(
            label = "Voice communication system tested?",
            selectedValue = voiceTested,
            options = listOf("Yes", "No", "N/A"),
            onOptionSelected = { voiceTested = it }
        )
        
        var phoneLoc by remember { mutableStateOf("") }
        FormField("Location of telephone tested:", phoneLoc) { phoneLoc = it }
        
        var facpClear by remember { mutableStateOf("") }
        DropdownField(
            label = "FACP Clear Upon Departure:",
            selectedValue = facpClear,
            options = listOf("Yes", "No", "N/A"),
            onOptionSelected = { facpClear = it }
        )
        
        var sprinklerTested by remember { mutableStateOf("") }
        DropdownField(
            label = "Was the sprinkler system alarm tested?",
            selectedValue = sprinklerTested,
            options = listOf("Yes", "No", "N/A"),
            onOptionSelected = { sprinklerTested = it }
        )
        
        var sprinklerReason by remember { mutableStateOf("") }
        FormField("If no, list reason why:", sprinklerReason) { sprinklerReason = it }
        
        var valvesInspected by remember { mutableStateOf("") }
        DropdownField(
            label = "Were all sprinkler control valves inspected?",
            selectedValue = valvesInspected,
            options = listOf("Yes", "No", "N/A"),
            onOptionSelected = { valvesInspected = it }
        )
        
        var valvesReason by remember { mutableStateOf("") }
        FormField("If no, list locations not checked and reason why:", valvesReason) { valvesReason = it }
        
        var extinguishersChecked by remember { mutableStateOf("") }
        DropdownField(
            label = "Were all fire extinguishers on site checked during inspection?",
            selectedValue = extinguishersChecked,
            options = listOf("Yes", "No", "N/A"),
            onOptionSelected = { extinguishersChecked = it }
        )
        
        var extinguishersReason by remember { mutableStateOf("") }
        FormField("If no, list locations not checked and reason why:", extinguishersReason) { extinguishersReason = it }
        
        var hoseChecked by remember { mutableStateOf("") }
        DropdownField(
            label = "Were all fire hose stations ( cabinets) checked during inspection?",
            selectedValue = hoseChecked,
            options = listOf("Yes", "No", "N/A"),
            onOptionSelected = { hoseChecked = it }
        )
        
        var hoseReason by remember { mutableStateOf("") }
        FormField("If no, list locations not checked and reason why:", hoseReason) { hoseReason = it }
        
        var emergencyTested by remember { mutableStateOf("") }
        DropdownField(
            label = "Were all emergency lighting units tested for transfer to battery power?",
            selectedValue = emergencyTested,
            options = listOf("Yes", "No", "N/A"),
            onOptionSelected = { emergencyTested = it }
        )
        
        var emergencyReason by remember { mutableStateOf("") }
        FormField("If no, list locations not tested and reason why:", emergencyReason) { emergencyReason = it }
        
        var inspectorName by remember { mutableStateOf("") }
        FormField("Name(s) of inspector(s):", inspectorName) { inspectorName = it }
        
        var comments by remember { mutableStateOf("") }
        FormField("COMMENTS", comments, isMultiline = true) { comments = it }
    }
}

@Composable
fun DeficiencyForm() {
    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Fire Alarm Deficiency", style = MaterialTheme.typography.titleMedium, color = Color(0xFF131A30), fontWeight = FontWeight.Black)
        
        var techName by remember { mutableStateOf("") }
        FormField("Technician Name", techName) { techName = it }
        
        var fireDef by remember { mutableStateOf("") }
        FormField("Fire Alarm Deficiency", fireDef) { fireDef = it }
        
        var sprinklerDef by remember { mutableStateOf("") }
        FormField("Sprinkler Deficiency", sprinklerDef) { sprinklerDef = it }
        
        var extDef by remember { mutableStateOf("") }
        FormField("Fire Extinguishers & Hoses Deficiency", extDef) { extDef = it }
        
        var lightDef by remember { mutableStateOf("") }
        FormField("Emergency Light Deficiency", lightDef) { lightDef = it }
    }
}

@Composable
fun FormField(label: String, value: String, isMultiline: Boolean = false, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label, 
            style = MaterialTheme.typography.labelMedium, 
            color = Color(0xFF131A30).copy(alpha = 0.6f), 
            fontWeight = FontWeight.Bold, 
            modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().then(if (isMultiline) Modifier.height(120.dp) else Modifier),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3B82F6),
                unfocusedBorderColor = Color.Black.copy(alpha = 0.1f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color(0xFF131A30),
                unfocusedTextColor = Color(0xFF131A30)
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            singleLine = !isMultiline
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

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label, 
            style = MaterialTheme.typography.labelMedium, 
            color = Color(0xFF131A30).copy(alpha = 0.6f), 
            fontWeight = FontWeight.Bold, 
            modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
        )
        Box {
            Surface(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedValue.isEmpty()) "Select..." else selectedValue,
                        color = if (selectedValue.isEmpty()) Color(0xFF131A30).copy(alpha = 0.4f) else Color(0xFF131A30),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF131A30).copy(alpha = 0.4f))
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f).background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF131A30)) },
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
