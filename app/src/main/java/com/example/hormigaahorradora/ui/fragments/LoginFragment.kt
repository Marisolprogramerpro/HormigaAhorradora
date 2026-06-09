package com.example.hormigaahorradora.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.hormigaahorradora.databinding.FragmentLoginBinding
import com.example.hormigaahorradora.ui.activities.HomeActivity
import com.example.hormigaahorradora.ui.viewmodels.SignInViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

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
        communicator = requireActivity() as FragmentCommunicator
        setupValidation()
        setupClickListeners()
        observeState()
        return binding.root
    }

    private fun setupValidation() {
        binding.btnIngresar.isEnabled = false
        binding.etEmail.addTextChangedListener { validateAndEnable() }
        binding.etPassword.addTextChangedListener { validateAndEnable() }
    }

    private fun validateAndEnable() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

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
                viewModel.signInState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.btnIngresar.isEnabled = false
                        }

                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            val intent = Intent(requireContext(), HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }

                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            binding.btnIngresar.isEnabled = true
                            Snackbar.make(
                                binding.root, state.error,
                                Snackbar.LENGTH_LONG
                            ).show()
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
