package com.example.agriassist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.agriassist.databinding.ActivityAccountBinding

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        binding.logoutButton.setOnClickListener {
            // Create an intent to go to the LoginActivity
            val intent = Intent(this, LoginActivity::class.java)

            // These flags clear the activity stack, so the user can't go back
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish() // Close the AccountActivity
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarAccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAccount.setNavigationOnClickListener {
            finish() // Go back when the arrow is clicked
        }
    }
}
