package com.example.bolatix.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bolatix.data.firebase.FirebaseAuthHelper
import com.example.bolatix.data.models.PurchaseHistory
import com.example.bolatix.databinding.ActivityNotificationBinding
import com.example.bolatix.preference.UserPreferences
import com.example.bolatix.ui.adapters.NotificationAdapter
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore

class NotificationActivity : AppCompatActivity() {

    private var _binding: ActivityNotificationBinding? = null
    private val binding get() = _binding!!
    private val firebaseAuthHelper = FirebaseAuthHelper()
    private lateinit var userPreferences: UserPreferences
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)

        binding.topBarNotif.toolbarTitle.text = "Notifikasi"
        binding.topBarNotif.btnBack.visibility = View.VISIBLE
        binding.topBarNotif.btnBack.setOnClickListener {
            onBackPressed()
        }

        setUpRecyclerView()
    }
    private fun setUpRecyclerView() {
        val currentUser = firebaseAuthHelper.getCurrentUser()
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
                            ticketQuantity = (it["ticket_quantity"] as? Long)?.toInt() ?: 0,
                            notification = it["notification"] as? Boolean ?: false,
                            totalPrice = it["total_price"] as? Double ?: 0.0,
                            gate = it["gate_list"] as? List<Int> ?: emptyList(),
                            stadium = it["stadium"] as? String ?: "",
                            homeTeam = it["home_team"] as? String ?: "",
                            purchaseDate = it["purchase_date"] as? Timestamp ?: Timestamp.now(),
                            orderId = it["order_id"] as? String ?: "",
                            matchDate = it["match_date"] as? String ?: "",
                            awayTeam = it["away_team"] as? String ?: ""
                        )
                    }
                    if (purchaseHistoryList != null) {
                        val rvNotification = binding.rvNotification
                        rvNotification.layoutManager = LinearLayoutManager(this@NotificationActivity)
                        val size = purchaseHistoryList.size
                        if (size == 0) {
                            binding.imgEmpty.visibility = View.VISIBLE
                            binding.rvNotification.visibility = View.GONE
                        } else {
                            binding.imgEmpty.visibility = View.GONE
                            binding.rvNotification.visibility = View.VISIBLE
                        }
                        val favoriteAdapter = NotificationAdapter(purchaseHistoryList.reversed())
                        rvNotification.adapter = favoriteAdapter
                    }
                }
            }
        }
    }
}