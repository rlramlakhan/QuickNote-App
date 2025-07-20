package com.rl.quicknote.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
                        val uid = noteSnap.child("uid").value.toString()
                        val note = Note(id, title, content, timeStamp, uid)
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
                            val uid = noteSnapshot.child("uid").value.toString()
                            val note = Note(id, title, content, timeStamp, uid)
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
}