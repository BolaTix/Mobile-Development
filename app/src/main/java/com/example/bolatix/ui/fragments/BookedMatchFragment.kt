package com.example.bolatix.ui.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bolatix.data.firebase.FirebaseAuthHelper
import com.example.bolatix.data.firebase.FirebaseService
import com.example.bolatix.data.firebase.FirestoreHelper
import com.example.bolatix.data.models.PurchaseHistory
import com.example.bolatix.databinding.FragmentBookedMatchBinding
import com.example.bolatix.ui.adapters.BookedMatchAdapter
import com.google.firebase.Timestamp

class BookedMatchFragment : Fragment() {


    private var _binding: FragmentBookedMatchBinding? = null
    private val binding get() = _binding!!
    private val firebaseAuthHelper = FirebaseAuthHelper()
    private val firestoreHelper = FirestoreHelper()
    private val firebaseService = FirebaseService(firebaseAuthHelper, firestoreHelper)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookedMatchBinding.inflate(inflater, container, false)
        createNotificationChannel(requireContext())
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadBookedMatchData()
    }

    private fun loadBookedMatchData() {
        val currentUser = firebaseAuthHelper.getCurrentUser()
        if (currentUser != null) {
            firebaseService.getUserData(currentUser.uid) { res ->
                if (res != null) {
                    val userData = res.data
                    userData?.let { v ->
                        v["purchase_history"]?.let { data ->
                            val purchaseHistoryList = data as? List<Map<String, Any>>
                            val mappedPurchaseHistory = purchaseHistoryList?.map { item ->
                                PurchaseHistory(
                                    orderId = item["order_id"] as? String ?: "",
                                    matchId = item["match_id"] as? String ?: "",
                                    awayTeam = item["away_team"] as? String ?: "",
                                    homeTeam = item["home_team"] as? String ?: "",
                                    purchaseDate = item["purchase_date"] as? Timestamp
                                        ?: Timestamp.now(),
                                    matchDate = item["match_date"] as? String ?: "",
                                    stadium = item["stadium"] as? String ?: "",
                                    ticketQuantity = item["ticket_quantity"] as? Int ?: 0,
                                    totalPrice = item["total_price"] as? Double ?: 0.0,
                                    gate = item["gate_list"] as? List<Int> ?: emptyList()
                                )
                            } ?: emptyList()
                            val rvBookedMatch = binding.rvBookedMatch
                            rvBookedMatch.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            val size = mappedPurchaseHistory.size
                            if (size == 0) {
                                binding.imgEmpty.visibility = View.VISIBLE
                                binding.rvBookedMatch.visibility = View.GONE
                            } else {
                                binding.imgEmpty.visibility = View.GONE
                                binding.rvBookedMatch.visibility = View.VISIBLE
                            }
                            val favoriteAdapter = BookedMatchAdapter(mappedPurchaseHistory.reversed())
                            rvBookedMatch.adapter = favoriteAdapter
                        }
                    }
                } else {
                    println("User tidak ditemukan.")
                }
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "bolatix_channel"
            val channelName = "Bolatix Notifications"
            val channelDescription = "Notifications for Bolatix app"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}