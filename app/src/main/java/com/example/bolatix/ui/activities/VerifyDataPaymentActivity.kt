package com.example.bolatix.ui.activities

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bolatix.BuildConfig
import com.example.bolatix.R
import com.example.bolatix.data.firebase.FirebaseAuthHelper
import com.example.bolatix.data.firebase.FirebaseService
import com.example.bolatix.data.firebase.FirestoreHelper
import com.example.bolatix.data.models.GateModel
import com.example.bolatix.data.remote.response.DataALlMatch
import com.example.bolatix.databinding.ActivityVerifyDataPaymentBinding
import com.example.bolatix.preference.UserPreferences
import com.example.bolatix.ui.adapters.ListOrderAdapter
import com.example.bolatix.utils.generateUUID
import com.example.bolatix.utils.toIDR
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.midtrans.sdk.uikit.api.model.CustomerDetails
import com.midtrans.sdk.uikit.api.model.ItemDetails
import com.midtrans.sdk.uikit.api.model.SnapTransactionDetail
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import kotlinx.coroutines.launch


class VerifyDataPaymentActivity : AppCompatActivity() {

    private val firebaseAuth = Firebase.auth
    private val firebaseAuthHelper = FirebaseAuthHelper()
    private val firestoreHelper = FirestoreHelper()
    private val firebaseService = FirebaseService(firebaseAuthHelper, firestoreHelper)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val ticketData: DataALlMatch? = intent.getParcelableExtra("ticket_data")
                    val selectedGate: ArrayList<GateModel>? = intent.getParcelableArrayListExtra("listPayment")
                    onPaymentResult(it, ticketData, selectedGate)
                }
            }
        }
    private var itemDetails = mutableListOf<ItemDetails>()
    private fun calculateTotalAmount(): Double {
        return itemDetails.sumOf {
            (it.price ?: 0.0) * it.quantity
        }
    }

    private fun initTransactionDetails(): SnapTransactionDetail {
        val totalAmount = calculateTotalAmount()
        return SnapTransactionDetail(
            orderId = generateUUID(),
            grossAmount = totalAmount
        )
    }

    private var _binding: ActivityVerifyDataPaymentBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPreferences: UserPreferences
    private lateinit var customerDetails: CustomerDetails

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVerifyDataPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel(this)
        userPreferences = UserPreferences(this)

        val listPayment: ArrayList<GateModel>? = intent.getParcelableArrayListExtra("listPayment")
        listPayment?.let {
            it.forEach { v ->
                itemDetails.add(
                    ItemDetails(
                        id = v.id.toString(),
                        price = v.price.toDouble(),
                        quantity = 1,
                        name = v.title
                    )
                )
            }
            itemDetails.add(
                ItemDetails(
                    id = "ADMIN",
                    price = 5000.0,
                    quantity = 1,
                    name = "Fee Pembayaran"
                )
            )
            itemDetails.add(
                ItemDetails(
                    id = "ADMIN",
                    price = 500.0,
                    quantity = 1,
                    name = "Fee Platform"
                )
            )
        }
        buildUiKit()

        with(binding) {
            tvCountOrder.text = calculateTotalAmount().toInt().toIDR()
            btnBack.setOnClickListener {
                onBackPressed()
            }
            val adapter = ListOrderAdapter(itemDetails)
            rvListOrder.layoutManager = LinearLayoutManager(this@VerifyDataPaymentActivity)
            rvListOrder.adapter = adapter
            btnPay.setOnClickListener {
                if (formValidation()) {
                    goToPayment()
                }
            }
        }
    }

    private fun buildUiKit() {
        UiKitApi.Builder()
            .withContext(this.applicationContext)
            .withMerchantUrl(BuildConfig.MIDTRANS_URL_CHARGE)
            .withMerchantClientKey(BuildConfig.MERCHANT_KEY)
            .enableLog(true)
            .build()
        uiKitCustomSetting()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onPaymentResult(intent: Intent, ticketData: DataALlMatch?, selectedGate: ArrayList<GateModel>?) {
        val transactionResult =
            intent.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
        transactionResult?.let { result ->
            when (result.status) {
                "success" -> {
                    val currentUser = firebaseAuth.currentUser
                    val userId =
                        currentUser?.uid ?: throw Exception("User is not logged in")
                    if (ticketData != null && selectedGate != null) {
                        val ticketMap = mapOf(
                            "order_id" to (result.transactionId ?: generateUUID()),
                            "match_id" to ticketData.idMatch,
                            "home_team" to ticketData.homeTeam,
                            "away_team" to ticketData.awayTeam,
                            "match_date" to "${ticketData.tanggal} ${ticketData.jam}",
                            "purchase_date" to Timestamp.now(),
                            "stadium" to ticketData.stadion,
                            "ticket_quantity" to selectedGate.size,
                            "total_price" to calculateTotalAmount(),
                            "notification" to false,
                            "gate_list" to selectedGate.map { v -> v.id },
                        )
                        updateTicketData(userId, ticketMap)
                        showNotification(this,"Pembayaran Berhasil", "Selamat, Tiket berhasil dibeli, Silahkan cek di halaman Riwayat Pembelian.",(result.transactionId ?: generateUUID()))
                        val intent = Intent(this, OrderDetailsCompletedActivity::class.java).apply {
                            putExtra("order_id", result.transactionId)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                    } else {
                        println("Data tiket atau gate tidak ditemukan.")
                    }
                }

                else -> {
                    showNotification(this,"Error", "Pembayaran gagal",(result.transactionId ?: generateUUID()))
                }
            }
        }
    }

    private fun uiKitCustomSetting() {
        val uIKitCustomSetting = UiKitApi.getDefaultInstance().uiKitSetting
        uIKitCustomSetting.saveCardChecked = true
    }

    private fun updateTicketData(userId: String, ticketData: Map<String, Any>) {
        firebaseService.addTicketToPurchaseHistory(userId, ticketData) { success, errorMessage ->
            if (success) {
                println("Data tiket berhasil disimpan.")
            } else {
                println("Gagal menyimpan data tiket: $errorMessage")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun showNotification(context: Context, title: String, message: String, orderId: String) {
        val channelId = "bolatix_channel"
        val notificationId = System.currentTimeMillis().toInt()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as Activity),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
            return
        }

        val intent = Intent(context, OrderDetailsCompletedActivity::class.java).apply {
            putExtra("order_id", orderId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_personal)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notification)
        }
    }

    private fun formValidation(): Boolean {
        with(binding){
            return when {
                inputName.text.toString().isEmpty() -> {
                    inputName.error = "Wajib diisi!"
                    false
                }
                inputIdentitas.text.toString().isEmpty() -> {
                    inputIdentitas.error = "Wajib diisi!"
                    false
                }
                inputEmail.text.toString().isEmpty() -> {
                    inputEmail.error = "Wajib diisi!"
                    false
                }
                inputNumberPhone.text.toString().isEmpty() -> {
                    inputNumberPhone.error = "Wajib diisi!"
                    false
                }
                else -> true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun goToPayment() {
        lifecycleScope.launch {
            userPreferences.getUser().collect { v ->
                customerDetails = CustomerDetails(
                    firstName = v.name,
                    email = v.email,
                    phone = v.phoneNumber
                )
                UiKitApi.getDefaultInstance().startPaymentUiFlow(
                    activity = this@VerifyDataPaymentActivity,
                    launcher = launcher,
                    transactionDetails = initTransactionDetails(),
                    customerDetails = customerDetails,
                    itemDetails = itemDetails,
                )
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