package com.example.agriassist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.agriassist.databinding.ActivityAccountBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadUserProfile()

        binding.saveProfileButton.setOnClickListener {
            saveFarmInfo()
        }

        binding.logoutButton.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun loadUserProfile() {
        val sharedPreferences = getSharedPreferences("AgriAssistPrefs", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("userName", "User")
        val farmName = sharedPreferences.getString("farmName", "")
        val phone = sharedPreferences.getString("emergencyPhone", "")

        binding.usernameText.text = name
        binding.editFarmName.setText(farmName)
        binding.editPhone.setText(phone)
    }

    private fun saveFarmInfo() {
        val farmName = binding.editFarmName.text.toString().trim()
        val phone = binding.editPhone.text.toString().trim()

        val sharedPreferences = getSharedPreferences("AgriAssistPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("farmName", farmName)
        editor.putString("emergencyPhone", phone)
        editor.apply()

        Toast.makeText(this, "Farm information updated!", Toast.LENGTH_SHORT).show()
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .show()
    }

    private fun performLogout() {
        val sharedPreferences = getSharedPreferences("AgriAssistPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // Clear session but keep farm info
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarAccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAccount.setNavigationOnClickListener {
            finish()
        }
    }
}
