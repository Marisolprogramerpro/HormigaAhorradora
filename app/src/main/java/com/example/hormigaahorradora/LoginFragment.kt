package com.example.hormigaahorradora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.hormigaahorradora.databinding.FragmentLoginBinding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.example.hormigaahorradora.core.FragmentCommunicator

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SingInViewModel>()

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
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }

        // Texto Register a Registro
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registro)
        }

        // Texto Forgot Password a Recuperación
        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recuperacion)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setupValidation(){
        binding.btnIngresar.isEnabled = false
        binding.etEmail.addTextChangedListener{
            validateFields()
        }
        binding.etPassword.addTextChangedListener{
            validateFields()
        }
    
    }
    private fun validateFields(){
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        binding.btnIngresar.isEnabled = email.isNotEmpty() && password.isNotEmpty()
        
        val isValidEmail = isValidEmail(email)
        val isValidPassword = password.length >= 8
        
        binding.etEmail.error = if (email.isEmpty()) "Correo requerido" else if (!isValidEmail) "Correo inválido" else null
        binding.etPassword.error = if (password.isEmpty()) "Contraseña requerida" else if (!isValidPassword) "Mínimo 8 carácteres" else null
        
        binding.btnIngresar.isEnabled = isValidEmail && isValidPassword
        
    
    }
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}


