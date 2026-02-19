package com.example.agriassist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.agriassist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupClickListeners()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.selectedItemId = R.id.navigation_home
    }

    private fun setupClickListeners() {
        binding.weatherCard.setOnClickListener {
            Toast.makeText(this, "Fetching latest weather data...", Toast.LENGTH_SHORT).show()
        }

        binding.learnMore.setOnClickListener {
            startActivity(Intent(this, Learn_More::class.java))
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true // Already here
                R.id.navigation_plants -> {
                    startActivity(Intent(this, PlantsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_diary -> {
                    startActivity(Intent(this, DiaryActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_account -> {
                    startActivity(Intent(this, AccountActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_camera -> {
                    startActivity(Intent(this, CameraActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
}
