package com.tapcard.app.domain.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b\u00a8\u0006\t"}, d2 = {"Lcom/tapcard/app/domain/model/SyncStatus;", "", "(Ljava/lang/String;I)V", "SAVED_LOCALLY", "UPLOADING", "SYNCING", "SYNCED", "SYNC_FAILED", "SIGN_IN_TO_SYNC", "app_debug"})
public enum SyncStatus {
    /*public static final*/ SAVED_LOCALLY /* = new SAVED_LOCALLY() */,
    /*public static final*/ UPLOADING /* = new UPLOADING() */,
    /*public static final*/ SYNCING /* = new SYNCING() */,
    /*public static final*/ SYNCED /* = new SYNCED() */,
    /*public static final*/ SYNC_FAILED /* = new SYNC_FAILED() */,
    /*public static final*/ SIGN_IN_TO_SYNC /* = new SIGN_IN_TO_SYNC() */;
    
    SyncStatus() {
    }
    
    @org.jetbrains.annotations.NotNull
    public static kotlin.enums.EnumEntries<com.tapcard.app.domain.model.SyncStatus> getEntries() {
        return null;
    }
}