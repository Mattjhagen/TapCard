package com.tapcard.app.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0007J\u0010\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00060\tH\u0016J\u0019\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0006H\u0096@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\rJ\f\u0010\u000e\u001a\u00020\u0006*\u00020\u000fH\u0002J\f\u0010\u0010\u001a\u00020\u000f*\u00020\u0006H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0011"}, d2 = {"Lcom/tapcard/app/data/repository/LocalProfileRepositoryImpl;", "Lcom/tapcard/app/domain/repository/ProfileRepository;", "profileDao", "Lcom/tapcard/app/data/local/ProfileDao;", "(Lcom/tapcard/app/data/local/ProfileDao;)V", "getProfile", "Lcom/tapcard/app/domain/model/Profile;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getProfileFlow", "Lkotlinx/coroutines/flow/Flow;", "saveProfile", "", "profile", "(Lcom/tapcard/app/domain/model/Profile;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toDomainModel", "Lcom/tapcard/app/data/local/ProfileEntity;", "toEntity", "app_debug"})
public final class LocalProfileRepositoryImpl implements com.tapcard.app.domain.repository.ProfileRepository {
    @org.jetbrains.annotations.NotNull
    private final com.tapcard.app.data.local.ProfileDao profileDao = null;
    
    public LocalProfileRepositoryImpl(@org.jetbrains.annotations.NotNull
    com.tapcard.app.data.local.ProfileDao profileDao) {
        super();
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
    
    private final com.tapcard.app.domain.model.Profile toDomainModel(com.tapcard.app.data.local.ProfileEntity $this$toDomainModel) {
        return null;
    }
    
    private final com.tapcard.app.data.local.ProfileEntity toEntity(com.tapcard.app.domain.model.Profile $this$toEntity) {
        return null;
    }
}