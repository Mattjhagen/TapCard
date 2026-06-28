package com.tapcard.app.domain.repository

import com.tapcard.app.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfileFlow(): Flow<Profile?>
    suspend fun getProfile(): Profile?
    suspend fun saveProfile(profile: Profile)
}
