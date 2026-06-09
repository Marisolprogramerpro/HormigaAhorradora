package com.example.hormigaahorradora.core.network

import com.example.hormigaahorradora.core.ResponseService
import com.example.hormigaahorradora.core.model.Expense

interface CalculatorService {
    suspend fun getCalculator(limit: Int = 20): ResponseService<List<Expense>>
}