package com.tapcard.app.di;

@dagger.Module
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u0007J\b\u0010\u0007\u001a\u00020\bH\u0007J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0004H\u0007J\u001a\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\n2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u0007J\b\u0010\u000f\u001a\u00020\u0010H\u0007\u00a8\u0006\u0011"}, d2 = {"Lcom/tapcard/app/di/AppModule;", "", "()V", "provideAppDatabase", "Lcom/tapcard/app/data/local/AppDatabase;", "context", "Landroid/content/Context;", "provideAuthRepository", "Lcom/tapcard/app/domain/auth/AuthRepository;", "provideProfileDao", "Lcom/tapcard/app/data/local/ProfileDao;", "appDatabase", "provideProfileRepository", "Lcom/tapcard/app/domain/repository/ProfileRepository;", "profileDao", "provideWalletService", "Lcom/tapcard/app/domain/wallet/WalletService;", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class AppModule {
    @org.jetbrains.annotations.NotNull
    public static final com.tapcard.app.di.AppModule INSTANCE = null;
    
    private AppModule() {
        super();
    }
    
    @dagger.Provides
    @javax.inject.Singleton
    @org.jetbrains.annotations.NotNull
    public final com.tapcard.app.data.local.AppDatabase provideAppDatabase(@dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        return null;
    }
    
    @dagger.Provides
    @javax.inject.Singleton
    @org.jetbrains.annotations.NotNull
    public final com.tapcard.app.data.local.ProfileDao provideProfileDao(@org.jetbrains.annotations.NotNull
    com.tapcard.app.data.local.AppDatabase appDatabase) {
        return null;
    }
    
    @dagger.Provides
    @javax.inject.Singleton
    @org.jetbrains.annotations.NotNull
    public final com.tapcard.app.domain.repository.ProfileRepository provideProfileRepository(@org.jetbrains.annotations.NotNull
    com.tapcard.app.data.local.ProfileDao profileDao, @dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        return null;
    }
    
    @dagger.Provides
    @javax.inject.Singleton
    @org.jetbrains.annotations.NotNull
    public final com.tapcard.app.domain.wallet.WalletService provideWalletService() {
        return null;
    }
    
    @dagger.Provides
    @javax.inject.Singleton
    @org.jetbrains.annotations.NotNull
    public final com.tapcard.app.domain.auth.AuthRepository provideAuthRepository() {
        return null;
    }
}