package com.rl.quicknote.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rl.quicknote.model.entities.Category

class CategoryRepository {
    private val firebaseDatabase = FirebaseDatabase.getInstance().getReference("categories")
    private val authRepository = AuthRepository()

    fun addCategory(category: Category) {
        firebaseDatabase.child(category.uid).child(category.id).setValue(category)
    }

    fun getCategories(): LiveData<List<Category>> {
        val list = MutableLiveData<List<Category>>()

        val userId = authRepository.getCurrentUser()?.uid.toString()
        firebaseDatabase.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryList = mutableListOf<Category>()
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val id = dataSnapshot.child("id").value.toString()
                        val categoryName = dataSnapshot.child("categoryName").value.toString()
                        val uid = dataSnapshot.child("uid").value.toString()
                        val category = Category(id, categoryName, uid)
                        categoryList.add(category)
                    }
                    list.value = categoryList
                } else {
                    list.value = emptyList()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                list.value = emptyList()
            }

        })
        return list
    }

    fun deleteCategory(category: Category) {
        firebaseDatabase.child(category.uid).child(category.id).removeValue()
    }
}