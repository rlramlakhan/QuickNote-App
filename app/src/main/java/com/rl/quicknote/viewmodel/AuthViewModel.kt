package com.rl.quicknote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import com.rl.quicknote.model.repositories.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository): ViewModel() {
    private val _signUpResult = MutableLiveData<Result<FirebaseUser?>>()
    val signUpResult: LiveData<Result<FirebaseUser?>> = _signUpResult

    private val _signInResult = MutableLiveData<Result<FirebaseUser?>>()
    val signInResult: LiveData<Result<FirebaseUser?>> = _signInResult

    private val _signOutResult = MutableLiveData<Result<Boolean>>()
    val signOutResult: LiveData<Result<Boolean>> = _signOutResult

    private val _resetPassResult = MutableLiveData<Result<Boolean>>()
    val resetPassResult: LiveData<Result<Boolean>> = _resetPassResult

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?> = _userName

    fun signUp(name: String, email: String, password: String) {
        authRepository.signUp(name, email, password).observeForever { result ->
            _signUpResult.value = result
        }
    }

    fun signIn(email: String, password: String) {
        authRepository.signIn(email, password).observeForever { result ->
            _signInResult.value = result
        }
    }

    fun signOut() {
        authRepository.signOut().observeForever { result ->
            _signOutResult.value = result
        }
    }

    fun resetPassword(email: String) {
        authRepository.resetPassword(email).observeForever { result ->
            _resetPassResult.value = result
        }
    }

    fun getCurrentUser() {
        _user.value = authRepository.getCurrentUser()
    }

    fun getUserName() {
        authRepository.getUserName().observeForever { result ->
            _userName.value = result
        }
    }
}

class AuthViewModelFactory(private val authRepository: AuthRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(authRepository) as T
    }
}