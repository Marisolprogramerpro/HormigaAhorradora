package com.example.hormigaahorradora.data.model

data class Gasto(
    val id: String = "",
    val uid: String = "",           // UID del usuario dueño del gasto
    val categoriaId: String = "",   // "CAT001", "CAT002", etc.
    val concepto: String = "",
    val monto: Double = 0.0,
    val fecha: String = "",
    val nota: String = ""
)
