package com.example.model

enum class UserRole {
    COMMUTER,
    ADMIN
}

enum class TransportType(val displayName: String, val iconName: String) {
    METRO("Metro Rail", "metro"),
    ELECTRIC_BUS("Smart EV Bus", "bus"),
    CAMPUS_SHUTTLE("Campus Shuttle", "shuttle"),
    SHARED_AUTO("EV Shared Auto", "auto"),
    WALK_CYCLE("Walk & Cycle", "walk")
}

enum class ToleranceLevel(val label: String) {
    LOW("Low (Prefers empty/seated)"),
    MODERATE("Moderate (Standard crowd ok)"),
    HIGH("High (Speed over comfort)")
}

enum class CrowdLevel(val label: String, val scoreWeight: Int) {
    LOW("Low Crowd (Seats Available)", 95),
    MODERATE("Moderate (Comfortable)", 75),
    HEAVY("Heavy (Standing Room)", 45),
    SEVERE("Peak Crowd (Congested)", 20)
}

enum class DelayRisk(val label: String, val penaltyMinutes: Int) {
    LOW("Low Risk (< 2 min)", 1),
    MEDIUM("Moderate Risk (3-7 min)", 5),
    HIGH("High Delay Risk (8-15 min)", 12)
}

enum class RouteCategory(val badgeTitle: String) {
    BEST_MATCH("Recommended for You"),
    FASTEST("Fastest Route"),
    CHEAPEST("Cheapest Option"),
    ALTERNATIVE("Alternative Transit")
}

data class CommuteFingerprint(
    val preferredTransport: TransportType = TransportType.METRO,
    val maxWalkingDistanceMeters: Int = 800,
    val dailyBudgetInr: Int = 40,
    val preferredDepartureTime: String = "08:30 AM",
    val crowdTolerance: ToleranceLevel = ToleranceLevel.LOW,
    val timePriority: Float = 0.85f,   // 0.0 to 1.0
    val costPriority: Float = 0.50f,   // 0.0 to 1.0
    val walkingPriority: Float = 0.70f // 0.0 to 1.0 (higher means less walking preferred)
) {
    val adaptabilityScore: Int
        get() {
            val balance = 100 - (kotlin.math.abs(timePriority - costPriority) * 20).toInt()
            return balance.coerceIn(60, 98)
        }
}

data class UserProfile(
    val id: String = "usr_demo_101",
    val name: String = "Naman Sharma",
    val email: String = "commuter@sih.edu",
    val role: UserRole = UserRole.COMMUTER,
    val homeLocation: String = "Green Glen Layout",
    val workLocation: String = "SIH Tech & Innovation Hub",
    val fingerprint: CommuteFingerprint = CommuteFingerprint()
)

data class RouteSegment(
    val mode: TransportType,
    val instruction: String,
    val durationMinutes: Int,
    val distanceMeters: Int,
    val routeCode: String? = null,
    val crowdLevel: CrowdLevel = CrowdLevel.MODERATE
)

data class RouteWaypoint(
    val id: String,
    val name: String,
    val xPercent: Float, // Relative X coordinate (0.0 to 1.0) on route canvas
    val yPercent: Float, // Relative Y coordinate (0.0 to 1.0) on route canvas
    val transportType: TransportType,
    val isTransferPoint: Boolean = false
)

data class ScoreBreakdown(
    val timeScore: Int,
    val costScore: Int,
    val crowdScore: Int,
    val walkingScore: Int,
    val delayRiskScore: Int,
    val preferenceMatchScore: Int
)

data class RouteOption(
    val id: String,
    val title: String,
    val category: RouteCategory,
    val primaryTransport: TransportType,
    val durationMinutes: Int,
    val costInr: Int,
    val walkingDistanceMeters: Int,
    val crowdLevel: CrowdLevel,
    val delayRisk: DelayRisk,
    val punctualityRate: Int, // e.g. 96%
    val comfortScore: Float, // e.g. 8.5 / 10
    val commuteScore: Int, // 0 - 100
    val scoreBreakdown: ScoreBreakdown,
    val segments: List<RouteSegment>,
    val waypoints: List<RouteWaypoint>,
    val aiReasoning: String,
    val warnings: List<String> = emptyList(),
    val isSimulatedDemo: Boolean = true
)

data class ActiveJourney(
    val journeyId: String,
    val route: RouteOption,
    val startTimeMillis: Long = System.currentTimeMillis(),
    val currentWaypointIndex: Int = 0,
    val progressPercent: Float = 0f,
    val etaMinutes: Int = route.durationMinutes,
    val delayStatus: String = "On Schedule (Real-time signals green)",
    val currentCrowd: CrowdLevel = route.crowdLevel,
    val isCompleted: Boolean = false
)

data class JourneyFeedback(
    val id: String,
    val journeyId: String,
    val routeTitle: String,
    val rating: Int, // 1 to 5
    val wasCrowdHigher: String,
    val wasTravelTimeAccurate: String,
    val wasComfortable: String,
    val comments: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class SavedRoute(
    val id: String,
    val name: String,
    val from: String,
    val to: String,
    val transportType: TransportType,
    val typicalScore: Int,
    val avgDurationMinutes: Int,
    val avgCostInr: Int
)

data class WeeklyTripStat(
    val dayLabel: String,
    val minutes: Int,
    val costInr: Int,
    val onTime: Boolean
)

data class CommuteInsights(
    val avgCommuteMinutes: Int = 32,
    val avgSpendingInr: Int = 28,
    val mostUsedTransport: String = "Smart Metro Line",
    val mostReliableRoute: String = "Route C (96% On-Time)",
    val frequentDelayPattern: String = "Silk Board Signal congestion (8:40 - 9:15 AM)",
    val co2SavedKg: Float = 4.2f,
    val weeklyTrips: List<WeeklyTripStat> = listOf(
        WeeklyTripStat("Mon", 34, 30, true),
        WeeklyTripStat("Tue", 31, 25, true),
        WeeklyTripStat("Wed", 40, 30, false),
        WeeklyTripStat("Thu", 29, 25, true),
        WeeklyTripStat("Fri", 28, 25, true)
    )
)

data class AdminAuditLog(
    val id: String,
    val adminEmail: String,
    val action: String, // e.g. ROUTE_CREATED, ROUTE_UPDATED, ROUTE_DELETED
    val targetId: String,
    val details: String,
    val timestampMillis: Long = System.currentTimeMillis()
)
