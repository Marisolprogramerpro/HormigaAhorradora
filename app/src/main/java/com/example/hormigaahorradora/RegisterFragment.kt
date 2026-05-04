package com.example.hormigaahorradora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.hormigaahorradora.databinding.FragmentRegistroBinding
import androidx.fragment.app.viewModels

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistroBinding.inflate(inflater, container, false)
        binding.btnRegistrar.setOnClickListener {
            viewModel.requestSignUp(binding.etEmail.text.toString().trim(), binding.etPassword.text.toString().trim())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupValidation()

        binding.btnRegistrar.setOnClickListener {
            findNavController().navigate(R.id.action_registro_to_registroDatos)
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupValidation() {
        binding.btnRegistrar.isEnabled = false
        
        binding.etName.addTextChangedListener { validateFields() }
        binding.etEmail.addTextChangedListener { validateFields() }
        binding.etPassword.addTextChangedListener { validateFields() }
    }

    private fun validateFields() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val isValidEmail = isValidEmail(email)
        val isValidPassword = password.length >= 8
        val isValidName = name.isNotEmpty()

        // Mostrar errores visuales
        binding.etName.error = if (name.isEmpty()) "Nombre requerido" else null
        binding.etEmail.error = if (email.isEmpty()) "Correo requerido" else if (!isValidEmail) "Correo inválido" else null
        binding.etPassword.error = if (password.isEmpty()) "Contraseña requerida" else if (!isValidPassword) "Mínimo 8 caracteres" else null

        // Habilitar botón solo si todo es válido
        binding.btnRegistrar.isEnabled = isValidName && isValidEmail && isValidPassword
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}