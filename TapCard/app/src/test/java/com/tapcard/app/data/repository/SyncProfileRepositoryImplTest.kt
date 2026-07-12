package com.tapcard.app.data.repository

import com.tapcard.app.data.local.ProfileDao
import com.tapcard.app.data.local.ProfileEntity
import com.tapcard.app.di.SupabaseClientProvider
import com.tapcard.app.domain.model.SyncStatus
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import android.content.Context

class SyncProfileRepositoryImplTest {

    private lateinit var repository: SyncProfileRepositoryImpl
    private val profileDao: ProfileDao = mockk()
    private val context: Context = mockk()

    @Before
    fun setup() {
        // Force the offline path regardless of whether Supabase credentials were
        // baked into BuildConfig at build time (CI writes .env from secrets, so
        // the real lazy client would otherwise try to initialize inside JVM tests).
        mockkObject(SupabaseClientProvider)
        every { SupabaseClientProvider.client } returns null

        coEvery { context.getSharedPreferences(any(), any()) } returns mockk(relaxed = true)
        repository = SyncProfileRepositoryImpl(profileDao, context)
    }

    @After
    fun tearDown() {
        unmockkObject(SupabaseClientProvider)
    }

    @Test
    fun testOfflineSave_SetsPendingSync() = runTest {
        val testEntity = ProfileEntity(id = "1", isPendingSync = true)
        
        coEvery { profileDao.getAllProfilesFlow() } returns flowOf(listOf(testEntity))
        coEvery { profileDao.saveProfile(any()) } returns Unit

        repository.getProfileFlow().first() // Initial load
        
        // Since we are not authenticated in tests, saveProfile goes into offline mode
        val profile = com.tapcard.app.domain.model.Profile(username = "offline_user")
        repository.saveProfile(profile)

        val syncStatus = repository.syncStatus.first()
        assertEquals(SyncStatus.SIGN_IN_TO_SYNC, syncStatus)
    }
}
