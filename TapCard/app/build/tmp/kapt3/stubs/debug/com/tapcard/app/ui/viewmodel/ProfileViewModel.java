package com.tapcard.app.ui.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0006\u0010\'\u001a\u00020(J\u0006\u0010)\u001a\u00020\u000fJ\u000e\u0010*\u001a\u00020(2\u0006\u0010+\u001a\u00020\u000fJ\u0019\u0010,\u001a\u00020(2\u0006\u0010+\u001a\u00020\u000fH\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010-J\u0006\u0010.\u001a\u00020(J\u000e\u0010/\u001a\u00020\u000b2\u0006\u00100\u001a\u000201J\u0018\u00102\u001a\u0004\u0018\u0001032\u0006\u00100\u001a\u0002012\u0006\u00104\u001a\u00020\u000fJ\u000e\u00105\u001a\u00020\u000b2\u0006\u00106\u001a\u000207J\u000e\u00108\u001a\u00020(2\u0006\u00106\u001a\u000207J\u000e\u00109\u001a\u00020(2\u0006\u0010:\u001a\u00020\u0013J\u0019\u0010;\u001a\u00020\u000b2\u0006\u0010+\u001a\u00020\u000fH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010-R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000f0\u000e0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u000f0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0019R#\u0010\u001a\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000f0\u000e0\u001b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00110\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0019R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00130\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0019R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020#0\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0019R\u0017\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00160\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u0019\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006<"}, d2 = {"Lcom/tapcard/app/ui/viewmodel/ProfileViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/tapcard/app/domain/repository/ProfileRepository;", "qrExportService", "Lcom/tapcard/app/utils/QRExportService;", "nfcService", "Lcom/tapcard/app/utils/NfcService;", "(Lcom/tapcard/app/domain/repository/ProfileRepository;Lcom/tapcard/app/utils/QRExportService;Lcom/tapcard/app/utils/NfcService;)V", "_isSaved", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_nfcProgrammingResult", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lkotlin/Pair;", "", "_nfcState", "Lcom/tapcard/app/utils/NfcState;", "_profileState", "Lcom/tapcard/app/domain/model/Profile;", "_usernameInputFlow", "_usernameValidationState", "Lcom/tapcard/app/ui/viewmodel/UsernameValidationState;", "isSaved", "Lkotlinx/coroutines/flow/StateFlow;", "()Lkotlinx/coroutines/flow/StateFlow;", "nfcProgrammingResult", "Lkotlinx/coroutines/flow/SharedFlow;", "getNfcProgrammingResult", "()Lkotlinx/coroutines/flow/SharedFlow;", "nfcState", "getNfcState", "profileState", "getProfileState", "syncStatus", "Lcom/tapcard/app/domain/model/SyncStatus;", "getSyncStatus", "usernameValidationState", "getUsernameValidationState", "checkNfcState", "", "getShareableUrl", "onUsernameChanged", "username", "performUsernameValidation", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveProfile", "saveQrToGallery", "bitmap", "Landroid/graphics/Bitmap;", "shareQrCode", "Landroid/content/Intent;", "text", "startNfcProgramming", "activity", "Landroid/app/Activity;", "stopNfcProgramming", "updateProfile", "newProfile", "validateUsername", "app_debug"})
@kotlin.OptIn(markerClass = {kotlinx.coroutines.FlowPreview.class})
@dagger.hilt.android.lifecycle.HiltViewModel
public final class ProfileViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final com.tapcard.app.domain.repository.ProfileRepository repository = null;
    @org.jetbrains.annotations.NotNull
    private final com.tapcard.app.utils.QRExportService qrExportService = null;
    @org.jetbrains.annotations.NotNull
    private final com.tapcard.app.utils.NfcService nfcService = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.tapcard.app.domain.model.Profile> _profileState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.tapcard.app.domain.model.Profile> profileState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.tapcard.app.utils.NfcState> _nfcState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.tapcard.app.utils.NfcState> nfcState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableSharedFlow<kotlin.Pair<java.lang.Boolean, java.lang.String>> _nfcProgrammingResult = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.SharedFlow<kotlin.Pair<java.lang.Boolean, java.lang.String>> nfcProgrammingResult = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isSaved = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isSaved = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.tapcard.app.ui.viewmodel.UsernameValidationState> _usernameValidationState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.tapcard.app.ui.viewmodel.UsernameValidationState> usernameValidationState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _usernameInputFlow = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.tapcard.app.domain.model.SyncStatus> syncStatus = null;
    
    @javax.inject.Inject
    public ProfileViewModel(@org.jetbrains.annotations.NotNull
    com.tapcard.app.domain.repository.ProfileRepository repository, @org.jetbrains.annotations.NotNull
    com.tapcard.app.utils.QRExportService qrExportService, @org.jetbrains.annotations.NotNull
    com.tapcard.app.utils.NfcService nfcService) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.tapcard.app.domain.model.Profile> getProfileState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.tapcard.app.utils.NfcState> getNfcState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.SharedFlow<kotlin.Pair<java.lang.Boolean, java.lang.String>> getNfcProgrammingResult() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isSaved() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.tapcard.app.ui.viewmodel.UsernameValidationState> getUsernameValidationState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.tapcard.app.domain.model.SyncStatus> getSyncStatus() {
        return null;
    }
    
    public final void updateProfile(@org.jetbrains.annotations.NotNull
    com.tapcard.app.domain.model.Profile newProfile) {
    }
    
    public final void saveProfile() {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getShareableUrl() {
        return null;
    }
    
    public final void onUsernameChanged(@org.jetbrains.annotations.NotNull
    java.lang.String username) {
    }
    
    private final java.lang.Object performUsernameValidation(java.lang.String username, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object validateUsername(@org.jetbrains.annotations.NotNull
    java.lang.String username, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    public final boolean saveQrToGallery(@org.jetbrains.annotations.NotNull
    android.graphics.Bitmap bitmap) {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable
    public final android.content.Intent shareQrCode(@org.jetbrains.annotations.NotNull
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull
    java.lang.String text) {
        return null;
    }
    
    public final void checkNfcState() {
    }
    
    public final boolean startNfcProgramming(@org.jetbrains.annotations.NotNull
    android.app.Activity activity) {
        return false;
    }
    
    public final void stopNfcProgramming(@org.jetbrains.annotations.NotNull
    android.app.Activity activity) {
    }
}