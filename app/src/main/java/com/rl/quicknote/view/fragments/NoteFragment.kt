package com.rl.quicknote.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rl.quicknote.databinding.FragmentNoteBinding
import com.rl.quicknote.model.entities.Category
import com.rl.quicknote.model.entities.Note
import com.rl.quicknote.model.repositories.AuthRepository
import com.rl.quicknote.model.repositories.NoteRepository
import com.rl.quicknote.viewmodel.AuthViewModel
import com.rl.quicknote.viewmodel.AuthViewModelFactory
import com.rl.quicknote.viewmodel.NoteViewModel
import com.rl.quicknote.viewmodel.NoteViewModelFactory
import com.rl.quicknote.viewmodel.SharedViewModel

class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private var isEdit = false
    private lateinit var savedNote: Note

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        val noteRepository = NoteRepository()
        val noteFactory = NoteViewModelFactory(noteRepository)
        noteViewModel = ViewModelProvider(this, noteFactory)[NoteViewModel::class.java]

        val authRepository = AuthRepository()
        val authFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ibBackNote.setOnClickListener {
            findNavController().navigateUp()
        }

        sharedViewModel.selectedNote.observe(viewLifecycleOwner) { note ->
            note?.let {
                isEdit = true
                savedNote = note
                binding.etTitleNote.setText(note.title)
                binding.etContentNote.setText(note.content)
            }
        }
        binding.ibSaveNote.setOnClickListener {
            val title = binding.etTitleNote.text.toString().trim()
            val content = binding.etContentNote.text.toString().trim()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                if (isEdit) {
                    val note = Note(savedNote.id, title, content, savedNote.timeStamp, savedNote.categories, savedNote.uid)
                    noteViewModel.updateNote(note)
                    findNavController().navigateUp()
                    findNavController().navigateUp()
                } else {
                    val id = System.currentTimeMillis().toString()
                    authViewModel.getCurrentUser()
                    val uid = authViewModel.user.value?.uid.toString()
                    val timestamp = System.currentTimeMillis().toString()
                    val note = Note(id, title, content, timestamp, listOf(Category("defaultAll", "All", "local")), uid)
                    noteViewModel.addNote(note)
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}