package com.example.bolatix.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.bolatix.R
import com.example.bolatix.data.remote.response.DataALlMatch
import com.example.bolatix.ui.activities.CheckTicketActivity
import com.example.bolatix.utils.findGate

class GateAdapter2(
    private val gate: List<Int>,
    private val match: DataALlMatch,
    private val orderId: String,
) : RecyclerView.Adapter<GateAdapter2.GateViewHolder>() {
    class GateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gateTitle: TextView = view.findViewById(R.id.gateTitle)
        val btnCheckTicket: AppCompatButton = view.findViewById(R.id.btnShowTicket)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gate_2, parent, false)
        return GateViewHolder(view)
    }

    override fun onBindViewHolder(holder: GateViewHolder, position: Int) {
        val context = holder.itemView.context
        val gateItem = gate[position]
        holder.gateTitle.text = findGate(gateItem)
        holder.btnCheckTicket.setOnClickListener {
            val intent = Intent(context, CheckTicketActivity::class.java)
            intent.putExtra("order_id", orderId)
            intent.putExtra("gate_item", gateItem)
            intent.putExtra("match", match)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = gate.size
}
