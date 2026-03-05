package com.example.agriassist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.agriassist.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Weather card
        binding.weatherCard.setOnClickListener {
            Toast.makeText(requireContext(), "Weather details coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Read More news
        binding.learnMore.setOnClickListener {
            val intent = android.content.Intent(requireContext(), Learn_More::class.java)
            startActivity(intent)
        }

        // Quick Action — Scan Crop → Camera Fragment
        binding.actionScanCrop.setOnClickListener {
            findNavController().navigate(R.id.navigation_camera)
        }

        // Quick Action — Weather Forecast
        binding.actionWeather.setOnClickListener {
            Toast.makeText(requireContext(), "Weather forecast coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Quick Action — My Diary
        binding.actionDiary.setOnClickListener {
            findNavController().navigate(R.id.navigation_diary)
        }

        // Quick Action — Marketplace
        binding.actionMarket.setOnClickListener {
            findNavController().navigate(R.id.navigation_marketplace)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
