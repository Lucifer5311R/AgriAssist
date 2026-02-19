package com.example.agriassist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.agriassist.databinding.FragmentPlantGridBinding

class PlantGridFragment : Fragment() {

    private var _binding: FragmentPlantGridBinding? = null
    private val binding get() = _binding!!
    private var listener: OnPlantClickListener? = null

    interface OnPlantClickListener {
        fun onPlantClick(plant: Plant)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPlantClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnPlantClickListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlantGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val plantList = listOf(
            Plant("Tomato", "Growing Active", "https://images.unsplash.com/photo-1598512752271-33f913a5af13?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "The tomato is the edible berry of the plant Solanum lycopersicum, commonly known as a tomato plant. The species originated in western South America and Central America."),
            Plant("Cucumber", "Ready to Harvest", "https://images.unsplash.com/photo-1621289196562-37f2653a7b53?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Cucumber is a widely-cultivated creeping vine plant in the Cucurbitaceae gourd family that bears cucumiform fruits, which are used as vegetables."),
            Plant("Bell Pepper", "Seeding Phase", "https://images.unsplash.com/photo-1599557422033-80f682a88a03?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "The bell pepper is the fruit of plants in the Grossum cultivar group of the species Capsicum annuum. Cultivars of the plant produce fruits in different colors, including red, yellow, orange, green, white, and purple."),
            Plant("Lettuce", "Growing Active", "https://plus.unsplash.com/premium_photo-1669935133691-91221768565a?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Lettuce is an annual plant of the daisy family, Asteraceae. It is most often grown as a leaf vegetable, but sometimes for its stem and seeds."),
            Plant("Carrot", "Ready to Harvest", "https://images.unsplash.com/photo-1590429446543-a6d1d4733e38?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "The carrot is a root vegetable, typically orange in color, though purple, black, red, white, and yellow cultivars exist."),
            Plant("Broccoli", "Seeding Phase", "https://images.unsplash.com/photo-1587354249918-b352329d2b27?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Broccoli is an edible green plant in the cabbage family whose large flowering head, stalk and small associated leaves are eaten as a vegetable.")
        )

        binding.plantsRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.plantsRecyclerView.adapter = PlantAdapter(plantList) {
            plant -> listener?.onPlantClick(plant)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
