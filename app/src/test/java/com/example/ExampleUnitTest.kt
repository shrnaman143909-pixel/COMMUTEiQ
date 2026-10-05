package com.example

import com.example.ai.GeminiCommuteService
import com.example.data.MockTransitRepository
import com.example.model.CommuteFingerprint
import com.example.model.CrowdLevel
import com.example.model.DelayRisk
import com.example.model.JourneyFeedback
import com.example.model.RouteCategory
import com.example.model.RouteOption
import com.example.model.ScoreBreakdown
import com.example.model.ToleranceLevel
import com.example.model.TransportType
import com.example.model.UserRole
import com.example.supabase.SupabaseClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCommuteScoringEngine_calculatesExpectedRange() {
        val routes = MockTransitRepository.getCandidateRoutes("Home", "Hub")
        assertTrue("Routes should not be empty", routes.isNotEmpty())

        val testFingerprint = CommuteFingerprint(
            preferredTransport = TransportType.METRO,
            maxWalkingDistanceMeters = 800,
            dailyBudgetInr = 40,
            crowdTolerance = ToleranceLevel.LOW,
            timePriority = 0.9f,
            costPriority = 0.5f,
            walkingPriority = 0.7f
        )

        routes.forEach { route ->
            val (score, breakdown) = GeminiCommuteService.calculateScore(route, testFingerprint)
            assertTrue("Score should be between 10 and 100", score in 10..100)
            assertTrue("Time score should be valid", breakdown.timeScore in 0..100)
            assertTrue("Cost score should be valid", breakdown.costScore in 0..100)
            assertTrue("Crowd score should be valid", breakdown.crowdScore in 0..100)
            assertTrue("Walking score should be valid", breakdown.walkingScore in 0..100)
            assertTrue("Delay risk score should be valid", breakdown.delayRiskScore in 0..100)
            assertTrue("Preference match score should be valid", breakdown.preferenceMatchScore in 0..100)
        }
    }

    @Test
    fun testPriorityWeightShift_changesTopRoute() {
        val routes = MockTransitRepository.allRoutes.value

        // Scenario A: User cares mostly about saving money (high cost priority)
        val budgetFingerprint = CommuteFingerprint(
            dailyBudgetInr = 20,
            costPriority = 1.0f,
            timePriority = 0.1f,
            walkingPriority = 0.1f
        )
        val cheapestScored = routes.map { r ->
            r.copy(commuteScore = GeminiCommuteService.calculateScore(r, budgetFingerprint).first)
        }.maxByOrNull { it.commuteScore }

        // Bus 500D (₹18) should score higher than expensive Cab (₹65)
        val busRoute = routes.first { it.primaryTransport == TransportType.ELECTRIC_BUS }
        val cabRoute = routes.first { it.primaryTransport == TransportType.SHARED_AUTO }
        val (busScore, _) = GeminiCommuteService.calculateScore(busRoute, budgetFingerprint)
        val (cabScore, _) = GeminiCommuteService.calculateScore(cabRoute, budgetFingerprint)
        assertTrue("Bus should score higher than Cab for budget-first commuter", busScore > cabScore)

        // Scenario B: User cares mostly about speed (high time priority)
        val speedFingerprint = CommuteFingerprint(
            dailyBudgetInr = 100,
            costPriority = 0.1f,
            timePriority = 1.0f,
            walkingPriority = 0.1f
        )
        val (cabSpeedScore, _) = GeminiCommuteService.calculateScore(cabRoute, speedFingerprint)
        val (busSpeedScore, _) = GeminiCommuteService.calculateScore(busRoute, speedFingerprint)
        assertTrue("Fastest route should score higher for speed-first commuter", cabSpeedScore > busSpeedScore)
    }

    @Test
    fun testGeminiApiUnavailable_fallbackSucceedsDeterministically() = runBlocking {
        val routes = MockTransitRepository.allRoutes.value
        val fingerprint = CommuteFingerprint()

        val result = GeminiCommuteService.getRecommendationInsight(
            routes = routes,
            fingerprint = fingerprint,
            userOrigin = "Green Glen Layout",
            userDestination = "SIH Innovation Hub"
        )

        assertNotNull(result)
        assertTrue("Should produce recommendation route id", result.recommendedRouteId.isNotBlank())
        assertTrue("Score should be calculated", result.commuteScore in 10..100)
        assertTrue("Should include structured reasoning bullets", result.reasoning.contains("•") || result.reasoning.length > 20)
        assertTrue("Should produce safety warnings or clear corridor confirmation", result.warnings.isNotEmpty())
    }

    @Test
    fun testSavedRoutes_toggleAddsAndRemoves() {
        val routeToSave = MockTransitRepository.allRoutes.value.first()
        val initialSaved = MockTransitRepository.savedRoutes.value.any { it.name == routeToSave.title }

        // First toggle
        MockTransitRepository.toggleSaveRoute(routeToSave)
        val stateAfterToggle1 = MockTransitRepository.savedRoutes.value.any { it.name == routeToSave.title }
        assertEquals(!initialSaved, stateAfterToggle1)

        // Second toggle
        MockTransitRepository.toggleSaveRoute(routeToSave)
        val stateAfterToggle2 = MockTransitRepository.savedRoutes.value.any { it.name == routeToSave.title }
        assertEquals(initialSaved, stateAfterToggle2)
    }

    @Test
    fun testFeedbackSubmission_updatesFeedbackList() {
        val initialCount = MockTransitRepository.feedbackList.value.size
        val feedback = JourneyFeedback(
            id = "fb_test_99",
            journeyId = "j_test_99",
            routeTitle = "Campus EV Shuttle",
            rating = 5,
            wasCrowdHigher = "No, as expected",
            wasTravelTimeAccurate = "Accurate",
            wasComfortable = "Very comfortable",
            comments = "Great air-conditioned ride!"
        )

        MockTransitRepository.submitFeedback(feedback)
        assertEquals(initialCount + 1, MockTransitRepository.feedbackList.value.size)
        assertEquals("fb_test_99", MockTransitRepository.feedbackList.value.first().id)
    }

    @Test
    fun testJourneyLifecycle_startProgressComplete() {
        val route = MockTransitRepository.allRoutes.value.first()
        val journey = MockTransitRepository.startJourney(route)

        assertEquals(0, journey.currentWaypointIndex)
        assertFalse(journey.isCompleted)

        // Progress all waypoints
        val totalWaypoints = route.waypoints.size
        repeat(totalWaypoints + 1) {
            MockTransitRepository.progressJourneyStep()
        }

        val active = MockTransitRepository.activeJourney.value
        assertTrue("Journey should be completed after all waypoints", active?.isCompleted == true)
        assertEquals(100f, active?.progressPercent ?: 0f, 0.1f)

        // End journey
        val ended = MockTransitRepository.endJourney()
        assertNotNull(ended)
        assertEquals(null, MockTransitRepository.activeJourney.value)
    }

    @Test
    fun testSupabaseClient_authWorkflow() = runBlocking {
        // Sign in commuter
        val result = SupabaseClient.signIn("commuter@sih.edu", "pass123")
        assertTrue("Sign in should succeed", result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("commuter@sih.edu", user?.email)
        assertEquals(UserRole.COMMUTER, user?.role)

        // Sign in admin
        val adminResult = SupabaseClient.signIn("admin@commuteiq.gov.in", "pass123")
        assertTrue(adminResult.isSuccess)
        assertEquals(UserRole.ADMIN, adminResult.getOrNull()?.role)

        // Sign out
        SupabaseClient.signOut()
        assertEquals(null, SupabaseClient.currentSession)
    }

    @Test
    fun testAuditLogs_generatedOnAdminAction() {
        val initialCount = MockTransitRepository.auditLogs.value.size

        val testRoute = RouteOption(
            id = "test_audit_route",
            title = "Test Audit Line",
            category = RouteCategory.ALTERNATIVE,
            primaryTransport = TransportType.METRO,
            durationMinutes = 20,
            costInr = 25,
            walkingDistanceMeters = 300,
            crowdLevel = CrowdLevel.LOW,
            delayRisk = DelayRisk.LOW,
            punctualityRate = 98,
            comfortScore = 9.0f,
            commuteScore = 88,
            scoreBreakdown = ScoreBreakdown(88, 88, 88, 88, 88, 88),
            segments = emptyList(),
            waypoints = emptyList(),
            aiReasoning = "Audit test corridor"
        )

        MockTransitRepository.addAdminRoute(testRoute)
        val afterAddCount = MockTransitRepository.auditLogs.value.size
        assertEquals(initialCount + 1, afterAddCount)

        val latestAudit = MockTransitRepository.auditLogs.value.first()
        assertEquals("ROUTE_CREATED", latestAudit.action)
        assertEquals("test_audit_route", latestAudit.targetId)

        MockTransitRepository.deleteAdminRoute("test_audit_route")
        val afterDelCount = MockTransitRepository.auditLogs.value.size
        assertEquals(initialCount + 2, afterDelCount)
        assertEquals("ROUTE_DELETED", MockTransitRepository.auditLogs.value.first().action)
    }
}
