package com.example.agriassist

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.RadioButton // Imported RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.agriassist.databinding.ActivityAddEntryBinding
import com.google.android.material.chip.Chip
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DiaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEntryBinding
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDatePicker()

        binding.saveEntryButton.setOnClickListener {
            saveDiaryEntry()
        }

        binding.btnViewHistory.setOnClickListener {
            showHistoryDialog()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarAddEntry)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddEntry.setNavigationOnClickListener {
            finish()
        }
        updateDateDisplay()
    }

    private fun setupDatePicker() {
        binding.datePickerContainer.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    updateDateDisplay()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateDateDisplay() {
        val format = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
        binding.selectedDateText.text = format.format(calendar.time)
    }

    private fun saveDiaryEntry() {
        val title = binding.entryTitleEditText.text.toString().trim()
        val notes = binding.entryNotesEditText.text.toString().trim()
        val isWatered = binding.wateredCheckbox.isChecked
        val date = binding.selectedDateText.text.toString()

        if (title.isEmpty()) {
            binding.entryTitleEditText.error = "Please enter a title"
            return
        }

        // 1. Get Status from ChipGroup
        val selectedChipId = binding.cropStatusChipGroup.checkedChipId
        val status = if (selectedChipId != -1) {
            findViewById<Chip>(selectedChipId).text.toString()
        } else {
            "Not Specified"
        }

        // 2. Get Weather from RadioGroup (NEW)
        val selectedWeatherId = binding.weatherRadioGroup.checkedRadioButtonId
        val weather = if (selectedWeatherId != -1) {
            findViewById<RadioButton>(selectedWeatherId).text.toString()
        } else {
            "Not Recorded"
        }

        // 3. Save to String
        val entryData = """
            --------------------------------
            Date: $date
            Title: $title
            Status: $status
            Weather: $weather
            Watered: ${if (isWatered) "Yes" else "No"}
            Notes: $notes
        """.trimIndent() + "\n"

        try {
            openFileOutput("diary_log.txt", Context.MODE_APPEND).use {
                it.write(entryData.toByteArray())
            }
            Toast.makeText(this, "Entry Saved to Diary!", Toast.LENGTH_SHORT).show()
            clearForm()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving entry", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showHistoryDialog() {
        val file = File(filesDir, "diary_log.txt")
        val history = if (file.exists()) {
            file.readText()
        } else {
            "No diary entries found yet."
        }

        AlertDialog.Builder(this)
            .setTitle("My Diary History")
            .setMessage(history)
            .setPositiveButton("Close", null)
            .setNeutralButton("Clear History") { _, _ ->
                if (file.exists()) file.delete()
                Toast.makeText(this, "History Cleared", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun clearForm() {
        binding.entryTitleEditText.text?.clear()
        binding.entryNotesEditText.text?.clear()
        binding.wateredCheckbox.isChecked = false
        binding.cropStatusChipGroup.clearCheck()
        binding.weatherRadioGroup.clearCheck() // Clear radio selection
    }
}