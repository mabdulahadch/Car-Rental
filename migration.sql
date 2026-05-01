-- ============================================
-- Car Rental System - Supabase Migration SQL
-- Run this in Supabase SQL Editor if Prisma
-- migration doesn't work
-- ============================================

-- Booking table
CREATE TABLE IF NOT EXISTS booking (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customername TEXT NOT NULL,
    customeremail TEXT NOT NULL,
    customerphone TEXT NOT NULL,
    gender TEXT,
    pickupdate TEXT NOT NULL,
    returndate TEXT NOT NULL,
    totalamount DOUBLE PRECISION NOT NULL DEFAULT 0,
    withdriver BOOLEAN DEFAULT false,
    status TEXT DEFAULT 'PENDING',
    carid UUID NOT NULL REFERENCES car(id) ON DELETE CASCADE,
    userid TEXT,
    createdat TIMESTAMPTZ DEFAULT now()
);

-- Review table
CREATE TABLE IF NOT EXISTS review (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username TEXT NOT NULL,
    userimage TEXT,
    rating DOUBLE PRECISION NOT NULL DEFAULT 5.0,
    comment TEXT NOT NULL,
    carid UUID NOT NULL REFERENCES car(id) ON DELETE CASCADE,
    createdat TIMESTAMPTZ DEFAULT now()
);

-- Users table (if not exists)
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL
);

-- Favorites table (if not exists)
CREATE TABLE IF NOT EXISTS favorites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id TEXT NOT NULL,
    car_id TEXT NOT NULL
);

-- Add rating & reviewcount columns to car table if missing
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'car' AND column_name = 'rating') THEN
        ALTER TABLE car ADD COLUMN rating DOUBLE PRECISION DEFAULT 5.0;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'car' AND column_name = 'reviewcount') THEN
        ALTER TABLE car ADD COLUMN reviewcount INTEGER DEFAULT 0;
    END IF;
END $$;

-- Enable Row Level Security (optional but recommended)
ALTER TABLE booking ENABLE ROW LEVEL SECURITY;
ALTER TABLE review ENABLE ROW LEVEL SECURITY;

-- Allow public access policies (for dev — tighten for production)
CREATE POLICY "Allow all on booking" ON booking FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Allow all on review" ON review FOR ALL USING (true) WITH CHECK (true);
