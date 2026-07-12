-- Supabase Schema for TapCard MVP
-- Note: do NOT try to ALTER auth.users - it is owned by Supabase's internal
-- auth role and fails with "must be owner of table users" in the SQL editor.
-- The auth schema is managed by Supabase; policies below only reference auth.uid().

-- Profiles table
CREATE TABLE public.profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES auth.users NOT NULL,
    profile_name TEXT NOT NULL DEFAULT 'Personal',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::TEXT, NOW()) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::TEXT, NOW()) NOT NULL,
    username TEXT UNIQUE NOT NULL,
    full_name TEXT,    
    job_title TEXT,
    company TEXT,
    phone TEXT,
    email TEXT,
    website TEXT,
    theme_color_hex TEXT DEFAULT '#000000',
    is_dark_theme BOOLEAN DEFAULT true,
    is_public BOOLEAN DEFAULT true,
    profile_photo_url TEXT,
    company_logo_url TEXT
);

-- RLS Policies
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

-- Allow public read access to profiles where is_public is true
CREATE POLICY "Public profiles are viewable by everyone."
    ON public.profiles FOR SELECT
    USING ( is_public = true );

-- Users can insert their own profile
CREATE POLICY "Users can insert their own profile."
    ON public.profiles FOR INSERT
    WITH CHECK ( auth.uid() = user_id );

-- Users can update their own profile
CREATE POLICY "Users can update own profile."
    ON public.profiles FOR UPDATE
    USING ( auth.uid() = user_id );

-- Feedback table
CREATE TABLE public.feedback (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::TEXT, NOW()) NOT NULL,
    type TEXT NOT NULL,
    description TEXT NOT NULL,
    app_version TEXT,
    android_version TEXT,
    device_model TEXT,
    build_type TEXT,
    user_id UUID REFERENCES auth.users
);

ALTER TABLE public.feedback ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can insert feedback"
    ON public.feedback FOR INSERT
    WITH CHECK ( true ); -- Allow anonymous and authenticated

CREATE POLICY "Users can view their own feedback"
    ON public.feedback FOR SELECT
    USING ( auth.uid() = user_id );

-- Set up Storage for profile images
INSERT INTO storage.buckets (id, name, public) 
VALUES ('profile-images', 'profile-images', true)
ON CONFLICT (id) DO NOTHING;

-- Storage Policies
-- Allow public access to all images
CREATE POLICY "Public image access" 
ON storage.objects FOR SELECT 
USING ( bucket_id = 'profile-images' );

-- Users can upload their own images to their folder
CREATE POLICY "Users can upload their own images" 
ON storage.objects FOR INSERT 
WITH CHECK (
    bucket_id = 'profile-images' 
    AND auth.uid()::text = (storage.foldername(name))[1]
);

-- Users can update their own images
CREATE POLICY "Users can update their own images" 
ON storage.objects FOR UPDATE 
USING (
    bucket_id = 'profile-images' 
    AND auth.uid()::text = (storage.foldername(name))[1]
);

-- Users can delete their own images
CREATE POLICY "Users can delete their own images" 
ON storage.objects FOR DELETE 
USING (
    bucket_id = 'profile-images' 
    AND auth.uid()::text = (storage.foldername(name))[1]
);

-- Analytics table
CREATE TABLE public.analytics_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::TEXT, NOW()) NOT NULL,
    profile_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE,
    event_type TEXT NOT NULL, -- e.g., 'profile_view', 'qr_scan', 'nfc_open', 'contact_download'
    source TEXT, -- e.g., 'web', 'nfc', 'qr'
    user_agent TEXT,
    ip_address TEXT -- Consider privacy implications; might want to hash or exclude
);

ALTER TABLE public.analytics_events ENABLE ROW LEVEL SECURITY;

-- Allow anonymous inserts for analytics
CREATE POLICY "Anyone can insert analytics events"
    ON public.analytics_events FOR INSERT
    WITH CHECK ( true );

-- Profile owners can read their own analytics
CREATE POLICY "Users can view analytics for their own profiles"
    ON public.analytics_events FOR SELECT
    USING (
        profile_id IN (
            SELECT id FROM public.profiles WHERE user_id = auth.uid()
        )
    );
