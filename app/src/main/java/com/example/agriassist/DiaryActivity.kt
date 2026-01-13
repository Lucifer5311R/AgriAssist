package com.example.agriassist

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.agriassist.databinding.ActivityAddEntryBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DiaryActivity : AppCompatActivity() {

    // 1. Enable ViewBinding
    private lateinit var binding: ActivityAddEntryBinding
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding (Matches activity_add_entry.xml)
        binding = ActivityAddEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDatePicker()

        // 2. Save Button Logic
        binding.saveEntryButton.setOnClickListener {
            saveDiaryEntry()
        }

        // 3. History Button Logic
        binding.btnViewHistory.setOnClickListener {
            showHistoryDialog()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarAddEntry)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddEntry.setNavigationOnClickListener {
            finish() // Go back when arrow is clicked
        }
        updateDateDisplay() // Show current date by default
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

        // Get Status
        val selectedId = binding.cropStatusRadioGroup.checkedRadioButtonId
        val status = if (selectedId != -1) {
            findViewById<RadioButton>(selectedId).text.toString()
        } else {
            "Not Specified"
        }

        // --- THE USEFUL PART: Save to Internal Storage ---
        val entryData = """
            --------------------------------
            Date: $date
            Title: $title
            Status: $status
            Watered: ${if (isWatered) "Yes" else "No"}
            Notes: $notes
        """.trimIndent() + "\n"

        try {
            // "diary_log.txt" is the file name, MODE_APPEND adds to the end instead of overwriting
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
        // Read the file
        val file = File(filesDir, "diary_log.txt")
        val history = if (file.exists()) {
            file.readText()
        } else {
            "No diary entries found yet."
        }

        // Show in a popup
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
        binding.cropStatusRadioGroup.clearCheck()
    }
}