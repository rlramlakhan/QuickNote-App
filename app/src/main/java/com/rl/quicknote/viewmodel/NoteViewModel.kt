package com.rl.quicknote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rl.quicknote.model.entities.Note
import com.rl.quicknote.model.repositories.NoteRepository

class NoteViewModel(private val noteRepository: NoteRepository): ViewModel() {

    val notes: LiveData<List<Note>> = noteRepository.getNotes()

    fun addNote(note: Note) {
        noteRepository.addNote(note)
    }

    fun updateNote(note: Note) {
        noteRepository.updateNote(note)
    }

    fun deleteNote(note: Note) {
        noteRepository.deleteNote(note)
    }
}

class NoteViewModelFactory(private val noteRepository: NoteRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(noteRepository) as T
    }
}