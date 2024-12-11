package com.example.bolatix.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.bolatix.R
import com.example.bolatix.databinding.ActivityOnboardingBinding
import com.example.bolatix.preference.OnBoardingPreference
import com.example.bolatix.ui.adapters.OnboardingViewPagerAdapter
import com.example.bolatix.utils.fadeInOut
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onBoardingStatus: OnBoardingPreference

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBoardingStatus = OnBoardingPreference(this)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.apply {
            viewPager.adapter = OnboardingViewPagerAdapter(
                this@OnboardingActivity,
                this@OnboardingActivity
            )
            viewPager.offscreenPageLimit = 1
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position == 0) {
                        btnPrevOrSkip.text = getText(R.string.skip)
                    } else {
                        btnPrevOrSkip.text = getText(R.string.back)
                    }
                    if (position == 2) {
                        btnNextOrFinish.text = getText(R.string.finish)
                    } else {
                        btnNextOrFinish.text = getText(R.string.next)
                    }
                }

                override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
                override fun onPageScrollStateChanged(arg0: Int) {}
            })
            TabLayoutMediator(binding.pageIndicator, viewPager) { _, _ -> }.attach()

            btnPrevOrSkip.setOnClickListener {
                if (getItem() == 0) {
                    initPermission()
                } else {
                    viewPager.setCurrentItem(getItem() - 1, true)
                }
            }

            btnNextOrFinish.setOnClickListener {
                if (getItem() > viewPager.childCount) {
                    initPermission()
                } else {
                    viewPager.setCurrentItem(getItem() + 1, true)
                }
            }
        }

    }

    private fun getItem(): Int {
        return binding.viewPager.currentItem
    }

    private fun finishOnboarding() {
        onBoardingStatus.setOnboardingCompleted(true)
        startActivity(Intent(this, SignInActivity::class.java))
        fadeInOut()
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initPermission() {
        requestPermissions {
            finishOnboarding()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions(onPermissionsGranted: () -> Unit) {
        val permissions = arrayOf(
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.POST_NOTIFICATIONS
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 1)
        } else {
            onPermissionsGranted()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            var allPermissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            if (allPermissionsGranted) {
                finishOnboarding()
            } else {
                val deniedPermissions = permissions.filterIndexed { index, _ ->
                    grantResults[index] == PackageManager.PERMISSION_DENIED && !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[index])
                }

                if (deniedPermissions.isNotEmpty()) {
                    showPermissionSettingsDialog()
                } else {
                    showPermissionDialog()
                }
            }
        }
    }

    private fun showPermissionSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.titleAlerPermission))
            .setMessage(getString(R.string.messageAlertPermission))
            .setPositiveButton(getString(R.string.goToSetting)) { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = android.net.Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionDialog(){
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.titleAlerPermission))
            .setMessage(getString(R.string.messageAlertPermission))
            .setPositiveButton(getString(R.string.tryAgain)) { _, _ ->
                requestPermissions {
                    finishOnboarding()
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

}