package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScoreBreakdown
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SlateBorderLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun ScoreBreakdownCard(
    breakdown: ScoreBreakdown,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .border(1.dp, SlateBorderLight, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Score Factor Breakdown",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Weight Adjusted",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        ScoreFactorRow(
            icon = Icons.Default.Schedule,
            label = "Time Efficiency",
            score = breakdown.timeScore,
            weightLabel = "25%",
            color = PrimaryBlue
        )
        Spacer(modifier = Modifier.height(10.dp))

        ScoreFactorRow(
            icon = Icons.Default.AttachMoney,
            label = "Cost Savings",
            score = breakdown.costScore,
            weightLabel = "20%",
            color = SuccessGreen
        )
        Spacer(modifier = Modifier.height(10.dp))

        ScoreFactorRow(
            icon = Icons.Default.Group,
            label = "Crowd Comfort",
            score = breakdown.crowdScore,
            weightLabel = "20%",
            color = SecondaryTeal
        )
        Spacer(modifier = Modifier.height(10.dp))

        ScoreFactorRow(
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
            label = "Walking Convenience",
            score = breakdown.walkingScore,
            weightLabel = "15%",
            color = Color(0xFF0284C7)
        )
        Spacer(modifier = Modifier.height(10.dp))

        ScoreFactorRow(
            icon = Icons.Default.Warning,
            label = "Delay Risk Mitigation",
            score = breakdown.delayRiskScore,
            weightLabel = "10%",
            color = WarningAmber
        )
        Spacer(modifier = Modifier.height(10.dp))

        ScoreFactorRow(
            icon = Icons.Default.ThumbUp,
            label = "Preference Alignment",
            score = breakdown.preferenceMatchScore,
            weightLabel = "10%",
            color = Color(0xFF7C3AED)
        )

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Scores adapt dynamically according to your Commute Fingerprint priorities.",
                fontSize = 11.sp,
                color = Color(0xFF475569)
            )
        }
    }
}

@Composable
private fun ScoreFactorRow(
    icon: ImageVector,
    label: String,
    score: Int,
    weightLabel: String,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$score/100",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "($weightLabel)",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (score.coerceIn(0, 100) / 100f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f),
        )
    }
}
