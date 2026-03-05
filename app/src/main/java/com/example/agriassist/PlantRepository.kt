package com.example.agriassist

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

/**
 * Persists user-added (AI-scanned) plants using SharedPreferences + local image files.
 */
object PlantRepository {

    private const val PREFS_NAME  = "AgriAssistPlants"
    private const val KEY_PLANTS  = "saved_plants"
    private const val TAG         = "PlantRepository"

    // ── Save a plant (with optional captured bitmap) ──────────────
    fun savePlant(context: Context, plant: Plant, bitmap: Bitmap?): Plant {
        val imagePath = if (bitmap != null) saveBitmapToFile(context, bitmap, plant.name) else ""
        val plantToSave = plant.copy(localImagePath = imagePath, isUserAdded = true)
        val existing = getAllUserPlants(context).toMutableList()
        // Avoid duplicates by name
        existing.removeAll { it.name.equals(plant.name, ignoreCase = true) }
        existing.add(0, plantToSave)   // newest first
        persistPlants(context, existing)
        return plantToSave
    }

    // ── Remove a plant ─────────────────────────────────────────────
    fun removePlant(context: Context, name: String) {
        val updated = getAllUserPlants(context)
            .filter { !it.name.equals(name, ignoreCase = true) }
        persistPlants(context, updated)
    }

    // ── Get all user-added plants ──────────────────────────────────
    fun getAllUserPlants(context: Context): List<Plant> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json  = prefs.getString(KEY_PLANTS, "[]") ?: "[]"
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i -> plantFromJson(arr.getJSONObject(i)) }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read plants", e)
            emptyList()
        }
    }

    // ── Check if a plant name already saved ───────────────────────
    fun isAlreadySaved(context: Context, name: String): Boolean =
        getAllUserPlants(context).any { it.name.equals(name, ignoreCase = true) }

    // ── JSON <-> Plant ────────────────────────────────────────────
    private fun plantToJson(p: Plant) = JSONObject().apply {
        put("name",            p.name)
        put("status",          p.status)
        put("imageUrl",        p.imageUrl)
        put("description",     p.description)
        put("localImagePath",  p.localImagePath)
        put("careInfo",        p.careInfo)
        put("family",          p.family)
        put("genus",           p.genus)
        put("scientificName",  p.scientificName)
        put("isUserAdded",     p.isUserAdded)
    }

    private fun plantFromJson(o: JSONObject) = Plant(
        name           = o.optString("name",           "Unknown"),
        status         = o.optString("status",         "In My Garden"),
        imageUrl       = o.optString("imageUrl",       ""),
        description    = o.optString("description",    ""),
        localImagePath = o.optString("localImagePath", ""),
        careInfo       = o.optString("careInfo",       ""),
        family         = o.optString("family",         ""),
        genus          = o.optString("genus",          ""),
        scientificName = o.optString("scientificName", ""),
        isUserAdded    = o.optBoolean("isUserAdded",   true)
    )

    private fun persistPlants(context: Context, plants: List<Plant>) {
        val arr = JSONArray().apply { plants.forEach { put(plantToJson(it)) } }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_PLANTS, arr.toString()).apply()
    }

    // ── Save bitmap to internal storage ───────────────────────────
    private fun saveBitmapToFile(context: Context, bitmap: Bitmap, name: String): String {
        return try {
            val dir  = File(context.filesDir, "plant_images").apply { mkdirs() }
            val safe = name.replace(Regex("[^a-zA-Z0-9_]"), "_")
            val file = File(dir, "${safe}_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save image", e)
            ""
        }
    }
}

