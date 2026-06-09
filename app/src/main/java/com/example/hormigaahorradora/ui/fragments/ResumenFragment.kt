package com.example.hormigaahorradora.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.hormigaahorradora.core.ResponseService
import com.example.hormigaahorradora.databinding.FragmentResumenBinding
import com.example.hormigaahorradora.ui.viewmodels.GastoViewModel
import kotlinx.coroutines.launch

class ResumenFragment : Fragment() {
    private var _binding: FragmentResumenBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<GastoViewModel>(
        ownerProducer = { requireActivity() }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResumenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Totales por categoría (ya calculados en GastoViewModel)
                launch {
                    viewModel.totalesPorCategoria.collect { totales ->
                        val totalGastado = totales.values.sum()
                        binding.tvTotalGeneral.text = "$%.2f".format(totalGastado)

                        // Categoría con más gasto
                        val catMayor = totales.maxByOrNull { it.value }
                        binding.tvCategoriaMayorGasto.text = catMayor?.key ?: "—"
                        binding.tvMontoMayor.text = "$%.2f".format(catMayor?.value ?: 0.0)

                        // Número de categorías con al menos un gasto
                        binding.tvNumCategorias.text = "${totales.size} categorías con gastos"
                    }
                }

                // Categorías (para calcular presupuesto total y porcentaje)
                launch {
                    viewModel.categoriasState.collect { state ->
                        if (state is ResponseService.Success) {
                            val presupuestoTotal = state.data.sumOf { it.presupuestoMensual }
                            val totalGastado = viewModel.totalesPorCategoria.value.values.sum()
                            val porcentaje = if (presupuestoTotal > 0)
                                ((totalGastado / presupuestoTotal) * 100).toInt().coerceAtMost(100)
                            else 0

                            binding.tvPresupuestoTotal.text = "de $%.2f".format(presupuestoTotal)
                            binding.progressGeneral.progress = porcentaje
                            binding.tvPorcentajeGeneral.text = "$porcentaje% del presupuesto total"

                            val restante = presupuestoTotal - totalGastado
                            binding.tvRestanteGeneral.text = if (restante >= 0)
                                "Disponible: $%.2f".format(restante)
                            else "¡Excedido por $%.2f!".format(-restante)

                            // Consejo de la hormiga
                            binding.tvConsejo.text = when {
                                porcentaje >= 90 -> "🚨 Has usado casi todo tu presupuesto. ¡Cuidado con los gastos!"
                                porcentaje >= 70 -> "⚠️ Llevas más del 70% del presupuesto. Monitorea bien."
                                porcentaje >= 50 -> "👍 Vas a la mitad del presupuesto. ¡Sigue así!"
                                else -> "🐜 ¡Excelente! La hormiga ahorradora está orgullosa de ti."
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
