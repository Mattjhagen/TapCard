package com.tapcard.app.ui.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t\u00a8\u0006\n"}, d2 = {"Lcom/tapcard/app/ui/viewmodel/UsernameValidationState;", "", "(Ljava/lang/String;I)V", "IDLE", "CHECKING", "AVAILABLE", "TAKEN", "INVALID_FORMAT", "SIGN_IN_TO_VALIDATE", "SUPABASE_NOT_CONFIGURED", "app_debug"})
public enum UsernameValidationState {
    /*public static final*/ IDLE /* = new IDLE() */,
    /*public static final*/ CHECKING /* = new CHECKING() */,
    /*public static final*/ AVAILABLE /* = new AVAILABLE() */,
    /*public static final*/ TAKEN /* = new TAKEN() */,
    /*public static final*/ INVALID_FORMAT /* = new INVALID_FORMAT() */,
    /*public static final*/ SIGN_IN_TO_VALIDATE /* = new SIGN_IN_TO_VALIDATE() */,
    /*public static final*/ SUPABASE_NOT_CONFIGURED /* = new SUPABASE_NOT_CONFIGURED() */;
    
    UsernameValidationState() {
    }
    
    @org.jetbrains.annotations.NotNull
    public static kotlin.enums.EnumEntries<com.tapcard.app.ui.viewmodel.UsernameValidationState> getEntries() {
        return null;
    }
}