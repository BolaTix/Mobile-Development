package com.example.bolatix.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bolatix.R
import com.example.bolatix.databinding.ActivityFaqactivityBinding
import com.example.bolatix.ui.adapters.FAQAdapter

class FAQActivity : AppCompatActivity() {

    private var _binding: ActivityFaqactivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityFaqactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topBarFAQ.toolbarTitle.text = "FAQ"
        binding.topBarFAQ.btnBack.visibility = View.VISIBLE
        binding.topBarFAQ.btnBack.setOnClickListener {
            onBackPressed()
        }

        val faqItems = listOf(
            R.string.faq_title_1, R.string.faq_desc_1,
            R.string.faq_title_2, R.string.faq_desc_2,
            R.string.faq_title_3, R.string.faq_desc_3,
            R.string.faq_title_4, R.string.faq_desc_4,
            R.string.faq_title_5, R.string.faq_desc_5,
            R.string.faq_title_6, R.string.faq_desc_6,
            R.string.faq_title_7, R.string.faq_desc_7,
            R.string.faq_title_8, R.string.faq_desc_8,
            R.string.faq_title_9, R.string.faq_desc_9,
            R.string.faq_title_10, R.string.faq_desc_10
        )

        val adapter = FAQAdapter(faqItems)
        binding.rvFAQ.layoutManager = LinearLayoutManager(this)
        binding.rvFAQ.adapter = adapter
    }
}