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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueDark
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SlateBorderLight
import com.example.ui.theme.SuccessGreen

@Composable
fun LandingScreen(
    onPlanCommuteClick: () -> Unit,
    onExploreDemoClick: () -> Unit,
    onLoginClick: () -> Unit = onExploreDemoClick,
    onAdminLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Top App Bar / Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(PrimaryBlue, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "CommuteIQ",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "CommuteIQ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "From Route Finding to Commute Intelligence",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = onLoginClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp).testTag("landing_signin_btn")
                ) {
                    Text("Sign In", fontSize = 12.sp, color = PrimaryBlue)
                }

                OutlinedButton(
                    onClick = onAdminLoginClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp).testTag("landing_admin_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Admin", fontSize = 12.sp, color = Color(0xFF475569))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SIH 2026 Innovation Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SIH 2026 Student Innovation • Smart Commutation Sector",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlueDark
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Hero Section
        Text(
            text = "Your commute should adapt to you.",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 36.sp,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "CommuteIQ learns how you travel and recommends the most suitable commute for your time, budget, walking tolerance and crowd preferences.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = Color(0xFF475569)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Primary Call to Actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onPlanCommuteClick,
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("plan_my_commute_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Plan My Commute", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }

            OutlinedButton(
                onClick = onExploreDemoClick,
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("explore_demo_btn"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Explore Demo Dashboard",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF0F172A)
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "Core Innovations",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3 Key Feature Cards
        FeatureCard(
            icon = Icons.Default.Psychology,
            title = "Personalized Commute Intelligence",
            description = "Learns your travel habits, crowd tolerance, walking limits and budget to curate tailored multi-modal paths rather than generic map routes."
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            icon = Icons.Default.Speed,
            title = "Dynamic Commute Score (0–100)",
            description = "A transparent composite metric weighting time efficiency (25%), cost (20%), crowd comfort (20%), walking convenience (15%) and delay mitigation."
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            icon = Icons.Default.Insights,
            title = "Adaptive Travel Recommendations",
            description = "Simulated real-time signal conditions, live ETA tracking, journey progression simulation, and post-trip feedback learning loops."
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Innovation Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CommuteIQ Platform • Prototype with Seeded Multi-Modal Transit Data",
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, SlateBorderLight, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFDBEAFE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = Color(0xFF475569)
            )
        }
    }
}
