package com.example.hormigaahorradora.logic

import com.example.hormigaahorradora.core.ResponseService
import com.example.hormigaahorradora.data.model.Gasto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GastoRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Colección: usuarios/{uid}/gastos/{gastoId}
    private fun gastosCollection() =
        firestore.collection("usuarios")
            .document(auth.currentUser?.uid ?: "")
            .collection("gastos")

    // CREAR gasto
    suspend fun agregarGasto(gasto: Gasto): ResponseService<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val docRef = gastosCollection().document()
                val gastoConId = gasto.copy(
                    id = docRef.id,
                    uid = auth.currentUser?.uid ?: ""
                )
                docRef.set(gastoConId).await()
                ResponseService.Success(Unit)
            } catch (e: Exception) {
                ResponseService.Error("Error al guardar el gasto: ${e.localizedMessage}")
            }
        }

    // LEER gastos por categoría
    suspend fun getGastosPorCategoria(categoriaId: String): ResponseService<List<Gasto>> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = gastosCollection()
                    .whereEqualTo("categoriaId", categoriaId)
                    .get()
                    .await()
                val gastos = snapshot.toObjects(Gasto::class.java)
                ResponseService.Success(gastos)
            } catch (e: Exception) {
                ResponseService.Error("Error al cargar gastos: ${e.localizedMessage}")
            }
        }

    // LEER todos los gastos del usuario (para el resumen)
    suspend fun getTodosLosGastos(): ResponseService<List<Gasto>> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = gastosCollection().get().await()
                val gastos = snapshot.toObjects(Gasto::class.java)
                ResponseService.Success(gastos)
            } catch (e: Exception) {
                ResponseService.Error("Error al cargar gastos: ${e.localizedMessage}")
            }
        }

    // EDITAR gasto
    suspend fun editarGasto(gasto: Gasto): ResponseService<Unit> =
        withContext(Dispatchers.IO) {
            try {
                gastosCollection().document(gasto.id).set(gasto).await()
                ResponseService.Success(Unit)
            } catch (e: Exception) {
                ResponseService.Error("Error al editar el gasto: ${e.localizedMessage}")
            }
        }

    // ELIMINAR gasto
    suspend fun eliminarGasto(gastoId: String): ResponseService<Unit> =
        withContext(Dispatchers.IO) {
            try {
                gastosCollection().document(gastoId).delete().await()
                ResponseService.Success(Unit)
            } catch (e: Exception) {
                ResponseService.Error("Error al eliminar el gasto: ${e.localizedMessage}")
            }
        }
}
