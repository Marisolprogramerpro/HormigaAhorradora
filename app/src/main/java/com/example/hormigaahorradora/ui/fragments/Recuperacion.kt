package com.example.hormigaahorradora.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.hormigaahorradora.core.FragmentCommunicator
import com.example.hormigaahorradora.core.ResponseService
import com.example.hormigaahorradora.databinding.FragmentRecuperacionBinding
import com.example.hormigaahorradora.ui.viewmodels.RecuperacionViewModel
import kotlinx.coroutines.launch

class Recuperacion : Fragment() {

    private var _binding: FragmentRecuperacionBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RecuperacionViewModel>()
    private lateinit var communicator: FragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecuperacionBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupValidation()
        observeState()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRecuperar.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            viewModel.resetPassword(email)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recuperacionState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.btnRecuperar.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            Toast.makeText(requireContext(), "Se ha enviado un correo para restablecer tu contraseña", Toast.LENGTH_LONG).show()
                            findNavController().navigateUp()
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            binding.btnRecuperar.isEnabled = true
                            Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }

    private fun setupValidation() {
        binding.btnRecuperar.isEnabled = false

        binding.etEmail.addTextChangedListener {
            val email = it.toString().trim()
            val isValidEmail = isValidEmail(email)

            binding.etEmail.error = if (email.isEmpty()) "Correo requerido" else if (!isValidEmail) "Correo inválido" else null
            
            binding.btnRecuperar.isEnabled = isValidEmail
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}