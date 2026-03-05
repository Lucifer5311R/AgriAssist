package com.example.agriassist

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.agriassist.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("AgriAssistPrefs", Context.MODE_PRIVATE)

        setSupportActionBar(binding.settingsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.settingsToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        val isDarkMode = prefs.getBoolean("darkMode", false)
        val reminders = prefs.getBoolean("reminders", true)
        val weatherAlerts = prefs.getBoolean("weatherAlerts", true)
        val saveHistory = prefs.getBoolean("saveHistory", true)
        val hqScan = prefs.getBoolean("hqScan", false)
        val fahrenheit = prefs.getBoolean("fahrenheit", false)
        val textSize = prefs.getInt("textSize", 2)

        binding.switchDarkMode.isChecked = isDarkMode
        binding.switchReminders.isChecked = reminders
        binding.switchWeatherAlerts.isChecked = weatherAlerts
        binding.switchSaveHistory.isChecked = saveHistory
        binding.switchHqScan.isChecked = hqScan
        binding.switchFahrenheit.isChecked = fahrenheit
        binding.sliderTextSize.value = textSize.toFloat()
        updateTextSizeLabel(textSize)
    }

    private fun setupListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("darkMode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.switchReminders.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("reminders", isChecked).apply()
        }

        binding.switchWeatherAlerts.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("weatherAlerts", isChecked).apply()
        }

        binding.switchSaveHistory.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("saveHistory", isChecked).apply()
        }

        binding.switchHqScan.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("hqScan", isChecked).apply()
        }

        binding.switchFahrenheit.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("fahrenheit", isChecked).apply()
        }

        binding.sliderTextSize.addOnChangeListener { _, value, _ ->
            val size = value.toInt()
            prefs.edit().putInt("textSize", size).apply()
            updateTextSizeLabel(size)
        }
    }

    private fun updateTextSizeLabel(size: Int) {
        binding.textSizeLabel.text = when (size) {
            1 -> "Small"
            3 -> "Large"
            else -> "Medium"
        }
    }
}

