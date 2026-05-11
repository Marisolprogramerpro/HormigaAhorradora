package com.example.hormigaahorradora.logic

import com.example.hormigaahorradora.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun requestSignUp(email: String, password: String, nombre: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("Usuario nulo"))
            
            val newUser = User(uid = firebaseUser.uid, nombre = nombre, email = email)
            
            // Guardar en Firestore
            firestore.collection("users").document(newUser.uid).set(newUser).await()
            
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestLogin(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("Usuario nulo"))
            
            val user = getCurrentUserData() ?: User(uid = firebaseUser.uid, email = firebaseUser.email ?: "")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUserData(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun logout() {
        auth.signOut()
    }
}
