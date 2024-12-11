package com.example.bolatix.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bolatix.data.remote.response.DataALlMatch
import com.example.bolatix.databinding.ItemTicketBinding
import com.example.bolatix.ui.activities.DetailTicketActivity
import com.example.bolatix.utils.findTeamLogo
import com.example.bolatix.utils.formatDate

class UpcomingTicketAdapter :
    ListAdapter<DataALlMatch, UpcomingTicketAdapter.ViewHolder>(DIFFUTIL) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = getItem(position)
        holder.bind(ticket)
    }

    inner class ViewHolder(private val binding: ItemTicketBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(v: DataALlMatch) {
            with(binding) {
                tvTitleTicket.text = "${v.homeTeam} VS ${v.awayTeam}"
                tvTime.text = "${v.tanggal.formatDate()} - ${v.jam}"
                tvStadion.text = v.stadion
                Glide.with(itemView.context).load(findTeamLogo(v.awayTeam)).into(ivAwayTeam)
                Glide.with(itemView.context).load(findTeamLogo(v.homeTeam)).into(ivHomeTeam)
                btnBuyTicket.setOnClickListener {
                    val intent = Intent(itemView.context, DetailTicketActivity::class.java)
                    intent.putExtra("idMatch", v.idMatch)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }


    companion object {
        val DIFFUTIL = object : DiffUtil.ItemCallback<DataALlMatch>() {
            override fun areItemsTheSame(
                oldItem: DataALlMatch,
                newItem: DataALlMatch
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DataALlMatch,
                newItem: DataALlMatch
            ): Boolean {
                return oldItem.idMatch == newItem.idMatch
            }

        }
    }

}