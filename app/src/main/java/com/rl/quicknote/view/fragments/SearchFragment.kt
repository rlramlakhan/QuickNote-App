package com.rl.quicknote.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rl.quicknote.R
import com.rl.quicknote.databinding.FragmentSearchBinding
import com.rl.quicknote.model.entities.Note
import com.rl.quicknote.model.repositories.CategoryRepository
import com.rl.quicknote.model.repositories.NoteRepository
import com.rl.quicknote.view.adapters.NoteAdapter
import com.rl.quicknote.viewmodel.CategoryViewModel
import com.rl.quicknote.viewmodel.CategoryViewModelFactory
import com.rl.quicknote.viewmodel.NoteViewModel
import com.rl.quicknote.viewmodel.NoteViewModelFactory
import com.rl.quicknote.viewmodel.SharedViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val noteRepository = NoteRepository()
        val noteFactory = NoteViewModelFactory(noteRepository)
        noteViewModel = ViewModelProvider(this, noteFactory)[NoteViewModel::class.java]

        binding.recyclerViewSearchNote.layoutManager = LinearLayoutManager(requireContext())
        noteAdapter = NoteAdapter()
        binding.recyclerViewSearchNote.adapter = noteAdapter

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        val categoryRepository = CategoryRepository()
        val categoryFactory = CategoryViewModelFactory(categoryRepository)
        categoryViewModel = ViewModelProvider(this, categoryFactory)[CategoryViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ibBackSearch.setOnClickListener {
            findNavController().navigateUp()
        }

        noteViewModel.searchResult.observe(viewLifecycleOwner) { notes ->
            if (notes.isEmpty()) {
                binding.tvNotesNotFound.visibility = View.VISIBLE
            } else {
                binding.tvNotesNotFound.visibility = View.GONE
                noteAdapter.updateList(notes)
            }
        }

        binding.etTextNoteSearch.addTextChangedListener {
            val query = it.toString().trim()
            if (query.isNotEmpty()) {
                noteViewModel.searchNote(query)
            } else {
                noteAdapter.updateList(emptyList())
            }
        }

        noteAdapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(note: Note) {
                sharedViewModel.selectNote(note)
                findNavController().navigate(R.id.action_searchFragment_to_showNoteFragment)
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}