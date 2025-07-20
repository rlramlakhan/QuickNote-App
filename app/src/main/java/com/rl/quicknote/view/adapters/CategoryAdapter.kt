package com.rl.quicknote.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rl.quicknote.databinding.LayoutEachCategoryBinding
import com.rl.quicknote.model.entities.Category

class CategoryAdapter: RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


    private var categories = listOf<Category>()
    private var listener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = LayoutEachCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.apply {
            tvCategoryName.text = category.categoryName
        }
        holder.binding.root.setOnClickListener {
            listener?.onItemClick(category)
        }
    }

    fun updateList(categories: List<Category>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    fun setOnCategoryClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(category: Category)
    }
    class CategoryViewHolder(val binding: LayoutEachCategoryBinding): RecyclerView.ViewHolder(binding.root)
}