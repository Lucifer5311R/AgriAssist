package com.example.agriassist

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.agriassist.databinding.FragmentDiaryBinding
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DiaryFragment : Fragment() {

    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDatePicker()

        binding.saveEntryButton.setOnClickListener {
            saveDiaryEntry()
        }

        binding.btnViewHistory.setOnClickListener {
            showHistoryDialog()
        }
        
        updateDateDisplay()
    }

    private fun setupDatePicker() {
        binding.datePickerContainer.setOnClickListener {
            DatePickerDialog(
                requireContext(),
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

        val selectedChipId = binding.cropStatusChipGroup.checkedChipId
        val status = if (selectedChipId != -1) {
            view?.findViewById<Chip>(selectedChipId)?.text.toString() ?: "Not Specified"
        } else {
            "Not Specified"
        }

        val selectedWeatherId = binding.weatherRadioGroup.checkedRadioButtonId
        val weather = if (selectedWeatherId != -1) {
            view?.findViewById<RadioButton>(selectedWeatherId)?.text.toString() ?: "Not Recorded"
        } else {
            "Not Recorded"
        }

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
            requireContext().openFileOutput("diary_log.txt", Context.MODE_APPEND).use {
                it.write(entryData.toByteArray())
            }
            Toast.makeText(requireContext(), "Entry Saved to Diary!", Toast.LENGTH_SHORT).show()
            clearForm()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error saving entry", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showHistoryDialog() {
        val file = File(requireContext().filesDir, "diary_log.txt")
        val history = if (file.exists()) {
            file.readText()
        } else {
            "No diary entries found yet."
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("My Diary History")
            .setMessage(history)
            .setPositiveButton("Close", null)
            .setNeutralButton("Clear History") { _, _ ->
                if (file.exists()) file.delete()
                Toast.makeText(requireContext(), "History Cleared", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun clearForm() {
        binding.entryTitleEditText.text?.clear()
        binding.entryNotesEditText.text?.clear()
        binding.wateredCheckbox.isChecked = false
        binding.cropStatusChipGroup.clearCheck()
        binding.weatherRadioGroup.clearCheck()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
