package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seats")
data class SeatEntity(
    @PrimaryKey val id: String, // busId_seatNumber
    val busId: String,
    val seatNumber: String, // U1..U15, L1..L15
    val deck: String, // "UPPER", "LOWER"
    val berthType: String, // "SINGLE_SLEEPER", "DOUBLE_SLEEPER", "SEATER"
    val row: Int, // 1 to 5
    val column: Int, // 1 (Single Left), 2 (Aisle), 3 (Right Inner), 4 (Right Window)
    val price: Double,
    val status: String, // "AVAILABLE", "BOOKED", "LADIES_RESERVED"
    val isLadiesSeat: Boolean = false
)
