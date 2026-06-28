package com.tapcard.app.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0004J\u0006\u0010\b\u001a\u00020\u0006J\u0006\u0010\t\u001a\u00020\u0006J\u001a\u0010\n\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000eJ\u0006\u0010\u000f\u001a\u00020\u0006J\u0006\u0010\u0010\u001a\u00020\u0006J\u0006\u0010\u0011\u001a\u00020\u0006J\u0006\u0010\u0012\u001a\u00020\u0006J\u0006\u0010\u0013\u001a\u00020\u0006J\u0006\u0010\u0014\u001a\u00020\u0006R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/tapcard/app/utils/AnalyticsManager;", "", "()V", "analytics", "Lcom/google/firebase/analytics/FirebaseAnalytics;", "initialize", "", "firebaseAnalytics", "logAccountCreated", "logCardSaved", "logEvent", "eventName", "", "params", "Landroid/os/Bundle;", "logImageUploaded", "logNfcProgrammed", "logQrGenerated", "logQrShared", "logSyncCompleted", "logWalletButtonTapped", "app_release"})
public final class AnalyticsManager {
    @org.jetbrains.annotations.Nullable
    private static com.google.firebase.analytics.FirebaseAnalytics analytics;
    @org.jetbrains.annotations.NotNull
    public static final com.tapcard.app.utils.AnalyticsManager INSTANCE = null;
    
    private AnalyticsManager() {
        super();
    }
    
    public final void initialize(@org.jetbrains.annotations.NotNull
    com.google.firebase.analytics.FirebaseAnalytics firebaseAnalytics) {
    }
    
    public final void logEvent(@org.jetbrains.annotations.NotNull
    java.lang.String eventName, @org.jetbrains.annotations.Nullable
    android.os.Bundle params) {
    }
    
    public final void logAccountCreated() {
    }
    
    public final void logCardSaved() {
    }
    
    public final void logQrGenerated() {
    }
    
    public final void logQrShared() {
    }
    
    public final void logNfcProgrammed() {
    }
    
    public final void logImageUploaded() {
    }
    
    public final void logSyncCompleted() {
    }
    
    public final void logWalletButtonTapped() {
    }
}