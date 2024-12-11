package com.example.bolatix.ui.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.bolatix.R
import com.example.bolatix.data.firebase.FirebaseAuthHelper
import com.example.bolatix.data.models.GateModel
import com.example.bolatix.data.models.PurchaseHistory
import com.example.bolatix.utils.showToast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class GateAdapter(
    private val gate: List<GateModel>,
    private val date: String? = null,
    private val idMatch: String? = null,
    private val onSelectionChanged: (Int) -> Unit
) : RecyclerView.Adapter<GateAdapter.GateViewHolder>() {

    private val gateSelected = mutableSetOf<GateModel>()
    private val firebaseAuthHelper = FirebaseAuthHelper()
    private val db = Firebase.firestore

    class GateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gateTitle: TextView = view.findViewById(R.id.gateLocation)
        val gatePrice: TextView = view.findViewById(R.id.gatePrice)
        val gateExp: TextView = view.findViewById(R.id.gateTime)
        val btnPilih: TextView = view.findViewById(R.id.btnPilih)
        val icChecked: ImageView = view.findViewById(R.id.icChecked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gate, parent, false)
        return GateViewHolder(view)
    }

    override fun onBindViewHolder(holder: GateViewHolder, position: Int) {
        val currentUser = firebaseAuthHelper.getCurrentUser()
        val context = holder.itemView.context
        val gateItem = gate[position]
        val bgPurchased = AppCompatResources.getDrawable(context, R.drawable.bg_gate_purchased)
        
        holder.gateTitle.text = gateItem.title
        holder.gatePrice.text = gateItem.price.toString()
        holder.gateExp.text = context.getString(R.string.berakhir_pada, date)
        if (currentUser != null) {
            val userDocRef = db.collection("users").document(currentUser.uid)
            userDocRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error listening for changes: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val userData = snapshot.data
                    val purchaseHistoryList = (userData?.get("purchase_history") as? List<Map<String, Any>>)?.map {
                        PurchaseHistory(
                            matchId = it["match_id"] as? String ?: "",
                            gate = it["gate_list"] as? List<Int> ?: emptyList(),
                        )
                    }

                    val filter = purchaseHistoryList?.filter { idMatch == it.matchId}
                    val flat = filter?.flatMap { it.gate } ?: emptyList()
                    val check = flat.map { it }.contains(gateItem.id)
                    if (check) {
                        holder.btnPilih.paintFlags = holder.btnPilih.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        holder.itemView.background = bgPurchased
                    }
                    holder.btnPilih.setOnClickListener {
                        if (check) {
                            showToast(context, "Anda Sudah Membeli item ini sebelumnya!")
                        } else {
                            if (gateSelected.contains(gateItem)) {
                                gateSelected.remove(gateItem)
                                holder.icChecked.visibility = View.GONE
                            } else {
                                gateSelected.add(gateItem)
                                holder.icChecked.visibility = View.VISIBLE
                            }
                        }
                        val totalPrice = getSelectedGate().sumOf { it.price }
                        onSelectionChanged(totalPrice)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = gate.size

    fun getSelectedGate(): List<GateModel> {
        return gateSelected.toList()
    }
}
