package com.example.hormigaahorradora.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hormigaahorradora.core.ResponseService
import com.example.hormigaahorradora.core.model.CategoriasResponse
import com.example.hormigaahorradora.core.model.Categoria
import com.example.hormigaahorradora.data.model.Gasto
import com.example.hormigaahorradora.logic.GastoRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GastoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GastoRepository()
    private val _categoriasState = MutableStateFlow<ResponseService<List<Categoria>>?>(null)
    val categoriasState: StateFlow<ResponseService<List<Categoria>>?> = _categoriasState.asStateFlow()
    private val _gastosState = MutableStateFlow<ResponseService<List<Gasto>>?>(null)
    val gastosState: StateFlow<ResponseService<List<Gasto>>?> = _gastosState.asStateFlow()
    private val _operacionState = MutableStateFlow<ResponseService<Unit>?>(null)
    val operacionState: StateFlow<ResponseService<Unit>?> = _operacionState.asStateFlow()
    private val _totalesPorCategoria = MutableStateFlow<Map<String, Double>>(emptyMap())
    val totalesPorCategoria: StateFlow<Map<String, Double>> = _totalesPorCategoria.asStateFlow()

    fun loadCategorias() {
        viewModelScope.launch {
            _categoriasState.value = ResponseService.Loading
            try {
                val categorias = withContext(Dispatchers.IO) {
                    val context = getApplication<Application>().applicationContext
                    val inputStream = context.resources.openRawResource(
                        context.resources.getIdentifier("categorias", "raw", context.packageName)
                    )
                    val json = inputStream.bufferedReader().use { it.readText() }
                    Gson().fromJson(json, CategoriasResponse::class.java).categorias
                }
                _categoriasState.value = ResponseService.Success(categorias)
                // Después de cargar categorías, carga todos los gastos para calcular totales
                loadTotalesPorCategoria()
            } catch (e: Exception) {
                _categoriasState.value = ResponseService.Error("Error al cargar categorías: ${e.localizedMessage}")
            }
        }
    }

    // Carga TODOS los gastos y calcula el total gastado por categoría
    private fun loadTotalesPorCategoria() {
        viewModelScope.launch {
            when (val result = repository.getTodosLosGastos()) {
                is ResponseService.Success -> {
                    val totales = result.data.groupBy { it.categoriaId }
                        .mapValues { (_, gastos) -> gastos.sumOf { it.monto } }
                    _totalesPorCategoria.value = totales
                }
                else -> {}
            }
        }
    }

    // --- Cargar gastos de una categoría específica ---
    fun loadGastosPorCategoria(categoriaId: String) {
        viewModelScope.launch {
            _gastosState.value = ResponseService.Loading
            _gastosState.value = repository.getGastosPorCategoria(categoriaId)
        }
    }

    // --- CRUD ---
    fun agregarGasto(gasto: Gasto) {
        viewModelScope.launch {
            _operacionState.value = ResponseService.Loading
            _operacionState.value = repository.agregarGasto(gasto)
            if (_operacionState.value is ResponseService.Success) {
                // Recarga gastos y totales
                loadGastosPorCategoria(gasto.categoriaId)
                loadTotalesPorCategoria()
            }
        }
    }

    fun editarGasto(gasto: Gasto) {
        viewModelScope.launch {
            _operacionState.value = ResponseService.Loading
            _operacionState.value = repository.editarGasto(gasto)
            if (_operacionState.value is ResponseService.Success) {
                loadGastosPorCategoria(gasto.categoriaId)
                loadTotalesPorCategoria()
            }
        }
    }

    fun eliminarGasto(gasto: Gasto) {
        viewModelScope.launch {
            _operacionState.value = ResponseService.Loading
            _operacionState.value = repository.eliminarGasto(gasto.id)
            if (_operacionState.value is ResponseService.Success) {
                loadGastosPorCategoria(gasto.categoriaId)
                loadTotalesPorCategoria()
            }
        }
    }

    fun resetOperacionState() {
        _operacionState.value = null
    }
}
