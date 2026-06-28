package com.tapcard.app.domain.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u0014\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\u0003H&J\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u000eH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0010J\u0010\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\u0003H&J\u0019\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u000eH\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0015J\u0010\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u0004H&J\u0019\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0004H\u00a6@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001bR\u001a\u0010\u0002\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00040\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006R\u001a\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00040\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\b\u0010\u0006R\u0018\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\u0006\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001c"}, d2 = {"Lcom/tapcard/app/domain/repository/ProfileRepository;", "", "activeProfileIdFlow", "Lkotlinx/coroutines/flow/Flow;", "", "getActiveProfileIdFlow", "()Lkotlinx/coroutines/flow/Flow;", "syncError", "getSyncError", "syncStatus", "Lcom/tapcard/app/domain/model/SyncStatus;", "getSyncStatus", "getAllProfilesFlow", "", "Lcom/tapcard/app/domain/model/Profile;", "getProfile", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getProfileFlow", "saveProfile", "", "profile", "(Lcom/tapcard/app/domain/model/Profile;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setActiveProfileId", "id", "validateUsernameUniqueness", "", "username", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
public abstract interface ProfileRepository {
    
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<com.tapcard.app.domain.model.Profile> getProfileFlow();
    
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.tapcard.app.domain.model.Profile>> getAllProfilesFlow();
    
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getProfile(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.tapcard.app.domain.model.Profile> $completion);
    
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object saveProfile(@org.jetbrains.annotations.NotNull
    com.tapcard.app.domain.model.Profile profile, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    public abstract void setActiveProfileId(@org.jetbrains.annotations.NotNull
    java.lang.String id);
    
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<java.lang.String> getActiveProfileIdFlow();
    
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<com.tapcard.app.domain.model.SyncStatus> getSyncStatus();
    
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<java.lang.String> getSyncError();
    
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object validateUsernameUniqueness(@org.jetbrains.annotations.NotNull
    java.lang.String username, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
}