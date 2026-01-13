package com.example.agriassist

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

// Class name changed to DiaryActivity to match your MainActivity intent
class DiaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure your XML file is named 'activity_add_entry.xml'
        setContentView(R.layout.activity_add_entry)

        // 1. Initialize Views
        val titleInput = findViewById<TextInputEditText>(R.id.entry_title_edit_text)
        val statusGroup = findViewById<RadioGroup>(R.id.crop_status_radio_group)
        val wateredBox = findViewById<CheckBox>(R.id.watered_checkbox)
        val saveButton = findViewById<Button>(R.id.save_entry_button)

        // 2. Set Click Listener for the Save Button
        saveButton.setOnClickListener {
            val title = titleInput.text.toString()
            val isWatered = wateredBox.isChecked

            // Get the text of the selected RadioButton
            val selectedStatusId = statusGroup.checkedRadioButtonId
            val statusText = if (selectedStatusId != -1) {
                // Find the button that was clicked inside the group
                findViewById<android.widget.RadioButton>(selectedStatusId).text.toString()
            } else {
                "No status selected"
            }

            // For now, just show a Toast with the data
            val message = "Saved: $title ($statusText)"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            // Optional: Close this screen and go back after saving
            // finish()
        }
    }
}