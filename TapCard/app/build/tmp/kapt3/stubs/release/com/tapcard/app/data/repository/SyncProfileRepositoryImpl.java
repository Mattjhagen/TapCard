package com.tapcard.app.data.repository;

@javax.inject.Singleton
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001c0\u001b0\u000eH\u0016J\u0013\u0010\u001d\u001a\u0004\u0018\u00010\u001cH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001eJ\u0010\u0010\u001f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001c0\u000eH\u0016J\u0019\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u001cH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010#J\u0010\u0010$\u001a\u00020!2\u0006\u0010%\u001a\u00020\tH\u0016J\u0019\u0010&\u001a\u00020\'2\u0006\u0010(\u001a\u00020\tH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010)R\u0016\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u000eX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0013\u001a\n \u0015*\u0004\u0018\u00010\u00140\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0016\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u000eX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0010R\u001a\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\f0\u000eX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0010\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006*"}, d2 = {"Lcom/tapcard/app/data/repository/SyncProfileRepositoryImpl;", "Lcom/tapcard/app/domain/repository/ProfileRepository;", "profileDao", "Lcom/tapcard/app/data/local/ProfileDao;", "context", "Landroid/content/Context;", "(Lcom/tapcard/app/data/local/ProfileDao;Landroid/content/Context;)V", "_activeProfileId", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_syncError", "_syncStatus", "Lcom/tapcard/app/domain/model/SyncStatus;", "activeProfileIdFlow", "Lkotlinx/coroutines/flow/Flow;", "getActiveProfileIdFlow", "()Lkotlinx/coroutines/flow/Flow;", "client", "Lio/github/jan/supabase/SupabaseClient;", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "syncError", "getSyncError", "syncStatus", "getSyncStatus", "getAllProfilesFlow", "", "Lcom/tapcard/app/domain/model/Profile;", "getProfile", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getProfileFlow", "saveProfile", "", "profile", "(Lcom/tapcard/app/domain/model/Profile;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setActiveProfileId", "id", "validateUsernameUniqueness", "", "username", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
public final class SyncProfileRepositoryImpl implements com.tapcard.app.domain.repository.ProfileRepository {
    @org.jetbrains.annotations.NotNull
    private final com.tapcard.app.data.local.ProfileDao profileDao = null;
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.Nullable
    private final io.github.jan.supabase.SupabaseClient client = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.tapcard.app.domain.model.SyncStatus> _syncStatus = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.Flow<com.tapcard.app.domain.model.SyncStatus> syncStatus = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _syncError = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.Flow<java.lang.String> syncError = null;
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _activeProfileId = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.Flow<java.lang.String> activeProfileIdFlow = null;
    
    @javax.inject.Inject
    public SyncProfileRepositoryImpl(@org.jetbrains.annotations.NotNull
    com.tapcard.app.data.local.ProfileDao profileDao, @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public kotlinx.coroutines.flow.Flow<com.tapcard.app.domain.model.SyncStatus> getSyncStatus() {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public kotlinx.coroutines.flow.Flow<java.lang.String> getSyncError() {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public kotlinx.coroutines.flow.Flow<java.lang.String> getActiveProfileIdFlow() {
        return null;
    }
    
    @java.lang.Override
    public void setActiveProfileId(@org.jetbrains.annotations.NotNull
    java.lang.String id) {
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public kotlinx.coroutines.flow.Flow<com.tapcard.app.domain.model.Profile> getProfileFlow() {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public kotlinx.coroutines.flow.Flow<java.util.List<com.tapcard.app.domain.model.Profile>> getAllProfilesFlow() {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.Nullable
    public java.lang.Object getProfile(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.tapcard.app.domain.model.Profile> $completion) {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.Nullable
    public java.lang.Object saveProfile(@org.jetbrains.annotations.NotNull
    com.tapcard.app.domain.model.Profile profile, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.Nullable
    public java.lang.Object validateUsernameUniqueness(@org.jetbrains.annotations.NotNull
    java.lang.String username, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
}