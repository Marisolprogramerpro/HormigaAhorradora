package com.example.hormigaahorradora.core

sealed class ResponseService {
    data class Success(val value: Boolean)
    data class Error(val message: String)
}
