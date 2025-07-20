package com.rl.quicknote.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.rl.quicknote.model.entities.Category
import com.rl.quicknote.model.entities.Note

class NoteRepository {

    private val firebaseDatabase = FirebaseDatabase.getInstance().getReference("notes")
    private val authRepository = AuthRepository()

    fun addNote(note: Note) {
        firebaseDatabase.child(note.uid).child(note.id).setValue(note)
    }

    fun getNotes(): LiveData<List<Note>> {
        val list = MutableLiveData<List<Note>>()

        val userId = authRepository.getCurrentUser()?.uid.toString()
        firebaseDatabase.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val noteList = mutableListOf<Note>()
                if (snapshot.exists()) {
                    for (noteSnap in snapshot.children) {
                        val id = noteSnap.child("id").value.toString()
                        val title = noteSnap.child("title").value.toString()
                        val content = noteSnap.child("content").value.toString()
                        val timeStamp = noteSnap.child("timeStamp").value.toString()
                        val categoriesData = noteSnap.child("categories")
                        val categories = mutableListOf<Category>()
                        for (data in categoriesData.children) {
                            categories.add(Category(
                                data.child("id").value.toString(),
                                data.child("categoryName").value.toString(),
                                data.child("uid").value.toString()))
                        }
                        val uid = noteSnap.child("uid").value.toString()
                        val note = Note(id, title, content, timeStamp, categories, uid)
                        noteList.add(note)
                    }
                    list.value = noteList
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

    fun updateNote(note: Note) {
        firebaseDatabase.child(note.uid).child(note.id).setValue(note)
    }

    fun deleteNote(note: Note) {
        firebaseDatabase.child(note.uid).child(note.id).removeValue()
    }

    fun searchNote(query: String): LiveData<List<Note>> {
        val result = MutableLiveData<List<Note>>()

        val userId = authRepository.getCurrentUser()?.uid.toString()
        if (userId.isEmpty()) {
            result.value = emptyList()
            return result
        }

        firebaseDatabase.child(userId).orderByChild("title")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notes = mutableListOf<Note>()

                    if (snapshot.exists()) {
                        for (noteSnapshot in snapshot.children) {
                            val id = noteSnapshot.child("id").value.toString()
                            val title = noteSnapshot.child("title").value.toString()
                            val content = noteSnapshot.child("content").value.toString()
                            val timeStamp = noteSnapshot.child("timeStamp").value.toString()
                            val categoriesData = noteSnapshot.child("categories")
                            val categories = mutableListOf<Category>()
                            for (data in categoriesData.children) {
                                categories.add(Category(
                                    data.child("id").value.toString(),
                                    data.child("categoryName").value.toString(),
                                    data.child("uid").value.toString()))
                            }
                            val uid = noteSnapshot.child("uid").value.toString()
                            val note = Note(id, title, content, timeStamp, categories, uid)
                            notes.add(note)
                            result.value = notes
                        }
                    } else {
                        result.value = emptyList()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    result.value = emptyList()
                }

            })
        return result
    }

    fun getNotesByCategory(category: String): LiveData<List<Note>> {
        val result = MutableLiveData<List<Note>>()

        val userId = authRepository.getCurrentUser()?.uid.toString()
        if (userId.isEmpty()) {
            result.value = emptyList()
            return result
        }

        firebaseDatabase.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val notes = mutableListOf<Note>()
                    for (noteSnapshot in snapshot.children) {
                        val id = noteSnapshot.child("id").value.toString()
                        val title = noteSnapshot.child("title").value.toString()
                        val content = noteSnapshot.child("content").value.toString()
                        val timeStamp = noteSnapshot.child("timeStamp").value.toString()
                        val categoriesData = noteSnapshot.child("categories")
                        val categories = mutableListOf<Category>()
                        for (data in categoriesData.children) {
                            categories.add(Category(
                                data.child("id").value.toString(),
                                data.child("categoryName").value.toString(),
                                data.child("uid").value.toString()))
                        }
                        val uid = noteSnapshot.child("uid").value.toString()
                        val note = Note(id, title, content, timeStamp, categories, uid)
                        val hasCategory = note.categories.any {
                            it.categoryName == category
                        }
                        if (hasCategory) {
                            notes.add(note)
                        }
                    }
                    result.value = notes

                } else {
                    result.value = emptyList()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                result.value = emptyList()
            }

        })
        return result
    }

    fun deleteCategory(category: Category) {
        val userId = authRepository.getCurrentUser()?.uid.toString()
        firebaseDatabase.child(userId).get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach { noteSnapshot ->

                val noteId = noteSnapshot.key ?: return@forEach

                val categoriesSnapshot = noteSnapshot.child("categories")

                categoriesSnapshot.children.forEach { catSnap ->

                    val categoryId = catSnap.child("id").getValue(String::class.java) ?: ""

                    if (categoryId == category.id) {
                        firebaseDatabase
                            .child(userId)
                            .child(noteId)
                            .child("categories")
                            .child(catSnap.key!!)
                            .removeValue()

                    }
                }
            }
        }
    }
}