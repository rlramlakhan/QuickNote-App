package com.rl.quicknote.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rl.quicknote.model.entities.User

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance().getReference("users")

    fun signUp(name: String, email: String, password: String): LiveData<Result<FirebaseUser?>> {
        val result = MutableLiveData<Result<FirebaseUser?>>()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val uid = firebaseAuth.currentUser?.uid.toString()
                    val user = User(uid, name, email)
                    firebaseDatabase.child(uid).setValue(user)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                result.value = Result.success(firebaseAuth.currentUser)
                            } else {
                                firebaseAuth.currentUser?.delete()
                                result.value = Result.failure(task.exception ?: Exception(task.exception?.message))
                            }
                        }
                } else {
                    result.value = Result.failure(authTask.exception ?: Exception(authTask.exception?.message))
                }
            }
        return result
    }

    fun signIn(email: String, password: String): LiveData<Result<FirebaseUser?>> {
        val result = MutableLiveData<Result<FirebaseUser?>>()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.value = Result.success(firebaseAuth.currentUser)
                } else {
                    result.value = Result.failure(task.exception ?: Exception(task.exception?.message))
                }
            }
        return result
    }

    fun signOut(): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        try {
            firebaseAuth.signOut()
            result.value = Result.success(true)
        } catch (e: Exception) {
            result.value = Result.failure(e)
        }
        return result
    }

    fun resetPassword(email: String): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.value = Result.success(true)
                } else {
                    result.value = Result.failure(task.exception ?: Exception(task.exception?.message))
                }
            }
        return result
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun getUserName(): LiveData<String?> {
        val result = MutableLiveData<String?>()

        firebaseDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = firebaseAuth.currentUser?.uid?.let { snapshot.child(it).child("name").value.toString() }
                    result.value = name
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        return result
    }
}