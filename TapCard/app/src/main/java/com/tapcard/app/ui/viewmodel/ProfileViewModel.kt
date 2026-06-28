package com.tapcard.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tapcard.app.domain.model.Profile
import com.tapcard.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) : ViewModel() {
    private val _profileState = MutableStateFlow(Profile())
    val profileState: StateFlow<Profile> = _profileState.asStateFlow()
    
    private val _isSaved = MutableStateFlow(false)
    val isSaved = _isSaved.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getProfileFlow().collect { profile ->
                if (profile != null) {
                    _profileState.value = profile
                }
            }
        }
    }

    fun updateProfile(newProfile: Profile) {
        _profileState.update { newProfile }
    }

    fun saveProfile() {
        viewModelScope.launch {
            repository.saveProfile(_profileState.value)
            _isSaved.value = true
        }
    }

    fun getShareableUrl(): String {
        val username = profileState.value.username.takeIf { it.isNotBlank() } ?: "user123"
        return "https://tapcard.app/card/$username"
    }
}
