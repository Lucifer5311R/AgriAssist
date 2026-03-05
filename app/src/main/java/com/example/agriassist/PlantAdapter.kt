package com.example.agriassist

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import java.io.File
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

    class PlantViewHolder(private val binding: PlantGridItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {
        init {
            binding.plantImage.setOnCreateContextMenuListener(this)
        }

        @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
        fun bind(plant: Plant, listener: (Plant) -> Unit) {
            binding.plantName.text   = plant.name
            binding.plantStatus.text = plant.status

            // Load local file first; fall back to URL
            if (plant.localImagePath.isNotEmpty()) {
                val file = File(plant.localImagePath)
                if (file.exists()) {
                    binding.plantImage.load(file) {
                        crossfade(true)
                        placeholder(R.drawable.img)
                    }
                } else {
                    binding.plantImage.load(plant.imageUrl) {
                        crossfade(true); placeholder(R.drawable.img)
                    }
                }
            } else {
                binding.plantImage.load(plant.imageUrl) {
                    crossfade(true); placeholder(R.drawable.img)
                }
            }

            itemView.setOnClickListener { listener(plant) }

            binding.moreButton.setOnClickListener { showPopupMenu(it) }
        }

        @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
        private fun showPopupMenu(view: View) {
            val popup = PopupMenu(view.context, view)
            popup.inflate(R.menu.plant_popup_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.popup_share -> {
                        Toast.makeText(view.context, "Share Plant clicked", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.popup_harvest -> {
                        Toast.makeText(view.context, "Mark as Harvested clicked", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        override fun onCreateContextMenu(menu: android.view.ContextMenu?, v: android.view.View?, menuInfo: android.view.ContextMenu.ContextMenuInfo?) {
            menu?.add(this.adapterPosition, R.id.context_analyze, 0, "Analyze with Camera")
            menu?.add(this.adapterPosition, R.id.context_history, 1, "View Growth History")
        }
    }
}
