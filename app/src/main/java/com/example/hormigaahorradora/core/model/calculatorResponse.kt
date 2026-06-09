package com.example.hormigaahorradora.core.model

import com.google.gson.annotations.SerializedName

data class CalculatorResponse(
    @SerializedName("results") val results: List<Expense>
)

data class Expense(
    @SerializedName("id") val id: String,
    @SerializedName("description") val description: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("category") val category: String,
    @SerializedName("date") val date: String,
    @SerializedName("note") val note: String? = null 
)