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
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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

    override fun getProfileFlow(): Flow<Profile?> {
        return profileDao.getProfileFlow().map { it?.toDomainModel() }
    }

    override suspend fun getProfile(): Profile? {
        return profileDao.getProfile()?.toDomainModel()
    }

    override suspend fun saveProfile(profile: Profile) {
        // 1. Save locally to Room first
        var entity = profile.toEntity().copy(isPendingSync = true)
        profileDao.saveProfile(entity)
        _syncStatus.value = SyncStatus.SAVED_LOCALLY

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

            val remoteDto = profileToUpload.toRemoteDto().copy(id = userId)

            client.postgrest["profiles"].upsert(remoteDto)
            
            // Mark as synced locally
            profileDao.saveProfile(entity.copy(isPendingSync = false))
            _syncStatus.value = SyncStatus.SYNCED
        } catch (e: Exception) {
            e.printStackTrace()
            // 3. If offline or error, keep local data and mark as pending sync
            _syncStatus.value = SyncStatus.SYNC_FAILED
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
