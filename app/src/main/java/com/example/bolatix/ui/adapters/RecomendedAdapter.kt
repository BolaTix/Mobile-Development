package com.example.bolatix.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bolatix.R
import com.example.bolatix.data.remote.response.DataRecomendded
import com.example.bolatix.ui.activities.DetailTicketActivity
import com.example.bolatix.utils.findTeamLogo
import com.example.bolatix.utils.formatDate

class RecomendedAdapter(
    private val teams: List<DataRecomendded>,
) : RecyclerView.Adapter<RecomendedAdapter.Adapter>() {

    class Adapter(view: View) : RecyclerView.ViewHolder(view) {
        val homeName: TextView = view.findViewById(R.id.homeName)
        val awayName: TextView = view.findViewById(R.id.awayName)
        val homeLogo: ImageView = view.findViewById(R.id.homeLogo)
        val awayLogo: ImageView = view.findViewById(R.id.awayLogo)
        val dateMatch: TextView = view.findViewById(R.id.tvDate)
        val btnBuyTicket: Button = view.findViewById(R.id.btnBuyTicket)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recomendation, parent, false)
        return Adapter(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Adapter, position: Int) {
        val context = holder.itemView.context
        val team = teams[position]
        val homeLogo = findTeamLogo(team.homeTeam)
        val awayLogo = findTeamLogo(team.awayTeam)
        Glide.with(holder.itemView.context).load(homeLogo).into(holder.homeLogo)
        Glide.with(holder.itemView.context).load(awayLogo).into(holder.awayLogo)
        holder.homeName.text = team.homeTeam
        holder.awayName.text = team.awayTeam
        holder.dateMatch.text = "${team.tanggal.formatDate()} - ${team.time}"
        holder.btnBuyTicket.setOnClickListener {
            val intent = Intent(context, DetailTicketActivity::class.java)
            intent.putExtra("idMatch", team.matchId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = teams.size
}
