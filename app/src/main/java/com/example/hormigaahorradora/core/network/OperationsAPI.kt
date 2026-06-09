package com.example.hormigaahorradora.core.network

import com.example.hormigaahorradora.core.model.CalculatorResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OperationesAPI {
    @GET("/Expense")
    suspend fun getExpense(
        @Query("limit") limit: Int
    ): Response<CalculatorResponse>
}