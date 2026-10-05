package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "buses")
data class BusEntity(
    @PrimaryKey val id: String,
    val name: String,
    val busNumber: String,
    val busType: String, // "AC Sleeper 2+1", "BharatBenz AC Sleeper", "Volvo Multi-Axle", "Non-AC Sleeper"
    val fromCity: String,
    val toCity: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val sleeperPrice: Double,
    val seaterPrice: Double,
    val availableSeatsCount: Int,
    val rating: Double,
    val amenities: String, // Comma-separated: WiFi,Charging,Blanket,Water,CCTV,GPS
    val liveStatus: String = "On Schedule",
    val isActive: Boolean = true
)
