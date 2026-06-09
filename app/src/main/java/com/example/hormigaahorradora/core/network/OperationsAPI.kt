package com.example.hormigaahorradora.core.network

import retrofit2.http.Query
import com.example.hormigaahorradora.core.model.Response
import com.example.hormigaahorradora.core.model.calculatorResponse
import retrofit2.http.GET
import  com.google.android.gms.common.api.Response
//aqui van todos los metodos que debeeriamos tener
interface OperationesAPI {
    @GET("/Expense")
    suspend fun getExpense(
        @Query("id") id: String,
        @Query("description") description: String,
        @Query("amount") amount: Double,
        @Query("category") category: String,
        @Query("date") date: String
    ):Response<calculatorResponse>
}