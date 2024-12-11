package com.example.bolatix.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bolatix.R
import com.example.bolatix.data.firebase.FirebaseAuthHelper
import com.example.bolatix.data.firebase.FirebaseService
import com.example.bolatix.data.firebase.FirestoreHelper
import com.example.bolatix.data.remote.response.DataALlMatch
import com.example.bolatix.databinding.ActivityOrderDetailsCompletedBinding
import com.example.bolatix.ui.adapters.GateAdapter2
import com.example.bolatix.ui.viewmodels.UpcomingTicketFragmentViewModel
import com.example.bolatix.utils.findTeamLogo
import com.example.bolatix.utils.formatDate
import com.example.bolatix.utils.toIDR
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailsCompletedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsCompletedBinding
    private val firebaseAuthHelper = FirebaseAuthHelper()
    private val firestoreHelper = FirestoreHelper()
    private val firebaseService = FirebaseService(firebaseAuthHelper, firestoreHelper)
    private val allMatchViewModel: UpcomingTicketFragmentViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val orderId = intent.getStringExtra("order_id")

        if (orderId != null) {
            getDetailOrder(orderId)
        }

        binding.topBar.toolbarTitle.text = getString(R.string.detail_pesanan_selesai)
        binding.topBar.btnBack.visibility = View.VISIBLE
        binding.topBar.btnBack.setOnClickListener {
            if (isTaskRoot) {
                val intent = Intent(this, MainMenuActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }

    }

    private fun getDetailOrder(orderId: String) {
        val currentUser = firebaseAuthHelper.getCurrentUser()
        if (currentUser != null) {
            firebaseService.getUserData(currentUser.uid) { res ->
                if (res != null) {
                    val userData = res.data
                    userData?.let { v ->
                        v["purchase_history"]?.let { data ->
                            val purchaseHistoryList = data as? List<Map<String, Any>>
                            println("purchaseHistoryList: ${purchaseHistoryList.toString()}")
                            val order = purchaseHistoryList?.find { it["order_id"] == orderId }
                            order?.let { foundOrder ->
                                val totalPrice = foundOrder["total_price"] as? Double
                                val gateList = foundOrder["gate_list"] as? List<Int>
                                val matchId = foundOrder["match_id"]
                                val purchaseDate = foundOrder["purchase_date"]
                                val formattedDate =
                                    if (purchaseDate is com.google.firebase.Timestamp) {
                                        val date = purchaseDate.toDate()
                                        SimpleDateFormat(
                                            "dd MMM yyyy, HH:mm",
                                            Locale.getDefault()
                                        ).format(date)
                                    } else {
                                        "-"
                                    }

                                getDetailTicket(matchId.toString()) {
                                    it?.let {
                                        with(binding){
                                            tvTotalHarga.text = "Total Pembayaran: ${(totalPrice?.toInt()?.toIDR() ?: "-")}"
                                            dateBuy.text = "Tanggal Pemesanan: $formattedDate"
                                            rvGateList.layoutManager = LinearLayoutManager(this@OrderDetailsCompletedActivity)
                                            val adapter = GateAdapter2(gateList ?: listOf(), it, orderId)
                                            rvGateList.adapter = adapter
                                        }
                                    }
                                }
                            } ?: run {
                                println("Order dengan ID $orderId tidak ditemukan.")
                            }
                        }
                    }
                } else {
                    println("User tidak ditemukan.")
                }
            }
        }
    }

    private fun getDetailTicket(idMatch: String, callback: (DataALlMatch?) -> Unit) {
        allMatchViewModel.getAllMatch()
        allMatchViewModel.upTickets.observe(this@OrderDetailsCompletedActivity) { result ->
            result.onSuccess { v ->
                val getNowTicket = v.find { it.idMatch == idMatch }
                getNowTicket?.let {
                    with(binding) {
                        tvDate.text = "${it.tanggal.formatDate()} - ${it.jam}"
                        Glide.with(this@OrderDetailsCompletedActivity)
                            .load(findTeamLogo(it.homeTeam)).into(homeLogo)
                        Glide.with(this@OrderDetailsCompletedActivity)
                            .load(findTeamLogo(it.awayTeam)).into(awayLogo)
                        homeName.text = it.homeTeam
                        awayName.text = it.awayTeam
                        tvStadion.text = it.stadion
                        tvTiketName.text = "${it.homeTeam} vs ${it.awayTeam}"
                    }
                }
                binding.loading.root.visibility = View.GONE
                binding.layoutContent.visibility = View.VISIBLE
                callback(getNowTicket)
            }
            result.onFailure {
                binding.loading.root.visibility = View.GONE
                binding.layoutContent.visibility = View.VISIBLE
                callback(null)
            }
        }
    }

    override fun onBackPressed() {
        if (isTaskRoot) {
            val intent = Intent(this, MainMenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } else {
            super.onBackPressed()
        }
    }
}
