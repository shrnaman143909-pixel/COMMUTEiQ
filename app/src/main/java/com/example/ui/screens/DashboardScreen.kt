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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.model.CommuteInsights
import com.example.model.JourneyFeedback
import com.example.model.RouteOption
import com.example.model.SavedRoute
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.components.CategoryBadge
import com.example.ui.components.CommuteScoreBadge
import com.example.ui.components.TransportModeIcon
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueDark
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SlateBorderLight
import com.example.ui.theme.SuccessGreen

@Composable
fun DashboardScreen(
    user: UserProfile,
    topRoute: RouteOption?,
    savedRoutes: List<SavedRoute>,
    recentFeedbacks: List<JourneyFeedback>,
    insights: CommuteInsights,
    onPlanCommuteClick: () -> Unit,
    onEditFingerprintClick: () -> Unit,
    onStartJourneyClick: (RouteOption) -> Unit,
    onViewInsightsClick: () -> Unit,
    onAdminPanelClick: () -> Unit,
    onLogoutClick: () -> Unit,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello, ${user.name} 👋",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "${user.homeLocation} ➔ ${user.workLocation}",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (user.role == UserRole.ADMIN) {
                    IconButton(
                        onClick = onAdminPanelClick,
                        modifier = Modifier.size(36.dp).testTag("dash_admin_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Panel",
                            tint = Color(0xFF0F766E)
                        )
                    }
                }
                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.size(36.dp).testTag("dash_logout_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        tint = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Plan Commute Button
        Button(
            onClick = onPlanCommuteClick,
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("dash_quick_plan_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Plan Commute / Compare Routes", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Today's Commute Highlight Card
        if (topRoute != null) {
            Text(
                text = "TODAY'S RECOMMENDED COMMUTE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.dp, SlateBorderLight, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryBadge(category = topRoute.category)
                    CommuteScoreBadge(score = topRoute.commuteScore, size = 46.dp, strokeWidth = 4.dp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = topRoute.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = "Departure target: ${user.fingerprint.preferredDepartureTime} • Signals green",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("⏱ ${topRoute.durationMinutes} mins", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                    Text("💳 ₹${topRoute.costInr}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                    Text("🚶 ${topRoute.walkingDistanceMeters}m walk", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onStartJourneyClick(topRoute) },
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("dash_start_today_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Start Today's Commute", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Commute Fingerprint Summary Card
        Text(
            text = "COMMUTE FINGERPRINT SUMMARY",
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Learned Preferences", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }

                TextButton(onClick = onEditFingerprintClick) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = PrimaryBlue)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp, color = PrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FingerprintChip(label = "Mode", value = user.fingerprint.preferredTransport.displayName, modifier = Modifier.weight(1f))
                FingerprintChip(label = "Budget", value = "₹${user.fingerprint.dailyBudgetInr}", modifier = Modifier.weight(1f))
                FingerprintChip(label = "Walk Limit", value = "${user.fingerprint.maxWalkingDistanceMeters}m", modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Commute Metrics Row (Averages)
        Text(
            text = "YOUR TRAVEL METRICS",
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
            MetricCard(title = "Avg Commute", value = "${insights.avgCommuteMinutes} min", subtitle = "2.4m faster than avg", modifier = Modifier.weight(1f))
            MetricCard(title = "Avg Daily Cost", value = "₹${insights.avgSpendingInr}", subtitle = "Under ₹45 budget", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Gemini AI Insights Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEFF6FF))
                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gemini Commute Intelligence", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlueDark)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Based on your 5-day feedback, your preference for low crowd transit saved 14 minutes in boarding delays. Silk Board corridor is clearest before 08:25 AM.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF1E3A8A)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Saved Routes Section
        if (savedRoutes.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SAVED ROUTES (${savedRoutes.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 1.sp
                )
                TextButton(onClick = onViewInsightsClick) {
                    Text("View Insights", fontSize = 12.sp, color = PrimaryBlue)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            savedRoutes.forEach { sr ->
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
                        TransportModeIcon(type = sr.transportType, size = 30)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(sr.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                            Text("${sr.avgDurationMinutes}m • ₹${sr.avgCostInr}", fontSize = 12.sp, color = Color(0xFF64748B))
                        }
                    }
                    CommuteScoreBadge(score = sr.typicalScore, size = 38.dp, strokeWidth = 3.dp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Recent Journeys / Feedback Section
        if (recentFeedbacks.isNotEmpty()) {
            Text(
                text = "RECENT TRAVEL FEEDBACK",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            recentFeedbacks.take(2).forEach { fb ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .border(1.dp, SlateBorderLight, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(fb.routeTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Text("⭐ ${fb.rating}/5", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Crowd: ${fb.wasCrowdHigher} • Time: ${fb.wasTravelTimeAccurate}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    if (fb.comments.isNotBlank()) {
                        Text(
                            text = "\"${fb.comments}\"",
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FingerprintChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
            .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 10.sp, color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
    }
}

@Composable
private fun MetricCard(title: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, SlateBorderLight, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(2.dp))
        Text(subtitle, fontSize = 11.sp, color = SuccessGreen)
    }
}
