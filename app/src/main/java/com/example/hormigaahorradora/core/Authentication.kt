package com.example.hormigaahorradora.core

interface Authentication {
   suspend fun requestLogin(email: String, password: String): ResponseService
    suspend fun requestSignUp (email: String, password: String)
}