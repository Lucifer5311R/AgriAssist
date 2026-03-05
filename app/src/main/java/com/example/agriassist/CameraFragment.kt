package com.example.agriassist

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.agriassist.databinding.ActivityCameraBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment() {

    private var _binding: ActivityCameraBinding? = null
    private val binding get() = _binding!!
    private var imageCapture: ImageCapture? = null
    private var currentBitmap: Bitmap? = null
    private var currentResult: PlantResult? = null

    // ── Pl@ntNet API — your personal key ─────────────────────────
    private val PLANTNET_API_KEY = "2b10M5FHEcgXT8btplZYcB3f"

    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    // ── Gallery picker ───────────────────────────────────────────
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { analyzeFromUri(it) } }

    // ── Camera permission ────────────────────────────────────────
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera()
        else Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ActivityCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) startCamera()
        else permissionLauncher.launch(Manifest.permission.CAMERA)

        binding.imageCaptureButton.setOnClickListener { takePhoto() }
        binding.btnGallery.setOnClickListener { galleryLauncher.launch("image/*") }
        binding.btnCloseResult.setOnClickListener {
            binding.resultCard.visibility = View.GONE
            currentBitmap = null
            currentResult = null
        }

        // ── Add to My Plants ──────────────────────────────────────
        binding.btnAddToPlants.setOnClickListener {
            val result = currentResult ?: return@setOnClickListener
            val ctx    = requireContext()

            if (PlantRepository.isAlreadySaved(ctx, result.commonName)) {
                binding.btnAddToPlants.visibility  = View.GONE
                binding.tvAlreadyAdded.visibility  = View.VISIBLE
                return@setOnClickListener
            }

            // Disable button while saving
            binding.btnAddToPlants.isEnabled = false
            binding.btnAddToPlants.text = "Saving…"

            viewLifecycleOwner.lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val plant = Plant(
                        name           = result.commonName,
                        status         = "In My Garden",
                        imageUrl       = "",
                        description    = result.description,
                        careInfo       = result.careInfo,
                        family         = result.family,
                        genus          = result.genus,
                        scientificName = result.scientificName,
                        isUserAdded    = true
                    )
                    PlantRepository.savePlant(ctx, plant, currentBitmap)
                }
                // Back on main thread
                binding.btnAddToPlants.visibility = View.GONE
                binding.tvAlreadyAdded.visibility = View.VISIBLE
                com.google.android.material.snackbar.Snackbar.make(
                    binding.root,
                    "✅ ${result.commonName} added to My Plants!",
                    com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                ).setAction("View Plants") {
                    androidx.navigation.fragment.NavHostFragment
                        .findNavController(this@CameraFragment)
                        .navigate(R.id.navigation_plants)
                }.setBackgroundTint(0xFF2E7D32.toInt())
                 .setTextColor(0xFFFFFFFF.toInt())
                 .setActionTextColor(0xFFA5D6A7.toInt())
                 .show()
            }
        }
    }

    // ── Take photo with CameraX ───────────────────────────────────
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "agri_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    output.savedUri?.let { analyzeFromUri(it) }
                }
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Capture failed: ${exc.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // ── Entry point: URI → bitmap → API → result card ─────────────
    private fun analyzeFromUri(uri: Uri) {
        showLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) { loadAndScaleBitmap(uri) }
                if (bitmap == null) {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Could not load image", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val imageBytes = withContext(Dispatchers.IO) { bitmapToBytes(bitmap) }
                val result = withContext(Dispatchers.IO) { callPlantNet(imageBytes) }
                showLoading(false)
                showResultCard(result, bitmap)
            } catch (e: Exception) {
                showLoading(false)
                Log.e(TAG, "Analysis failed", e)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ── Pl@ntNet multipart API call ───────────────────────────────
    private fun callPlantNet(imageBytes: ByteArray): PlantResult {
        // Try multiple organs in order: auto, leaf, flower, fruit, bark
        val organs = listOf("auto", "leaf", "flower", "fruit", "bark")

        for (organ in organs) {
            val result = tryPlantNetOrgan(imageBytes, organ)
            if (result != null && result.confidence >= 5) {
                Log.d(TAG, "PlantNet matched with organ=$organ confidence=${result.confidence}%")
                return result
            }
        }
        return PlantResult.unknown()
    }

    private fun tryPlantNetOrgan(imageBytes: ByteArray, organ: String): PlantResult? {
        return try {
            val url = "https://my-api.plantnet.org/v2/identify/all" +
                    "?api-key=$PLANTNET_API_KEY" +
                    "&lang=en" +
                    "&include-related-images=false" +
                    "&no-reject=false"

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "images", "plant.jpg",
                    imageBytes.toRequestBody("image/jpeg".toMediaType())
                )
                .addFormDataPart("organs", organ)
                .build()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val bodyStr = response.body?.string() ?: return null

            Log.d(TAG, "PlantNet [$organ] HTTP ${response.code}: $bodyStr")

            if (!response.isSuccessful) {
                Log.w(TAG, "PlantNet error for organ=$organ: ${response.code}")
                return null
            }

            parsePlantNetResponse(bodyStr)
        } catch (e: Exception) {
            Log.e(TAG, "PlantNet call failed for organ=$organ", e)
            null
        }
    }

    // ── Parse Pl@ntNet v2 JSON response ───────────────────────────
    private fun parsePlantNetResponse(json: String): PlantResult? {
        return try {
            val root = JSONObject(json)

            // Check for API error in body
            if (root.has("statusCode") && root.getInt("statusCode") != 200) {
                Log.w(TAG, "PlantNet API error: ${root.optString("message")}")
                return null
            }

            val results = root.optJSONArray("results") ?: return null
            if (results.length() == 0) return null

            val best = results.getJSONObject(0)
            val score = best.optDouble("score", 0.0)
            val confidence = (score * 100).toInt()

            val species = best.optJSONObject("species") ?: return null

            // Scientific name
            val scientificName = species.optString("scientificNameWithoutAuthor", "Unknown")

            // Common names
            val commonNamesArr = species.optJSONArray("commonNames")
            val commonName = if (commonNamesArr != null && commonNamesArr.length() > 0)
                commonNamesArr.getString(0).replaceFirstChar { it.uppercase() }
            else scientificName

            // Family
            val family = species.optJSONObject("family")
                ?.optString("scientificNameWithoutAuthor", "—") ?: "—"

            // Genus
            val genus = species.optJSONObject("genus")
                ?.optString("scientificNameWithoutAuthor", "—") ?: "—"

            PlantResult(
                commonName     = commonName,
                scientificName = scientificName,
                family         = family,
                genus          = genus,
                confidence     = confidence,
                description    = buildDescription(commonName, scientificName, family, genus),
                careInfo       = buildCareInfo(family, genus),
                isHealthy      = confidence >= 30
            )
        } catch (e: Exception) {
            Log.e(TAG, "Parse error", e)
            null
        }
    }

    // ── Build smart description from names ────────────────────────
    private fun buildDescription(
        commonName: String, scientificName: String, family: String, genus: String
    ): String {
        val sb = StringBuilder()
        sb.append("$commonName (${scientificName}) is a member of the $family family")
        if (genus != "—") sb.append(", genus $genus")
        sb.append(".\n\n")
        sb.append(getFamilyDescription(family))
        return sb.toString()
    }

    private fun getFamilyDescription(family: String): String = when {
        family.contains("Rosaceae", true)   -> "The rose family includes many fruit trees and ornamental plants. Members often have 5-petalled flowers and are commonly cultivated worldwide."
        family.contains("Solanaceae", true) -> "The nightshade family includes tomatoes, peppers, potatoes, and eggplants — many of which are staple crops. Some members can be toxic."
        family.contains("Poaceae", true)    -> "The grass family is one of the most economically important plant families, including rice, wheat, corn, sugarcane, and bamboo."
        family.contains("Fabaceae", true)   -> "The legume family includes beans, lentils, peas, and many nitrogen-fixing plants crucial for agriculture and soil health."
        family.contains("Asteraceae", true) -> "The daisy family is one of the largest flowering plant families, including sunflowers, daisies, marigolds, and chrysanthemums."
        family.contains("Apiaceae", true)   -> "The carrot family includes many edible plants such as carrots, celery, parsley, coriander, and fennel."
        family.contains("Lamiaceae", true)  -> "The mint family includes many aromatic herbs like basil, oregano, thyme, rosemary, lavender, and mint."
        family.contains("Cucurbitaceae", true) -> "The gourd family includes cucumbers, melons, pumpkins, zucchini, and squash — all important vegetable crops."
        family.contains("Moraceae", true)   -> "The mulberry family includes figs, mulberries, and breadfruit — many valued for their fruits or medicinal uses."
        family.contains("Euphorbiaceae", true) -> "The spurge family is diverse, including rubber trees, castor oil plants, and many succulents. Sap can be toxic in many species."
        else -> "This plant belongs to the $family family. It has unique characteristics that make it identifiable among related species."
    }

    // ── Build care info based on family ──────────────────────────
    private fun buildCareInfo(family: String, genus: String): String = when {
        family.contains("Rosaceae", true)   -> "💧 Water regularly, keep soil moist\n☀️ Full sun (6+ hrs)\n🌡 Temp: 15–25°C\n🪴 Rich, well-draining soil\n✂️ Prune after flowering"
        family.contains("Solanaceae", true) -> "💧 Water deeply, let top soil dry\n☀️ Full sun essential\n🌡 Temp: 18–30°C\n🪴 Fertile, slightly acidic soil\n🌿 Feed with tomato fertiliser"
        family.contains("Poaceae", true)    -> "💧 Moderate watering\n☀️ Full sun to partial shade\n🌡 Temp: 10–35°C\n🪴 Any well-draining soil\n✂️ Trim regularly"
        family.contains("Fabaceae", true)   -> "💧 Moderate, avoid waterlogging\n☀️ Full sun\n🌡 Temp: 15–28°C\n🪴 Well-draining, low nitrogen soil\n🌿 Fixes its own nitrogen"
        family.contains("Asteraceae", true) -> "💧 Water when top inch is dry\n☀️ Full to partial sun\n🌡 Temp: 15–25°C\n🪴 Well-draining, moderately fertile soil"
        family.contains("Lamiaceae", true)  -> "💧 Minimal — drought tolerant\n☀️ Full sun\n🌡 Temp: 15–30°C\n🪴 Sandy, well-draining soil\n✂️ Pinch tips to encourage bushiness"
        family.contains("Cucurbitaceae", true) -> "💧 Consistent watering, never waterlogged\n☀️ Full sun\n🌡 Temp: 18–32°C\n🪴 Rich, well-draining soil\n🌿 Feed every 2 weeks"
        else -> "💧 Water moderately when soil is dry\n☀️ Bright indirect or direct light\n🌡 Temp: 15–28°C\n🪴 Well-draining soil\n🌿 Feed monthly during growing season"
    }

    // ── UI helpers ───────────────────────────────────────────────
    private fun showLoading(show: Boolean) {
        binding.loadingOverlay.visibility = if (show) View.VISIBLE else View.GONE
        binding.resultCard.visibility     = View.GONE
        if (show) binding.loadingText.text = "🔍 Analysing plant with AI…"
    }

    private fun showResultCard(result: PlantResult, bitmap: Bitmap) {
        currentBitmap = bitmap
        currentResult = result

        binding.apply {
            resultCard.visibility    = View.VISIBLE
            capturedImage.setImageBitmap(bitmap)
            resultPlantName.text     = result.commonName
            resultScientificName.text = result.scientificName
            resultFamily.text        = "Family: ${result.family}  •  Genus: ${result.genus}"
            resultConfidence.text    = "AI Confidence: ${result.confidence}%"
            resultDescription.text  = result.description
            resultCareInfo.text     = result.careInfo
            confidenceBar.progress  = result.confidence

            // Reset Add button state for each new result
            btnAddToPlants.visibility = View.VISIBLE
            btnAddToPlants.isEnabled  = true
            tvAlreadyAdded.visibility = View.GONE

            // If already in plants, show that immediately
            if (PlantRepository.isAlreadySaved(requireContext(), result.commonName)) {
                btnAddToPlants.visibility = View.GONE
                tvAlreadyAdded.visibility = View.VISIBLE
            } else {
                btnAddToPlants.text = "🌿  Add to My Plants"
            }

            // Badge colour based on confidence
            val (badgeLabel, badgeColor) = when {
                result.confidence >= 70 -> "✅ High Confidence"  to 0xFF2E7D32.toInt()
                result.confidence >= 35 -> "🟡 Medium Confidence" to 0xFFF57F17.toInt()
                else                    -> "⚠️ Low Confidence"   to 0xFFB71C1C.toInt()
            }
            resultHealthBadge.text = badgeLabel
            resultHealthBadge.setBackgroundColor(badgeColor)

            // Slide-up animation
            resultCard.translationY = 500f
            resultCard.animate().translationY(0f).setDuration(400).start()
        }
    }

    // ── Start CameraX preview ─────────────────────────────────────
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e(TAG, "Camera bind failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // ── Image utilities ───────────────────────────────────────────
    /** Load bitmap and scale to max 1024px on the longest side */
    private fun loadAndScaleBitmap(uri: Uri): Bitmap? {
        return try {
            val stream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val raw = BitmapFactory.decodeStream(stream) ?: return null
            val maxPx = 1024
            val w = raw.width; val h = raw.height
            if (w <= maxPx && h <= maxPx) raw
            else {
                val scale = maxPx.toFloat() / maxOf(w, h)
                Bitmap.createScaledBitmap(raw, (w * scale).toInt(), (h * scale).toInt(), true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "loadBitmap failed", e)
            null
        }
    }

    /** Compress to JPEG bytes — Pl@ntNet needs actual bytes, not base64 */
    private fun bitmapToBytes(bitmap: Bitmap): ByteArray {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        return out.toByteArray()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CameraFragment"
    }
}

// ── Data model ────────────────────────────────────────────────────
data class PlantResult(
    val commonName: String,
    val scientificName: String,
    val family: String,
    val genus: String,
    val confidence: Int,
    val description: String,
    val careInfo: String,
    val isHealthy: Boolean
) {
    companion object {
        fun unknown() = PlantResult(
            commonName     = "Plant Not Identified",
            scientificName = "Could not determine species",
            family         = "—",
            genus          = "—",
            confidence     = 0,
            description    = "The AI could not identify this plant.\n\nTips for better results:\n• Take a clear, well-lit photo\n• Focus on a single leaf or flower\n• Avoid blurry or dark images\n• Try the gallery picker with a sharper photo",
            careInfo       = "—",
            isHealthy      = false
        )
    }
}
