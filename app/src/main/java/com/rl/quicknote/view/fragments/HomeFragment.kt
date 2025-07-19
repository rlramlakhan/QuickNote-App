package com.rl.quicknote.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rl.quicknote.R
import com.rl.quicknote.databinding.FragmentHomeBinding
import com.rl.quicknote.model.entities.Note
import com.rl.quicknote.model.repositories.NoteRepository
import com.rl.quicknote.view.adapters.NoteAdapter
import com.rl.quicknote.viewmodel.NoteViewModel
import com.rl.quicknote.viewmodel.NoteViewModelFactory
import com.rl.quicknote.viewmodel.SharedViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val noteRepository = NoteRepository()
        val noteFactory = NoteViewModelFactory(noteRepository)
        noteViewModel = ViewModelProvider(this, noteFactory)[NoteViewModel::class.java]

        binding.recyclerViewMain.layoutManager = LinearLayoutManager(requireContext())
        noteAdapter = NoteAdapter()
        binding.recyclerViewMain.adapter = noteAdapter

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        binding.fabBtnAdd.setOnClickListener {
            sharedViewModel.selectNote(null)
            findNavController().navigate(R.id.action_homeFragment_to_noteFragment)
        }

        noteAdapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(note: Note) {
                sharedViewModel.selectNote(note)
                findNavController().navigate(R.id.action_homeFragment_to_showNoteFragment)
            }

        })
    }

    private fun observeViewModel() {
        noteViewModel.notes.observe(viewLifecycleOwner) { notes ->
            if (notes.isEmpty()) {
                binding.tvNoNotes.visibility = View.VISIBLE
            } else {
                noteAdapter.updateList(notes)
            }
            binding.progressBarMain.visibility = View.GONE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}