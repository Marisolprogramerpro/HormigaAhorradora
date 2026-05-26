package com.example.hormigaahorradora.core.model

import com.google.gson.annotations.SerializedName
data class Response {

    @SerializedName("results") val results: List<Expense>

    data class Expense(
        @SerializedName("id") val id: String,
        @SerializedName("description") val description: String,
        @SerializedName("amount") val amount: Double,
        @SerializedName("category") val category: String, // Aquí irá: "viajes", "comida", "renta", etc.
        @SerializedName("date") val date: String,
        @SerializedName("note") val note: String? = null // Nota opcional sobre el gasto
    )
}