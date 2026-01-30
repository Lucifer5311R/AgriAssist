package com.example.agriassist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.agriassist.databinding.ActivityPlantsBinding

class PlantsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlantsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarPlants)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarPlants.setNavigationOnClickListener {
            finish() // Go back when the arrow is clicked
        }
    }

    private fun setupRecyclerView() {
        // Create some sample plant data
        val plantList = listOf(
            Plant("Tomato", "Growing Active", R.drawable.img),
            Plant("Cucumber", "Ready to Harvest", R.drawable.img),
            Plant("Bell Pepper", "Seeding Phase", R.drawable.img),
            Plant("Lettuce", "Growing Active", R.drawable.img),
            Plant("Carrot", "Ready to Harvest", R.drawable.img),
            Plant("Broccoli", "Seeding Phase", R.drawable.img)
        )

        // Set up the RecyclerView with the adapter
        binding.plantsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.plantsRecyclerView.adapter = PlantAdapter(plantList)
    }
}
