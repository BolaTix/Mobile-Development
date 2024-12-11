package com.example.bolatix.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.bolatix.R
import com.example.bolatix.databinding.ActivityPrivacyAndPolicyBinding
import io.noties.markwon.Markwon

class PrivacyAndPolicyActivity : AppCompatActivity() {

    private var _binding: ActivityPrivacyAndPolicyBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityPrivacyAndPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topBarPrivacy.toolbarTitle.text = getString(R.string.text_pap)
        binding.topBarPrivacy.btnBack.visibility = View.VISIBLE
        binding.topBarPrivacy.btnBack.setOnClickListener {
            onBackPressed()
        }

        val markdown = """
            Selamat datang di aplikasi Bolatix! Privasi Anda sangat penting bagi kami. Kebijakan Privasi ini menjelaskan bagaimana kami mengumpulkan, menggunakan, melindungi, dan membagikan informasi Anda saat menggunakan aplikasi Bolatix.

            Dengan menggunakan aplikasi kami, Anda setuju untuk mematuhi dan terikat oleh Kebijakan Privasi ini. Jika Anda tidak setuju, harap jangan menggunakan aplikasi kami.

            #### 1. Informasi yang Kami Kumpulkan 
            Kami dapat mengumpulkan informasi pribadi Anda seperti: 
            - Nama lengkap
            - Alamat email
            - Nomor telepon
            - Informasi pembayaran (jika diperlukan untuk pembelian tiket) 

            #### 2. Penggunaan Informasi
            Menggunakan informasi Anda untuk: 
            - Memproses pembelian tiket pertandingan.
            - Memberikan informasi jadwal pertandingan, klasemen, dan berita bola.
            - Meningkatkan pengalaman pengguna dan layanan aplikasi.
            - Mengirimkan notifikasi atau promosi terkait Bolatix (hanya jika Anda memberikan izin). 

            #### 3. Berbagi Informasi
            Kami tidak akan menjual, menyewakan, atau membagikan informasi pribadi Anda kepada pihak ketiga kecuali untuk tujuan berikut: 
            - Penyedia Layanan: Kami dapat membagikan data dengan pihak ketiga yang membantu dalam pemrosesan pembayaran, pengiriman tiket, atau layanan lainnya.
            - Kepatuhan Hukum: Kami dapat membagikan informasi jika diwajibkan oleh hukum atau perintah pengadilan. 

            #### 4. Keamanan Data
            Kami berkomitmen untuk melindungi data Anda dengan: 
            - Enkripsi pada transaksi pembayaran.
            - Sistem keamanan untuk mencegah akses tidak sah. 
            Namun, kami tidak dapat menjamin 100% keamanan karena transmisi data melalui internet memiliki risiko.

            #### 5. Hak Anda
            Anda memiliki hak untuk: 
            - Mengakses data pribadi Anda.
            - Memperbarui atau memperbaiki data yang tidak akurat.
            - Meminta penghapusan data Anda, kecuali diperlukan oleh hukum. 

            #### 6. Penggunaan Cookie
            Aplikasi kami dapat menggunakan cookie untuk:
            - Menyimpan preferensi pengguna.
            - Meningkatkan pengalaman pengguna. 
            Anda dapat mengatur perangkat Anda untuk menolak cookie, tetapi beberapa fitur aplikasi mungkin tidak berfungsi optimal.

            #### 7. Pihak Ketiga
            Aplikasi kami dapat berisi tautan ke situs web atau layanan pihak ketiga. Kami tidak bertanggung jawab atas kebijakan privasi atau konten pihak ketiga tersebut.

            #### 8. Perubahan pada Kebijakan Privasi
            Kami dapat memperbarui Kebijakan Privasi ini dari waktu ke waktu. Perubahan akan diberitahukan melalui aplikasi atau email. Harap tinjau kebijakan secara berkala.

            #### 9. Hubungi Kami
            Jika Anda memiliki pertanyaan atau kekhawatiran tentang Kebijakan Privasi ini, hubungi kami melalui:
            Email: bolatix.official@gmail.com 
        """.trimIndent()

        val markwon = Markwon.create(this)
        markwon.setMarkdown(binding.tvMarkdown, markdown)

    }
}