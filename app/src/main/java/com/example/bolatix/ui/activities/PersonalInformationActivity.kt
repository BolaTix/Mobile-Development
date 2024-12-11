package com.example.bolatix.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.bolatix.R
import com.example.bolatix.data.firebase.FirebaseAuthHelper
import com.example.bolatix.data.firebase.FirebaseService
import com.example.bolatix.data.firebase.FirestoreHelper
import com.example.bolatix.data.models.User
import com.example.bolatix.databinding.ActivityPersonalInformationBinding
import com.example.bolatix.databinding.BottomSheetChangeBirthdayBinding
import com.example.bolatix.databinding.BottomSheetChangeGenderBinding
import com.example.bolatix.databinding.BottomSheetChangeNameBinding
import com.example.bolatix.databinding.BottomSheetChangeNumberBinding
import com.example.bolatix.databinding.BottomSheetChangePasswordBinding
import com.example.bolatix.preference.UserPreferences
import com.example.bolatix.ui.viewmodels.ProfileViewModel
import com.example.bolatix.utils.createMultipartBodyPart
import com.example.bolatix.utils.cropImage
import com.example.bolatix.utils.getImageUri
import com.example.bolatix.utils.showToast
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class PersonalInformationActivity : AppCompatActivity() {

    private var _binding: ActivityPersonalInformationBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPreferences: UserPreferences
    private var currentImageUri: Uri? = null
    private val firebaseAuth = Firebase.auth
    private val firebaseAuthHelper = FirebaseAuthHelper()
    private val firestoreHelper = FirestoreHelper()
    private val firebaseService = FirebaseService(firebaseAuthHelper, firestoreHelper)
    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            currentImageUri = resultUri
            showImage()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            cropError?.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPersonalInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpProfile()
        setUpBtnChangeData()
    }

    private fun setUpProfile() {
        userPreferences = UserPreferences(this)
        with(binding) {
            lifecycleScope.launch {
                userPreferences.getUser().collect { v ->
                    val quote = "belum diisi"
                    tvShowName.text = if (v.name.isNullOrEmpty()) "Nama $quote" else v.name
                    tvShowEmail.text = if (v.email.isNullOrEmpty()) "Email $quote" else v.email
                    tvShowNumber.text =
                        if (v.phoneNumber.isNullOrEmpty()) "Nomor telepon $quote" else v.phoneNumber
                    tvShowBirthday.text =
                        if (v.birthday.isNullOrEmpty()) "Tanggal lahir $quote" else v.birthday
                    tvShowGender.text =
                        if (v.gender.isNullOrEmpty()) "Jenis kelamin $quote" else v.gender
                    if (v.profilePicture.isNullOrEmpty()) {
                        Glide.with(this@PersonalInformationActivity).load(R.drawable.ic_profile)
                            .into(ivProfile)
                    } else {
                        Glide.with(this@PersonalInformationActivity).load(v.profilePicture)
                            .into(ivProfile)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpBtnChangeData() {
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid ?: throw Exception("User Belum Login")
        with(binding) {
            topBarPersonalInformation.toolbarTitle.text = "Informasi Pribadi"
            topBarPersonalInformation.btnBack.visibility = View.VISIBLE
            topBarPersonalInformation.btnBack.setOnClickListener {
                onBackPressed()
            }
            btnChangePicture.setOnClickListener {
                showImageSourceDialog()
            }
            btnChangeName.setOnClickListener { changeName(userId) }
            btnChangeEmail.setOnClickListener {
                // TODO: GANTI EMAIL
            }
            btnChangePassword.setOnClickListener { changePassword() }
            btnChangeNumber.setOnClickListener { changeNumber(userId) }
            btnChangeGender.setOnClickListener { changeGender(userId) }
            btnChangeBirthday.setOnClickListener { changeBirtDay(userId) }
        }
    }

    private fun showImageSourceDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_image_source, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<LinearLayout>(R.id.option_camera).setOnClickListener {
            dialog.dismiss()
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri!!)
        }

        dialogView.findViewById<LinearLayout>(R.id.option_gallery).setOnClickListener {
            dialog.dismiss()
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        dialog.show()
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            cropImage(this, currentImageUri!!)
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        binding.loading.root.visibility = View.VISIBLE
        currentImageUri?.let {
            val file = uriToFile2(it)
            val multipartBody = createMultipartBodyPart(file)
            profileViewModel.uploadProfilePicture(
                firebaseAuth.currentUser?.uid.toString(),
                multipartBody
            )
            profileViewModel.uploadStatus.observe(this) { result ->
                result.onSuccess { response ->
                    val profilePictureUrl = response.data.profile_picture_url
                    updateUserData(
                        userId = firebaseAuth.currentUser?.uid.toString(),
                        data = mapOf("profile_picture" to profilePictureUrl),
                        successMessage = "Foto profil berhasil diganti",
                        failureMessage = "Gagal mengganti Foto profil",
                    )
                }
                result.onFailure {
                    showToast(this@PersonalInformationActivity, "Gagal upload gambar.")
                }
                binding.loading.root.visibility = View.GONE
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            cropImage(this, uri)
        }
    }

    private fun changeName(userId: String) {
        val dialog = BottomSheetDialog(this@PersonalInformationActivity)
        val binding = BottomSheetChangeNameBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        with(binding) {
            btnBack.setOnClickListener { dialog.dismiss() }
            btnChange.setOnClickListener {
                val name = input.text.toString()
                if (name.isNotEmpty()) {
                    updateUserData(
                        userId = userId,
                        data = mapOf("name" to name),
                        successMessage = "Nama berhasil diganti.",
                        failureMessage = "Gagal mengganti nama.",
                        onSuccess = { dialog.dismiss() }
                    )
                }
            }
        }
        dialog.show()
    }

    private fun changeNumber(userId: String) {
        val dialog = BottomSheetDialog(this@PersonalInformationActivity)
        val binding = BottomSheetChangeNumberBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        with(binding) {
            btnBack.setOnClickListener { dialog.dismiss() }
            btnChange.setOnClickListener {
                val numberPhone = input.text.toString()
                if (numberPhone.isNotEmpty()) {
                    updateUserData(
                        userId = userId,
                        data = mapOf("phone_number" to numberPhone),
                        successMessage = "No Telepon berhasil diganti.",
                        failureMessage = "Gagal mengganti No Telepon.",
                        onSuccess = { dialog.dismiss() }
                    )
                }
            }
        }
        dialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun changeBirtDay(userId: String) {
        val dialog = BottomSheetDialog(this@PersonalInformationActivity)
        val binding = BottomSheetChangeBirthdayBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        with(binding) {
            btnBack.setOnClickListener { dialog.dismiss() }
            input.setOnTouchListener { _, _ ->
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Pilih Tanggal Lahir.")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Default to today's date
                    .build()
                datePicker.addOnPositiveButtonClickListener { selection ->
                    val selectedDate = selection as Long
                    val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(
                        Date(selectedDate)
                    )
                    input.setText(formattedDate)
                }
                datePicker.show(supportFragmentManager, datePicker.toString())
                true
            }
            btnChange.setOnClickListener {
                val birthday = input.text.toString()
                if (birthday.isNotEmpty()) {
                    updateUserData(
                        userId = userId,
                        data = mapOf("birth_date" to birthday),
                        successMessage = "Tanggal Lahir berhasil diganti.",
                        failureMessage = "Gagal mengganti Tanggal Lahir.",
                        onSuccess = { dialog.dismiss() }
                    )
                } else {
                    binding.input.error = "Tanggal Lahir tidak boleh kosong"
                }
            }
        }

        dialog.show()
    }

    private fun changeGender(userId: String) {
        val dialog = BottomSheetDialog(this@PersonalInformationActivity)
        val binding = BottomSheetChangeGenderBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        val genderOptions = arrayOf("Laki-laki", "Perempuan", "Lainnya")
        val spinnerGender: Spinner = binding.root.findViewById(R.id.spinnerGender)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter
        with(binding) {
            btnBack.setOnClickListener { dialog.dismiss() }
            btnChange.setOnClickListener {
                val gender = spinnerGender.selectedItem.toString()

                if (gender.isNotEmpty()) {
                    updateUserData(
                        userId = userId,
                        data = mapOf("gender" to gender),
                        successMessage = "Jenis Kelamin berhasil diganti.",
                        failureMessage = "Gagal mengganti Jenis Kelamin.",
                        onSuccess = { dialog.dismiss() }
                    )
                }
            }
        }
        dialog.show()
    }

    private fun changePassword() {
        val dialog = BottomSheetDialog(this@PersonalInformationActivity)
        val binding = BottomSheetChangePasswordBinding.inflate(layoutInflater)
        val user = firebaseAuth.currentUser!!
        dialog.setContentView(binding.root)
        bindProgressButton(binding.btnChange)
        val color = getResources().getColor(R.color.white)
        with(binding) {
            btnBack.setOnClickListener { dialog.dismiss() }
            btnChange.setOnClickListener {
                btnChange.showProgress { progressColor = color }
                val newPassword = inputNewPassword.text.toString()
                user.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast(
                                this@PersonalInformationActivity,
                                "Password berhasil diganti."
                            )
                            dialog.dismiss()
                        } else {
                            showToast(this@PersonalInformationActivity, "Gagal mengganti Password.")
                        }
                        btnChange.hideProgress("Ubah Password")
                    }
            }
        }
        dialog.show()
    }

    private fun updateUserData(
        userId: String,
        data: Map<String, Any>,
        successMessage: String,
        failureMessage: String,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = firebaseAuthHelper.getCurrentUser()
        db.collection("users").document(userId)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                showToast(this@PersonalInformationActivity, successMessage)
                onSuccess()
            }
            .addOnFailureListener {
                showToast(this@PersonalInformationActivity, failureMessage)
                onFailure()
            }
        if (currentUser != null) {
            firebaseService.getUserData(currentUser.uid) { res ->
                lifecycleScope.launch {
                    val user = res?.toObject(User::class.java) ?: throw Exception("User not found")
                    userPreferences.setUser(user)
                }
            }
        }
    }

    private fun uriToFile2(uri: Uri): File {
        val contentResolver = contentResolver
        val tempFile = File.createTempFile("temp", ".jpg", cacheDir)

        contentResolver.openInputStream(uri).use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }
        return tempFile
    }
}