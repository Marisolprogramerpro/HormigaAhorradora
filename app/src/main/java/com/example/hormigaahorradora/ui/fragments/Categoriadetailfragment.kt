package com.example.hormigaahorradora.ui.fragments
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hormigaahorradora.core.ResponseService
import com.example.hormigaahorradora.core.operations.GastosAdapter
import com.example.hormigaahorradora.data.model.Gasto
import com.example.hormigaahorradora.databinding.DialogGastoBinding
import com.example.hormigaahorradora.databinding.FragmentCategoriaDetailBinding
import com.example.hormigaahorradora.ui.viewmodels.GastoViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Calendar
import com.example.hormigaahorradora.signUp.GastoViewModel
import com.example.hormigaahorradora.core.model.CategoriasResponse.Categoria
import com.example.hormigaahorradora.data.model.Gasto
import com.example.hormigaahorradora.databinding.DialogGastoBinding
class Categoriadetailfragment {


    class CategoriaDetailFragment : Fragment() {

        private var _binding: FragmentCategoriaDetailBinding? = null
        private val binding get() = _binding!!

        // Comparte el ViewModel con HomeFragment para actualizar totales al regresar
        private val viewModel by viewModels<GastoViewModel>(
            ownerProducer = { requireActivity() }
        )

        private lateinit var categoria: Categoria
        private var gastoEditando: Gasto? = null  // null = nuevo gasto, != null = edición

        private val adapter = GastosAdapter(
            onEditClick = { gasto -> mostrarDialogGasto(gasto) },
            onDeleteClick = { gasto -> confirmarEliminar(gasto) }
        )

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            categoria = requireArguments().getParcelable("categoria")
                ?: error("Se requiere el argumento 'categoria'")
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentCategoriaDetailBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setupHeader()
            setupRecyclerView()
            setupListeners()
            observeState()
            viewModel.loadGastosPorCategoria(categoria.id)
        }

        private fun setupHeader() {
            try { binding.headerDetalle.setBackgroundColor(Color.parseColor(categoria.color)) }
            catch (e: Exception) { /* fallback */ }
            binding.tvIconoDetalle.text = categoria.icono
            binding.tvNombreDetalle.text = categoria.nombre
            binding.tvDescripcionDetalle.text = categoria.descripcion
            binding.tvPresupuestoDetalle.text = "Presupuesto: $%.2f".format(categoria.presupuestoMensual)
        }

        private fun setupRecyclerView() {
            binding.rvGastos.layoutManager = LinearLayoutManager(requireContext())
            binding.rvGastos.adapter = adapter
        }

        private fun setupListeners() {
            binding.btnRegresar.setOnClickListener { findNavController().navigateUp() }
            binding.fabAgregarGasto.setOnClickListener { mostrarDialogGasto(null) }
        }

        private fun observeState() {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {

                    // Gastos de esta categoría
                    launch {
                        viewModel.gastosState.collect { state ->
                            when (state) {
                                is ResponseService.Success -> {
                                    adapter.submitList(state.data)
                                    actualizarResumen(state.data)
                                }
                                is ResponseService.Error ->
                                    Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                                else -> {}
                            }
                        }
                    }

                    // Resultado de agregar/editar/eliminar
                    launch {
                        viewModel.operacionState.collect { state ->
                            when (state) {
                                is ResponseService.Success -> {
                                    viewModel.resetOperacionState()
                                }
                                is ResponseService.Error -> {
                                    Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                                    viewModel.resetOperacionState()
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }

        private fun actualizarResumen(gastos: List<Gasto>) {
            val total = gastos.sumOf { it.monto }
            val porcentaje = if (categoria.presupuestoMensual > 0)
                ((total / categoria.presupuestoMensual) * 100).toInt().coerceAtMost(100) else 0

            binding.tvTotalDetalle.text = "Total gastado: $%.2f".format(total)
            binding.progressBarDetalle.progress = porcentaje
            binding.tvPorcentaje.text = "$porcentaje% del presupuesto"

            val restante = categoria.presupuestoMensual - total
            binding.tvRestante.text = if (restante >= 0)
                "Disponible: $%.2f".format(restante)
            else "¡Excedido por $%.2f!".format(-restante)
            binding.tvRestante.setTextColor(
                if (restante >= 0) Color.parseColor("#43A047") else Color.parseColor("#E53935")
            )
        }

        // Dialog para agregar o editar un gasto
        private fun mostrarDialogGasto(gastoExistente: Gasto?) {
            gastoEditando = gastoExistente
            val dialogBinding = DialogGastoBinding.inflate(layoutInflater)

            // Pre-llenar si es edición
            gastoExistente?.let {
                dialogBinding.etConcepto.setText(it.concepto)
                dialogBinding.etMonto.setText(it.monto.toString())
                dialogBinding.etFecha.setText(it.fecha)
                dialogBinding.etNota.setText(it.nota)
            }

            // DatePicker en el campo fecha
            dialogBinding.etFecha.setOnClickListener {
                val cal = Calendar.getInstance()
                DatePickerDialog(
                    requireContext(),
                    { _, y, m, d -> dialogBinding.etFecha.setText("%04d-%02d-%02d".format(y, m + 1, d)) },
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            val titulo = if (gastoExistente == null) "Agregar gasto" else "Editar gasto"

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(titulo)
                .setView(dialogBinding.root)
                .setPositiveButton("Guardar") { _, _ ->
                    val concepto = dialogBinding.etConcepto.text.toString().trim()
                    val montoStr = dialogBinding.etMonto.text.toString().trim()
                    val fecha = dialogBinding.etFecha.text.toString().trim()
                    val nota = dialogBinding.etNota.text.toString().trim()

                    if (concepto.isBlank() || montoStr.isBlank() || fecha.isBlank()) {
                        Snackbar.make(binding.root, "Completa concepto, monto y fecha", Snackbar.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    val monto = montoStr.toDoubleOrNull()
                    if (monto == null || monto <= 0) {
                        Snackbar.make(binding.root, "El monto debe ser un número mayor a 0", Snackbar.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    if (gastoExistente == null) {
                        // CREAR
                        viewModel.agregarGasto(
                            Gasto(categoriaId = categoria.id, concepto = concepto,
                                monto = monto, fecha = fecha, nota = nota)
                        )
                    } else {
                        // EDITAR
                        viewModel.editarGasto(
                            gastoExistente.copy(concepto = concepto, monto = monto,
                                fecha = fecha, nota = nota)
                        )
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        private fun confirmarEliminar(gasto: Gasto) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar gasto")
                .setMessage("¿Segura que quieres eliminar \"${gasto.concepto}\"?")
                .setPositiveButton("Eliminar") { _, _ -> viewModel.eliminarGasto(gasto) }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
}