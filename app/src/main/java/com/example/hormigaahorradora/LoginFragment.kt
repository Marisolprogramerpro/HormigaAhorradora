package com.example.hormigaahorradora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.hormigaahorradora.databinding.FragmentLoginBinding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.example.hormigaahorradora.core.FragmentCommunicator
import com.example.hormigaahorradora.core.ResponseService
import com.example.hormigaahorradora.core.SignInViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SignInViewModel>()

    private lateinit var communicator: FragmentCommunicator
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        communicator = activity as FragmentCommunicator

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón Ingresar a Home
        setupValidation()
        binding.btnIngresar.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.requestLogin(email, password)
        }

        observeViewModel()

        setupClickListeners()
        observeState()
    }

    private fun observeViewModel() {
        viewModel.signInStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is AuthStatus.Loading -> {
                    communicator.manageLoader(true)
                }

                is AuthStatus.Success -> {
                    communicator.manageLoader(false)
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }

                is AuthStatus.Error -> {
                    communicator.manageLoader(false)
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupValidation() {
        binding.btnIngresar.isEnabled = false
        binding.etEmail.addTextChangedListener { validateAndEnable() }
        binding.etPassword.addTextChangedListener { validateAndEnable() }


    }

    private fun validateAndEnable() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        binding.btnIngresar.isEnabled = email.isNotEmpty() && password.isNotEmpty()

        binding.etEmail.error = viewModel.validateEmail(email)
        binding.etPassword.error = viewModel.validatePassword(password)
        binding.btnIngresar.isEnabled = viewModel.isLoginFormValid(email, password)
    }

    private fun setupClickListeners() {
        binding.btnIngresar.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.requestLogin(email, password)
        }
        binding.tvRegister.setOnClickListener {
            findNavController()
                .navigate(R.id.action_loginFragment_to_registro)
        }

    }
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInStatus.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.btnIngresar.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            // TODO: navegar a MainActivity
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            binding.btnIngresar.isEnabled = true
                            Snackbar.make(binding.root, state.error,
                                Snackbar.LENGTH_LONG).show()
                        }
                        null -> Unit
                    }
                }
            }
        }

}