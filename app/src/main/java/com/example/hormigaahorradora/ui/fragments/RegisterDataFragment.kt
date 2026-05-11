package com.example.hormigaahorradora.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.hormigaahorradora.data.model.User
import com.example.hormigaahorradora.databinding.FragmentRegistroDatosBinding
import com.example.hormigaahorradora.logic.AuthRepository
import com.example.hormigaahorradora.ui.activities.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterDataFragment : Fragment() {

    private var _binding: FragmentRegistroDatosBinding? = null
    private val binding get() = _binding!!
    private val repository = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistroDatosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFinalizar.setOnClickListener {
            saveUserDataAndNavigate()
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun saveUserDataAndNavigate() {
        val name = binding.etName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val birthDate = binding.etBirthDate.text.toString().trim()

        if (name.isEmpty() || lastName.isEmpty() || username.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor completa los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val user = User(
                uid = currentUser.uid,
                nombre = name,
                apellidos = lastName,
                email = currentUser.email ?: "",
                nombreUsuario = username,
                telefono = phone,
                fechaNacimiento = birthDate
            )

            viewLifecycleOwner.lifecycleScope.launch {
                // Usaremos un método en el repositorio para actualizar/guardar estos datos adicionales
                try {
                    val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    firestore.collection("users").document(user.uid).set(user).await()
                    
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}