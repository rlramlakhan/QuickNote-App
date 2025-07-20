package com.rl.quicknote.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rl.quicknote.databinding.LayoutEachNoteBinding
import com.rl.quicknote.model.entities.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter: RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var notes = listOf<Note>()
    private var listener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = LayoutEachNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.binding.apply {
            tvTitle.text = note.title
            tvContent.text = note.content
            tvTimeStamp.text = formateTimeStamp(note.timeStamp)
        }
        holder.binding.root.setOnClickListener {
            listener?.onItemClick(note)
        }
        holder.binding.root.setOnLongClickListener {
            listener?.onLongItemClick(note)
            true
        }
    }

    private fun formateTimeStamp(timeStamp: String): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(timeStamp.toLong()))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    fun getNoteAt(position: Int) : Note {
        return notes[position]
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(note: Note)

        fun onLongItemClick(note: Note) {}
    }
    class NoteViewHolder(val binding: LayoutEachNoteBinding): RecyclerView.ViewHolder(binding.root)
}