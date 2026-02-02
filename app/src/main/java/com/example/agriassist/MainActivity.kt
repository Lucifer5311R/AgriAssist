package com.example.agriassist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.agriassist.databinding.ActivityMainBinding
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions  // these are the imports required for machine learning kit which labels the images



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

    // Change THIS line to include "result" before the arrow
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                identifyPlant(imageBitmap)
            }
        }
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

    private fun dispatchTakePictureIntent(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            takePictureLauncher.launch(takePictureIntent)
        }
        catch(e : Exception)
        {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun identifyPlant(bitmap: Bitmap) {
        // 1. Prepare the image for ML Kit
        val image = InputImage.fromBitmap(bitmap, 0)

        // 2. Get the labeler (default generic model)
        // Note: For specific plants, you would eventually use 'ImageLabelerOptions.Builder()' with a custom plant model.
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        // 3. Process the image
        labeler.process(image)
            .addOnSuccessListener { labels ->
                // Task completed successfully
                val stringBuilder = StringBuilder()
                for (label in labels) {
                    val text = label.text
                    val confidence = label.confidence
                    stringBuilder.append("$text : $confidence\n")
                }

                // 4. Show result to user (For now, a Toast or Dialog)
                Toast.makeText(this, "Identified: \n$stringBuilder", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Toast.makeText(this, "Failed to identify: ${e.message}", Toast.LENGTH_SHORT).show()
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
                    true
                }
                R.id.navigation_diary -> {
                    startActivity(Intent(this, DiaryActivity::class.java))
                    true
                }
                R.id.navigation_account -> {
                    startActivity(Intent(this, AccountActivity::class.java))
                    true
                }
                R.id.navigation_camera -> {
                    dispatchTakePictureIntent()
                    true
                }
                else -> false
            }
        }
    }
}
