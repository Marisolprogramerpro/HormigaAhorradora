package com.example.hormigaahorradora.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hormigaahorradora.core.ResponseService
import com.example.hormigaahorradora.logic.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecuperacionViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _recuperacionState = MutableStateFlow<ResponseService<Unit>?>(null)
    val recuperacionState: StateFlow<ResponseService<Unit>?> = _recuperacionState.asStateFlow()

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _recuperacionState.value = ResponseService.Loading
            val result = repository.requestPasswordReset(email)
            
            result.onSuccess {
                _recuperacionState.value = ResponseService.Success(Unit)
            }.onFailure { exception ->
                _recuperacionState.value = ResponseService.Error(exception.message ?: "Error al enviar el correo")
            }
        }
    }
}