package com.example.bolatix.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bolatix.R
import com.example.bolatix.data.remote.response.DataALlMatch
import com.example.bolatix.utils.findTeamLogo
import com.example.bolatix.utils.formatDate

class MatchResultAdapter(
    private val teams: List<DataALlMatch>,
) : RecyclerView.Adapter<MatchResultAdapter.MatchResultAdapter>() {

    class MatchResultAdapter(view: View) : RecyclerView.ViewHolder(view) {
        val homeName: TextView = view.findViewById(R.id.homeName)
        val awayName: TextView = view.findViewById(R.id.awayName)
        val homeLogo: ImageView = view.findViewById(R.id.homeIcon)
        val awayLogo: ImageView = view.findViewById(R.id.awayIcon)
        val dateMatch: TextView = view.findViewById(R.id.date)
        val score: TextView = view.findViewById(R.id.tvScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchResultAdapter {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_match_result, parent, false)
        return MatchResultAdapter(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MatchResultAdapter, position: Int) {
        val context = holder.itemView.context
        val team = teams[position]
        val homeScore = if(team.scoreHome == "") "0" else team.scoreHome
        val awayScore = if(team.scoreAway == "") "0" else team.scoreAway
        Glide.with(context).load(findTeamLogo(team.homeTeam)).into(holder.homeLogo)
        Glide.with(context).load(findTeamLogo(team.awayTeam)).into(holder.awayLogo)
        holder.homeName.text = team.homeTeam
        holder.awayName.text = team.awayTeam
        holder.dateMatch.text = "${team.tanggal.formatDate()} - ${team.jam}"
        holder.score.text = "$homeScore - $awayScore"
        holder.itemView.setOnClickListener{
            println("${team.scoreHome} - ${team.scoreAway}")
        }
    }

    override fun getItemCount(): Int = teams.size
}
