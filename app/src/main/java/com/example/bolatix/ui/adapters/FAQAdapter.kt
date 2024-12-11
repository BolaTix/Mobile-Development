package com.example.bolatix.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bolatix.databinding.ItemFaqBinding

class FAQAdapter(private val faqItems: List<Int>) : RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    inner class FAQViewHolder(val binding: ItemFaqBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FAQViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val context = holder.itemView.context
        val titleResId = faqItems[position * 2]
        val descResId = faqItems[position * 2 + 1]

        holder.binding.title.text = context.getString(titleResId)
        holder.binding.desc.text = context.getString(descResId)
    }

    override fun getItemCount(): Int = faqItems.size / 2
}