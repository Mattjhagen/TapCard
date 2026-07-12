package com.tapcard.app.di

import com.tapcard.app.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {
    val client: SupabaseClient? by lazy {
        if (BuildConfig.SUPABASE_PROJECT_URL.isNotBlank() && BuildConfig.SUPABASE_PUBLISHABLE_KEY.isNotBlank()) {
            createSupabaseClient(
                supabaseUrl = BuildConfig.SUPABASE_PROJECT_URL,
                supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
            ) {
                install(Auth) {
                    scheme = "tapcard"
                    host = "login-callback"
                }
                install(Postgrest)
            }
        } else {
            null
        }
    }
}
