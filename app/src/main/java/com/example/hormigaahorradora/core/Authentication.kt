package com.example.hormigaahorradora.core

import com.google.firebase.auth.FirebaseUser
import com.example.hormigaahorradora.core.ResponseService

interface Authentication {
    suspend fun requestLogin(email: String, password: String): ResponseService<FirebaseUser>
    suspend fun requestSignUp(email: String, password: String): ResponseService<FirebaseUser>
}