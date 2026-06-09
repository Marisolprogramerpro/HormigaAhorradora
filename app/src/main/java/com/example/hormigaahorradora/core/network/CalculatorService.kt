package com.example.hormigaahorradora.core.network

import com.example.hormigaahorradora.core.model.calculatorResponse

interface CalculatorService {
    suspend fun getCalculator(limit: Int = 20): CalculatorService<List<Expense>>

}