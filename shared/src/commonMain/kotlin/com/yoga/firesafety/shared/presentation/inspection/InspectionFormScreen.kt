package com.yoga.firesafety.shared.presentation.inspection

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
                title = { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("SAVE REPORT", fontWeight = FontWeight.Bold)
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
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Last updated", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("Just now", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Status", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("In Progress", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("General Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            
            var techName by remember { mutableStateOf("Vikram Singh") }
            FormField("Technician Name", techName) { techName = it }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Deficiency Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Fire Alarm Test", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            
            DropdownField("Month of Inspection")
            DropdownField("AC Power Light On Upon Arrival?")
            DropdownField("System Normal Upon Arrival?")
            
            var condition by remember { mutableStateOf("") }
            var deviceLoc by remember { mutableStateOf("") }
            
            FormField("If No, Explain Condition", condition) { condition = it }
            FormField("Alarm Initiating Device Location", deviceLoc) { deviceLoc = it }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Hose & Lighting", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Fire hose stations checked?", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            DropdownField("Select Option")
            var hoseReason by remember { mutableStateOf("") }
            FormField("If no, specify reason", hoseReason) { hoseReason = it }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text("Emergency lighting tested?", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            DropdownField("Select Option")
            var lightReason by remember { mutableStateOf("") }
            FormField("If no, specify reason", lightReason) { lightReason = it }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Verification", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            
            var inspectorName by remember { mutableStateOf("") }
            var comments by remember { mutableStateOf("") }
            
            FormField("Inspector Name(s)", inspectorName) { inspectorName = it }
            FormField("Additional Comments", comments) { comments = it }
        }
    }
}

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color(0xFFF8FAFC)
            )
        )
    }
}

@Composable
fun DropdownField(label: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF8FAFC)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Select...", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
            }
        }
    }
}
