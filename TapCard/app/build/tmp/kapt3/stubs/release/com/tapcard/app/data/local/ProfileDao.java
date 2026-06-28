package com.tapcard.app.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0007J\u001b\u0010\b\u001a\u0004\u0018\u00010\u00052\u0006\u0010\t\u001a\u00020\nH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000bJ\u0018\u0010\f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u00032\u0006\u0010\t\u001a\u00020\nH\'J\u0019\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0010\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0011"}, d2 = {"Lcom/tapcard/app/data/local/ProfileDao;", "", "getAllProfilesFlow", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/tapcard/app/data/local/ProfileEntity;", "getFirstProfile", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getProfile", "id", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getProfileFlow", "saveProfile", "", "profile", "(Lcom/tapcard/app/data/local/ProfileEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
@androidx.room.Dao
public abstract interface ProfileDao {
    
    @androidx.room.Query(value = "SELECT * FROM profile")
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.tapcard.app.data.local.ProfileEntity>> getAllProfilesFlow();
    
    @androidx.room.Query(value = "SELECT * FROM profile WHERE id = :id")
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<com.tapcard.app.data.local.ProfileEntity> getProfileFlow(@org.jetbrains.annotations.NotNull
    java.lang.String id);
    
    @androidx.room.Query(value = "SELECT * FROM profile WHERE id = :id")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getProfile(@org.jetbrains.annotations.NotNull
    java.lang.String id, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.tapcard.app.data.local.ProfileEntity> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM profile LIMIT 1")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getFirstProfile(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.tapcard.app.data.local.ProfileEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object saveProfile(@org.jetbrains.annotations.NotNull
    com.tapcard.app.data.local.ProfileEntity profile, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}