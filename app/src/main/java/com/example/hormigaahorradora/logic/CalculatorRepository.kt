package com.example.hormigaahorradora.logic
 import com.example.hormigaahorradora.core.ResponseService
 import com.example.hormigaahorradora.core.network.ApiClient
 import com.example.hormigaahorradora.core.network.CalculatorService
import com.example.hormigaahorradora.core.model.calculatorResponse.Expense
 import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.lang.Exception


class CalculatorRepository: CalculatorService {
    private val api = ApiClient.OperationesApi


    override suspend fun getCalculator(limit: Int): ResponseService<List<Expense>> {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getExpense(
                    clientId = ApiClient.CLIENT_ID,
                    limit = limit
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        ResponseService.Success(body.results)
                    } else {
                        ResponseService.Error("Respuesta vacía del servidor")
                    }
                } else {
                    ResponseService.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                ResponseService.Error(
                    "No pudieron cargar los datos: ${e.localizedMessage}"
                )
            }

        }
    }
}

