package com.tapcard.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tapcard.app.ui.navigation.AppNavigation
import com.tapcard.app.ui.theme.TapCardTheme
import com.tapcard.app.ui.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.tapcard.app.di.SupabaseClientProvider
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.handleDeeplinks

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.tapcard.app.utils.AnalyticsManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle deep link if app is launched from it
        intent?.let {
            try {
                SupabaseClientProvider.client?.handleDeeplinks(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        try {
            AnalyticsManager.initialize(Firebase.analytics)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            val sharedViewModel: ProfileViewModel = hiltViewModel()
            
            TapCardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(sharedViewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        try {
            SupabaseClientProvider.client?.handleDeeplinks(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
