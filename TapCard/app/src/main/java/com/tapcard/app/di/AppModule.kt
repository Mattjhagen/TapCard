package com.tapcard.app.di

import android.content.Context
import com.tapcard.app.data.local.AppDatabase
import com.tapcard.app.data.local.ProfileDao
import com.tapcard.app.data.repository.LocalProfileRepositoryImpl
import com.tapcard.app.domain.repository.ProfileRepository
import com.tapcard.app.domain.wallet.WalletService
import com.tapcard.app.data.wallet.GoogleWalletServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideProfileDao(appDatabase: AppDatabase): ProfileDao {
        return appDatabase.profileDao()
    }

    @Provides
    @Singleton
    fun provideProfileRepository(dao: ProfileDao): ProfileRepository {
        return LocalProfileRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideWalletService(): WalletService {
        return GoogleWalletServiceImpl()
    }
}
