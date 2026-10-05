package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.model.CommuteFingerprint
import com.example.model.ToleranceLevel
import com.example.model.TransportType
import com.example.model.UserProfile
import com.example.ui.components.TransportModeIcon
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueDark
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SlateBorderLight
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    currentProfile: UserProfile,
    onSaveFingerprint: (
        name: String,
        home: String,
        work: String,
        transport: TransportType,
        maxWalking: Int,
        budget: Int,
        departureTime: String,
        tolerance: ToleranceLevel,
        timePriority: Float,
        costPriority: Float,
        walkPriority: Float
    ) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(currentProfile.name) }
    var home by remember { mutableStateOf(currentProfile.homeLocation) }
    var work by remember { mutableStateOf(currentProfile.workLocation) }
    var departureTime by remember { mutableStateOf(currentProfile.fingerprint.preferredDepartureTime) }

    var selectedTransport by remember { mutableStateOf(currentProfile.fingerprint.preferredTransport) }
    var maxWalking by remember { mutableIntStateOf(currentProfile.fingerprint.maxWalkingDistanceMeters) }
    var dailyBudget by remember { mutableIntStateOf(currentProfile.fingerprint.dailyBudgetInr) }
    var crowdTolerance by remember { mutableStateOf(currentProfile.fingerprint.crowdTolerance) }

    var timePriority by remember { mutableFloatStateOf(currentProfile.fingerprint.timePriority) }
    var costPriority by remember { mutableFloatStateOf(currentProfile.fingerprint.costPriority) }
    var walkingPriority by remember { mutableFloatStateOf(currentProfile.fingerprint.walkingPriority) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Top Nav
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp).testTag("onboarding_back_btn")) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Commute Fingerprint Setup",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Explanation Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Your Commute Fingerprint",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlueDark
                    )
                    Text(
                        text = "CommuteIQ weights each factor to synthesize personalized routes calibrated to your exact habits.",
                        fontSize = 12.sp,
                        color = Color(0xFF1E3A8A)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Locations
        Text("1. Personal Details & Frequent Hubs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("onboard_name_input"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = home,
            onValueChange = { home = it },
            label = { Text("Home Location / Origin") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("onboard_home_input"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = work,
            onValueChange = { work = it },
            label = { Text("College / Innovation Hub Location") },
            leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("onboard_work_input"),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Preferred Transport
        Text("2. Preferred Mode of Transport", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TransportType.entries.forEach { type ->
                val isSelected = selectedTransport == type
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) PrimaryBlue else Color.White)
                        .border(1.dp, if (isSelected) PrimaryBlue else SlateBorderLight, RoundedCornerShape(8.dp))
                        .clickable { selectedTransport = type }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TransportModeIcon(type = type, size = 24)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = type.displayName,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color(0xFF0F172A)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Maximum Walking Distance
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("3. Max Walking Distance", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text("${maxWalking}m (${maxWalking / 1000f} km)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        }
        Slider(
            value = maxWalking.toFloat(),
            onValueChange = { maxWalking = (it / 100).toInt() * 100 },
            valueRange = 200f..2500f,
            steps = 22,
            colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue),
            modifier = Modifier.testTag("onboard_walking_slider")
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Daily Travel Budget
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("4. Daily Travel Budget", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text("₹$dailyBudget / day", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
        }
        Slider(
            value = dailyBudget.toFloat(),
            onValueChange = { dailyBudget = (it / 5).toInt() * 5 },
            valueRange = 10f..150f,
            steps = 27,
            colors = SliderDefaults.colors(thumbColor = SuccessGreen, activeTrackColor = SuccessGreen),
            modifier = Modifier.testTag("onboard_budget_slider")
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 5. Crowd Tolerance
        Text("5. Crowd Tolerance", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ToleranceLevel.entries.forEach { level ->
                val isSelected = crowdTolerance == level
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) SecondaryTeal else Color.White)
                        .border(1.dp, if (isSelected) SecondaryTeal else SlateBorderLight, RoundedCornerShape(8.dp))
                        .clickable { crowdTolerance = level }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = level.name.replace("_", " "),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF0F172A)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. Priorities Sliders
        Text("6. Scoring Priority Weights", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Fine tune which dimension matters most for your daily commute recommendations.",
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrioritySlider(
            label = "Time Priority (Faster routes)",
            value = timePriority,
            onValueChange = { timePriority = it },
            color = PrimaryBlue
        )
        PrioritySlider(
            label = "Cost Priority (Frugal fares)",
            value = costPriority,
            onValueChange = { costPriority = it },
            color = SuccessGreen
        )
        PrioritySlider(
            label = "Walking Priority (Minimize foot fatigue)",
            value = walkingPriority,
            onValueChange = { walkingPriority = it },
            color = Color(0xFF0284C7)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Submit Button
        Button(
            onClick = {
                onSaveFingerprint(
                    name,
                    home,
                    work,
                    selectedTransport,
                    maxWalking,
                    dailyBudget,
                    departureTime,
                    crowdTolerance,
                    timePriority,
                    costPriority,
                    walkingPriority
                )
            },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_fingerprint_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Fingerprint & View Dashboard", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun PrioritySlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
            Text("${(value * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.1f..1.0f,
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
        )
    }
}
