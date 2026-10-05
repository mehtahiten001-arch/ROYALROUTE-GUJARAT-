package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boarding_points")
data class BoardingPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val busId: String,
    val city: String,
    val pointType: String, // "BOARDING" or "DROPPING"
    val locationName: String,
    val landmark: String,
    val time: String,
    val contactPhone: String = "9023377492"
)
