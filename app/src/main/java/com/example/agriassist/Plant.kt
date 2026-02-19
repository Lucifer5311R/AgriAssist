package com.example.agriassist

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    val name: String,
    val status: String,
    val imageUrl: String,
    val description: String
) : Parcelable
