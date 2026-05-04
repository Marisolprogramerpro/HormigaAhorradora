package com.example.hormigaahorradora

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hormigaahorradora.core.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.util.Patterns
import com.example.hormigaahorradora.core.ResponseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignInViewModel : ViewModel() {
    val repository = AuthRepository()

    private val _signInState = MutableStateFlow<ResponseService<FirebaseUser>?>(null)
    val signInState: StateFlow<ResponseService<FirebaseUser>?> = _signInState.asStateFlow()
    fun validateEmail(email: String): String? {
        if (email.isBlank()) return "El correo es requerido"
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "Correo inválido"
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isBlank()) return "La contraseña es requerida"
        if (password.length < 8) return "Mínimo 8 caracteres"
        return null
    }

    fun isLoginFormValid(email: String, password: String): Boolean {
        return validateEmail(email) == null &&
                validatePassword(password) == null
    }

    // --- Operación de login ---
    fun requestLogin(email: String, password: String) {

        _signInState.value = ResponseService.Loading
        _signInState.value = repository.requestLogin(email, password)

    }
}