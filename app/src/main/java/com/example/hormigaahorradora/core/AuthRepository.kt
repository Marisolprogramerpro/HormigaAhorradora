package com.example.hormigaahorradora.core

import java.net.Authenticator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import android.util.Log


class AuthRepository (): Authentication {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    override suspend fun requestLogin(
        email: String,
        password: String
    ): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            Log.e("Error", "${e.printStackTrace()}")
            null
        }
    }

    override suspend fun requestSignUp(
        email: String,
        password: String
    ) : FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            Log.e("Error", "\${e.printStackTrace()}")
            null
        }
    }
}