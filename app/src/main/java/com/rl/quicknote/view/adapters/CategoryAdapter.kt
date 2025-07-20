package com.rl.quicknote.view.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rl.quicknote.R
import com.rl.quicknote.databinding.LayoutEachCategoryBinding
import com.rl.quicknote.model.entities.Category

class CategoryAdapter: RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


    private var categories = listOf<Category>()
    private var listener: OnItemClickListener? = null
    private var selectedCategoryId: String = "defaultAll"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = LayoutEachCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        val isSelected = category.id == selectedCategoryId
        holder.binding.root.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(holder.itemView.context, if (isSelected) R.color.md_theme_primary else R.color.md_theme_surfaceContainer)
        )
        holder.binding.root.setTextColor(
            ContextCompat.getColor(holder.itemView.context, if (isSelected) R.color.md_theme_onPrimary else R.color.md_theme_secondary)
        )
        holder.binding.apply {
            tvCategoryName.text = category.categoryName
        }
        holder.binding.root.setOnClickListener {
            selectedCategoryId = category.id
            listener?.onItemClick(category)
            notifyDataSetChanged()
        }
        holder.binding.root.setOnLongClickListener {
            listener?.onItemLongClick(category, holder.absoluteAdapterPosition)
            true
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(categories: List<Category>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    fun setOnCategoryClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(category: Category)

        fun onItemLongClick(category: Category, position: Int)
    }
    class CategoryViewHolder(val binding: LayoutEachCategoryBinding): RecyclerView.ViewHolder(binding.root)
}