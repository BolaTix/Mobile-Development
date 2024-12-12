package com.example.bolatix.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.bolatix.data.football.Football
import com.example.bolatix.data.remote.response.DataALlMatch
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

private const val FILENAME_FORMAT = "dd-MMM-yyyy-HH-mm-ss-SSS"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())


fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    return uri ?: getImageUriProvider(context)
}

fun createMultipartBodyPart(file: File): MultipartBody.Part {
    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("profile_picture", file.name, requestFile)
}

private fun getImageUriProvider(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "file-provider",
        imageFile
    )
}

fun generateQRCode(text: String): Bitmap {
    val qrCodeWriter = QRCodeWriter()
    val hints = mapOf(EncodeHintType.MARGIN to 1)
    val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200, hints)

    val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565)
    for (x in 0 until 200) {
        for (y in 0 until 200) {
            bitmap.setPixel(
                x,
                y,
                if (bitMatrix.get(
                        x,
                        y
                    )
                ) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            )
        }
    }
    return bitmap
}

fun Int.toIDR(): String {
    val formatSymbols = DecimalFormatSymbols(Locale("in", "ID")).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    val decimalFormat = DecimalFormat("Rp #,###", formatSymbols)
    return decimalFormat.format(this)
}

fun generateUUID(): String {
    return "BOLATIX-${
        UUID.randomUUID().toString().replace("-", "").uppercase().substring(0, 10)
    }"
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun findTeamLogo(teamName: String): String? {
    return Football.teams.find { it.teamName.equals(teamName.trim(), ignoreCase = true) }?.teamLogo
}

fun findGate(id: Int): String? {
    return Football.tickets.find { it.id == id }?.title
}

fun String.formatDate(): String {
    val inputFormat = SimpleDateFormat("d/M/yyyy", Locale("id", "ID"))
    val outputFormat = SimpleDateFormat("EEEE dd yyyy", Locale("id", "ID"))
    return try {
        val date = inputFormat.parse(this)
        outputFormat.format(date)
    } catch (e: Exception) {
        "Tanggal tidak valid"
    }
}

@SuppressLint("SimpleDateFormat")
fun filterUpcomingMatches(matches: List<DataALlMatch>): List<DataALlMatch> {
    val now = java.util.Date()
    return matches.filter { match ->
        val cleanedDate = match.tanggal.trim()
        val cleanedTime = match.jam.trim()
        try {
            val dateParts = cleanedDate.split("/")
            val year = if (dateParts[2].length == 2) "20${dateParts[2]}" else dateParts[2]
            val formattedDate =
                "${year}-${dateParts[1].padStart(2, '0')}-${dateParts[0].padStart(2, '0')}"
            val dateTimeString = "${formattedDate}T${cleanedTime}"
            val matchDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(dateTimeString)
            matchDate != null && matchDate.after(now)
        } catch (e: Exception) {
            println("Error parsing date for Match ID: ${match.idMatch} - ${match.tanggal}, Error: ${e.message}")
            false
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun filterPastMatches(matches: List<DataALlMatch>): List<DataALlMatch> {
    val now = java.util.Date()
    return matches.filter { match ->
        val cleanedDate = match.tanggal.trim()
        val cleanedTime = match.jam.trim()
        try {
            val dateParts = cleanedDate.split("/")
            val year = if (dateParts[2].length == 2) "20${dateParts[2]}" else dateParts[2]
            val formattedDate =
                "${year}-${dateParts[1].padStart(2, '0')}-${dateParts[0].padStart(2, '0')}"
            val dateTimeString = "${formattedDate}T${cleanedTime}"
            val matchDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                .parse(dateTimeString)
            matchDate != null && matchDate.before(now)
        } catch (e: Exception) {
            println("Error parsing date for Match ID: ${match.idMatch}, Error: ${e.message}")
            false
        }
    }
}

fun cropImage(context: Activity, uri: Uri) {
    val destinationUri =
        Uri.fromFile(File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
    UCrop.of(uri, destinationUri)
        .withAspectRatio(1f, 1f)
        .withMaxResultSize(1000, 1000)
        .start(context)
}




