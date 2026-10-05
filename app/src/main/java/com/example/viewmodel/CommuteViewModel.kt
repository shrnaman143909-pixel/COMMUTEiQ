package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiRecommendationResult
import com.example.ai.GeminiCommuteService
import com.example.data.MockTransitRepository
import com.example.model.ActiveJourney
import com.example.model.CommuteFingerprint
import com.example.model.CommuteInsights
import com.example.model.CrowdLevel
import com.example.model.DelayRisk
import com.example.model.JourneyFeedback
import com.example.model.RouteCategory
import com.example.model.RouteOption
import com.example.model.SavedRoute
import com.example.model.ScoreBreakdown
import com.example.model.ToleranceLevel
import com.example.model.TransportType
import com.example.model.UserProfile
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    LANDING,
    AUTH,
    ONBOARDING,
    DASHBOARD,
    PLANNER,
    ROUTE_COMPARISON,
    JOURNEY_ACTIVE,
    INSIGHTS,
    FINGERPRINT_PROFILE,
    ADMIN_PANEL
}

class CommuteViewModel : ViewModel() {

    // Current navigation screen
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.LANDING)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Screen backstack for back press handling
    private val backStack = mutableListOf<AppScreen>()

    // Current user
    val currentUser: StateFlow<UserProfile> = MockTransitRepository.currentUser
    val users: StateFlow<List<UserProfile>> = MockTransitRepository.users

    // Search and planner states
    val searchOrigin = MutableStateFlow("Green Glen Layout")
    val searchDestination = MutableStateFlow("SIH Tech & Innovation Hub")
    val searchDepartureTime = MutableStateFlow("08:30 AM")
    val selectedTransportFilter = MutableStateFlow<TransportType?>(null)

    // Recommended routes
    private val _routeOptions = MutableStateFlow<List<RouteOption>>(emptyList())
    val routeOptions: StateFlow<List<RouteOption>> = _routeOptions.asStateFlow()

    private val _selectedRoute = MutableStateFlow<RouteOption?>(null)
    val selectedRoute: StateFlow<RouteOption?> = _selectedRoute.asStateFlow()

    // AI recommendation state
    private val _aiRecommendation = MutableStateFlow<AiRecommendationResult?>(null)
    val aiRecommendation: StateFlow<AiRecommendationResult?> = _aiRecommendation.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Active Journey
    val activeJourney: StateFlow<ActiveJourney?> = MockTransitRepository.activeJourney
    val savedRoutes: StateFlow<List<SavedRoute>> = MockTransitRepository.savedRoutes
    val feedbackList: StateFlow<List<JourneyFeedback>> = MockTransitRepository.feedbackList
    val auditLogs: StateFlow<List<com.example.model.AdminAuditLog>> = MockTransitRepository.auditLogs
    val insights: StateFlow<CommuteInsights> = MockTransitRepository.insights

    // Toast message event
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Pending feedback after journey
    private val _completedJourneyForFeedback = MutableStateFlow<ActiveJourney?>(null)
    val completedJourneyForFeedback: StateFlow<ActiveJourney?> = _completedJourneyForFeedback.asStateFlow()

    init {
        // Initial candidate routes calculation
        refreshPlan()
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        return false
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun login(email: String, role: UserRole = UserRole.COMMUTER) {
        viewModelScope.launch {
            val result = com.example.supabase.SupabaseClient.signIn(email, "default_pass")
            result.onSuccess { user ->
                val finalUser = user.copy(role = role)
                MockTransitRepository.setCurrentUser(finalUser)
                showToast("Logged in: ${finalUser.name}")
                refreshPlan()
                navigateTo(if (role == UserRole.ADMIN) AppScreen.ADMIN_PANEL else AppScreen.DASHBOARD)
            }.onFailure { err ->
                showToast("Auth: ${err.message ?: "Login failed"}")
            }
        }
    }

    fun signUp(email: String, pass: String, fullName: String) {
        viewModelScope.launch {
            val result = com.example.supabase.SupabaseClient.signUp(email, pass, fullName)
            result.onSuccess { user ->
                MockTransitRepository.setCurrentUser(user)
                showToast("Account created in Supabase: ${user.name}")
                refreshPlan()
                navigateTo(AppScreen.ONBOARDING)
            }.onFailure { err ->
                showToast("Registration: ${err.message ?: "Sign up failed"}")
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            com.example.supabase.SupabaseClient.resetPassword(email)
            showToast("Password reset instructions dispatched to $email")
        }
    }

    fun logout() {
        viewModelScope.launch {
            com.example.supabase.SupabaseClient.signOut()
            showToast("Session cleared & logged out successfully")
            navigateTo(AppScreen.LANDING)
        }
    }

    fun updateFingerprint(
        preferredTransport: TransportType,
        maxWalkingMeters: Int,
        dailyBudgetInr: Int,
        departureTime: String,
        crowdTolerance: ToleranceLevel,
        timePriority: Float,
        costPriority: Float,
        walkingPriority: Float
    ) {
        val newFingerprint = CommuteFingerprint(
            preferredTransport = preferredTransport,
            maxWalkingDistanceMeters = maxWalkingMeters,
            dailyBudgetInr = dailyBudgetInr,
            preferredDepartureTime = departureTime,
            crowdTolerance = crowdTolerance,
            timePriority = timePriority,
            costPriority = costPriority,
            walkingPriority = walkingPriority
        )
        MockTransitRepository.updateCurrentUserFingerprint(newFingerprint)
        showToast("Commute Fingerprint updated")
        refreshPlan()
    }

    fun updateProfile(name: String, home: String, work: String) {
        MockTransitRepository.updateCurrentUserProfile(name, home, work)
        searchOrigin.value = home
        searchDestination.value = work
        showToast("Profile settings saved")
        refreshPlan()
    }

    fun refreshPlan() {
        viewModelScope.launch {
            _isAiLoading.value = true
            val candidates = MockTransitRepository.getCandidateRoutes(
                origin = searchOrigin.value,
                destination = searchDestination.value
            )
            val filtered = if (selectedTransportFilter.value != null) {
                candidates.filter { it.primaryTransport == selectedTransportFilter.value }
            } else {
                candidates
            }

            _routeOptions.value = if (filtered.isNotEmpty()) filtered else candidates
            val topRoute = _routeOptions.value.firstOrNull()
            _selectedRoute.value = topRoute

            // Query Gemini or deterministic scoring
            val aiResult = GeminiCommuteService.getRecommendationInsight(
                routes = _routeOptions.value,
                fingerprint = currentUser.value.fingerprint,
                userOrigin = searchOrigin.value,
                userDestination = searchDestination.value
            )
            _aiRecommendation.value = aiResult
            _isAiLoading.value = false
        }
    }

    fun selectRoute(route: RouteOption) {
        _selectedRoute.value = route
    }

    fun toggleSaveRoute(route: RouteOption) {
        MockTransitRepository.toggleSaveRoute(route)
        val isNowSaved = MockTransitRepository.savedRoutes.value.any { it.name == route.title }
        showToast(if (isNowSaved) "Route added to saved routes" else "Route removed from saved routes")
    }

    fun startJourney(route: RouteOption) {
        MockTransitRepository.startJourney(route)
        navigateTo(AppScreen.JOURNEY_ACTIVE)
        showToast("Journey started: Live commute intelligence active")
    }

    fun progressJourneyStep() {
        MockTransitRepository.progressJourneyStep()
        val current = MockTransitRepository.activeJourney.value
        if (current?.isCompleted == true) {
            _completedJourneyForFeedback.value = current
            MockTransitRepository.endJourney()
            showToast("Commute completed! Please submit quick feedback.")
        }
    }

    fun endJourneyManual() {
        val finished = MockTransitRepository.endJourney()
        if (finished != null) {
            _completedJourneyForFeedback.value = finished
            showToast("Journey ended. Feedback helps train your Commute Fingerprint!")
        } else {
            navigateTo(AppScreen.DASHBOARD)
        }
    }

    fun submitFeedback(
        rating: Int,
        wasCrowdHigher: String,
        wasTravelTimeAccurate: String,
        wasComfortable: String,
        comments: String
    ) {
        val completed = _completedJourneyForFeedback.value
        val fb = JourneyFeedback(
            id = "fb_${UUID.randomUUID().toString().take(6)}",
            journeyId = completed?.journeyId ?: "j_prev",
            routeTitle = completed?.route?.title ?: "Commute Route",
            rating = rating,
            wasCrowdHigher = wasCrowdHigher,
            wasTravelTimeAccurate = wasTravelTimeAccurate,
            wasComfortable = wasComfortable,
            comments = comments
        )
        MockTransitRepository.submitFeedback(fb)
        _completedJourneyForFeedback.value = null
        showToast("Thank you! Feedback integrated into your Commute Fingerprint.")
        navigateTo(AppScreen.DASHBOARD)
    }

    fun dismissFeedback() {
        _completedJourneyForFeedback.value = null
        navigateTo(AppScreen.DASHBOARD)
    }

    // Admin Operations
    fun adminAddRoute(
        title: String,
        transport: TransportType,
        minutes: Int,
        cost: Int,
        walkingMeters: Int,
        crowd: CrowdLevel,
        delayRisk: DelayRisk
    ) {
        val newRoute = RouteOption(
            id = "admin_r_${UUID.randomUUID().toString().take(6)}",
            title = title,
            category = RouteCategory.ALTERNATIVE,
            primaryTransport = transport,
            durationMinutes = minutes,
            costInr = cost,
            walkingDistanceMeters = walkingMeters,
            crowdLevel = crowd,
            delayRisk = delayRisk,
            punctualityRate = 92,
            comfortScore = 8.0f,
            commuteScore = 80,
            scoreBreakdown = ScoreBreakdown(80, 80, crowd.scoreWeight, 80, 80, 80),
            segments = listOf(
                com.example.model.RouteSegment(
                    mode = transport,
                    instruction = "Transit corridor: $title",
                    durationMinutes = minutes,
                    distanceMeters = 5000,
                    routeCode = "Admin Route"
                )
            ),
            waypoints = listOf(
                com.example.model.RouteWaypoint("w1", "Origin Depot", 0.15f, 0.80f, transport),
                com.example.model.RouteWaypoint("w2", "Midway Junction", 0.50f, 0.50f, transport, true),
                com.example.model.RouteWaypoint("w3", "Destination Terminal", 0.85f, 0.20f, transport)
            ),
            aiReasoning = "Operator added corridor with calibrated headway and frequency."
        )
        MockTransitRepository.addAdminRoute(newRoute)
        refreshPlan()
        showToast("Transit route published successfully")
    }

    fun adminDeleteRoute(routeId: String) {
        MockTransitRepository.deleteAdminRoute(routeId)
        refreshPlan()
        showToast("Transit route decommissioned")
    }

    fun adminUpdateDelay(routeId: String, newDelayRisk: DelayRisk, newCrowd: CrowdLevel) {
        MockTransitRepository.updateRouteDelayStatus(routeId, newDelayRisk, newCrowd)
        refreshPlan()
        showToast("Transit status alert broadcasted to commuters")
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}
