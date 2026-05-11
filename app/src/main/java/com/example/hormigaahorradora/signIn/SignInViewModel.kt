package com.example.hormigaahorradora.signIn

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hormigaahorradora.core.ResponseService
import com.example.hormigaahorradora.data.model.User
import com.example.hormigaahorradora.logic.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _signInState = MutableStateFlow<ResponseService<User>?>(null)
    val signInState: StateFlow<ResponseService<User>?> = _signInState.asStateFlow()

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
        viewModelScope.launch {
            _signInState.value = ResponseService.Loading
            val result = repository.requestLogin(email, password)
            
            result.onSuccess { user ->
                _signInState.value = ResponseService.Success(user)
            }.onFailure { exception ->
                _signInState.value = ResponseService.Error(exception.message ?: "Error al iniciar sesión")
            }
        }
    }
}