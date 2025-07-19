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
import com.rl.quicknote.databinding.FragmentSignInBinding
import com.rl.quicknote.model.repositories.AuthRepository
import com.rl.quicknote.view.activities.MainActivity
import com.rl.quicknote.viewmodel.AuthViewModel
import com.rl.quicknote.viewmodel.AuthViewModelFactory

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val authRepository = AuthRepository()
        val authFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.getCurrentUser()
        redirectIfLoggedIn()
        observeViewModel()
        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_resetPasswordFragment)
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.etEmailSignIn.doOnTextChanged { _, _, _, _ ->  validateInputs()}
        binding.etPasswordSignIn.doOnTextChanged { _, _, _, _ ->  validateInputs()}

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmailSignIn.text.toString().trim()
            val password = binding.etPasswordSignIn.text.toString().trim()

            authViewModel.signIn(email, password)
            binding.progressBarSignIn.visibility = View.VISIBLE
        }
    }

    private fun validateInputs() {
        val email = binding.etEmailSignIn.text.toString().trim()
        val password = binding.etPasswordSignIn.text.toString().trim()

        binding.btnSignIn.isEnabled = email.isNotEmpty() && password.isNotEmpty()
    }

    private fun observeViewModel() {
        authViewModel.signInResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }.onFailure { error ->
                Snackbar.make(binding.root, error.message.toString(), Snackbar.LENGTH_LONG).show()
            }
            binding.progressBarSignIn.visibility = View.GONE
        }
    }

    private fun redirectIfLoggedIn() {
        authViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}