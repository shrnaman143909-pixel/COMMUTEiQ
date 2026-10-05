package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CrowdLevel
import com.example.model.DelayRisk
import com.example.model.RouteCategory
import com.example.model.RouteOption
import com.example.model.RouteSegment
import com.example.model.TransportType
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueDark
import com.example.ui.theme.SlateBorderLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberLight

@Composable
fun RouteOptionCard(
    route: RouteOption,
    isSelected: Boolean,
    isSaved: Boolean,
    onSelect: () -> Unit,
    onStartJourney: () -> Unit,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) PrimaryBlue else SlateBorderLight
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(borderWidth, borderColor, RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .padding(16.dp)
            .testTag("route_card_${route.id}")
    ) {
        // Category Badge & Score Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryBadge(category = route.category)

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier.size(36.dp).testTag("save_route_${route.id}")
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save route",
                        tint = if (isSaved) PrimaryBlue else Color(0xFF64748B)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                CommuteScoreBadge(score = route.commuteScore, size = 48.dp, strokeWidth = 4.dp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title & Mode Icon
        Row(verticalAlignment = Alignment.CenterVertically) {
            TransportModeIcon(type = route.primaryTransport)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = route.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${route.primaryTransport.displayName} • ${route.punctualityRate}% On-Time Record",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Key Metrics Grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            MetricItem(label = "TIME", value = "${route.durationMinutes} min", highlight = true)
            MetricItem(label = "COST", value = "₹${route.costInr}", highlight = false)
            MetricItem(label = "WALK", value = "${route.walkingDistanceMeters}m", highlight = false)
            MetricItem(label = "COMFORT", value = "${route.comfortScore}/10", highlight = false)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status Badges (Crowd & Delay Risk)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CrowdBadge(level = route.crowdLevel, modifier = Modifier.weight(1f))
            DelayRiskBadge(risk = route.delayRisk, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onStartJourney,
                modifier = Modifier.weight(1f).height(44.dp).testTag("start_journey_${route.id}"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Start Journey", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun CategoryBadge(category: RouteCategory) {
    val (bgColor, textColor) = when (category) {
        RouteCategory.BEST_MATCH -> Pair(SuccessGreenLight, SuccessGreen)
        RouteCategory.FASTEST -> Pair(Color(0xFFDBEAFE), PrimaryBlueDark)
        RouteCategory.CHEAPEST -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        RouteCategory.ALTERNATIVE -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (category == RouteCategory.BEST_MATCH) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = category.badgeTitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun CrowdBadge(level: CrowdLevel, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (level) {
        CrowdLevel.LOW -> Pair(SuccessGreenLight, SuccessGreen)
        CrowdLevel.MODERATE -> Pair(Color(0xFFF0F9FF), Color(0xFF0369A1))
        CrowdLevel.HEAVY -> Pair(WarningAmberLight, Color(0xFFB45309))
        CrowdLevel.SEVERE -> Pair(DangerRedLight, DangerRed)
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "👥 ${level.label.substringBefore(" (")}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
fun DelayRiskBadge(risk: DelayRisk, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (risk) {
        DelayRisk.LOW -> Pair(SuccessGreenLight, SuccessGreen)
        DelayRisk.MEDIUM -> Pair(WarningAmberLight, Color(0xFFB45309))
        DelayRisk.HIGH -> Pair(DangerRedLight, DangerRed)
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "⏱ ${risk.label.substringBefore(" (")}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun MetricItem(label: String, value: String, highlight: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) PrimaryBlue else Color(0xFF0F172A)
        )
    }
}

@Composable
fun TransportModeIcon(type: TransportType, size: Int = 36) {
    val (icon, bg) = when (type) {
        TransportType.METRO -> Pair(Icons.Default.DirectionsSubway, Color(0xFFEEF2FF))
        TransportType.ELECTRIC_BUS -> Pair(Icons.Default.DirectionsBus, Color(0xFFFEF3C7))
        TransportType.CAMPUS_SHUTTLE -> Pair(Icons.Default.ElectricCar, Color(0xFFCCFBF1))
        TransportType.SHARED_AUTO -> Pair(Icons.Default.TwoWheeler, Color(0xFFE0F2FE))
        TransportType.WALK_CYCLE -> Pair(Icons.AutoMirrored.Filled.DirectionsWalk, Color(0xFFF1F5F9))
    }

    Box(
        modifier = Modifier
            .size(size.dp)
            .background(bg, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = type.displayName,
            tint = Color(0xFF1E293B),
            modifier = Modifier.size((size * 0.55).dp)
        )
    }
}

@Composable
fun RouteSegmentStep(
    stepNumber: Int,
    segment: RouteSegment,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(PrimaryBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$stepNumber",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(Color(0xFFCBD5E1))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Text(
                text = segment.instruction,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = "${segment.durationMinutes} min • ${segment.distanceMeters}m",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
                if (segment.routeCode != null) {
                    Text(
                        text = " • ${segment.routeCode}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}
