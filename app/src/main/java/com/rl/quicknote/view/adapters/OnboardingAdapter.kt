package com.rl.quicknote.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rl.quicknote.databinding.ItemOnboardingBinding
import com.rl.quicknote.model.entities.OnboardingItem

class OnboardingAdapter(private val items: List<OnboardingItem>): RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            ivOnboarding.setImageResource(item.imageId)
            tvOnboardingTitle.text = item.title
            tvOnboardingDescription.text = item.description
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class OnboardingViewHolder(val binding: ItemOnboardingBinding): RecyclerView.ViewHolder(binding.root)
}