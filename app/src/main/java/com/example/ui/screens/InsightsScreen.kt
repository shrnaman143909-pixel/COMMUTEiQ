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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CommuteFingerprint
import com.example.model.CommuteInsights
import com.example.model.UserProfile
import com.example.ui.components.CommuteScoreBadge
import com.example.ui.components.TransportModeIcon
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueDark
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SlateBorderLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun InsightsScreen(
    user: UserProfile,
    insights: CommuteInsights,
    onEditFingerprint: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp).testTag("insights_back_btn")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Commute Insights & Fingerprint",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Behavioral telemetry & learned transit habits",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Commute Fingerprint Visual Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(1.5.dp, PrimaryBlue, RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFDBEAFE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Commute Fingerprint", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text("User Profile: ${user.name}", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                }

                CommuteScoreBadge(score = user.fingerprint.adaptabilityScore, size = 48.dp, strokeWidth = 4.dp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Learned Weights Breakdown
            PriorityMeter("Time Sensitivity", user.fingerprint.timePriority, PrimaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            PriorityMeter("Cost Sensitivity", user.fingerprint.costPriority, SuccessGreen)
            Spacer(modifier = Modifier.height(8.dp))
            PriorityMeter("Walking Comfort", user.fingerprint.walkingPriority, SecondaryTeal)

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("PREFERRED MODE", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                    Text(user.fingerprint.preferredTransport.displayName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }
                Column {
                    Text("DAILY BUDGET", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                    Text("₹${user.fingerprint.dailyBudgetInr}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                }
                Column {
                    Text("MAX WALKING", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                    Text("${user.fingerprint.maxWalkingDistanceMeters}m", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onEditFingerprint,
                modifier = Modifier.align(Alignment.End).testTag("insights_edit_fingerprint_btn")
            ) {
                Text("Recalibrate Fingerprint", fontSize = 12.sp, color = PrimaryBlue)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Commute Insights Metrics
        Text(
            text = "TRANSIT TELEMETRY & BEHAVIOR",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InsightCard(
                title = "Average Duration",
                value = "${insights.avgCommuteMinutes} min",
                subtitle = "Across all regular routes",
                icon = Icons.Default.Schedule,
                accentColor = PrimaryBlue,
                modifier = Modifier.weight(1f)
            )
            InsightCard(
                title = "Average Spending",
                value = "₹${insights.avgSpendingInr} / day",
                subtitle = "Under daily budget cap",
                icon = Icons.Default.Analytics,
                accentColor = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InsightCard(
                title = "Most Used Mode",
                value = insights.mostUsedTransport,
                subtitle = "84% punctuality score",
                icon = Icons.Default.DirectionsBus,
                accentColor = SecondaryTeal,
                modifier = Modifier.weight(1f)
            )
            InsightCard(
                title = "CO2 Emissions Saved",
                value = "${insights.co2SavedKg} kg",
                subtitle = "Via EV & Metro routes",
                icon = Icons.Default.Eco,
                accentColor = Color(0xFF059669),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Frequent Delay Pattern & Reliability
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, SlateBorderLight, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Most Reliable Route Corridor", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text(insights.mostReliableRoute, fontSize = 12.sp, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Frequent Delay Pattern", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text(insights.frequentDelayPattern, fontSize = 12.sp, color = Color(0xFFB45309))
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Weekly Commute Summary
        Text(
            text = "WEEKLY COMMUTE SUMMARY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, SlateBorderLight, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                insights.weeklyTrips.forEach { trip ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(trip.dayLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                        Spacer(modifier = Modifier.height(6.dp))

                        // Visual bar
                        val barHeight = ((trip.minutes / 50f) * 60f).coerceIn(20f, 60f)
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(barHeight.dp)
                                .background(if (trip.onTime) PrimaryBlue else WarningAmber, RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${trip.minutes}m", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                        Text("₹${trip.costInr}", fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                }
            }
        }
    }
}

@Composable
private fun PriorityMeter(label: String, value: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
            Text("${(value * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun InsightCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, SlateBorderLight, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(2.dp))
        Text(subtitle, fontSize = 10.sp, color = Color(0xFF64748B))
    }
}
