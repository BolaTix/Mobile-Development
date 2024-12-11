package com.example.bolatix.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bolatix.R
import com.example.bolatix.data.football.Football
import com.example.bolatix.databinding.ActivityDetailTicketBinding
import com.example.bolatix.ui.adapters.GateAdapter
import com.example.bolatix.ui.viewmodels.UpcomingTicketFragmentViewModel
import com.example.bolatix.utils.findTeamLogo
import com.example.bolatix.utils.formatDate
import com.example.bolatix.utils.showToast
import com.example.bolatix.utils.toIDR
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailTicketActivity : AppCompatActivity() {


    private lateinit var binding: ActivityDetailTicketBinding
    private val allMatchViewModel: UpcomingTicketFragmentViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val idMatch = intent.getStringExtra("idMatch")
        binding.topBar.btnBack.visibility = View.VISIBLE
        if (idMatch != null) {
            getDetailTicket(idMatch)
        } else {
            binding.topBar.toolbarTitle.text = getString(R.string.detail_tiket)
        }
        binding.topBar.btnBack.setOnClickListener {
            onBackPressed()
        }


    }

    private fun getDetailTicket(idMatch: String) {
        allMatchViewModel.getAllMatch()
        allMatchViewModel.upTickets.observe(this@DetailTicketActivity) { result ->
            result.onSuccess { v ->
                val getNowTicket = v.find { it.idMatch == idMatch }
                getNowTicket?.let {
                    with(binding) {
                        val homeTeam = it.homeTeam.split(" ")[0]
                        val awayTeam = it.awayTeam.split(" ")[0]
                        topBar.toolbarTitle.text = "$homeTeam vs $awayTeam"
                        tvDate.text = getString(R.string.strip, it.tanggal.formatDate(), it.jam)
                        tvStadion.text = it.stadion
                        homeName.text = homeTeam
                        awayName.text = awayTeam
                        Glide.with(this@DetailTicketActivity).load(findTeamLogo(it.homeTeam))
                            .into(homeLogo)
                        Glide.with(this@DetailTicketActivity).load(findTeamLogo(it.awayTeam))
                            .into(awayLogo)
                        val date = "${it.tanggal} - ${it.jam}"
                        rvTicket.layoutManager = LinearLayoutManager(this@DetailTicketActivity)
                        val adapter = GateAdapter(Football.tickets, date, it.idMatch) { totalPrice ->
                            btnBuyNow.text = totalPrice.toIDR()
                        }
                        rvTicket.adapter = adapter
                        binding.btnBuyNow.setOnClickListener {
                            val selectedGate = adapter.getSelectedGate()
                            val message = getString(R.string.pilih_tempat_duduk_terlebih_dahulu)
                            if (selectedGate.isEmpty()) {
                                showToast(this@DetailTicketActivity, message)
                            } else {
                                val intent = Intent(this@DetailTicketActivity, VerifyDataPaymentActivity::class.java)
                                intent.putExtra("listPayment", ArrayList(selectedGate))
                                intent.putExtra("ticket_data", getNowTicket)
                                startActivity(intent)
                            }
                        }
                    }
                    binding.loading.root.visibility = View.GONE
                    binding.content.visibility = View.VISIBLE 
                }
            }
        }
    }
}
