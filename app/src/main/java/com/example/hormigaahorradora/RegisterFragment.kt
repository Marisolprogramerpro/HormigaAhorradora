package com.example.hormigaahorradora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.hormigaahorradora.databinding.FragmentRegistroBinding
import androidx.fragment.app.viewModels
import com.example.hormigaahorradora.core.FragmentCommunicator
import com.example.hormigaahorradora.core.RegisterViewModel
import com.example.hormigaahorradora.core.AuthStatus
import com.example.hormigaahorradora.R
import kotlin.getValue
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Lifecycle
import com.example.hormigaahorradora.core.ResponseService
import com.google.android.material.snackbar.Snackbar
import androidx.lifecycle.repeatOnLifecycle

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RegisterViewModel>()
    private lateinit var communicator: FragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistroBinding.inflate(inflater, container, false)
        communicator =requireActivity() as FragmentCommunicator
        setupValidation()
        setupclickListeners()
        observeState()
        return binding.root
    }

    private fun setupValidation() {
        binding.btnRegistrar.isEnabled = false
        val watcher = { validateAndEnable() }
        binding.etName.addTextChangedListener { validateAndEnable() }
        binding.etEmail.addTextChangedListener { validateAndEnable() }
        binding.etPassword.addTextChangedListener { validateAndEnable() }
    }

    private fun validateAndEnable() {
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString().trim()
        val confirm = binding.confirmPasswordTiet.text.toString().trim()

        binding.etEmail.error = viewModel.validateEmail(email)
        binding.etPassword.error = viewModel.validatePassword(pass)
        binding.confirmPasswordTil.error =
            viewModel.validateConfirmPassword(pass, confirm)

        binding.btnRegistrar.isEnabled =
            viewModel.isRegisterFormValid(email, pass, confirm)
    }

    private fun setupClickListeners() {
        binding.btnRegistrar.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.requestSignUp(email, password)
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
                            // TODO: navegar a pantalla de datos personales
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

}

