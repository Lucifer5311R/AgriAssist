package com.example.agriassist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.agriassist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 1. Declare the binding variable
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets to handle status bar overlap
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) // Keep bottom padding 0 for nav bar
            insets
        }

        // --- Setup Listeners --- //
        setupClickListeners()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        // 3. Ensure "Home" is selected when returning to this activity
        binding.bottomNavigation.selectedItemId = R.id.navigation_home
    }

    private fun setupClickListeners() {
        // 4. Make the weather card interactive
        binding.weatherCard.setOnClickListener {
            Toast.makeText(this, "Fetching latest weather data...", Toast.LENGTH_SHORT).show()
            // In the future, you could open a detailed weather screen here
        }

        binding.learnMore.setOnClickListener {
            startActivity(Intent(this, Learn_More::class.java))
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            // 5. Add a smooth, non-animated transition between activities
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Already on the home screen, do nothing
                    true
                }
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
                    true
                }
                else -> false
            }
        }
    }
}
