package com.example.agriassist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import coil.load
import com.example.agriassist.databinding.FragmentPlantDetailBinding

class PlantDetailFragment : Fragment() {

    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!
    private val TAG = "PlantDetailLifecycle"

    companion object {
        private const val ARG_PLANT = "plant"

        fun newInstance(plant: Plant): PlantDetailFragment {
            val fragment = PlantDetailFragment()
            val args = Bundle().apply {
                putParcelable(ARG_PLANT, plant)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlantDetailBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView() called")
        Toast.makeText(activity, "Detail: onCreateView", Toast.LENGTH_SHORT).show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")
        Toast.makeText(activity, "Detail: onViewCreated", Toast.LENGTH_SHORT).show()

        arguments?.getParcelable<Plant>(ARG_PLANT)?.let {
            binding.plantNameDetail.text = it.name
            binding.plantStatusDetail.text = it.status
            binding.plantDescription.text = it.description
            binding.plantImageDetail.load(it.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.img)
            }
        }
    }

    //<editor-fold desc="Lifecycle Logging">
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach() called")
        Toast.makeText(context, "Detail: onAttach", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
        Toast.makeText(activity, "Detail: onCreate", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
        Toast.makeText(activity, "Detail: onStart", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
        Toast.makeText(activity, "Detail: onResume", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
        Toast.makeText(activity, "Detail: onPause", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
        Toast.makeText(activity, "Detail: onStop", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView() called")
        Toast.makeText(activity, "Detail: onDestroyView", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
        Toast.makeText(activity, "Detail: onDestroy", Toast.LENGTH_SHORT).show()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach() called")
        Toast.makeText(activity, "Detail: onDetach", Toast.LENGTH_SHORT).show()
    }
    //</editor-fold>
}
