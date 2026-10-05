-- =============================================================================
-- CommuteIQ – Supabase PostgreSQL Schema with Row Level Security (RLS)
-- SIH 2026 Student Innovation Project
-- =============================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. PROFILES (extends auth.users)
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    home_location TEXT DEFAULT 'Green Glen Layout',
    work_location TEXT DEFAULT 'SIH Tech & Innovation Hub',
    role TEXT NOT NULL DEFAULT 'COMMUTER' CHECK (role IN ('COMMUTER', 'ADMIN')),
    avatar_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 2. COMMUTE PREFERENCES (User Commute Fingerprint)
CREATE TABLE IF NOT EXISTS public.commute_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE UNIQUE,
    preferred_transport TEXT NOT NULL DEFAULT 'METRO',
    max_walking_distance_meters INT NOT NULL DEFAULT 800 CHECK (max_walking_distance_meters >= 100),
    daily_budget_inr INT NOT NULL DEFAULT 40 CHECK (daily_budget_inr >= 0),
    preferred_departure_time TEXT NOT NULL DEFAULT '08:30 AM',
    crowd_tolerance TEXT NOT NULL DEFAULT 'LOW' CHECK (crowd_tolerance IN ('LOW', 'MODERATE', 'HIGH')),
    time_priority REAL NOT NULL DEFAULT 0.85 CHECK (time_priority BETWEEN 0.0 AND 1.0),
    cost_priority REAL NOT NULL DEFAULT 0.50 CHECK (cost_priority BETWEEN 0.0 AND 1.0),
    walking_priority REAL NOT NULL DEFAULT 0.70 CHECK (walking_priority BETWEEN 0.0 AND 1.0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 3. TRANSPORT OPTIONS
CREATE TABLE IF NOT EXISTS public.transport_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mode TEXT NOT NULL UNIQUE,
    display_name TEXT NOT NULL,
    base_fare_inr INT NOT NULL DEFAULT 15,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 4. ROUTES
CREATE TABLE IF NOT EXISTS public.routes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    category TEXT NOT NULL DEFAULT 'BEST_MATCH' CHECK (category IN ('BEST_MATCH', 'FASTEST', 'CHEAPEST', 'ALTERNATIVE')),
    primary_transport TEXT NOT NULL,
    duration_minutes INT NOT NULL CHECK (duration_minutes > 0),
    cost_inr INT NOT NULL CHECK (cost_inr >= 0),
    walking_distance_meters INT NOT NULL CHECK (walking_distance_meters >= 0),
    crowd_level TEXT NOT NULL DEFAULT 'MODERATE' CHECK (crowd_level IN ('LOW', 'MODERATE', 'HEAVY', 'SEVERE')),
    delay_risk TEXT NOT NULL DEFAULT 'LOW' CHECK (delay_risk IN ('LOW', 'MEDIUM', 'HIGH')),
    punctuality_rate INT NOT NULL DEFAULT 95 CHECK (punctuality_rate BETWEEN 0 AND 100),
    comfort_score REAL NOT NULL DEFAULT 8.5 CHECK (comfort_score BETWEEN 0.0 AND 10.0),
    commute_score INT NOT NULL DEFAULT 85 CHECK (commute_score BETWEEN 0 AND 100),
    ai_reasoning TEXT,
    warnings TEXT[] DEFAULT '{}',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 5. ROUTE SEGMENTS
CREATE TABLE IF NOT EXISTS public.route_segments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    route_id UUID NOT NULL REFERENCES public.routes(id) ON DELETE CASCADE,
    segment_order INT NOT NULL,
    mode TEXT NOT NULL,
    instruction TEXT NOT NULL,
    duration_minutes INT NOT NULL,
    distance_meters INT NOT NULL,
    route_code TEXT,
    crowd_level TEXT DEFAULT 'MODERATE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 6. JOURNEYS
CREATE TABLE IF NOT EXISTS public.journeys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    route_id UUID REFERENCES public.routes(id) ON DELETE SET NULL,
    route_title TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'IN_PROGRESS' CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    start_time TIMESTAMPTZ NOT NULL DEFAULT now(),
    end_time TIMESTAMPTZ,
    duration_minutes INT,
    fare_paid_inr INT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 7. JOURNEY FEEDBACK
CREATE TABLE IF NOT EXISTS public.journey_feedback (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    journey_id UUID REFERENCES public.journeys(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    route_title TEXT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    was_crowd_higher TEXT NOT NULL,
    was_travel_time_accurate TEXT NOT NULL,
    was_comfortable TEXT NOT NULL,
    comments TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 8. SAVED ROUTES
CREATE TABLE IF NOT EXISTS public.saved_routes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    from_location TEXT NOT NULL,
    to_location TEXT NOT NULL,
    transport_type TEXT NOT NULL,
    typical_score INT NOT NULL,
    avg_duration_minutes INT NOT NULL,
    avg_cost_inr INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 9. COMMUTE INSIGHTS
CREATE TABLE IF NOT EXISTS public.commute_insights (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE UNIQUE,
    avg_commute_minutes INT NOT NULL DEFAULT 32,
    avg_spending_inr INT NOT NULL DEFAULT 28,
    most_used_transport TEXT NOT NULL DEFAULT 'Smart Metro Line',
    most_reliable_route TEXT NOT NULL DEFAULT 'Purple Line Metro',
    frequent_delay_pattern TEXT NOT NULL DEFAULT 'Silk Board Signal congestion (8:40 - 9:15 AM)',
    co2_saved_kg REAL NOT NULL DEFAULT 4.2,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 10. ADMIN USERS (Secure role table - not editable by clients)
CREATE TABLE IF NOT EXISTS public.admin_users (
    id UUID PRIMARY KEY REFERENCES public.profiles(id) ON DELETE CASCADE,
    permissions TEXT[] DEFAULT '{"manage_routes", "view_analytics", "manage_alerts"}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 11. ADMIN AUDIT LOGS
CREATE TABLE IF NOT EXISTS public.admin_audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admin_id UUID NOT NULL REFERENCES public.profiles(id),
    action TEXT NOT NULL CHECK (action IN ('ROUTE_CREATED', 'ROUTE_UPDATED', 'ROUTE_DELETED', 'USER_MODIFIED', 'DELAY_ALERT_BROADCAST')),
    target_id TEXT,
    details JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =============================================================================
-- INDEXES FOR PERFORMANCE
-- =============================================================================
CREATE INDEX IF NOT EXISTS idx_journeys_user_id ON public.journeys(user_id);
CREATE INDEX IF NOT EXISTS idx_feedback_user_id ON public.journey_feedback(user_id);
CREATE INDEX IF NOT EXISTS idx_saved_routes_user_id ON public.saved_routes(user_id);
CREATE INDEX IF NOT EXISTS idx_route_segments_route_id ON public.route_segments(route_id);
CREATE INDEX IF NOT EXISTS idx_audit_admin_id ON public.admin_audit_logs(admin_id);

-- =============================================================================
-- HELPER FUNCTIONS FOR SECURITY RULES
-- =============================================================================
CREATE OR REPLACE FUNCTION public.is_admin()
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1 FROM public.admin_users WHERE id = auth.uid()
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Trigger to auto-create profile on auth signup
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.profiles (id, email, name, role)
    VALUES (
        new.id,
        new.email,
        COALESCE(new.raw_user_meta_data->>'full_name', split_part(new.email, '@', 1)),
        'COMMUTER'
    )
    ON CONFLICT (id) DO NOTHING;

    INSERT INTO public.commute_preferences (user_id)
    VALUES (new.id)
    ON CONFLICT (user_id) DO NOTHING;

    INSERT INTO public.commute_insights (user_id)
    VALUES (new.id)
    ON CONFLICT (user_id) DO NOTHING;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE PROCEDURE public.handle_new_user();

-- =============================================================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- =============================================================================

-- Enable RLS on every table
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.commute_preferences ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.transport_options ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.routes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.route_segments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.journeys ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.journey_feedback ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.saved_routes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.commute_insights ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.admin_users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.admin_audit_logs ENABLE ROW LEVEL SECURITY;

-- Profiles: Users see/edit own profile, admins can view all
CREATE POLICY "Users can view own profile"
    ON public.profiles FOR SELECT
    USING (auth.uid() = id OR public.is_admin());

CREATE POLICY "Users can update own profile"
    ON public.profiles FOR UPDATE
    USING (auth.uid() = id);

-- Commute Preferences: strictly private
CREATE POLICY "Users can view own preferences"
    ON public.commute_preferences FOR SELECT
    USING (auth.uid() = user_id OR public.is_admin());

CREATE POLICY "Users can update own preferences"
    ON public.commute_preferences FOR UPDATE
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own preferences"
    ON public.commute_preferences FOR INSERT
    WITH CHECK (auth.uid() = user_id);

-- Routes & Segments: Publicly readable for transit, writeable ONLY by admins
CREATE POLICY "Anyone can view active routes"
    ON public.routes FOR SELECT
    USING (is_active = true OR public.is_admin());

CREATE POLICY "Admins can insert routes"
    ON public.routes FOR INSERT
    WITH CHECK (public.is_admin());

CREATE POLICY "Admins can update routes"
    ON public.routes FOR UPDATE
    USING (public.is_admin());

CREATE POLICY "Admins can delete routes"
    ON public.routes FOR DELETE
    USING (public.is_admin());

CREATE POLICY "Anyone can view route segments"
    ON public.route_segments FOR SELECT
    USING (true);

CREATE POLICY "Admins can manage route segments"
    ON public.route_segments FOR ALL
    USING (public.is_admin());

-- Journeys: Strictly private per commuter
CREATE POLICY "Users can view own journeys"
    ON public.journeys FOR SELECT
    USING (auth.uid() = user_id OR public.is_admin());

CREATE POLICY "Users can insert own journeys"
    ON public.journeys FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own journeys"
    ON public.journeys FOR UPDATE
    USING (auth.uid() = user_id);

-- Feedback: Users view and submit own feedback, admins view all
CREATE POLICY "Users can view own feedback or admin views all"
    ON public.journey_feedback FOR SELECT
    USING (auth.uid() = user_id OR public.is_admin());

CREATE POLICY "Users can submit own feedback"
    ON public.journey_feedback FOR INSERT
    WITH CHECK (auth.uid() = user_id);

-- Saved Routes: Private per commuter
CREATE POLICY "Users manage own saved routes"
    ON public.saved_routes FOR ALL
    USING (auth.uid() = user_id);

-- Commute Insights: Private per commuter
CREATE POLICY "Users view own insights"
    ON public.commute_insights FOR SELECT
    USING (auth.uid() = user_id OR public.is_admin());

-- Admin tables: Only admins can view
CREATE POLICY "Admins can view admin_users list"
    ON public.admin_users FOR SELECT
    USING (public.is_admin());

CREATE POLICY "Admins can view audit logs"
    ON public.admin_audit_logs FOR SELECT
    USING (public.is_admin());

CREATE POLICY "Admins can insert audit logs"
    ON public.admin_audit_logs FOR INSERT
    WITH CHECK (public.is_admin());
