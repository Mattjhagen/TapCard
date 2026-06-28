package com.tapcard.app.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u0016\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\n\u0010\u0004\u001a\u00020\u0005*\u00020\u0006\u001a\n\u0010\u0007\u001a\u00020\u0006*\u00020\u0005\"\u0011\u0010\u0000\u001a\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003\u00a8\u0006\b"}, d2 = {"MIGRATION_1_2", "Landroidx/room/migration/Migration;", "getMIGRATION_1_2", "()Landroidx/room/migration/Migration;", "toDomainModel", "Lcom/tapcard/app/domain/model/Profile;", "Lcom/tapcard/app/data/local/ProfileEntity;", "toEntity", "app_debug"})
public final class ProfileEntityKt {
    
    /**
     * Room migration 1 → 2: adds profileSlug column, backfills from profileName.
     */
    @org.jetbrains.annotations.NotNull
    private static final androidx.room.migration.Migration MIGRATION_1_2 = null;
    
    /**
     * Room migration 1 → 2: adds profileSlug column, backfills from profileName.
     */
    @org.jetbrains.annotations.NotNull
    public static final androidx.room.migration.Migration getMIGRATION_1_2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final com.tapcard.app.domain.model.Profile toDomainModel(@org.jetbrains.annotations.NotNull
    com.tapcard.app.data.local.ProfileEntity $this$toDomainModel) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final com.tapcard.app.data.local.ProfileEntity toEntity(@org.jetbrains.annotations.NotNull
    com.tapcard.app.domain.model.Profile $this$toEntity) {
        return null;
    }
}