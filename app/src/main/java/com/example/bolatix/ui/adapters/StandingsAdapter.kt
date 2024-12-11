package com.example.bolatix.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bolatix.data.remote.response.Standings
import com.example.bolatix.databinding.ItemTableRowBinding

class StandingsAdapter :
    ListAdapter<Standings, StandingsAdapter.StandingsViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandingsViewHolder {
        val binding =
            ItemTableRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StandingsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StandingsViewHolder, position: Int) {
        val standing = getItem(position)
        holder.bind(standing)
    }

    class StandingsViewHolder(private val binding: ItemTableRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(standing: Standings) {
            Glide.with(binding.ivLogo.context).load(standing.logo).into(binding.ivLogo)
            binding.tvMain.text = standing.main
            binding.tvPoin.text = standing.poin
            binding.tvMenang.text = standing.menang
            binding.tvSeri.text = standing.seri
            binding.tvKalah.text = standing.kalah
            binding.tvGoal.text = standing.goal
            binding.tvSelisihGoal.text = standing.selisihGoal
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Standings>() {
            override fun areItemsTheSame(oldItem: Standings, newItem: Standings): Boolean {
                return oldItem.posisi == newItem.posisi
            }

            override fun areContentsTheSame(oldItem: Standings, newItem: Standings): Boolean {
                return oldItem == newItem
            }
        }
    }
}
