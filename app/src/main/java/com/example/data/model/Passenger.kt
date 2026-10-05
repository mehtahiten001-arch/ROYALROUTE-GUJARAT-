package com.example.data.model

data class Passenger(
    val name: String = "",
    val age: Int = 0,
    val gender: String = "Male", // "Male" or "Female"
    val seatNumber: String = "",
    val berthType: String = "" // "Upper Sleeper", "Lower Sleeper", "Seater"
)

data class CityStop(
    val cityName: String,
    val state: String,
    val isMajorHub: Boolean = false
)

object RouteConstants {
    const val SUPPORT_PHONE = "9023377492"
    const val PAYMENT_UPI_ID = "9023377492@upi"
    const val PAYMENT_PHONE = "9023377492"
    const val WEBSITE_URL = "www.royalroutegujarat.in"
    const val WHATSAPP_URL = "https://wa.me/919023377492"

    val CITIES = listOf(
        "Dhari",
        "Amreli",
        "Babra",
        "Jasdan",
        "Ahmedabad",
        "Himatnagar",
        "Shamlaji",
        "Kherwara",
        "Udaipur",
        "Nathdwara",
        "Rajkot",
        "Surat"
    )
}
