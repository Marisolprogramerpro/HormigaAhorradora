package com.example.hormigaahorradora.ui.viewmodels

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

class RegisterViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _registerState = MutableStateFlow<ResponseService<User>?>(null)
    val registerState: StateFlow<ResponseService<User>?> = _registerState.asStateFlow()
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
    fun validateConfirmPassword(password: String, confirm: String): String? {
        if (confirm.isBlank()) return "Confirma tu contraseña"
        if (password != confirm) return "Las contraseñas no coinciden"
        return null
    }
    fun isRegisterFormValid(
        email: String, password: String, confirm: String
    ): Boolean {
        return validateEmail(email) == null &&
                validatePassword(password) == null &&
                validateConfirmPassword(password, confirm) == null
    }
    fun requestSignUp(email: String, password: String, nombre: String) {
        viewModelScope.launch {
            _registerState.value = ResponseService.Loading
            val result = authRepository.requestSignUp(email, password, nombre)
            
            result.onSuccess { user ->
                _registerState.value = ResponseService.Success(user)
            }.onFailure { exception ->
                _registerState.value = ResponseService.Error(exception.message ?: "Error en el registro")
            }
        }
    }
}
