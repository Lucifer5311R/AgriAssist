package com.example.agriassist

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    val name: String,
    val status: String,
    val imageUrl: String,
    val description: String,
    val localImagePath: String = "",   // local file path for scanned plants
    val careInfo: String = "",
    val family: String = "",
    val genus: String = "",
    val scientificName: String = "",
    val isUserAdded: Boolean = false   // true = added from AI scan
) : Parcelable
