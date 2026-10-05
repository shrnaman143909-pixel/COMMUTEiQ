package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CrowdLevel
import com.example.model.DelayRisk
import com.example.model.JourneyFeedback
import com.example.model.RouteOption
import com.example.model.TransportType
import com.example.model.UserProfile
import com.example.ui.components.CrowdBadge
import com.example.ui.components.DelayRiskBadge
import com.example.ui.components.TransportModeIcon
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SlateBorderLight
import com.example.ui.theme.SuccessGreen

@Composable
fun AdminScreen(
    users: List<UserProfile>,
    routes: List<RouteOption>,
    feedbackList: List<JourneyFeedback>,
    auditLogs: List<com.example.model.AdminAuditLog> = emptyList(),
    onAddRoute: (title: String, transport: TransportType, duration: Int, cost: Int, walking: Int, crowd: CrowdLevel, delay: DelayRisk) -> Unit,
    onDeleteRoute: (String) -> Unit,
    onUpdateDelay: (routeId: String, newDelay: DelayRisk, newCrowd: CrowdLevel) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Routes, 1: Feedback, 2: Users & Stats, 3: Audit Logs
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddRouteDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, transport, duration, cost, walking, crowd, delay ->
                onAddRoute(title, transport, duration, cost, walking, crowd, delay)
                showAddDialog = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp).testTag("admin_back_btn")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Transit Control & Admin",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Operator dashboard & route fleet management",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFFCCFBF1), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("ADMIN ROLE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F766E))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // High-level Metrics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminMetricBox("ACTIVE ROUTES", "${routes.size}", PrimaryBlue, Modifier.weight(1f))
            AdminMetricBox("USERS", "${users.size}", SecondaryTeal, Modifier.weight(1f))
            AdminMetricBox("FEEDBACKS", "${feedbackList.size}", Color(0xFFB45309), Modifier.weight(1f))
            AdminMetricBox("AI HEALTH", "99.4%", SuccessGreen, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tab Navigation
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = PrimaryBlue,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SlateBorderLight, RoundedCornerShape(8.dp))
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Routes", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Feedback", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Users", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Audit (${auditLogs.size})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) })
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                // MANAGE ROUTES
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("MANAGED TRANSIT CORRIDORS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), letterSpacing = 1.sp)
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(36.dp).testTag("admin_add_route_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Route", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                routes.forEach { r ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, SlateBorderLight, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TransportModeIcon(type = r.primaryTransport, size = 32)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(r.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Text("${r.durationMinutes} min • ₹${r.costInr} • ${r.punctualityRate}% on-time", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }

                            IconButton(
                                onClick = { onDeleteRoute(r.id) },
                                modifier = Modifier.size(32.dp).testTag("admin_del_${r.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CrowdBadge(level = r.crowdLevel, modifier = Modifier.weight(1f))
                            DelayRiskBadge(risk = r.delayRisk, modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Toggle Delay
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    val nextRisk = when (r.delayRisk) {
                                        DelayRisk.LOW -> DelayRisk.MEDIUM
                                        DelayRisk.MEDIUM -> DelayRisk.HIGH
                                        DelayRisk.HIGH -> DelayRisk.LOW
                                    }
                                    onUpdateDelay(r.id, nextRisk, r.crowdLevel)
                                }
                            ) {
                                Text("Toggle Delay Status", fontSize = 11.sp, color = PrimaryBlue)
                            }
                        }
                    }
                }
            }

            1 -> {
                // FEEDBACK LOGS
                Text("COMMUTER EXPERIENCES & RATINGS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(10.dp))

                feedbackList.forEach { fb ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, SlateBorderLight, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(fb.routeTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text("⭐ ${fb.rating}/5", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Crowd: ${fb.wasCrowdHigher} • Time Accuracy: ${fb.wasTravelTimeAccurate} • Comfort: ${fb.wasComfortable}",
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                        if (fb.comments.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Text("\"${fb.comments}\"", fontSize = 12.sp, color = Color(0xFF1E293B))
                            }
                        }
                    }
                }
            }

            2 -> {
                // USERS & ANONYMIZED STATS
                Text("REGISTERED COMMUTERS (ANONYMIZED)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(10.dp))

                users.forEach { u ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(1.dp, SlateBorderLight, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(32.dp).background(Color(0xFFDBEAFE), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(u.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Text("${u.homeLocation} ➔ ${u.workLocation}", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }

                        Text(
                            text = u.role.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (u.role == com.example.model.UserRole.ADMIN) SecondaryTeal else PrimaryBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, SlateBorderLight, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text("System Transit Health", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Gemini Recommendation Latency: 420ms avg", fontSize = 12.sp, color = Color(0xFF475569))
                    Text("• Simulated Sensor Telemetry: 100% operational", fontSize = 12.sp, color = Color(0xFF475569))
                    Text("• Peak Congestion Hour: 08:35 AM - 09:15 AM", fontSize = 12.sp, color = Color(0xFF475569))
                    Text("• Student Passenger Share: 68%", fontSize = 12.sp, color = Color(0xFF475569))
                }
            }

            3 -> {
                // AUDIT LOGS (Feature 6: Auditability)
                Text("ADMINISTRATIVE AUDIT LOGS (RLS ENFORCED)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(10.dp))

                if (auditLogs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No audit log entries recorded yet.", color = Color(0xFF64748B), fontSize = 13.sp)
                    }
                } else {
                    auditLogs.forEach { log ->
                        val actionColor = when {
                            log.action.contains("CREATE") -> SuccessGreen
                            log.action.contains("DELETE") -> DangerRed
                            else -> Color(0xFF0284C7)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .border(1.dp, SlateBorderLight, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(actionColor.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(log.action, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = actionColor)
                                }
                                Text("ID: ${log.id}", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(log.details, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0F172A))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("By: ${log.adminEmail} • Target: ${log.targetId}", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMetricBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.dp, SlateBorderLight, RoundedCornerShape(10.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun AddRouteDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, transport: TransportType, duration: Int, cost: Int, walking: Int, crowd: CrowdLevel, delay: DelayRisk) -> Unit
) {
    var title by remember { mutableStateOf("New Campus Metro Express") }
    var transport by remember { mutableStateOf(TransportType.METRO) }
    var durationText by remember { mutableStateOf("25") }
    var costText by remember { mutableStateOf("30") }
    var walkingText by remember { mutableStateOf("350") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Demo Transit Route", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Route Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it },
                    label = { Text("Cost (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = walkingText,
                    onValueChange = { walkingText = it },
                    label = { Text("Walking Distance (meters)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val duration = durationText.toIntOrNull() ?: 25
                    val cost = costText.toIntOrNull() ?: 30
                    val walking = walkingText.toIntOrNull() ?: 350
                    onConfirm(title, transport, duration, cost, walking, CrowdLevel.MODERATE, DelayRisk.LOW)
                }
            ) {
                Text("Publish Route")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
