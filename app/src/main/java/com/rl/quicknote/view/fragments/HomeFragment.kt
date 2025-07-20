package com.rl.quicknote.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rl.quicknote.R
import com.rl.quicknote.databinding.FragmentHomeBinding
import com.rl.quicknote.databinding.LayoutBottomSheetCategoryBinding
import com.rl.quicknote.model.entities.Category
import com.rl.quicknote.model.entities.Note
import com.rl.quicknote.model.repositories.AuthRepository
import com.rl.quicknote.model.repositories.CategoryRepository
import com.rl.quicknote.model.repositories.NoteRepository
import com.rl.quicknote.view.activities.AuthActivity
import com.rl.quicknote.view.adapters.CategoryAdapter
import com.rl.quicknote.view.adapters.NoteAdapter
import com.rl.quicknote.viewmodel.AuthViewModel
import com.rl.quicknote.viewmodel.AuthViewModelFactory
import com.rl.quicknote.viewmodel.CategoryViewModel
import com.rl.quicknote.viewmodel.CategoryViewModelFactory
import com.rl.quicknote.viewmodel.NoteViewModel
import com.rl.quicknote.viewmodel.NoteViewModelFactory
import com.rl.quicknote.viewmodel.SharedViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter


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

        val authRepository = AuthRepository()
        val authFactory = AuthViewModelFactory(authRepository)
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]

        val categoryRepository = CategoryRepository()
        val categoryFactory = CategoryViewModelFactory(categoryRepository)
        categoryViewModel = ViewModelProvider(this, categoryFactory)[CategoryViewModel::class.java]

        binding.recyclerViewNav.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter()
        binding.recyclerViewNav.adapter = categoryAdapter
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryViewModel.getCategories()
        observeCategories()
        observeViewModel()
        observeSignOutResult()
        authViewModel.getUserName()
        authViewModel.userName.observe(viewLifecycleOwner) {name ->
            if (name != null) {
                binding.tvUserName.text = "Hello, $name"
            }
        }
        binding.fabBtnAdd.setOnClickListener {
            sharedViewModel.selectNote(null)
            findNavController().navigate(R.id.action_homeFragment_to_noteFragment)
        }

        categoryAdapter.setOnCategoryClickListener(object : CategoryAdapter.OnItemClickListener {
            override fun onItemClick(category: Category) {
                if (category.id == "defaultAdd") {
                    showAddCategorySheet()
                }
            }

        })

        noteAdapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(note: Note) {
                sharedViewModel.selectNote(note)
                findNavController().navigate(R.id.action_homeFragment_to_showNoteFragment)
            }

        })

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val note = noteAdapter.getNoteAt(position)

                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Yes") {_, _ ->
                        noteViewModel.deleteNote(note)
                    }
                    .setNegativeButton("No") {_, _ ->
                        noteAdapter.notifyItemChanged(position)
                    }
                    .setCancelable(false)
                    .show()
            }

        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerViewMain)

        binding.ibMenu.setOnClickListener {
            showMenu(it)
        }

        binding.ibSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun observeCategories() {
        categoryViewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.updateList(categories)
        }
    }

    private fun showAddCategorySheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding = LayoutBottomSheetCategoryBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)

        binding.btnCreate.setOnClickListener {
            val categoryName = binding.etCategory.text.toString().trim()

            if (categoryName.isNotEmpty() && categoryName !in listOf("All", "Favorites", "+")) {
                val id = System.currentTimeMillis().toString()
                authViewModel.getCurrentUser()
                val uid = authViewModel.user.value?.uid.toString()
                categoryViewModel.addCategory(Category(id, categoryName, uid))
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

    private fun observeViewModel() {
        noteViewModel.notes.observe(viewLifecycleOwner) { notes ->
            if (notes.isEmpty()) {
                binding.tvNoNotes.visibility = View.VISIBLE
            } else {
                noteAdapter.updateList(notes)
                binding.tvNoNotes.visibility = View.GONE
            }
            binding.progressBarMain.visibility = View.GONE
        }
    }

    private fun showMenu(view: View) {
        val popupmenu = PopupMenu(requireContext(), view)
        popupmenu.menuInflater.inflate(R.menu.menu, popupmenu.menu)

        popupmenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.signOut -> {
                    authViewModel.signOut()
                    true
                }
                else -> false
            }
        }
        popupmenu.show()
    }

    private fun observeSignOutResult() {
        authViewModel.signOutResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                val intent = Intent(requireContext(), AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}