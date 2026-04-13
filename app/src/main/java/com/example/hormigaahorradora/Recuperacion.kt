package com.example.hormigaahorradora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.hormigaahorradora.databinding.FragmentRecuperacionBinding

class Recuperacion : Fragment() {

    private var _binding: FragmentRecuperacionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecuperacionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupValidation()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRecuperar.setOnClickListener {
            // Acción para enviar el código (puedes navegar a otra pantalla si existe)
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