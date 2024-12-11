package com.example.bolatix.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.bolatix.R
import com.example.bolatix.databinding.BottomSheetVerivyLogoutBinding
import com.example.bolatix.databinding.FragmentProfileBinding
import com.example.bolatix.preference.UserPreferences
import com.example.bolatix.ui.activities.AboutUsActivity
import com.example.bolatix.ui.activities.FAQActivity
import com.example.bolatix.ui.activities.NotificationActivity
import com.example.bolatix.ui.activities.PersonalInformationActivity
import com.example.bolatix.ui.activities.PrivacyAndPolicyActivity
import com.example.bolatix.ui.activities.SignInActivity
import com.example.bolatix.ui.viewmodels.AuthViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModel()
    private lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userPreferences = UserPreferences(requireContext())

        lifecycleScope.launch {
            userPreferences.getUser().collect { user ->
                val imageProfile = if (user.profilePicture.isNullOrEmpty())  R.drawable.ic_profile else user.profilePicture
                Glide.with(binding.ivProfile.context).load(imageProfile).into(binding.ivProfile)
                binding.tvName.text = user.name
                binding.tvEmail.text = user.email
            }
        }
        setUpListButton()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpListButton() {
        val buttons = mapOf(
            binding.btnPersonalInformation to PersonalInformationActivity::class.java,
            binding.btnNotification to NotificationActivity::class.java,
            binding.btnAboutUs to AboutUsActivity::class.java,
            binding.btnPrivacyPolice to PrivacyAndPolicyActivity::class.java,
            binding.btnFAQ to FAQActivity::class.java,
        )


        binding.btnLogout.setOnClickListener { logout() }

        buttons.forEach { (button, activity) ->
            button.setOnClickListener { startActivity(Intent(requireContext(), activity)) }
        }
    }

    private fun logout() {
        val dialog = BottomSheetDialog(requireContext())
        val binding = BottomSheetVerivyLogoutBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        binding.btnBack.setOnClickListener { dialog.dismiss() }
        binding.btnLogOut.setOnClickListener {
            lifecycleScope.launch {
                authViewModel.logout()
                val intent = Intent(requireContext(), SignInActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                requireActivity().finish()
            }
        }
        dialog.show()
    }
}