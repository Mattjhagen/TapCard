package com.tapcard.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tapcard.app.domain.model.Profile
import com.tapcard.app.domain.model.SyncStatus
import com.tapcard.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.graphics.Bitmap
import android.content.Intent
import com.tapcard.app.utils.QRExportService
import com.tapcard.app.utils.AnalyticsManager
import com.tapcard.app.utils.NfcService
import com.tapcard.app.utils.NfcState
import android.app.Activity

enum class UsernameValidationState {
    IDLE,
    CHECKING,
    AVAILABLE,
    TAKEN,
    INVALID_FORMAT,
    SIGN_IN_TO_VALIDATE,
    SUPABASE_NOT_CONFIGURED
}

@OptIn(FlowPreview::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val qrExportService: QRExportService,
    private val nfcService: NfcService
) : ViewModel() {
    private val _profileState = MutableStateFlow(Profile())
    val profileState: StateFlow<Profile> = _profileState.asStateFlow()

    private val _profilesList = MutableStateFlow<List<Profile>>(emptyList())
    val profilesList: StateFlow<List<Profile>> = _profilesList.asStateFlow()
    
    private val _nfcState = MutableStateFlow(nfcService.getNfcState())
    val nfcState: StateFlow<NfcState> = _nfcState.asStateFlow()
    
    private val _nfcProgrammingResult = MutableSharedFlow<Pair<Boolean, String>>()
    val nfcProgrammingResult = _nfcProgrammingResult.asSharedFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved = _isSaved.asStateFlow()

    private val _usernameValidationState = MutableStateFlow(UsernameValidationState.IDLE)
    val usernameValidationState = _usernameValidationState.asStateFlow()

    private val _usernameInputFlow = MutableStateFlow("")

    val syncStatus: StateFlow<SyncStatus> = repository.syncStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SyncStatus.SAVED_LOCALLY
        )

    val syncError: StateFlow<String?> = repository.syncError
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            repository.getProfileFlow().collect { profile ->
                if (profile != null) {
                    _profileState.value = profile
                }
            }
        }

        viewModelScope.launch {
            repository.getAllProfilesFlow().collect { list ->
                _profilesList.value = list
            }
        }

        viewModelScope.launch {
            _usernameInputFlow
                .debounce(500)
                .distinctUntilChanged()
                .collect { username ->
                    performUsernameValidation(username)
                }
        }
    }

    fun updateProfile(newProfile: Profile) {
        _profileState.update { newProfile }
    }

    fun switchProfile(id: String) {
        repository.setActiveProfileId(id)
    }

    fun createNewProfile(name: String) {
        val newProfile = Profile(
            profileName = name,
            profileSlug = computeSlug(name)
        )
        viewModelScope.launch {
            repository.saveProfile(newProfile)
            repository.setActiveProfileId(newProfile.id)
        }
    }

    companion object {
        /**
         * Converts a display name to a URL-safe slug.
         * "Real Estate" → "real-estate", "Mortgage Loan Officer" → "mortgage-loan-officer"
         * This is the single source of truth for slug generation.
         */
        fun computeSlug(name: String): String {
            return name
                .trim()
                .lowercase()
                .replace(Regex("[^a-z0-9\\s-]"), "")  // strip non-alphanumeric
                .replace(Regex("\\s+"), "-")           // spaces → hyphen
                .replace(Regex("-{2,}"), "-")          // collapse double hyphens
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            repository.saveProfile(_profileState.value)
            _isSaved.value = true
            AnalyticsManager.logCardSaved()
        }
    }

    fun getShareableUrl(): String {
        val username = profileState.value.username.ifBlank { profileState.value.id.toString() }
        // Use the stored slug — never re-derive from display name at runtime.
        val slug = profileState.value.profileSlug.ifBlank { computeSlug(profileState.value.profileName) }
        return if (slug == "personal" || slug == "default") {
            "https://tapcard.space/u/$username"
        } else {
            "https://tapcard.space/u/$username/$slug"
        }
    }

    fun onUsernameChanged(username: String) {
        // Enforce basic characters during typing if we want, but better to let them type and show INVALID_FORMAT
        _usernameInputFlow.value = username
        _usernameValidationState.value = UsernameValidationState.CHECKING
    }

    private suspend fun performUsernameValidation(username: String) {
        if (username.isBlank()) {
            _usernameValidationState.value = UsernameValidationState.IDLE
            return
        }

        if (!username.matches(Regex("^[a-z0-9-]{3,30}$"))) {
            _usernameValidationState.value = UsernameValidationState.INVALID_FORMAT
            return
        }

        _usernameValidationState.value = UsernameValidationState.CHECKING

        // Only checking if we need to call Supabase. 
        // We allow local-only save if signed out, but we should let user know.
        val currentSyncStatus = syncStatus.value
        if (currentSyncStatus == SyncStatus.SIGN_IN_TO_SYNC) {
            _usernameValidationState.value = UsernameValidationState.SIGN_IN_TO_VALIDATE
            return
        }

        val isAvailable = repository.validateUsernameUniqueness(username)
        if (isAvailable) {
            _usernameValidationState.value = UsernameValidationState.AVAILABLE
        } else {
            // Wait, if it's taken, is it taken by US? 
            // For now, if we get here and it's our own username, it shouldn't show TAKEN.
            if (profileState.value.username == username) {
                _usernameValidationState.value = UsernameValidationState.AVAILABLE
            } else {
                _usernameValidationState.value = UsernameValidationState.TAKEN
            }
        }
    }

    suspend fun validateUsername(username: String): Boolean {
        return repository.validateUsernameUniqueness(username)
    }

    fun saveQrToGallery(bitmap: Bitmap): Boolean {
        AnalyticsManager.logQrGenerated()
        return qrExportService.saveQrToGallery(bitmap)
    }

    fun shareQrCode(bitmap: Bitmap, text: String): Intent? {
        AnalyticsManager.logQrShared()
        return qrExportService.shareQrCode(bitmap, text)
    }

    fun checkNfcState() {
        _nfcState.value = nfcService.getNfcState()
    }

    fun startNfcProgramming(activity: Activity): Boolean {
        checkNfcState()
        return nfcService.startTagProgramming(activity, getShareableUrl()) { success, message ->
            viewModelScope.launch {
                _nfcProgrammingResult.emit(Pair(success, message))
            }
        }
    }

    fun stopNfcProgramming(activity: Activity) {
        nfcService.stopTagProgramming(activity)
    }
}
