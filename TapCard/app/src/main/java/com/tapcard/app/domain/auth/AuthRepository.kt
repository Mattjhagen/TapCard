package com.tapcard.app.domain.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>
    
    suspend fun signUp(email: String, password: String): Boolean
    suspend fun signIn(email: String, password: String): Boolean
    suspend fun signInWithGoogle(): Boolean
    suspend fun signOut()
}
