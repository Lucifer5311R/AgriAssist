package com.example.agriassist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.agriassist.databinding.FragmentPlantGridBinding

class PlantsFragment : Fragment() {

    private var _binding: FragmentPlantGridBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    // Reload every time the fragment resumes (so newly added plants show instantly)
    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // ── Default showcase plants ───────────────────────────────
        val defaultPlants = listOf(
            Plant("Tomato",     "Growing Active",    "https://images.unsplash.com/photo-1598512752271-33f913a5af13?w=400", "The tomato is the edible berry of the plant Solanum lycopersicum."),
            Plant("Cucumber",   "Ready to Harvest",  "https://images.unsplash.com/photo-1621289196562-37f2653a7b53?w=400", "Cucumber is a widely-cultivated creeping vine in the Cucurbitaceae family."),
            Plant("Bell Pepper","Seeding Phase",     "https://images.unsplash.com/photo-1599557422033-80f682a88a03?w=400", "The bell pepper is the fruit of plants in the Grossum cultivar group."),
            Plant("Lettuce",    "Growing Active",    "https://plus.unsplash.com/premium_photo-1669935133691-91221768565a?w=400", "Lettuce is an annual plant of the daisy family, Asteraceae."),
            Plant("Carrot",     "Ready to Harvest",  "https://images.unsplash.com/photo-1590429446543-a6d1d4733e38?w=400", "The carrot is a root vegetable, typically orange in color."),
            Plant("Broccoli",   "Seeding Phase",     "https://images.unsplash.com/photo-1587354249918-b352329d2b27?w=400", "Broccoli is an edible green plant in the cabbage family.")
        )

        // ── User-saved plants from AI scanner (newest first) ──────
        val userPlants = PlantRepository.getAllUserPlants(requireContext())

        // ── Merge: user plants on top, then defaults ──────────────
        val allPlants = (userPlants + defaultPlants).distinctBy { it.name.lowercase() }

        binding.plantsRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.plantsRecyclerView.adapter = PlantAdapter(allPlants) { plant ->
            val bundle = Bundle().apply { putParcelable("plant", plant) }
            findNavController().navigate(R.id.action_plants_to_detail, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
