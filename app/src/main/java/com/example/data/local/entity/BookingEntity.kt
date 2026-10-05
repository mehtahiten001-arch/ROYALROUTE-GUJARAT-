package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val bookingId: String, // e.g. "RRG-902337-1234"
    val pnrNumber: String,
    val busId: String,
    val busName: String,
    val busNumber: String,
    val fromCity: String,
    val toCity: String,
    val journeyDate: String,
    val departureTime: String,
    val arrivalTime: String,
    val boardingPoint: String,
    val droppingPoint: String,
    val seatNumbers: String, // "U3, U4"
    val passengersSummary: String, // "John (28, M), Jane (26, F)"
    val primaryPassengerName: String,
    val contactPhone: String,
    val contactEmail: String,
    val baseFare: Double,
    val taxAmount: Double,
    val totalAmount: Double,
    val paymentMethod: String, // "UPI (9023377492)", "Card", "Net Banking"
    val paymentUpiId: String = "9023377492@upi",
    val paymentStatus: String = "CONFIRMED", // "CONFIRMED", "CANCELLED"
    val bookingTimestamp: Long = System.currentTimeMillis(),
    val cancellationRefund: Double = 0.0
)
