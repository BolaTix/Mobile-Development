package com.example.bolatix.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bolatix.R
import com.example.bolatix.data.models.TeamModel

class TeamAdapter(
    private val teams: List<TeamModel>,
    private val maxSelection: Int
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    private val selectedTeams = mutableSetOf<Int>()

    class TeamViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val teamLogo: ImageView = view.findViewById(R.id.teamLogo)
        val teamName: TextView = view.findViewById(R.id.teamName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_team, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val context = holder.itemView.context
        val team = teams[position]
        Glide.with(holder.itemView.context).load(team.teamLogo).into(holder.teamLogo)
        holder.teamName.text = team.teamName
        holder.itemView.setOnClickListener {
            if (selectedTeams.contains(team.teamId)) {
                selectedTeams.remove(team.teamId)
                holder.itemView.background = AppCompatResources.getDrawable(context, R.drawable.bg_fav_innactive)
            } else {
                if (selectedTeams.size < maxSelection) {
                    selectedTeams.add(team.teamId)
                    holder.itemView.background = AppCompatResources.getDrawable(context, R.drawable.bg_fav_active)
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        "Kamu hanya bisa memilih 1 tim saja!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = teams.size

    fun getSelectedTeams(): List<TeamModel> {
        return teams.filter { selectedTeams.contains(it.teamId) }
    }
}
