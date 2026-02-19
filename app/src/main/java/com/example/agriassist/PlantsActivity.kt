package com.example.agriassist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.agriassist.databinding.ActivityPlantsBinding
import com.example.agriassist.PlantDetailFragment

class PlantsActivity : AppCompatActivity(), PlantGridFragment.OnPlantClickListener {

    private lateinit var binding: ActivityPlantsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarPlants)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarPlants.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PlantGridFragment())
                .commit()
        }

        binding.fabCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }

    override fun onPlantClick(plant: Plant) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PlantDetailFragment.newInstance(plant))
            .addToBackStack(null)
            .commit()
    }
}
