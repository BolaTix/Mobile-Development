package com.example.bolatix.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bolatix.R
import com.example.bolatix.data.firebase.FirebaseAuthHelper
import com.example.bolatix.data.models.PurchaseHistory
import com.example.bolatix.databinding.ItemNotificationBinding
import com.example.bolatix.ui.activities.OrderDetailsCompletedActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class NotificationAdapter(
    private var teams: List<PurchaseHistory>,
) : RecyclerView.Adapter<NotificationAdapter.NotificationAdapter>() {
    class NotificationAdapter(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val firebaseAuthHelper = FirebaseAuthHelper()
    private val db = Firebase.firestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationAdapter(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationAdapter, position: Int) {
        val item = teams[position]
        val context = holder.itemView.context
        with(holder.binding) {
            desc.text =
                context.getString(R.string.messageNotifPaymentSuccess, item.awayTeam, item.homeTeam)
            status.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (item.notification) R.color.white else R.color.colorButtonBlue
                )
            )
            root.strokeColor = ContextCompat.getColor(
                context,
                if (item.notification) R.color.neutral_light40 else R.color.colorButtonBlue
            )
            root.setOnClickListener {
                context.startActivity(
                    Intent(
                        context,
                        OrderDetailsCompletedActivity::class.java
                    ).apply {
                        putExtra("order_id", item.orderId)
                    }
                )
                val currentUser = firebaseAuthHelper.getCurrentUser()
                if (currentUser != null) {
                    val userDocRef = db.collection("users").document(currentUser.uid)
                    db.runTransaction { transaction ->
                        val snapshot = transaction.get(userDocRef)
                        val purchaseHistory = snapshot.get("purchase_history") as? List<Map<String, Any>> ?: emptyList()
                        val updatedPurchaseHistory = purchaseHistory.map { purchase ->
                            if (purchase["order_id"] == item.orderId) {
                                purchase.toMutableMap().apply { this["notification"] = true }
                            } else {
                                purchase
                            }
                        }
                        transaction.update(userDocRef, "purchase_history", updatedPurchaseHistory)
                    }.addOnSuccessListener {
                        item.notification = true
                        notifyItemChanged(position)
                    }.addOnFailureListener { e ->
                    }
                }
            }
        }
    }


    override fun getItemCount(): Int = teams.size

}