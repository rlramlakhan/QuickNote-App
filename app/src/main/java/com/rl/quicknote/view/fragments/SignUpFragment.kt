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
import com.rl.quicknote.databinding.FragmentSignUpBinding
import com.rl.quicknote.model.repositories.AuthRepository
import com.rl.quicknote.view.activities.MainActivity
import com.rl.quicknote.viewmodel.AuthViewModel
import com.rl.quicknote.viewmodel.AuthViewModelFactory

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val authRepository = AuthRepository()
        val authFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        binding.ivBackSignUp.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        binding.etNameSignUp.doOnTextChanged { _, _, _, _ -> validateInputs() }
        binding.etEmailSignUp.doOnTextChanged {_, _, _, _ -> validateInputs()}
        binding.etPasswordSignUp.doOnTextChanged { _, _, _, _ ->  validateInputs()}

        binding.btnSignUp.setOnClickListener {
            val name = binding.etNameSignUp.text.toString().trim()
            val email = binding.etEmailSignUp.text.toString().trim()
            val password = binding.etPasswordSignUp.text.toString().trim()

            authViewModel.signUp(name, email, password)
            binding.progressBarSignUp.visibility = View.VISIBLE
        }

    }

    private fun validateInputs() {
        val name = binding.etNameSignUp.text.toString().trim()
        val email = binding.etEmailSignUp.text.toString().trim()
        val password = binding.etPasswordSignUp.text.toString().trim()

        binding.btnSignUp.isEnabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
    }

    private fun observeViewModel() {
        authViewModel.signUpResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }.onFailure { error ->
                Snackbar.make(binding.root, error.message.toString(), Snackbar.LENGTH_LONG).show()
            }
            binding.progressBarSignUp.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}