package com.rl.quicknote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rl.quicknote.model.entities.Note

class SharedViewModel: ViewModel() {

    private val _selectedNote = MutableLiveData<Note>()
    val selectedNote: LiveData<Note> = _selectedNote

    fun selectNote(note: Note) {
        _selectedNote.value = note
    }
}