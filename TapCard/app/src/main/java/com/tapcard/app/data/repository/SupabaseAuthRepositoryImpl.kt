package com.tapcard.app.data.repository

import com.tapcard.app.di.SupabaseClientProvider
import com.tapcard.app.domain.auth.AuthRepository
import com.tapcard.app.domain.auth.AuthState
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseAuthRepositoryImpl @Inject constructor() : AuthRepository {
    
    private val client = SupabaseClientProvider.client
    
    private val _authState = MutableStateFlow(
        if (client == null) AuthState.UNCONFIGURED else AuthState.SIGNED_OUT
    )
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        client?.auth?.let { auth ->
            CoroutineScope(Dispatchers.Main).launch {
                auth.sessionStatus.collect { status ->
                    _authState.value = when (status) {
                        is SessionStatus.Authenticated -> AuthState.SIGNED_IN
                        else -> AuthState.SIGNED_OUT
                    }
                }
            }
        }
    }

    override suspend fun signUp(email: String, password: String): Boolean {
        if (client == null) return false
        _authState.value = AuthState.LOADING
        return try {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            _authState.value = AuthState.SIGNED_IN
            true
        } catch (e: Exception) {
            e.printStackTrace()
            _authState.value = AuthState.ERROR
            false
        }
    }

    override suspend fun signIn(email: String, password: String): Boolean {
        if (client == null) return false
        _authState.value = AuthState.LOADING
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            _authState.value = AuthState.SIGNED_IN
            true
        } catch (e: Exception) {
            e.printStackTrace()
            _authState.value = AuthState.ERROR
            false
        }
    }

    override suspend fun signInWithGoogle(): Boolean {
        if (client == null) return false
        _authState.value = AuthState.LOADING
        return try {
            client.auth.signInWith(Google, redirectUrl = "tapcard://login-callback")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            _authState.value = AuthState.ERROR
            false
        }
    }

    override suspend fun signOut() {
        if (client == null) return
        try {
            client.auth.signOut()
            _authState.value = AuthState.SIGNED_OUT
        } catch (e: Exception) {
            e.printStackTrace()
            _authState.value = AuthState.ERROR
        }
    }
}
