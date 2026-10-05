package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RouteOption
import com.example.model.TransportType
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun RouteCanvasMap(
    route: RouteOption,
    activeWaypointIndex: Int = 0,
    showSimulatedGps: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(Color(0xFF0F172A), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val canvasW = size.width
            val canvasH = size.height

            // 1. Draw subtle transit grid network lines
            val gridStep = 40.dp.toPx()
            var x = 0f
            while (x < canvasW) {
                drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(x, 0f),
                    end = Offset(x, canvasH),
                    strokeWidth = 1f
                )
                x += gridStep
            }
            var y = 0f
            while (y < canvasH) {
                drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(0f, y),
                    end = Offset(canvasW, y),
                    strokeWidth = 1f
                )
                y += gridStep
            }

            // 2. Draw route connecting corridors
            val waypoints = route.waypoints
            if (waypoints.isNotEmpty()) {
                val offsets = waypoints.map { wp ->
                    Offset(wp.xPercent * canvasW, wp.yPercent * canvasH)
                }

                for (i in 0 until offsets.size - 1) {
                    val start = offsets[i]
                    val end = offsets[i + 1]
                    val mode = waypoints[i].transportType

                    val (lineColor, isDotted) = when (mode) {
                        TransportType.METRO -> Pair(Color(0xFF818CF8), false) // Indigo Metro
                        TransportType.ELECTRIC_BUS -> Pair(WarningAmber, false) // Bus corridor
                        TransportType.CAMPUS_SHUTTLE -> Pair(SecondaryTeal, false) // Campus EV
                        TransportType.SHARED_AUTO -> Pair(Color(0xFF38BDF8), false) // Shared cab
                        TransportType.WALK_CYCLE -> Pair(Color(0xFF38BDF8), true) // Walking
                    }

                    val pathEffect = if (isDotted) PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f) else null

                    // Glow line
                    drawLine(
                        color = lineColor.copy(alpha = 0.25f),
                        start = start,
                        end = end,
                        strokeWidth = 10f,
                        pathEffect = pathEffect
                    )

                    // Solid line
                    drawLine(
                        color = lineColor,
                        start = start,
                        end = end,
                        strokeWidth = 4f,
                        pathEffect = pathEffect
                    )
                }

                // 3. Draw station nodes
                waypoints.forEachIndexed { idx, wp ->
                    val pos = offsets[idx]
                    val isCurrent = idx == activeWaypointIndex
                    val isOrigin = idx == 0
                    val isDest = idx == waypoints.size - 1

                    val nodeColor = when {
                        isDest -> SuccessGreen
                        isOrigin -> PrimaryBlue
                        wp.isTransferPoint -> WarningAmber
                        else -> Color(0xFFE2E8F0)
                    }

                    // Outer node circle
                    drawCircle(
                        color = Color(0xFF0F172A),
                        radius = 12f,
                        center = pos
                    )
                    drawCircle(
                        color = nodeColor,
                        radius = if (wp.isTransferPoint) 9f else 7f,
                        center = pos,
                        style = if (wp.isTransferPoint) Stroke(width = 4f) else Stroke(width = 3f)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4f,
                        center = pos
                    )

                    // Station Label
                    val textLayout = textMeasurer.measure(
                        text = wp.name,
                        style = TextStyle(
                            color = Color(0xFFCBD5E1),
                            fontSize = 10.sp,
                            fontWeight = if (isCurrent || isDest) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                    val labelY = if (pos.y > canvasH * 0.7f) pos.y - 20f else pos.y + 12f
                    val labelX = (pos.x - textLayout.size.width / 2f).coerceIn(4f, canvasW - textLayout.size.width - 4f)
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(labelX, labelY)
                    )
                }

                // 4. Draw active commuter / transit GPS beacon
                if (showSimulatedGps && activeWaypointIndex in offsets.indices) {
                    val activePos = offsets[activeWaypointIndex]

                    // Pulse wave
                    drawCircle(
                        color = Color(0xFF38BDF8).copy(alpha = pulseAlpha),
                        radius = pulseRadius,
                        center = activePos
                    )
                    // Core beacon
                    drawCircle(
                        color = Color(0xFF0284C7),
                        radius = 7f,
                        center = activePos
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3f,
                        center = activePos
                    )
                }
            }
        }

        // Live telemetry legend tag
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(Color(0xCC1E293B), RoundedCornerShape(8.dp))
                .border(0.5.dp, Color(0xFF475569), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Live Network Map • Simulated Telemetry",
                color = Color(0xFF94A3B8),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
