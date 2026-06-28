package com.tapcard.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tapcard.app.data.local.AppDatabase
import com.tapcard.app.data.repository.LocalProfileRepositoryImpl
import com.tapcard.app.ui.navigation.AppNavigation
import com.tapcard.app.ui.theme.TapCardTheme
import com.tapcard.app.ui.viewmodel.ProfileViewModel
import com.tapcard.app.ui.viewmodel.ProfileViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val repository = LocalProfileRepositoryImpl(database.profileDao())
        val factory = ProfileViewModelFactory(repository)

        setContent {
            val sharedViewModel: ProfileViewModel = viewModel(factory = factory)
            
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
}
