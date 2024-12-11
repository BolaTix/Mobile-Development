package com.example.bolatix.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bolatix.R
import com.example.bolatix.data.football.Football
import com.example.bolatix.data.models.User
import com.example.bolatix.databinding.ActivityFavoriteBinding
import com.example.bolatix.preference.UserPreferences
import com.example.bolatix.ui.adapters.TeamAdapter
import com.example.bolatix.utils.showToast
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var userPreferences: UserPreferences
    private val db = Firebase.firestore
    private val firebaseAuth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(this)
        val recyclerView: RecyclerView = binding.recyclerView
        bindProgressButton(binding.btnAddFavorite)

        recyclerView.layoutManager = GridLayoutManager(this, 3)

        val adapter = TeamAdapter(Football.teams, maxSelection = 1)
        recyclerView.adapter = adapter

        binding.btnAddFavorite.setOnClickListener {
            binding.btnAddFavorite.showProgress{ progressColor = Color.WHITE }
            val currentUser = firebaseAuth.currentUser
            val userId = currentUser?.uid ?: throw Exception("User is not logged in")
            val selectedTeams = adapter.getSelectedTeams()
            if (selectedTeams.isEmpty()) {
                showToast(this, "Pilih 1 tim!")
                binding.btnAddFavorite.hideProgress(R.string.addToFavorite)
            } else {
                db.collection("users").document(userId).set(
                    mapOf(
                        "favorite_team" to selectedTeams.first().teamName,
                        "updated_at" to Timestamp.now()
                    ),
                    SetOptions.merge()
                ).addOnSuccessListener {
                    startActivity(Intent(this, MainMenuActivity::class.java))
                    binding.btnAddFavorite.hideProgress(R.string.addToFavorite)
                    lifecycleScope.launch {
                        userPreferences.updateUser(User(favoriteTeam = selectedTeams.first().teamName))
                    }
                }.addOnFailureListener {
                    showToast(this, "Gagal Menambah Team Favorite!")
                    binding.btnAddFavorite.hideProgress(R.string.addToFavorite)
                }
            }
        }

    }
}