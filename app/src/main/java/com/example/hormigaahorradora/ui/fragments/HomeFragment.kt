package com.example.hormigaahorradora.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hormigaahorradora.databinding.FragmentHomeBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hormigaahorradora.R
import com.example.hormigaahorradora.core.ResponseService
import com.example.hormigaahorradora.core.operations.CategoriasAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.hormigaahorradora.core.model.CategoriasResponse.Categoria
import com.example.hormigaahorradora.data.model.Gasto

class HomeFragment : Fragment() {



    class HomeFragment : Fragment() {

        private var _binding: FragmentHomeBinding? = null
        private val binding get() = _binding!!

        // Usamos la Activity como owner para que el ViewModel se comparta con CategoriaDetailFragment
        private val viewModel by viewModels <GastoViewModel>(
            ownerProducer = { requireActivity() }
        )

        private val adapter = CategoriasAdapter { categoria ->
            val bundle = Bundle().apply { putParcelable("categoria", categoria) }
            findNavController().navigate(R.id.action_homeFragment_to_categoriaDetailFragment, bundle)
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentHomeBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setupRecyclerView()
            observeState()
            viewModel.loadCategorias()

            val mes = SimpleDateFormat("MMMM yyyy", Locale("es", "MX"))
                .format(Date()).replaceFirstChar { it.uppercase() }
            binding.tvMesActual.text = "Informe: $mes"
        }

        private fun setupRecyclerView() {
            binding.rvCategorias.layoutManager = LinearLayoutManager(requireContext())
            binding.rvCategorias.adapter = adapter
        }

        private fun observeState() {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {

                    // Categorías del JSON
                    launch {
                        viewModel.categoriasState.collect { state ->
                            when (state) {
                                is ResponseService.Loading ->
                                    binding.progressBar.visibility = View.VISIBLE
                                is ResponseService.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    adapter.submitList(state.data)
                                }
                                is ResponseService.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                                }
                                null -> {}
                            }
                        }
                    }

                    // Totales por categoría desde Firestore
                    launch {
                        viewModel.totalesPorCategoria.collect { totales ->
                            adapter.submitTotales(totales)
                            // Actualizar resumen del header
                            val totalGastado = totales.values.sum()
                            binding.tvTotalGastado.text = "$%.2f".format(totalGastado)
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
}