package com.tapcard.app.viewmodel

import androidx.lifecycle.ViewModel
import com.tapcard.app.data.models.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {
    private val _profileState = MutableStateFlow(Profile())
    val profileState: StateFlow<Profile> = _profileState.asStateFlow()
    
    private val _isSaved = MutableStateFlow(false)
    val isSaved = _isSaved.asStateFlow()

    fun updateProfile(newProfile: Profile) {
        _profileState.update { newProfile }
    }

    fun saveProfile() {
        // Mocking a network save operation to Supabase
        _isSaved.value = true
    }

    fun getShareableUrl(): String {
        val username = profileState.value.username.takeIf { it.isNotBlank() } ?: "user123"
        return "https://tapcard.app/card/$username"
    }
}
