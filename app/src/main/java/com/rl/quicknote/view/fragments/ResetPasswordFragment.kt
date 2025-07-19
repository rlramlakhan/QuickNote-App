package com.rl.quicknote.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rl.quicknote.R
import com.rl.quicknote.databinding.FragmentResetPasswordBinding
import com.rl.quicknote.model.repositories.AuthRepository
import com.rl.quicknote.view.activities.MainActivity
import com.rl.quicknote.viewmodel.AuthViewModel
import com.rl.quicknote.viewmodel.AuthViewModelFactory

class ResetPasswordFragment : Fragment() {
    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        val authRepository = AuthRepository()
        val authFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        binding.ivBackResetPass.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.etEmailResetPass.doOnTextChanged { _, _, _, _ ->  validateInputs()}

        binding.btnSend.setOnClickListener {
            val email = binding.etEmailResetPass.text.toString().trim()
            authViewModel.resetPassword(email)
        }
    }

    private fun validateInputs() {
        val email = binding.etEmailResetPass.text.toString().trim()

        binding.btnSend.isEnabled = email.isNotEmpty()
    }

    private fun observeViewModel() {
        authViewModel.resetPassResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Snackbar.make(binding.root, "Email sent", Snackbar.LENGTH_LONG).show()
                findNavController().navigateUp()
            }.onFailure { error ->
                Snackbar.make(binding.root, error.message.toString(), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}