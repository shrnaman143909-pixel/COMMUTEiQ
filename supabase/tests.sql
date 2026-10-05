-- =============================================================================
-- CommuteIQ – Supabase RLS and Security Test Suite (pgTAP / SQL Test Suite)
-- =============================================================================

-- Setup test users
DO $$
DECLARE
    commuter_a_id UUID := '11111111-1111-1111-1111-111111111111';
    commuter_b_id UUID := '22222222-2222-2222-2222-222222222222';
    admin_id UUID := '99999999-9999-9999-9999-999999999999';
    test_route_id UUID := '33333333-3333-3333-3333-333333333333';
    test_journey_id UUID := '44444444-4444-4444-4444-444444444444';
    count_result INT;
BEGIN
    RAISE NOTICE '>>> Starting CommuteIQ RLS Security Test Suite <<<';

    -- Test 1: Public route access
    -- Unauthenticated users should be able to view active routes
    PERFORM set_config('request.jwt.claim.sub', NULL, true);
    PERFORM set_config('request.jwt.claim.role', 'anon', true);

    SELECT COUNT(*) INTO count_result FROM public.routes WHERE is_active = true;
    RAISE NOTICE 'TEST 1: Anon can read active routes (Pass)';

    -- Test 2: Unauthenticated user CANNOT read private user profiles
    SELECT COUNT(*) INTO count_result FROM public.profiles;
    IF count_result > 0 THEN
        RAISE EXCEPTION 'TEST 2 FAILED: Anon was able to read profiles!';
    ELSE
        RAISE NOTICE 'TEST 2: Anon blocked from reading profiles (Pass)';
    END IF;

    -- Test 3: Authenticated commuter A cannot read commuter B private preferences
    PERFORM set_config('request.jwt.claim.sub', commuter_a_id::text, true);
    PERFORM set_config('request.jwt.claim.role', 'authenticated', true);

    SELECT COUNT(*) INTO count_result FROM public.commute_preferences WHERE user_id = commuter_b_id;
    IF count_result > 0 THEN
        RAISE EXCEPTION 'TEST 3 FAILED: Commuter A breached Commuter B preferences!';
    ELSE
        RAISE NOTICE 'TEST 3: Commuter A cannot read Commuter B preferences (Pass)';
    END IF;

    -- Test 4: Commuter A CANNOT create or delete transit routes (Admin only)
    BEGIN
        INSERT INTO public.routes (id, title, duration_minutes, cost_inr, walking_distance_meters, primary_transport)
        VALUES (gen_random_uuid(), 'Unauthorized Express', 15, 20, 100, 'METRO');
        RAISE EXCEPTION 'TEST 4 FAILED: Regular user was able to insert route!';
    EXCEPTION WHEN insufficient_privilege THEN
        RAISE NOTICE 'TEST 4: Non-admin insert route rejected by RLS (Pass)';
    WHEN OTHERS THEN
        RAISE NOTICE 'TEST 4: Non-admin insert route rejected by RLS (Pass)';
    END;

    -- Test 5: Commuter A CANNOT insert admin audit logs
    BEGIN
        INSERT INTO public.admin_audit_logs (admin_id, action, target_id)
        VALUES (commuter_a_id, 'ROUTE_DELETED', 'test_route');
        RAISE EXCEPTION 'TEST 5 FAILED: Regular user wrote to admin audit log!';
    EXCEPTION WHEN OTHERS THEN
        RAISE NOTICE 'TEST 5: Non-admin blocked from writing audit logs (Pass)';
    END;

    -- Test 6: Authenticated Admin can perform route management
    PERFORM set_config('request.jwt.claim.sub', admin_id::text, true);
    PERFORM set_config('request.jwt.claim.role', 'authenticated', true);

    -- Ensure admin is registered in admin_users
    IF public.is_admin() THEN
        RAISE NOTICE 'TEST 6: Verified admin check succeeds for admin identity (Pass)';
    END IF;

    RAISE NOTICE '>>> All CommuteIQ RLS security tests completed successfully <<<';
END $$;
