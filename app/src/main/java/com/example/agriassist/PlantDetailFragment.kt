package com.example.agriassist

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.agriassist.databinding.FragmentPlantDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.net.URLEncoder

class PlantDetailFragment : Fragment() {

    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_PLANT = "plant"
        fun newInstance(plant: Plant): PlantDetailFragment {
            return PlantDetailFragment().apply {
                arguments = Bundle().apply { putParcelable(ARG_PLANT, plant) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val plant = arguments?.getParcelable<Plant>(ARG_PLANT) ?: return

        // ── Basic info ───────────────────────────────────────────
        binding.plantNameDetail.text   = plant.name
        binding.plantStatusDetail.text = plant.status
        binding.plantDescription.text  = plant.description

        // ── Hero image — local file first, then URL ───────────────
        if (plant.localImagePath.isNotEmpty()) {
            val file = File(plant.localImagePath)
            if (file.exists()) {
                binding.plantImageDetail.load(file) {
                    crossfade(true); placeholder(R.drawable.img)
                }
            } else loadImageFromUrl(plant.imageUrl)
        } else {
            loadImageFromUrl(plant.imageUrl)
        }

        // ── AI-scanned extras ────────────────────────────────────
        if (plant.isUserAdded) {
            binding.badgeAiScanned.visibility = View.VISIBLE

            if (plant.scientificName.isNotEmpty()) {
                binding.plantScientificName.text       = plant.scientificName
                binding.plantScientificName.visibility = View.VISIBLE
            }
            if (plant.family.isNotEmpty() && plant.family != "—") {
                binding.chipFamily.text       = "Family: ${plant.family}"
                binding.chipFamily.visibility = View.VISIBLE
            }
            if (plant.genus.isNotEmpty() && plant.genus != "—") {
                binding.chipGenus.text       = "Genus: ${plant.genus}"
                binding.chipGenus.visibility = View.VISIBLE
            }
            if (plant.careInfo.isNotEmpty() && plant.careInfo != "—") {
                binding.plantCareInfo.text      = plant.careInfo
                binding.cardCareInfo.visibility = View.VISIBLE
            }

            // Show remove button only for user-added plants
            binding.btnRemovePlant.visibility = View.VISIBLE
        }

        // ── Remove from My Plants ────────────────────────────────
        binding.btnRemovePlant.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Remove Plant")
                .setMessage("Remove \"${plant.name}\" from My Plants?")
                .setPositiveButton("Remove") { _, _ ->
                    PlantRepository.removePlant(requireContext(), plant.name)
                    Toast.makeText(requireContext(), "${plant.name} removed", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // ── WhatsApp share ───────────────────────────────────────
        binding.btnWhatsappShare.setOnClickListener { sendWhatsAppReport(plant) }
    }

    private fun loadImageFromUrl(url: String) {
        if (url.isNotEmpty()) {
            binding.plantImageDetail.load(url) {
                crossfade(true); placeholder(R.drawable.img)
            }
        }
    }

    private fun sendWhatsAppReport(plant: Plant) {
        val prefs       = requireContext().getSharedPreferences("AgriAssistPrefs", Context.MODE_PRIVATE)
        val farmName    = prefs.getString("farmName", "My Farm")
        val phone       = prefs.getString("emergencyPhone", "")

        val careSection = if (plant.careInfo.isNotEmpty() && plant.careInfo != "—")
            "\n*Care Guide:*\n${plant.careInfo}\n" else ""

        val msg = """
🌱 *AgriAssist Farm Report* 🌱
──────────────────────
*Farm:* $farmName
*Plant:* ${plant.name}
*Status:* ${plant.status}
${if (plant.scientificName.isNotEmpty()) "*Scientific Name:* ${plant.scientificName}\n" else ""}${if (plant.family.isNotEmpty() && plant.family != "—") "*Family:* ${plant.family}\n" else ""}
*About:*
${plant.description}
$careSection
_Sent via AgriAssist Smart Farming App_ 🌿
        """.trimIndent()

        try {
            val url = "https://api.whatsapp.com/send?phone=$phone&text=" +
                      URLEncoder.encode(msg, "UTF-8")
            val i = Intent(Intent.ACTION_VIEW).apply {
                setPackage("com.whatsapp")
                data = Uri.parse(url)
            }
            if (i.resolveActivity(requireContext().packageManager) != null) {
                startActivity(i)
            } else {
                startActivity(Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, msg)
                    }, "Share Report"
                ))
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
