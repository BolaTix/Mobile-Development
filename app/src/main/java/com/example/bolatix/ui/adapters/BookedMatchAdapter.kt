package com.example.bolatix.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bolatix.data.models.PurchaseHistory
import com.example.bolatix.databinding.ItemBookedMatchBinding
import com.example.bolatix.ui.activities.OrderDetailsCompletedActivity
import com.example.bolatix.utils.findTeamLogo
import com.example.bolatix.utils.toIDR
import java.text.SimpleDateFormat
import java.util.Locale

class BookedMatchAdapter(
    private val teams: List<PurchaseHistory>,
) : RecyclerView.Adapter<BookedMatchAdapter.Adapter>() {
    class Adapter(val binding: ItemBookedMatchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter {
        val binding = ItemBookedMatchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Adapter(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Adapter, position: Int) {
        val team = teams[position]
        val context = holder.itemView.context
        val homeLogo = findTeamLogo(team.homeTeam)
        val awayLogo = findTeamLogo(team.awayTeam)
        with(holder.binding) {
            Glide.with(context).load(homeLogo).into(ivHomeTeam)
            Glide.with(context).load(awayLogo).into(ivAwayTeam)
            tvTitleMatch.text = "${team.awayTeam} VS ${team.homeTeam}"
            tvDateTime.text = format(team.matchDate)
            println(team.matchDate)
            tvPrice.text = team.totalPrice.toInt().toIDR()
            tvStadion.text = team.stadium

            btnBuyTicket.setOnClickListener {
                val intent = Intent(context, OrderDetailsCompletedActivity::class.java)
                intent.putExtra("order_id", team.orderId)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = teams.size
}

private fun format(inputDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale("id", "ID"))
        outputFormat.format(date ?: return "Format tidak valid")
    } catch (e: Exception) {
        "Format tidak valid"
    }
}