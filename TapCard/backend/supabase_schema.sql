-- Supabase Schema for TapCard MVP

-- Enable RLS
ALTER TABLE auth.users ENABLE ROW LEVEL SECURITY;

-- Profiles table
CREATE TABLE public.profiles (
    id UUID REFERENCES auth.users NOT NULL PRIMARY KEY,
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
    WITH CHECK ( auth.uid() = id );

-- Users can update their own profile
CREATE POLICY "Users can update own profile."
    ON public.profiles FOR UPDATE
    USING ( auth.uid() = id );

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
