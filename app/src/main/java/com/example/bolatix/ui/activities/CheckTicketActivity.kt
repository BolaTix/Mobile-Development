package com.example.bolatix.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bolatix.R
import com.example.bolatix.data.firebase.FirebaseAuthHelper
import com.example.bolatix.data.firebase.FirebaseService
import com.example.bolatix.data.firebase.FirestoreHelper
import com.example.bolatix.data.remote.response.DataALlMatch
import com.example.bolatix.databinding.ActivityCheckTicketBinding
import com.example.bolatix.preference.UserPreferences
import com.example.bolatix.utils.findGate
import com.example.bolatix.utils.findTeamLogo
import com.example.bolatix.utils.generateQRCode
import com.example.bolatix.utils.showToast


class CheckTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckTicketBinding
    private lateinit var pdfDocument: PdfDocument
    private val REQUEST_CODE_SAVE_PDF = 1001
    private val firebaseAuthHelper = FirebaseAuthHelper()
    private val firestoreHelper = FirestoreHelper()
    private val firebaseService = FirebaseService(firebaseAuthHelper, firestoreHelper)
    private lateinit var userPreferences: UserPreferences

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SAVE_PDF && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                try {
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                        pdfDocument.close()
                    }
                    showToast(this, "PDF berhasil disimpan!")
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast(this, "Gagal menyimpan PDF: ${e.message}")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        val orderId = intent.getStringExtra("order_id")
        val gateItem = intent.getIntExtra("gate_item", 0)
        val match = intent.getParcelableExtra<DataALlMatch>("match")
        getDetailTicket(orderId.orEmpty(), gateItem)
        with(binding) {
            qrCode.setImageBitmap(generateQRCode("$orderId.$gateItem"))
            btnDownloadPdf.setOnClickListener { downloadAsPdf(1) }
            btnBack.setOnClickListener { onBackPressed() }
            btnShareWa.setOnClickListener { savePdfAndShareToWhatsApp(1) }
            Glide.with(this@CheckTicketActivity).load(findTeamLogo(match?.homeTeam.orEmpty()))
                .into(icHome)
            Glide.with(this@CheckTicketActivity).load(findTeamLogo(match?.awayTeam.orEmpty()))
                .into(icAway)
            tvDate.text = match?.tanggal
            tvTime.text = match?.jam
            tvStadion.text = match?.stadion
        }
    }

    private fun getDetailTicket(orderId: String, gateItem: Int) {
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
                                with(binding) {
                                    tvType.text = findGate(gateItem)?.substringBefore(" ")
                                    tvGate.text = findGate(gateItem)?.substringAfter("GATE ")
                                        ?.substringBefore(")")
                                    loading.root.visibility = View.GONE
                                    layoutContent.visibility = View.VISIBLE
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

    private fun downloadAsPdf(pageCount: Int) {
        val ticketLayout = findViewById<LinearLayout>(R.id.ticketLayout)
        pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 50
        for (i in 1..pageCount) {
            val bitmap = Bitmap.createBitmap(
                ticketLayout.width,
                ticketLayout.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            ticketLayout.draw(canvas)
            val scaledBitmap = Bitmap.createScaledBitmap(
                bitmap,
                pageWidth - 2 * margin,
                (bitmap.height * (pageWidth - 2 * margin).toFloat() / bitmap.width).toInt(),
                true
            )
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i).create()
            val page = pdfDocument.startPage(pageInfo)
            val pageCanvas = page.canvas
            val centerX = (pageWidth - scaledBitmap.width) / 2f
            val centerY = (pageHeight - scaledBitmap.height) / 2f
            pageCanvas.drawBitmap(scaledBitmap, centerX, centerY, null)
            pdfDocument.finishPage(page)
        }
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "Ticket_${System.currentTimeMillis()}.pdf")
        }
        startActivityForResult(intent, REQUEST_CODE_SAVE_PDF)
    }

    private fun savePdfAndShareToWhatsApp(pageCount: Int) {
        val ticketLayout = findViewById<LinearLayout>(R.id.ticketLayout)
        pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 50

        for (i in 1..pageCount) {
            val bitmap = Bitmap.createBitmap(
                ticketLayout.width,
                ticketLayout.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            ticketLayout.draw(canvas)
            val scaledBitmap = Bitmap.createScaledBitmap(
                bitmap,
                pageWidth - 2 * margin,
                (bitmap.height * (pageWidth - 2 * margin).toFloat() / bitmap.width).toInt(),
                true
            )
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i).create()
            val page = pdfDocument.startPage(pageInfo)
            val pageCanvas = page.canvas
            val centerX = (pageWidth - scaledBitmap.width) / 2f
            val centerY = (pageHeight - scaledBitmap.height) / 2f
            pageCanvas.drawBitmap(scaledBitmap, centerX, centerY, null)
            pdfDocument.finishPage(page)
        }

        try {
            val fileName = "Ticket_${System.currentTimeMillis()}.pdf"
            val filePath = getExternalFilesDir(null)?.absolutePath + "/$fileName"
            val file = java.io.File(filePath)
            file.outputStream().use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()
            sharePdfToWhatsApp(file)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast(this, "Gagal menyimpan PDF: ${e.message}")
            println(e.message)
        }
    }

    private fun sharePdfToWhatsApp(file: java.io.File) {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Bagikan PDF menggunakan"))

        try {
            startActivity(shareIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast(this, "WhatsApp tidak ditemukan.")
        }
    }
}

