package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveJourney
import com.example.ui.components.CrowdBadge
import com.example.ui.components.RouteCanvasMap
import com.example.ui.components.RouteSegmentStep
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SlateBorderLight
import com.example.ui.theme.SuccessGreen

@Composable
fun JourneyScreen(
    journey: ActiveJourney?,
    onProgressStep: () -> Unit,
    onEndJourney: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (journey == null) {
        Box(
            modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No active journey found.", color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBack) {
                    Text("Return to Dashboard")
                }
            }
        }
        return
    }

    val route = journey.route
    val currentWaypoint = route.waypoints.getOrNull(journey.currentWaypointIndex)
    val nextWaypoint = route.waypoints.getOrNull(journey.currentWaypointIndex + 1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // Top Nav Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp).testTag("journey_back_btn")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Live Journey Mode",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFFDCFCE7), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(SuccessGreen, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("LIVE NAVIGATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Map Canvas with live pulsing commuter beacon
        RouteCanvasMap(
            route = route,
            activeWaypointIndex = journey.currentWaypointIndex,
            showSimulatedGps = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Live ETA and Progress Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, SlateBorderLight, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("ESTIMATED ARRIVAL (ETA)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    Text(
                        text = if (journey.isCompleted) "Arrived!" else "${journey.etaMinutes} mins remaining",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (journey.isCompleted) SuccessGreen else PrimaryBlue
                    )
                }

                CrowdBadge(level = journey.currentCrowd)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Journey Progress", fontSize = 11.sp, color = Color(0xFF64748B))
                Text("${journey.progressPercent.toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (journey.progressPercent / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = PrimaryBlue,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Real-time Telemetry & Delay Status
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                    .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (journey.isCompleted) Icons.Default.CheckCircle else Icons.Default.Schedule,
                        contentDescription = null,
                        tint = if (journey.isCompleted) SuccessGreen else PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = journey.delayStatus,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Current & Next Stop
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text("CURRENT WAYPOINT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    Text(currentWaypoint?.name ?: "In Transit", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text("NEXT TRANSIT STOP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    Text(nextWaypoint?.name ?: "Destination Reached", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simulation Controls
        Text(
            text = "PROTOTYPE SENSOR / SIMULATION CONTROLS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onProgressStep,
                modifier = Modifier.weight(1f).height(46.dp).testTag("simulate_next_stop_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simulate Next Stop", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = onEndJourney,
                modifier = Modifier.weight(1f).height(46.dp).testTag("end_journey_btn"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp), tint = DangerRed)
                Spacer(modifier = Modifier.width(6.dp))
                Text("End Journey", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DangerRed)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Route Steps Details
        Text(
            text = "ROUTE STEPS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, SlateBorderLight, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            route.segments.forEachIndexed { idx, segment ->
                RouteSegmentStep(
                    stepNumber = idx + 1,
                    segment = segment,
                    isLast = idx == route.segments.size - 1
                )
            }
        }
    }
}
