package com.rl.quicknote.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rl.quicknote.R
import com.rl.quicknote.databinding.FragmentShowNoteBinding
import com.rl.quicknote.viewmodel.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShowNoteFragment : Fragment() {

    private var _binding: FragmentShowNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowNoteBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.selectedNote.observe(viewLifecycleOwner) { note ->
            binding.tvTitleShowNote.text = note.title
            binding.tvTimeStampShowNote.text = formateTimeStamp(note.timeStamp)
            binding.tvContentShowNote.text = note.content
        }

        binding.ibBackShowNote.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun formateTimeStamp(timeStamp: String): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(timeStamp.toLong()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}