package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.model.ActiveJourney
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SlateBorderLight

@Composable
fun FeedbackDialog(
    journey: ActiveJourney,
    onSubmit: (rating: Int, crowd: String, timeAccuracy: String, comfort: String, comments: String) -> Unit,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var wasCrowdHigher by remember { mutableStateOf("No, as expected") }
    var wasTravelTimeAccurate by remember { mutableStateOf("Accurate") }
    var wasComfortable by remember { mutableStateOf("Very comfortable") }
    var comments by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Journey Completed 🎉", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(journey.route.title, fontSize = 12.sp, color = Color(0xFF64748B))
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Rate your overall experience:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star $star",
                            tint = if (star <= rating) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { rating = star }
                                .padding(2.dp)
                                .testTag("star_rating_$star")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question 1: Crowd
                Text("Was crowd higher than expected?", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("No, as expected", "Yes, crowded", "Lower crowd").forEach { opt ->
                        val isSelected = wasCrowdHigher == opt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) PrimaryBlue else Color(0xFFF1F5F9))
                                .clickable { wasCrowdHigher = opt }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(opt, fontSize = 10.sp, color = if (isSelected) Color.White else Color(0xFF0F172A), fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Question 2: Travel Time Accuracy
                Text("Was travel time accurate?", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Accurate", "Delayed >5m", "Faster").forEach { opt ->
                        val isSelected = wasTravelTimeAccurate == opt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) PrimaryBlue else Color(0xFFF1F5F9))
                                .clickable { wasTravelTimeAccurate = opt }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(opt, fontSize = 10.sp, color = if (isSelected) Color.White else Color(0xFF0F172A), fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Comments
                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    label = { Text("Optional feedback / notes") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(rating, wasCrowdHigher, wasTravelTimeAccurate, wasComfortable, comments)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_feedback_btn")
            ) {
                Text("Submit & Train Fingerprint", fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Skip", color = Color(0xFF64748B))
            }
        }
    )
}
