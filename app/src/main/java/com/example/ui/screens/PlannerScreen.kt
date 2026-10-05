package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import com.example.ai.AiRecommendationResult
import com.example.model.CommuteFingerprint
import com.example.model.RouteOption
import com.example.model.SavedRoute
import com.example.model.TransportType
import com.example.ui.components.CategoryBadge
import com.example.ui.components.CommuteScoreBadge
import com.example.ui.components.CrowdBadge
import com.example.ui.components.DelayRiskBadge
import com.example.ui.components.RouteCanvasMap
import com.example.ui.components.RouteOptionCard
import com.example.ui.components.RouteSegmentStep
import com.example.ui.components.ScoreBreakdownCard
import com.example.ui.components.TransportModeIcon
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueDark
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SlateBorderLight
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlannerScreen(
    origin: String,
    destination: String,
    departureTime: String,
    selectedFilter: TransportType?,
    routes: List<RouteOption>,
    selectedRoute: RouteOption?,
    aiRecommendation: AiRecommendationResult?,
    isAiLoading: Boolean,
    savedRoutes: List<SavedRoute>,
    fingerprint: CommuteFingerprint,
    onOriginChange: (String) -> Unit,
    onDestinationChange: (String) -> Unit,
    onDepartureTimeChange: (String) -> Unit,
    onFilterChange: (TransportType?) -> Unit,
    onSearchClick: () -> Unit,
    onSelectRoute: (RouteOption) -> Unit,
    onStartJourney: (RouteOption) -> Unit,
    onToggleSave: (RouteOption) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var plannerTab by remember { mutableIntStateOf(0) } // 0: Recommendations & Result, 1: Route Comparison Table, 2: Map & Segments

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp).testTag("planner_back_btn")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Smart Commute Planner",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Personalized scoring calibrated to your fingerprint",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Origin & Destination Inputs Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, SlateBorderLight, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            OutlinedTextField(
                value = origin,
                onValueChange = onOriginChange,
                label = { Text("Origin / From") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryBlue) },
                modifier = Modifier.fillMaxWidth().testTag("planner_origin_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = destination,
                onValueChange = onDestinationChange,
                label = { Text("Destination / Hub") },
                leadingIcon = { Icon(Icons.Default.Navigation, contentDescription = null, tint = SuccessGreen) },
                modifier = Modifier.fillMaxWidth().testTag("planner_dest_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = departureTime,
                    onValueChange = onDepartureTimeChange,
                    label = { Text("Departure") },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    modifier = Modifier.weight(1f).testTag("planner_time_input"),
                    singleLine = true
                )

                Button(
                    onClick = onSearchClick,
                    modifier = Modifier.weight(1f).height(56.dp).testTag("planner_search_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isAiLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Calculate", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Transport Filter Chips
            Text("Filter Transport Mode:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { onFilterChange(null) },
                    label = { Text("All Modes", fontSize = 11.sp) }
                )
                TransportType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedFilter == type,
                        onClick = { onFilterChange(if (selectedFilter == type) null else type) },
                        label = { Text(type.displayName, fontSize = 11.sp) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Tabs: Recommendations vs Comparison Matrix vs Map View
        TabRow(
            selectedTabIndex = plannerTab,
            containerColor = Color.White,
            contentColor = PrimaryBlue,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SlateBorderLight, RoundedCornerShape(8.dp))
        ) {
            Tab(
                selected = plannerTab == 0,
                onClick = { plannerTab = 0 },
                text = { Text("Recommendation", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = plannerTab == 1,
                onClick = { plannerTab = 1 },
                text = { Text("3-Way Compare", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = plannerTab == 2,
                onClick = { plannerTab = 2 },
                text = { Text("Map & Segments", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (plannerTab) {
            0 -> {
                // TAB 0: RECOMMENDED COMMUTE RESULT
                val best = selectedRoute ?: routes.firstOrNull()
                if (best != null) {
                    // Hero Recommendation Card
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
                            CategoryBadge(category = best.category)
                            CommuteScoreBadge(score = best.commuteScore, size = 64.dp, strokeWidth = 5.dp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = best.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${best.primaryTransport.displayName} • ${best.punctualityRate}% Historical Punctuality",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats Summary Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("DURATION", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                                Text("${best.durationMinutes} mins", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("FARE", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                                Text("₹${best.costInr}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("WALKING", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                                Text("${best.walkingDistanceMeters}m", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("COMFORT", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
                                Text("${best.comfortScore}/10", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SecondaryTeal)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // "Why this recommendation?" Section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEFF6FF), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (aiRecommendation?.isFromAi == true) "Why this recommendation? (Gemini AI)" else "Why this recommendation? (CommuteIQ Engine)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlueDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = aiRecommendation?.reasoning ?: best.aiReasoning,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = Color(0xFF1E3A8A)
                                )
                                if (best.warnings.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    best.warnings.forEach { warning ->
                                        Text(
                                            text = "• $warning",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFB45309)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { onStartJourney(best) },
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("rec_start_journey_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Journey with this Route", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Detailed Score Breakdown
                    ScoreBreakdownCard(breakdown = best.scoreBreakdown)

                    Spacer(modifier = Modifier.height(18.dp))

                    // All Generated Candidate Routes
                    Text(
                        text = "ALL GENERATED OPTIONS (${routes.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    routes.forEach { r ->
                        RouteOptionCard(
                            route = r,
                            isSelected = r.id == best.id,
                            isSaved = savedRoutes.any { it.name == r.title },
                            onSelect = { onSelectRoute(r) },
                            onStartJourney = { onStartJourney(r) },
                            onToggleSave = { onToggleSave(r) },
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }

            1 -> {
                // TAB 1: 3-WAY ROUTE COMPARISON
                RouteComparisonSection(
                    routes = routes,
                    onStartJourney = onStartJourney
                )
            }

            2 -> {
                // TAB 2: INTERACTIVE MAP & TURN-BY-TURN SEGMENTS
                val active = selectedRoute ?: routes.firstOrNull()
                if (active != null) {
                    Text(
                        text = "VISUAL TRANSIT NETWORK MAP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    RouteCanvasMap(
                        route = active,
                        activeWaypointIndex = 0,
                        showSimulatedGps = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "TURN-BY-TURN MULTI-MODAL SEGMENTS",
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
                            .padding(16.dp)
                    ) {
                        active.segments.forEachIndexed { idx, segment ->
                            RouteSegmentStep(
                                stepNumber = idx + 1,
                                segment = segment,
                                isLast = idx == active.segments.size - 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onStartJourney(active) },
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("map_start_journey_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Journey: ${active.title}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Demo disclaimer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Demo prototype data: Scores and traffic conditions are dynamically simulated for the Bangalore/SIH Tech corridor.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun RouteComparisonSection(
    routes: List<RouteOption>,
    onStartJourney: (RouteOption) -> Unit
) {
    val routeFastest = routes.firstOrNull { it.durationMinutes == routes.minOf { r -> r.durationMinutes } } ?: routes.getOrNull(1)
    val routeCheapest = routes.firstOrNull { it.costInr == routes.minOf { r -> r.costInr } } ?: routes.getOrNull(2)
    val routeBestMatch = routes.firstOrNull { it.commuteScore == routes.maxOf { r -> r.commuteScore } } ?: routes.firstOrNull()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "3-WAY ROUTE COMPARISON MATRIX",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Compare the Fastest, Cheapest and Best Match routes side by side against your preferences.",
            fontSize = 12.sp,
            color = Color(0xFF475569)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Comparison Cards
        if (routeBestMatch != null) {
            ComparisonCard("Route C: Best Match for You", routeBestMatch, PrimaryBlue, onStartJourney)
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (routeFastest != null && routeFastest.id != routeBestMatch?.id) {
            ComparisonCard("Route A: Fastest Route", routeFastest, Color(0xFF0369A1), onStartJourney)
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (routeCheapest != null && routeCheapest.id != routeBestMatch?.id) {
            ComparisonCard("Route B: Most Economical", routeCheapest, SuccessGreen, onStartJourney)
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Side-by-side comparative table
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, SlateBorderLight, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Text("Decision Matrix Summary", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(10.dp))

            MatrixRow(metric = "Travel Time", valA = "${routeFastest?.durationMinutes ?: 22}m (Best)", valB = "${routeCheapest?.durationMinutes ?: 36}m", valC = "${routeBestMatch?.durationMinutes ?: 27}m")
            MatrixRow(metric = "Cost", valA = "₹${routeFastest?.costInr ?: 65}", valB = "₹${routeCheapest?.costInr ?: 18} (Best)", valC = "₹${routeBestMatch?.costInr ?: 35}")
            MatrixRow(metric = "Walking Dist", valA = "${routeFastest?.walkingDistanceMeters ?: 180}m", valB = "${routeCheapest?.walkingDistanceMeters ?: 650}m", valC = "${routeBestMatch?.walkingDistanceMeters ?: 420}m")
            MatrixRow(metric = "Crowd Level", valA = "Low", valB = "Heavy", valC = "Moderate")
            MatrixRow(metric = "Delay Risk", valA = "Moderate", valB = "Moderate", valC = "Low")
            MatrixRow(metric = "Commute Score", valA = "${routeFastest?.commuteScore ?: 82}/100", valB = "${routeCheapest?.commuteScore ?: 78}/100", valC = "${routeBestMatch?.commuteScore ?: 89}/100 (Winner)")
        }
    }
}

@Composable
private fun ComparisonCard(
    heading: String,
    route: RouteOption,
    accentColor: Color,
    onStartJourney: (RouteOption) -> Unit
) {
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
            Text(heading, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentColor)
            CommuteScoreBadge(score = route.commuteScore, size = 42.dp, strokeWidth = 3.5.dp)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(route.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("⏱ ${route.durationMinutes} min", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
            Text("💳 ₹${route.costInr}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
            Text("🚶 ${route.walkingDistanceMeters}m walk", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
            Text("👥 ${route.crowdLevel.name}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { onStartJourney(route) },
            modifier = Modifier.fillMaxWidth().height(38.dp),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text("Select this Route", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = accentColor)
        }
    }
}

@Composable
private fun MatrixRow(metric: String, valA: String, valB: String, valC: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(metric, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), modifier = Modifier.weight(1.2f))
            Text(valA, fontSize = 11.sp, color = Color(0xFF334155), modifier = Modifier.weight(1f))
            Text(valB, fontSize = 11.sp, color = Color(0xFF334155), modifier = Modifier.weight(1f))
            Text(valC, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue, modifier = Modifier.weight(1.2f))
        }
        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color(0xFFE2E8F0)).padding(top = 4.dp))
    }
}
