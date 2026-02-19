package com.example.agriassist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.agriassist.databinding.PlantGridItemBinding

class PlantAdapter(private val plants: List<Plant>, private val listener: (Plant) -> Unit) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = PlantGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bind(plants[position], listener)
    }

    override fun getItemCount() = plants.size

    class PlantViewHolder(private val binding: PlantGridItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant, listener: (Plant) -> Unit) {
            binding.plantName.text = plant.name
            binding.plantStatus.text = plant.status
            binding.plantImage.load(plant.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.img)
            }
            itemView.setOnClickListener { listener(plant) }
        }
    }
}
