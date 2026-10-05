package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.UserRole
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FeedbackDialog
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.JourneyScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.theme.CommuteIQTheme
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SlateBorderLight
import com.example.viewmodel.AppScreen
import com.example.viewmodel.CommuteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CommuteIQTheme {
                CommuteIQApp()
            }
        }
    }
}

@Composable
fun CommuteIQApp(viewModel: CommuteViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.users.collectAsState()
    val routes by viewModel.routeOptions.collectAsState()
    val selectedRoute by viewModel.selectedRoute.collectAsState()
    val aiRecommendation by viewModel.aiRecommendation.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val activeJourney by viewModel.activeJourney.collectAsState()
    val savedRoutes by viewModel.savedRoutes.collectAsState()
    val feedbackList by viewModel.feedbackList.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val insights by viewModel.insights.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val completedJourneyForFeedback by viewModel.completedJourneyForFeedback.collectAsState()

    val origin by viewModel.searchOrigin.collectAsState()
    val destination by viewModel.searchDestination.collectAsState()
    val departureTime by viewModel.searchDepartureTime.collectAsState()
    val transportFilter by viewModel.selectedTransportFilter.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Toast event handler
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearToast()
        }
    }

    // Android Hardware / Gesture BackHandler
    BackHandler(enabled = currentScreen != AppScreen.LANDING) {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(AppScreen.DASHBOARD)
        }
    }

    // Show bottom nav bar only for authenticated main screens
    val showBottomBar = currentScreen in listOf(
        AppScreen.DASHBOARD,
        AppScreen.PLANNER,
        AppScreen.INSIGHTS,
        AppScreen.ADMIN_PANEL
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    NavigationBar(
                        containerColor = Color.White,
                        contentColor = PrimaryBlue,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 760.dp)
                            .testTag("main_bottom_nav")
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.DASHBOARD,
                            onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard", modifier = Modifier.size(20.dp)) },
                            label = { Text("Dashboard", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryBlue,
                                selectedTextColor = PrimaryBlue,
                                indicatorColor = Color(0xFFDBEAFE)
                            ),
                            modifier = Modifier.testTag("nav_dashboard")
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.PLANNER,
                            onClick = { viewModel.navigateTo(AppScreen.PLANNER) },
                            icon = { Icon(Icons.Default.Navigation, contentDescription = "Planner", modifier = Modifier.size(20.dp)) },
                            label = { Text("Planner", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryBlue,
                                selectedTextColor = PrimaryBlue,
                                indicatorColor = Color(0xFFDBEAFE)
                            ),
                            modifier = Modifier.testTag("nav_planner")
                        )

                        if (activeJourney != null) {
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.JOURNEY_ACTIVE,
                                onClick = { viewModel.navigateTo(AppScreen.JOURNEY_ACTIVE) },
                                icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Journey", modifier = Modifier.size(20.dp)) },
                                label = { Text("Active Trip", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF10B981),
                                    selectedTextColor = Color(0xFF10B981),
                                    indicatorColor = Color(0xFFDCFCE7)
                                ),
                                modifier = Modifier.testTag("nav_active_journey")
                            )
                        }

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.INSIGHTS || currentScreen == AppScreen.FINGERPRINT_PROFILE,
                            onClick = { viewModel.navigateTo(AppScreen.INSIGHTS) },
                            icon = { Icon(Icons.Default.Analytics, contentDescription = "Insights", modifier = Modifier.size(20.dp)) },
                            label = { Text("Insights", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryBlue,
                                selectedTextColor = PrimaryBlue,
                                indicatorColor = Color(0xFFDBEAFE)
                            ),
                            modifier = Modifier.testTag("nav_insights")
                        )

                        if (currentUser.role == UserRole.ADMIN) {
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.ADMIN_PANEL,
                                onClick = { viewModel.navigateTo(AppScreen.ADMIN_PANEL) },
                                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", modifier = Modifier.size(20.dp)) },
                                label = { Text("Admin", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = SecondaryTeal,
                                    selectedTextColor = SecondaryTeal,
                                    indicatorColor = Color(0xFFCCFBF1)
                                ),
                                modifier = Modifier.testTag("nav_admin")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 760.dp)
            ) {
                when (currentScreen) {
                AppScreen.LANDING -> {
                    LandingScreen(
                        onPlanCommuteClick = { viewModel.navigateTo(AppScreen.PLANNER) },
                        onExploreDemoClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        onLoginClick = { viewModel.navigateTo(AppScreen.AUTH) },
                        onAdminLoginClick = { viewModel.navigateTo(AppScreen.ADMIN_PANEL) }
                    )
                }

                AppScreen.AUTH -> {
                    AuthScreen(
                        onLoginSuccess = { email, role ->
                            viewModel.login(email, role)
                        },
                        onSignUp = { email, pass, name ->
                            viewModel.signUp(email, pass, name)
                        },
                        onForgotPassword = { email ->
                            viewModel.resetPassword(email)
                        },
                        onBack = { viewModel.navigateTo(AppScreen.LANDING) }
                    )
                }

                AppScreen.ONBOARDING -> {
                    OnboardingScreen(
                        currentProfile = currentUser,
                        onSaveFingerprint = { name, home, work, transport, maxWalk, budget, time, tolerance, tPri, cPri, wPri ->
                            viewModel.updateProfile(name, home, work)
                            viewModel.updateFingerprint(transport, maxWalk, budget, time, tolerance, tPri, cPri, wPri)
                            viewModel.navigateTo(AppScreen.DASHBOARD)
                        },
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }

                AppScreen.DASHBOARD -> {
                    DashboardScreen(
                        user = currentUser,
                        topRoute = selectedRoute ?: routes.firstOrNull(),
                        savedRoutes = savedRoutes,
                        recentFeedbacks = feedbackList,
                        insights = insights,
                        onPlanCommuteClick = { viewModel.navigateTo(AppScreen.PLANNER) },
                        onEditFingerprintClick = { viewModel.navigateTo(AppScreen.ONBOARDING) },
                        onStartJourneyClick = { route -> viewModel.startJourney(route) },
                        onViewInsightsClick = { viewModel.navigateTo(AppScreen.INSIGHTS) },
                        onAdminPanelClick = { viewModel.navigateTo(AppScreen.ADMIN_PANEL) },
                        onLogoutClick = { viewModel.logout() }
                    )
                }

                AppScreen.PLANNER -> {
                    PlannerScreen(
                        origin = origin,
                        destination = destination,
                        departureTime = departureTime,
                        selectedFilter = transportFilter,
                        routes = routes,
                        selectedRoute = selectedRoute,
                        aiRecommendation = aiRecommendation,
                        isAiLoading = isAiLoading,
                        savedRoutes = savedRoutes,
                        fingerprint = currentUser.fingerprint,
                        onOriginChange = { viewModel.searchOrigin.value = it },
                        onDestinationChange = { viewModel.searchDestination.value = it },
                        onDepartureTimeChange = { viewModel.searchDepartureTime.value = it },
                        onFilterChange = {
                            viewModel.selectedTransportFilter.value = it
                            viewModel.refreshPlan()
                        },
                        onSearchClick = { viewModel.refreshPlan() },
                        onSelectRoute = { viewModel.selectRoute(it) },
                        onStartJourney = { viewModel.startJourney(it) },
                        onToggleSave = { viewModel.toggleSaveRoute(it) },
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }

                AppScreen.ROUTE_COMPARISON -> {
                    PlannerScreen(
                        origin = origin,
                        destination = destination,
                        departureTime = departureTime,
                        selectedFilter = transportFilter,
                        routes = routes,
                        selectedRoute = selectedRoute,
                        aiRecommendation = aiRecommendation,
                        isAiLoading = isAiLoading,
                        savedRoutes = savedRoutes,
                        fingerprint = currentUser.fingerprint,
                        onOriginChange = { viewModel.searchOrigin.value = it },
                        onDestinationChange = { viewModel.searchDestination.value = it },
                        onDepartureTimeChange = { viewModel.searchDepartureTime.value = it },
                        onFilterChange = {
                            viewModel.selectedTransportFilter.value = it
                            viewModel.refreshPlan()
                        },
                        onSearchClick = { viewModel.refreshPlan() },
                        onSelectRoute = { viewModel.selectRoute(it) },
                        onStartJourney = { viewModel.startJourney(it) },
                        onToggleSave = { viewModel.toggleSaveRoute(it) },
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }

                AppScreen.JOURNEY_ACTIVE -> {
                    JourneyScreen(
                        journey = activeJourney,
                        onProgressStep = { viewModel.progressJourneyStep() },
                        onEndJourney = { viewModel.endJourneyManual() },
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }

                AppScreen.INSIGHTS, AppScreen.FINGERPRINT_PROFILE -> {
                    InsightsScreen(
                        user = currentUser,
                        insights = insights,
                        onEditFingerprint = { viewModel.navigateTo(AppScreen.ONBOARDING) },
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }

                AppScreen.ADMIN_PANEL -> {
                    AdminScreen(
                        users = allUsers,
                        routes = routes,
                        feedbackList = feedbackList,
                        auditLogs = auditLogs,
                        onAddRoute = { title, transport, duration, cost, walking, crowd, delay ->
                            viewModel.adminAddRoute(title, transport, duration, cost, walking, crowd, delay)
                        },
                        onDeleteRoute = { viewModel.adminDeleteRoute(it) },
                        onUpdateDelay = { id, delay, crowd ->
                            viewModel.adminUpdateDelay(id, delay, crowd)
                        },
                        onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                    )
                }
            }

            // Post-Journey Feedback Dialog
            completedJourneyForFeedback?.let { finishedJourney ->
                FeedbackDialog(
                    journey = finishedJourney,
                    onSubmit = { rating, crowd, time, comfort, comments ->
                        viewModel.submitFeedback(rating, crowd, time, comfort, comments)
                    },
                    onDismiss = { viewModel.dismissFeedback() }
                )
            }
            }
        }
    }
}

