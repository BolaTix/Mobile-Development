package com.example.bolatix.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bolatix.R
import com.example.bolatix.utils.toIDR
import com.midtrans.sdk.uikit.api.model.ItemDetails

class ListOrderAdapter(
    private val list: List<ItemDetails>
) :
    RecyclerView.Adapter<ListOrderAdapter.ListOrderViewAdapter>() {

    class ListOrderViewAdapter(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val price: TextView = view.findViewById(R.id.price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListOrderViewAdapter {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_goods, parent, false)
        return ListOrderViewAdapter(view)
    }

    override fun onBindViewHolder(holder: ListOrderViewAdapter, position: Int) {
        val team = list[position]
        holder.title.text = team.name
        holder.price.text = team.price?.toInt()?.toIDR() ?: ""
    }

    override fun getItemCount(): Int = list.size
}