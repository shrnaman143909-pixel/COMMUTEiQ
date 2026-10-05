package com.example.data

import com.example.ai.GeminiCommuteService
import com.example.model.ActiveJourney
import com.example.model.CommuteFingerprint
import com.example.model.CommuteInsights
import com.example.model.CrowdLevel
import com.example.model.DelayRisk
import com.example.model.JourneyFeedback
import com.example.model.RouteCategory
import com.example.model.RouteOption
import com.example.model.RouteSegment
import com.example.model.RouteWaypoint
import com.example.model.SavedRoute
import com.example.model.ScoreBreakdown
import com.example.model.ToleranceLevel
import com.example.model.TransportType
import com.example.model.UserProfile
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object MockTransitRepository {

    // Registered users for commuter & admin
    private val _users = MutableStateFlow<List<UserProfile>>(
        listOf(
            UserProfile(
                id = "usr_naman_101",
                name = "Naman Sharma",
                email = "shrnaman143909@gmail.com",
                role = UserRole.COMMUTER,
                homeLocation = "Green Glen Layout",
                workLocation = "SIH Tech & Innovation Hub",
                fingerprint = CommuteFingerprint(
                    preferredTransport = TransportType.METRO,
                    maxWalkingDistanceMeters = 800,
                    dailyBudgetInr = 45,
                    preferredDepartureTime = "08:30 AM",
                    crowdTolerance = ToleranceLevel.LOW,
                    timePriority = 0.85f,
                    costPriority = 0.55f,
                    walkingPriority = 0.70f
                )
            ),
            UserProfile(
                id = "usr_ananya_102",
                name = "Ananya Roy",
                email = "ananya.student@sih.org",
                role = UserRole.COMMUTER,
                homeLocation = "HSR Sector 2",
                workLocation = "Campus Engineering Block",
                fingerprint = CommuteFingerprint(
                    preferredTransport = TransportType.ELECTRIC_BUS,
                    maxWalkingDistanceMeters = 1200,
                    dailyBudgetInr = 30,
                    preferredDepartureTime = "08:15 AM",
                    crowdTolerance = ToleranceLevel.MODERATE,
                    timePriority = 0.60f,
                    costPriority = 0.90f,
                    walkingPriority = 0.40f
                )
            ),
            UserProfile(
                id = "admin_super_001",
                name = "Transit Authority Admin",
                email = "admin@commuteiq.gov.in",
                role = UserRole.ADMIN,
                homeLocation = "Central Transit Control",
                workLocation = "Smart City HQ",
                fingerprint = CommuteFingerprint()
            )
        )
    )
    val users: StateFlow<List<UserProfile>> = _users.asStateFlow()

    // Current logged in user
    private val _currentUser = MutableStateFlow<UserProfile>(_users.value.first())
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Available transit options & routes (can be managed by Admin)
    private val _allRoutes = MutableStateFlow<List<RouteOption>>(generateInitialRoutes())
    val allRoutes: StateFlow<List<RouteOption>> = _allRoutes.asStateFlow()

    // Saved routes by commuter
    private val _savedRoutes = MutableStateFlow<List<SavedRoute>>(
        listOf(
            SavedRoute(
                id = "saved_1",
                name = "Daily College Route",
                from = "Green Glen Layout",
                to = "SIH Tech & Innovation Hub",
                transportType = TransportType.METRO,
                typicalScore = 89,
                avgDurationMinutes = 28,
                avgCostInr = 35
            ),
            SavedRoute(
                id = "saved_2",
                name = "Budget Library Commute",
                from = "Green Glen Layout",
                to = "Central University Library",
                transportType = TransportType.ELECTRIC_BUS,
                typicalScore = 84,
                avgDurationMinutes = 38,
                avgCostInr = 20
            )
        )
    )
    val savedRoutes: StateFlow<List<SavedRoute>> = _savedRoutes.asStateFlow()

    // Journey Feedback log
    private val _feedbackList = MutableStateFlow<List<JourneyFeedback>>(
        listOf(
            JourneyFeedback(
                id = "fb_01",
                journeyId = "j_past_99",
                routeTitle = "Smart EV Express 500D",
                rating = 5,
                wasCrowdHigher = "No, as expected",
                wasTravelTimeAccurate = "Accurate",
                wasComfortable = "Very comfortable",
                comments = "Smooth air conditioned bus, arrived on the dot!",
                timestampMillis = System.currentTimeMillis() - 86400000L
            ),
            JourneyFeedback(
                id = "fb_02",
                journeyId = "j_past_98",
                routeTitle = "Direct Purple Line Metro",
                rating = 4,
                wasCrowdHigher = "Yes, slightly higher",
                wasTravelTimeAccurate = "Accurate",
                wasComfortable = "Fair",
                comments = "Fastest route but coach was crowded around interchange.",
                timestampMillis = System.currentTimeMillis() - 172800000L
            )
        )
    )
    val feedbackList: StateFlow<List<JourneyFeedback>> = _feedbackList.asStateFlow()

    // Commute Insights
    private val _insights = MutableStateFlow(CommuteInsights())
    val insights: StateFlow<CommuteInsights> = _insights.asStateFlow()

    // Admin audit logs (Feature 6)
    private val _auditLogs = MutableStateFlow<List<com.example.model.AdminAuditLog>>(
        listOf(
            com.example.model.AdminAuditLog(
                id = "audit_01",
                adminEmail = "admin@commuteiq.gov.in",
                action = "ROUTE_CREATED",
                targetId = "route_best_c",
                details = "Initialized Smart Combined Metro corridor with calibrated frequency",
                timestampMillis = System.currentTimeMillis() - 3600000L
            )
        )
    )
    val auditLogs: StateFlow<List<com.example.model.AdminAuditLog>> = _auditLogs.asStateFlow()

    // Active Journey state
    private val _activeJourney = MutableStateFlow<ActiveJourney?>(null)
    val activeJourney: StateFlow<ActiveJourney?> = _activeJourney.asStateFlow()

    fun setCurrentUser(user: UserProfile) {
        _currentUser.value = user
    }

    fun updateCurrentUserFingerprint(fingerprint: CommuteFingerprint) {
        val updated = _currentUser.value.copy(fingerprint = fingerprint)
        _currentUser.value = updated
        _users.value = _users.value.map { if (it.id == updated.id) updated else it }
    }

    fun updateCurrentUserProfile(name: String, home: String, work: String) {
        val updated = _currentUser.value.copy(
            name = name,
            homeLocation = home,
            workLocation = work
        )
        _currentUser.value = updated
        _users.value = _users.value.map { if (it.id == updated.id) updated else it }
    }

    fun switchRole(role: UserRole) {
        val target = _users.value.firstOrNull { it.role == role } ?: _currentUser.value.copy(role = role)
        _currentUser.value = target
    }

    fun getCandidateRoutes(origin: String, destination: String): List<RouteOption> {
        val currentFingerprint = _currentUser.value.fingerprint
        // Re-score all routes according to user's personalized weights
        return _allRoutes.value.map { r ->
            val (score, breakdown) = GeminiCommuteService.calculateScore(r, currentFingerprint)
            r.copy(commuteScore = score, scoreBreakdown = breakdown)
        }.sortedByDescending { it.commuteScore }
    }

    fun startJourney(route: RouteOption): ActiveJourney {
        val journey = ActiveJourney(
            journeyId = "j_${UUID.randomUUID().toString().take(8)}",
            route = route,
            startTimeMillis = System.currentTimeMillis()
        )
        _activeJourney.value = journey
        return journey
    }

    fun progressJourneyStep() {
        val current = _activeJourney.value ?: return
        val totalWaypoints = current.route.waypoints.size
        val nextIndex = current.currentWaypointIndex + 1

        if (nextIndex >= totalWaypoints) {
            _activeJourney.value = current.copy(
                currentWaypointIndex = totalWaypoints - 1,
                progressPercent = 100f,
                etaMinutes = 0,
                delayStatus = "Arrived at destination",
                isCompleted = true
            )
        } else {
            val progress = (nextIndex.toFloat() / (totalWaypoints - 1)) * 100f
            val remainingMin = ((1f - (progress / 100f)) * current.route.durationMinutes).toInt().coerceAtLeast(1)
            val delayStr = if (nextIndex % 2 == 0) "Clear corridor • On time" else "+2 min signal hold at junction"
            _activeJourney.value = current.copy(
                currentWaypointIndex = nextIndex,
                progressPercent = progress,
                etaMinutes = remainingMin,
                delayStatus = delayStr
            )
        }
    }

    fun endJourney(): ActiveJourney? {
        val finished = _activeJourney.value
        _activeJourney.value = null
        return finished
    }

    fun submitFeedback(feedback: JourneyFeedback) {
        _feedbackList.value = listOf(feedback) + _feedbackList.value
    }

    fun toggleSaveRoute(route: RouteOption) {
        val exists = _savedRoutes.value.any { it.name == route.title }
        if (exists) {
            _savedRoutes.value = _savedRoutes.value.filterNot { it.name == route.title }
        } else {
            val newSaved = SavedRoute(
                id = "saved_${UUID.randomUUID().toString().take(6)}",
                name = route.title,
                from = _currentUser.value.homeLocation,
                to = _currentUser.value.workLocation,
                transportType = route.primaryTransport,
                typicalScore = route.commuteScore,
                avgDurationMinutes = route.durationMinutes,
                avgCostInr = route.costInr
            )
            _savedRoutes.value = _savedRoutes.value + newSaved
        }
    }

    // Admin Operations
    fun addAdminRoute(newRoute: RouteOption) {
        _allRoutes.value = listOf(newRoute) + _allRoutes.value
        val audit = com.example.model.AdminAuditLog(
            id = "audit_${UUID.randomUUID().toString().take(6)}",
            adminEmail = _currentUser.value.email,
            action = "ROUTE_CREATED",
            targetId = newRoute.id,
            details = "Added corridor '${newRoute.title}' with duration ${newRoute.durationMinutes}m"
        )
        _auditLogs.value = listOf(audit) + _auditLogs.value
    }

    fun deleteAdminRoute(routeId: String) {
        val deleted = _allRoutes.value.firstOrNull { it.id == routeId }
        _allRoutes.value = _allRoutes.value.filterNot { it.id == routeId }
        val audit = com.example.model.AdminAuditLog(
            id = "audit_${UUID.randomUUID().toString().take(6)}",
            adminEmail = _currentUser.value.email,
            action = "ROUTE_DELETED",
            targetId = routeId,
            details = "Decommissioned route '${deleted?.title ?: routeId}' from active network"
        )
        _auditLogs.value = listOf(audit) + _auditLogs.value
    }

    fun updateRouteDelayStatus(routeId: String, newDelayRisk: DelayRisk, newCrowd: CrowdLevel) {
        _allRoutes.value = _allRoutes.value.map { r ->
            if (r.id == routeId) r.copy(delayRisk = newDelayRisk, crowdLevel = newCrowd) else r
        }
        val audit = com.example.model.AdminAuditLog(
            id = "audit_${UUID.randomUUID().toString().take(6)}",
            adminEmail = _currentUser.value.email,
            action = "DELAY_ALERT_BROADCAST",
            targetId = routeId,
            details = "Updated telemetry: Delay risk to ${newDelayRisk.name}, Crowd to ${newCrowd.name}"
        )
        _auditLogs.value = listOf(audit) + _auditLogs.value
    }

    private fun generateInitialRoutes(): List<RouteOption> {
        return listOf(
            // Route C: Best Match (Personalized Multi-modal)
            RouteOption(
                id = "route_best_c",
                title = "Smart Combined: Metro + Campus EV Shuttle",
                category = RouteCategory.BEST_MATCH,
                primaryTransport = TransportType.METRO,
                durationMinutes = 27,
                costInr = 35,
                walkingDistanceMeters = 420,
                crowdLevel = CrowdLevel.MODERATE,
                delayRisk = DelayRisk.LOW,
                punctualityRate = 96,
                comfortScore = 9.2f,
                commuteScore = 89,
                scoreBreakdown = ScoreBreakdown(
                    timeScore = 88,
                    costScore = 85,
                    crowdScore = 82,
                    walkingScore = 94,
                    delayRiskScore = 95,
                    preferenceMatchScore = 98
                ),
                segments = listOf(
                    RouteSegment(TransportType.WALK_CYCLE, "Walk to Green Glen Metro Stn (Gate 2)", 5, 300, crowdLevel = CrowdLevel.LOW),
                    RouteSegment(TransportType.METRO, "Board Purple Line toward Tech Interchange", 14, 5200, "Line 2 (Train #14)", CrowdLevel.MODERATE),
                    RouteSegment(TransportType.CAMPUS_SHUTTLE, "Transfer to Smart Campus EV Shuttle #3", 6, 1800, "Shuttle 3A", CrowdLevel.LOW),
                    RouteSegment(TransportType.WALK_CYCLE, "Short walk into SIH Innovation Hub foyer", 2, 120, crowdLevel = CrowdLevel.LOW)
                ),
                waypoints = listOf(
                    RouteWaypoint("wp1", "Home (Green Glen)", 0.10f, 0.85f, TransportType.WALK_CYCLE),
                    RouteWaypoint("wp2", "Green Glen Metro", 0.25f, 0.70f, TransportType.METRO, true),
                    RouteWaypoint("wp3", "Central Interchange", 0.50f, 0.45f, TransportType.METRO, true),
                    RouteWaypoint("wp4", "Campus North Terminal", 0.75f, 0.28f, TransportType.CAMPUS_SHUTTLE, true),
                    RouteWaypoint("wp5", "SIH Tech Hub", 0.90f, 0.15f, TransportType.WALK_CYCLE)
                ),
                aiReasoning = "Matches preferred Metro transit with minimum walking (420m). Avoids the Silk Board road congestion and maintains a 96% on-time record.",
                warnings = listOf("Real-time telemetry green", "Next EV shuttle departs in 3 mins from Interchange")
            ),

            // Route A: Fastest
            RouteOption(
                id = "route_fastest_a",
                title = "Express Shared EV Cab + Rapid Flyover",
                category = RouteCategory.FASTEST,
                primaryTransport = TransportType.SHARED_AUTO,
                durationMinutes = 22,
                costInr = 65,
                walkingDistanceMeters = 180,
                crowdLevel = CrowdLevel.LOW,
                delayRisk = DelayRisk.MEDIUM,
                punctualityRate = 88,
                comfortScore = 8.6f,
                commuteScore = 82,
                scoreBreakdown = ScoreBreakdown(
                    timeScore = 97,
                    costScore = 65,
                    crowdScore = 92,
                    walkingScore = 98,
                    delayRiskScore = 72,
                    preferenceMatchScore = 76
                ),
                segments = listOf(
                    RouteSegment(TransportType.WALK_CYCLE, "Walk to Corner Pickup Stand", 2, 120, crowdLevel = CrowdLevel.LOW),
                    RouteSegment(TransportType.SHARED_AUTO, "Shared EV Transit via Elevated Expressway", 18, 7100, "EV Cabpool #9", CrowdLevel.LOW),
                    RouteSegment(TransportType.WALK_CYCLE, "Step directly into Hub entrance", 2, 60, crowdLevel = CrowdLevel.LOW)
                ),
                waypoints = listOf(
                    RouteWaypoint("wp1", "Home Pickup Point", 0.10f, 0.85f, TransportType.WALK_CYCLE),
                    RouteWaypoint("wp2", "Elevated Flyover Ramp", 0.35f, 0.60f, TransportType.SHARED_AUTO),
                    RouteWaypoint("wp3", "Tech Park Toll Plaza", 0.65f, 0.38f, TransportType.SHARED_AUTO),
                    RouteWaypoint("wp4", "SIH Tech Hub Entrance", 0.90f, 0.15f, TransportType.WALK_CYCLE)
                ),
                aiReasoning = "Fastest travel time (22 mins) by utilizing the elevated express toll corridor. However, cost (₹65) exceeds budget threshold.",
                warnings = listOf("Expressway toll applies", "Minor slowdown near Tech Park Toll plaza (+3 mins)")
            ),

            // Route B: Cheapest
            RouteOption(
                id = "route_cheapest_b",
                title = "Smart Electric City Bus 500D (Direct)",
                category = RouteCategory.CHEAPEST,
                primaryTransport = TransportType.ELECTRIC_BUS,
                durationMinutes = 36,
                costInr = 18,
                walkingDistanceMeters = 650,
                crowdLevel = CrowdLevel.HEAVY,
                delayRisk = DelayRisk.MEDIUM,
                punctualityRate = 84,
                comfortScore = 6.8f,
                commuteScore = 78,
                scoreBreakdown = ScoreBreakdown(
                    timeScore = 70,
                    costScore = 98,
                    crowdScore = 52,
                    walkingScore = 82,
                    delayRiskScore = 70,
                    preferenceMatchScore = 75
                ),
                segments = listOf(
                    RouteSegment(TransportType.WALK_CYCLE, "Walk to Outer Ring Road Bus Depot", 6, 450, crowdLevel = CrowdLevel.LOW),
                    RouteSegment(TransportType.ELECTRIC_BUS, "Board AC Electric Bus 500D", 26, 6800, "BMTC EV-500D", CrowdLevel.HEAVY),
                    RouteSegment(TransportType.WALK_CYCLE, "Walk from Campus Gate to Hub", 4, 200, crowdLevel = CrowdLevel.LOW)
                ),
                waypoints = listOf(
                    RouteWaypoint("wp1", "Green Glen Residence", 0.10f, 0.85f, TransportType.WALK_CYCLE),
                    RouteWaypoint("wp2", "Ring Road Bus Stop", 0.22f, 0.78f, TransportType.ELECTRIC_BUS),
                    RouteWaypoint("wp3", "Silk Board Junction", 0.48f, 0.55f, TransportType.ELECTRIC_BUS),
                    RouteWaypoint("wp4", "Campus South Gate", 0.80f, 0.26f, TransportType.ELECTRIC_BUS),
                    RouteWaypoint("wp5", "SIH Innovation Hub", 0.90f, 0.15f, TransportType.WALK_CYCLE)
                ),
                aiReasoning = "Most economical choice at only ₹18 (saving 48% vs metro). Note that standing crowd is anticipated between 8:30 AM and 9:00 AM.",
                warnings = listOf("Moderate to heavy morning passenger load on Bus 500D")
            ),

            // Route D: Alternative Active Transit
            RouteOption(
                id = "route_alt_d",
                title = "Smart SmartCycle + Direct Metro",
                category = RouteCategory.ALTERNATIVE,
                primaryTransport = TransportType.WALK_CYCLE,
                durationMinutes = 31,
                costInr = 25,
                walkingDistanceMeters = 950,
                crowdLevel = CrowdLevel.LOW,
                delayRisk = DelayRisk.LOW,
                punctualityRate = 94,
                comfortScore = 8.1f,
                commuteScore = 75,
                scoreBreakdown = ScoreBreakdown(
                    timeScore = 80,
                    costScore = 90,
                    crowdScore = 90,
                    walkingScore = 65,
                    delayRiskScore = 92,
                    preferenceMatchScore = 68
                ),
                segments = listOf(
                    RouteSegment(TransportType.WALK_CYCLE, "SmartCycle Dock at Green Glen -> Station", 8, 800, "Bike #412", CrowdLevel.LOW),
                    RouteSegment(TransportType.METRO, "Direct Metro Ride to Innovation Stn", 19, 5800, "Green Line", CrowdLevel.MODERATE),
                    RouteSegment(TransportType.WALK_CYCLE, "Walk into Campus Innovation Hub", 4, 150, crowdLevel = CrowdLevel.LOW)
                ),
                waypoints = listOf(
                    RouteWaypoint("wp1", "Bike Dock #12", 0.10f, 0.85f, TransportType.WALK_CYCLE),
                    RouteWaypoint("wp2", "Metro Station West", 0.30f, 0.65f, TransportType.METRO),
                    RouteWaypoint("wp3", "Innovation Stn Platform", 0.75f, 0.30f, TransportType.METRO),
                    RouteWaypoint("wp4", "SIH Innovation Hub", 0.90f, 0.15f, TransportType.WALK_CYCLE)
                ),
                aiReasoning = "Zero emissions and active health bonus (+180 kcal burned). Reliable zero delay path.",
                warnings = listOf("Dock availability: 6 cycles currently docked at Station West")
            )
        )
    }
}
