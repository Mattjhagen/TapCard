package com.tapcard.app.data.repository

import com.tapcard.app.data.local.ProfileDao
import com.tapcard.app.data.local.toDomainModel
import com.tapcard.app.data.local.toEntity
import com.tapcard.app.data.remote.RemoteProfileDto
import com.tapcard.app.data.remote.toRemoteDto
import com.tapcard.app.di.SupabaseClientProvider
import com.tapcard.app.domain.model.Profile
import com.tapcard.app.domain.model.SyncStatus
import com.tapcard.app.domain.repository.ProfileRepository
import com.tapcard.app.utils.ImageCompressor
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.HttpRequestException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context

@Singleton
class SyncProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
    private val context: Context
) : ProfileRepository {

    private val client = SupabaseClientProvider.client
    
    private val _syncStatus = MutableStateFlow(SyncStatus.SAVED_LOCALLY)
    override val syncStatus: Flow<SyncStatus> = _syncStatus.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    override val syncError: Flow<String?> = _syncError.asStateFlow()

    private val prefs = context.getSharedPreferences("tapcard_prefs", Context.MODE_PRIVATE)

    private val _activeProfileId = MutableStateFlow<String?>(prefs.getString("active_profile_id", null))
    override val activeProfileIdFlow: Flow<String?> = _activeProfileId.asStateFlow()

    override fun setActiveProfileId(id: String) {
        prefs.edit().putString("active_profile_id", id).apply()
        _activeProfileId.value = id
    }

    init {
        client?.auth?.let { auth ->
            CoroutineScope(Dispatchers.IO).launch {
                auth.sessionStatus.collect { status ->
                    if (status is SessionStatus.Authenticated) {
                        try {
                            val userId = status.session.user?.id
                            if (userId != null) {
                                // Fetch remote profiles from Supabase
                                val remoteProfiles = client.postgrest["profiles"]
                                    .select {
                                        filter {
                                            eq("user_id", userId)
                                        }
                                    }.decodeList<RemoteProfileDto>()
                                
                                // Save remote profiles into local Room DB to sync identities and UUIDs
                                remoteProfiles.forEach { remote ->
                                    profileDao.saveProfile(
                                        com.tapcard.app.data.local.ProfileEntity(
                                            id = remote.id,
                                            userId = remote.userId,
                                            profileName = remote.profileName,
                                            profileSlug = remote.profileSlug,
                                            fullName = remote.fullName ?: "",
                                            jobTitle = remote.jobTitle ?: "",
                                            company = remote.company ?: "",
                                            phone = remote.phone ?: "",
                                            email = remote.email ?: "",
                                            username = remote.username,
                                            website = remote.website ?: "",
                                            themeColorHex = remote.themeColorHex ?: "#000000",
                                            isDarkTheme = remote.isDarkTheme,
                                            isPublic = remote.isPublic,
                                            profilePhotoUrl = remote.profilePhotoUrl,
                                            companyLogoUrl = remote.companyLogoUrl,
                                            isPendingSync = false
                                        )
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    override fun getProfileFlow(): Flow<Profile?> {
        return profileDao.getAllProfilesFlow().map { profiles ->
            if (profiles.isEmpty()) return@map null
            val activeId = _activeProfileId.value
            val activeProfile = profiles.find { it.id == activeId } ?: profiles.first()
            if (_activeProfileId.value != activeProfile.id) {
                setActiveProfileId(activeProfile.id)
            }
            activeProfile.toDomainModel()
        }
    }

    override fun getAllProfilesFlow(): Flow<List<Profile>> {
        return profileDao.getAllProfilesFlow().map { list -> list.map { it.toDomainModel() } }
    }

    override suspend fun getProfile(): Profile? {
        val activeId = _activeProfileId.value
        val entity = if (activeId != null) profileDao.getProfile(activeId) else profileDao.getFirstProfile()
        return entity?.toDomainModel()
    }

    override suspend fun saveProfile(profile: Profile) {
        // 1. Save locally to Room first
        var entity = profile.toEntity().copy(isPendingSync = true)
        profileDao.saveProfile(entity)
        _syncStatus.value = SyncStatus.SAVED_LOCALLY
        _syncError.value = null

        // 2. If signed in and Supabase configured, sync to Supabase
        if (client == null) {
            _syncStatus.value = SyncStatus.SIGN_IN_TO_SYNC
            return
        }
        
        val session = client.auth.currentSessionOrNull()
        if (session == null) {
            _syncStatus.value = SyncStatus.SIGN_IN_TO_SYNC
            return
        }

        try {
            val userId = session.user?.id ?: return
            
            var profilePhotoUrlToSave = profile.profilePhotoUrl
            var companyLogoUrlToSave = profile.companyLogoUrl

            // Upload profile photo if needed
            if (profile.profilePhotoLocalUri != null && profile.profilePhotoLocalUri != profile.profilePhotoUrl) {
                _syncStatus.value = SyncStatus.UPLOADING
                val bytes = ImageCompressor.compressImage(context, profile.profilePhotoLocalUri)
                if (bytes != null) {
                    val path = "$userId/profile-photo.jpg"
                    client.storage["profile-images"].upload(path, bytes, upsert = true)
                    profilePhotoUrlToSave = client.storage["profile-images"].publicUrl(path)
                }
            }

            // Upload company logo if needed
            if (profile.companyLogoLocalUri != null && profile.companyLogoLocalUri != profile.companyLogoUrl) {
                _syncStatus.value = SyncStatus.UPLOADING
                val bytes = ImageCompressor.compressImage(context, profile.companyLogoLocalUri)
                if (bytes != null) {
                    val path = "$userId/company-logo.jpg"
                    client.storage["profile-images"].upload(path, bytes, upsert = true)
                    companyLogoUrlToSave = client.storage["profile-images"].publicUrl(path)
                }
            }

            val profileToUpload = profile.copy(profilePhotoUrl = profilePhotoUrlToSave, companyLogoUrl = companyLogoUrlToSave)
            
            // Save updated URLs to Room if they changed
            if (profilePhotoUrlToSave != profile.profilePhotoUrl || companyLogoUrlToSave != profile.companyLogoUrl) {
                entity = profileToUpload.toEntity().copy(isPendingSync = true)
                profileDao.saveProfile(entity)
            }

            _syncStatus.value = SyncStatus.SYNCING

            val remoteDto = profileToUpload.toRemoteDto().copy(userId = userId)

            client.postgrest["profiles"].upsert(remoteDto)
            
            // Mark as synced locally
            profileDao.saveProfile(entity.copy(isPendingSync = false))
            _syncStatus.value = SyncStatus.SYNCED
            _syncError.value = null
        } catch (e: Exception) {
            e.printStackTrace()
            // 3. If offline or error, keep local data and mark as pending sync
            _syncStatus.value = SyncStatus.SYNC_FAILED
            
            _syncError.value = when (e) {
                is RestException -> {
                    when {
                        e.message?.contains("does not exist") == true && e.message?.contains("profiles") == true -> "Table 'profiles' is missing."
                        e.message?.contains("duplicate key value violates unique constraint") == true -> {
                            if (e.message?.contains("profiles_username_key") == true) {
                                "Username is already taken."
                            } else if (e.message?.contains("profiles_user_id_profile_slug_idx") == true) {
                                "A card with this name/slug already exists on your account."
                            } else {
                                "Duplicate key violation: ${e.message}"
                            }
                        }
                        e.message?.contains("new row violates row-level security policy") == true -> "RLS Policy failure. Check your Postgres policies."
                        e.message?.contains("bucket") == true -> "Storage bucket is missing or unauthenticated."
                        else -> "Backend error: ${e.message}"
                    }
                }
                is HttpRequestException -> "Network error. Please check your connection."
                else -> {
                    // Check for storage bucket errors wrapped in other exceptions
                    if (e.message?.contains("Bucket") == true || e.message?.contains("does not exist") == true) {
                        "Storage bucket 'profile-images' might be missing."
                    } else if (e.message?.contains("row-level security") == true || e.message?.contains("rls") == true) {
                        "RLS Policy failure."
                    } else {
                        e.message ?: "An unexpected error occurred."
                    }
                }
            }
        }
    }

    override suspend fun validateUsernameUniqueness(username: String): Boolean {
        if (client == null) return true // Cannot validate offline, assume true or false depending on product needs. For now, we allow offline.
        
        return try {
            val result = client.postgrest["profiles"]
                .select(columns = Columns.list("id")) {
                    filter {
                        eq("username", username)
                    }
                }.decodeList<RemoteProfileDto>()
            
            result.isEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            true // Allow local save if network fails
        }
    }
}
