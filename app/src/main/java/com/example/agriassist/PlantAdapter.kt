package com.example.agriassist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.agriassist.databinding.PlantGridItemBinding

class PlantAdapter(private val plants: List<Plant>) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = PlantGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bind(plants[position])
    }

    override fun getItemCount() = plants.size

    class PlantViewHolder(private val binding: PlantGridItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant) {
            binding.plantName.text = plant.name
            binding.plantStatus.text = plant.status
            binding.plantImage.setImageResource(plant.imageResId)

            // Set a click listener on the item view
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "Clicked on ${plant.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
