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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SlateBorderLight

@Composable
fun AuthScreen(
    onLoginSuccess: (email: String, role: UserRole) -> Unit,
    onSignUp: ((email: String, pass: String, fullName: String) -> Unit)? = null,
    onForgotPassword: ((email: String) -> Unit)? = null,
    onBack: () -> Unit,
    initialRole: UserRole = UserRole.COMMUTER,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Sign Up
    var email by remember { mutableStateOf(if (initialRole == UserRole.ADMIN) "admin@commuteiq.gov.in" else "shrnaman143909@gmail.com") }
    var password by remember { mutableStateOf("••••••••") }
    var fullName by remember { mutableStateOf("Naman Sharma") }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Reset Password") },
            text = {
                Text("A secure password reset link has been dispatched to $email via Supabase Auth.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onForgotPassword?.invoke(email)
                        showForgotPasswordDialog = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(40.dp).testTag("auth_back_btn")
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title Header
        Text(
            text = if (selectedTab == 0) "Welcome to CommuteIQ" else "Create Your Account",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = if (selectedTab == 0) "Sign in to access your personal commute intelligence" else "Start learning and optimizing your daily journeys",
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = PrimaryBlue,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SlateBorderLight, RoundedCornerShape(8.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Login", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Sign Up", fontWeight = FontWeight.SemiBold) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Inputs
        if (selectedTab == 1) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("auth_name_input"),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
            singleLine = true
        )

        if (selectedTab == 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { showForgotPasswordDialog = true }) {
                    Text("Forgot Password?", fontSize = 13.sp, color = PrimaryBlue)
                }
            }
        } else {
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Submit Button
        Button(
            onClick = {
                if (email.isBlank()) {
                    errorMessage = "Please enter an email address."
                } else {
                    if (selectedTab == 1 && onSignUp != null) {
                        onSignUp(email, password, fullName)
                    } else {
                        val role = if (email.contains("admin", ignoreCase = true)) UserRole.ADMIN else UserRole.COMMUTER
                        onLoginSuccess(email, role)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("auth_submit_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (selectedTab == 0) "Sign In" else "Create Account",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Demo Accounts Quick Access
        Text(
            text = "QUICK DEMO ACCESS (PROTOTYPE)",
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
            OutlinedButton(
                onClick = {
                    onLoginSuccess("shrnaman143909@gmail.com", UserRole.COMMUTER)
                },
                modifier = Modifier.weight(1f).height(44.dp).testTag("quick_commuter_btn"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = PrimaryBlue
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Commuter Demo", fontSize = 12.sp, color = PrimaryBlue)
            }

            OutlinedButton(
                onClick = {
                    onLoginSuccess("admin@commuteiq.gov.in", UserRole.ADMIN)
                },
                modifier = Modifier.weight(1f).height(44.dp).testTag("quick_admin_btn"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF0F766E)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Admin Demo", fontSize = 12.sp, color = Color(0xFF0F766E))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Supabase Auth emulation enabled. Session persists locally with Row-Level Security rules simulation.",
                    fontSize = 11.sp,
                    color = Color(0xFF475569)
                )
            }
        }
    }
}
