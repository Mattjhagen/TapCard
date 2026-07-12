package com.tapcard.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tapcard.app.domain.auth.AuthRepository
import com.tapcard.app.domain.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState

    fun signUp(email: String, pass: String) {
        viewModelScope.launch {
            authRepository.signUp(email, pass)
        }
    }

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            authRepository.signIn(email, pass)
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            authRepository.signInWithGoogle()
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
