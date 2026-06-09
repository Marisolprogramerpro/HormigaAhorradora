package com.example.hormigaahorradora.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.hormigaahorradora.R
import com.example.hormigaahorradora.core.FragmentCommunicator
import com.example.hormigaahorradora.core.ResponseService
import com.example.hormigaahorradora.databinding.FragmentRegistroBinding
import com.example.hormigaahorradora.ui.viewmodels.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RegisterViewModel>()
    private lateinit var communicator: FragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistroBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        setupValidation()
        setupClickListeners()
        observeState()
        return binding.root
    }

    private fun setupValidation() {
        binding.btnRegistrar.isEnabled = false
        binding.etName.addTextChangedListener { validateAndEnable() }
        binding.etEmail.addTextChangedListener { validateAndEnable() }
        binding.etPassword.addTextChangedListener { validateAndEnable() }
        binding.etConfirmPassword.addTextChangedListener { validateAndEnable() }
    }

    private fun validateAndEnable() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString().trim()
        val confirm = binding.etConfirmPassword.text.toString().trim()

        binding.etName.error = if (name.isEmpty()) "Nombre requerido" else null
        binding.etEmail.error = viewModel.validateEmail(email)
        binding.etPassword.error = viewModel.validatePassword(pass)
        binding.etConfirmPassword.error =
            viewModel.validateConfirmPassword(pass, confirm)

        binding.btnRegistrar.isEnabled = name.isNotEmpty() &&
            viewModel.isRegisterFormValid(email, pass, confirm)
    }

    private fun setupClickListeners() {
        binding.btnRegistrar.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.requestSignUp(email, password, name)
        }
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.btnRegistrar.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_registro_to_registroDatos)
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            binding.btnRegistrar.isEnabled = true
                            Snackbar.make(binding.root, state.error,
                                Snackbar.LENGTH_LONG).show()
                        }
                        null -> Unit
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
