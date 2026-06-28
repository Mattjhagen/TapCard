package com.tapcard.app.data.repository;

@javax.inject.Singleton
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0012J\u0010\u0010\u0013\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\rH\u0016J\u0019\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0011H\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0017J\u0019\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001cR\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\rX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001d"}, d2 = {"Lcom/tapcard/app/data/repository/SyncProfileRepositoryImpl;", "Lcom/tapcard/app/domain/repository/ProfileRepository;", "profileDao", "Lcom/tapcard/app/data/local/ProfileDao;", "context", "Landroid/content/Context;", "(Lcom/tapcard/app/data/local/ProfileDao;Landroid/content/Context;)V", "_syncStatus", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/tapcard/app/domain/model/SyncStatus;", "client", "Lio/github/jan/supabase/SupabaseClient;", "syncStatus", "Lkotlinx/coroutines/flow/Flow;", "getSyncStatus", "()Lkotlinx/coroutines/flow/Flow;", "getProfile", "Lcom/tapcard/app/domain/model/Profile;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getProfileFlow", "saveProfile", "", "profile", "(Lcom/tapcard/app/domain/model/Profile;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "validateUsernameUniqueness", "", "username", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
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
    public kotlinx.coroutines.flow.Flow<com.tapcard.app.domain.model.Profile> getProfileFlow() {
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