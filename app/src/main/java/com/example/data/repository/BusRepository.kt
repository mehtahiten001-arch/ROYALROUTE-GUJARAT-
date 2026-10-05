package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.BoardingPointEntity
import com.example.data.local.entity.BookingEntity
import com.example.data.local.entity.BusEntity
import com.example.data.local.entity.SeatEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class BusRepository(private val database: AppDatabase) {
    private val busDao = database.busDao()
    private val seatDao = database.seatDao()
    private val bookingDao = database.bookingDao()
    private val boardingPointDao = database.boardingPointDao()

    val allActiveBuses: Flow<List<BusEntity>> = busDao.getAllActiveBuses()
    val allBusesForAdmin: Flow<List<BusEntity>> = busDao.getAllBusesForAdmin()
    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()

    fun searchBuses(fromCity: String, toCity: String): Flow<List<BusEntity>> {
        return busDao.searchBuses(fromCity, toCity)
    }

    suspend fun getBusById(busId: String): BusEntity? = withContext(Dispatchers.IO) {
        busDao.getBusById(busId)
    }

    fun getSeatsForBus(busId: String): Flow<List<SeatEntity>> {
        return seatDao.getSeatsForBus(busId)
    }

    fun getBoardingPoints(busId: String): Flow<List<BoardingPointEntity>> {
        return boardingPointDao.getPointsForBus(busId, "BOARDING")
    }

    fun getDroppingPoints(busId: String): Flow<List<BoardingPointEntity>> {
        return boardingPointDao.getPointsForBus(busId, "DROPPING")
    }

    fun findBookings(query: String): Flow<List<BookingEntity>> {
        return bookingDao.findBookings(query)
    }

    suspend fun getBookingById(bookingId: String): BookingEntity? = withContext(Dispatchers.IO) {
        bookingDao.getBookingById(bookingId)
    }

    suspend fun confirmBooking(booking: BookingEntity, seatNumbersList: List<String>) = withContext(Dispatchers.IO) {
        bookingDao.insertBooking(booking)
        seatDao.updateSeatsStatus(booking.busId, seatNumbersList, "BOOKED")
        // Update remaining seats count on bus
        val bus = busDao.getBusById(booking.busId)
        if (bus != null) {
            val newAvailable = maxOf(0, bus.availableSeatsCount - seatNumbersList.size)
            busDao.updateBus(bus.copy(availableSeatsCount = newAvailable))
        }
    }

    suspend fun cancelBooking(bookingId: String): Double = withContext(Dispatchers.IO) {
        val booking = bookingDao.getBookingById(bookingId) ?: return@withContext 0.0
        val refundAmount = booking.totalAmount * 0.85 // 15% cancellation charge
        bookingDao.cancelBooking(bookingId, refundAmount)

        val seatList = booking.seatNumbers.split(",").map { it.trim() }
        seatDao.releaseSeats(booking.busId, seatList)

        val bus = busDao.getBusById(booking.busId)
        if (bus != null) {
            busDao.updateBus(bus.copy(availableSeatsCount = bus.availableSeatsCount + seatList.size))
        }
        refundAmount
    }

    suspend fun insertBus(bus: BusEntity) = withContext(Dispatchers.IO) {
        busDao.insertBus(bus)
        // Generate seats for newly added bus
        val seats = generateSeatsForBus(bus.id, bus.sleeperPrice)
        seatDao.insertSeats(seats)
    }

    suspend fun updateBus(bus: BusEntity) = withContext(Dispatchers.IO) {
        busDao.updateBus(bus)
    }

    suspend fun deleteBus(busId: String) = withContext(Dispatchers.IO) {
        busDao.deleteBus(busId)
    }

    suspend fun updateSeatStatus(seat: SeatEntity, newStatus: String) = withContext(Dispatchers.IO) {
        seatDao.updateSeat(seat.copy(status = newStatus))
    }

    suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        val existing = busDao.getBusById("BUS-RRG-101")
        if (existing == null) {
            // Seed Luxury Buses for Dhari <-> Nathdwara and other Gujarat-Rajasthan routes
            val buses = listOf(
                BusEntity(
                    id = "BUS-RRG-101",
                    name = "Royal BharatBenz 2+1 AC Sleeper",
                    busNumber = "GJ-14-RR-9023",
                    busType = "AC Sleeper (2+1)",
                    fromCity = "Dhari",
                    toCity = "Nathdwara",
                    departureTime = "08:30 PM",
                    arrivalTime = "07:30 AM",
                    duration = "11h 00m",
                    sleeperPrice = 1150.0,
                    seaterPrice = 850.0,
                    availableSeatsCount = 22,
                    rating = 4.9,
                    amenities = "WiFi,Charging Point,Blanket,Water Bottle,Reading Lamp,CCTV,Emergency Exit,Live GPS Tracking"
                ),
                BusEntity(
                    id = "BUS-RRG-102",
                    name = "Royal Volvo Multi-Axle AC Sleeper",
                    busNumber = "GJ-14-RR-9024",
                    busType = "Volvo Multi-Axle AC Sleeper",
                    fromCity = "Dhari",
                    toCity = "Nathdwara",
                    departureTime = "09:30 PM",
                    arrivalTime = "08:00 AM",
                    duration = "10h 30m",
                    sleeperPrice = 1250.0,
                    seaterPrice = 900.0,
                    availableSeatsCount = 19,
                    rating = 4.8,
                    amenities = "WiFi,Charging Point,Blanket,Water Bottle,Pillow,CCTV,GPS Tracking,Clean Washroom Stop"
                ),
                BusEntity(
                    id = "BUS-RRG-201",
                    name = "Royal Shrinathji Express AC Sleeper",
                    busNumber = "GJ-14-RR-9025",
                    busType = "AC Sleeper (2+1)",
                    fromCity = "Nathdwara",
                    toCity = "Dhari",
                    departureTime = "07:45 PM",
                    arrivalTime = "06:45 AM",
                    duration = "11h 00m",
                    sleeperPrice = 1150.0,
                    seaterPrice = 850.0,
                    availableSeatsCount = 20,
                    rating = 4.9,
                    amenities = "WiFi,Charging Point,Blanket,Water Bottle,Reading Lamp,CCTV,Emergency Exit,Live GPS Tracking"
                ),
                BusEntity(
                    id = "BUS-RRG-202",
                    name = "Royal Maharaja Platinum Sleeper",
                    busNumber = "GJ-14-RR-9026",
                    busType = "Volvo Multi-Axle AC Sleeper",
                    fromCity = "Nathdwara",
                    toCity = "Dhari",
                    departureTime = "08:45 PM",
                    arrivalTime = "07:30 AM",
                    duration = "10h 45m",
                    sleeperPrice = 1250.0,
                    seaterPrice = 900.0,
                    availableSeatsCount = 18,
                    rating = 4.9,
                    amenities = "WiFi,Charging Point,Blanket,Water Bottle,Air Suspension,CCTV,Emergency Exit,Snacks"
                ),
                BusEntity(
                    id = "BUS-RRG-301",
                    name = "Royal Amreli - Nathdwara Superfast",
                    busNumber = "GJ-14-RR-9027",
                    busType = "AC Sleeper 2+1",
                    fromCity = "Amreli",
                    toCity = "Nathdwara",
                    departureTime = "09:15 PM",
                    arrivalTime = "07:30 AM",
                    duration = "10h 15m",
                    sleeperPrice = 1100.0,
                    seaterPrice = 800.0,
                    availableSeatsCount = 24,
                    rating = 4.7,
                    amenities = "Charging Point,Blanket,Water Bottle,Reading Lamp,CCTV,Emergency Exit"
                ),
                BusEntity(
                    id = "BUS-RRG-401",
                    name = "Royal Ahmedabad - Nathdwara Royal Coach",
                    busNumber = "GJ-14-RR-9028",
                    busType = "BharatBenz AC Sleeper",
                    fromCity = "Ahmedabad",
                    toCity = "Nathdwara",
                    departureTime = "11:00 PM",
                    arrivalTime = "06:30 AM",
                    duration = "07h 30m",
                    sleeperPrice = 950.0,
                    seaterPrice = 700.0,
                    availableSeatsCount = 16,
                    rating = 4.8,
                    amenities = "WiFi,Charging Point,Blanket,Water Bottle,Reading Lamp,CCTV"
                )
            )
            busDao.insertBuses(buses)

            // Seed seats for each bus
            for (bus in buses) {
                val seats = generateSeatsForBus(bus.id, bus.sleeperPrice)
                seatDao.insertSeats(seats)
            }

            // Seed Boarding and Dropping points
            val boardingPoints = listOf(
                BoardingPointEntity(busId = "BUS-RRG-101", city = "Dhari", pointType = "BOARDING", locationName = "Dhari ST Bus Stand Gate", landmark = "Opposite Main ST Bus Stand, Dhari", time = "08:30 PM", contactPhone = "9023377492"),
                BoardingPointEntity(busId = "BUS-RRG-101", city = "Dhari", pointType = "BOARDING", locationName = "Royal Route Travels Office", landmark = "Station Road, Near Tower Chowk, Dhari", time = "08:45 PM", contactPhone = "9023377492"),
                BoardingPointEntity(busId = "BUS-RRG-101", city = "Amreli", pointType = "BOARDING", locationName = "Amreli - Chital Road Circle", landmark = "Near Patel Petrol Pump, Amreli Bypass", time = "09:30 PM", contactPhone = "9023377492"),
                BoardingPointEntity(busId = "BUS-RRG-101", city = "Ahmedabad", pointType = "BOARDING", locationName = "Ahmedabad CTM Express Highway", landmark = "Near Toll Plaza, CTM Cross Road", time = "02:00 AM", contactPhone = "9023377492"),
                BoardingPointEntity(busId = "BUS-RRG-101", city = "Nathdwara", pointType = "DROPPING", locationName = "Shrinathji Mandir VIP Gate", landmark = "Near Shrinathji Temple Darshan Hall", time = "07:00 AM", contactPhone = "9023377492"),
                BoardingPointEntity(busId = "BUS-RRG-101", city = "Nathdwara", pointType = "DROPPING", locationName = "Private Luxury Bus Stand", landmark = "Udaipur Highway Road, Nathdwara", time = "07:30 AM", contactPhone = "9023377492"),

                // For BUS-RRG-102
                BoardingPointEntity(busId = "BUS-RRG-102", city = "Dhari", pointType = "BOARDING", locationName = "Royal Route Travels Office", landmark = "Station Road, Near Tower Chowk, Dhari", time = "09:30 PM", contactPhone = "9023377492"),
                BoardingPointEntity(busId = "BUS-RRG-102", city = "Nathdwara", pointType = "DROPPING", locationName = "Shrinathji Mandir VIP Gate", landmark = "Near Darshan Parking, Nathdwara", time = "08:00 AM", contactPhone = "9023377492"),

                // For Nathdwara to Dhari (BUS-RRG-201)
                BoardingPointEntity(busId = "BUS-RRG-201", city = "Nathdwara", pointType = "BOARDING", locationName = "Shrinathji Mandir Bus Stand", landmark = "Near VIP Parking Gate No 1", time = "07:45 PM", contactPhone = "9023377492"),
                BoardingPointEntity(busId = "BUS-RRG-201", city = "Nathdwara", pointType = "BOARDING", locationName = "Nathdwara Private Bus Stand", landmark = "Near Sukher Road Crossing", time = "08:15 PM", contactPhone = "9023377492"),
                BoardingPointEntity(busId = "BUS-RRG-201", city = "Dhari", pointType = "DROPPING", locationName = "Dhari ST Bus Stand Chowk", landmark = "Main ST Depot Gate", time = "06:45 AM", contactPhone = "9023377492"),
                BoardingPointEntity(busId = "BUS-RRG-201", city = "Dhari", pointType = "DROPPING", locationName = "Royal Route Office Dhari", landmark = "Station Road, Dhari", time = "07:00 AM", contactPhone = "9023377492"),

                // For Nathdwara to Dhari (BUS-RRG-202)
                BoardingPointEntity(busId = "BUS-RRG-202", city = "Nathdwara", pointType = "BOARDING", locationName = "Shrinathji Mandir Bus Stand", landmark = "Near VIP Parking Gate", time = "08:45 PM", contactPhone = "9023377492"),
                BoardingPointEntity(busId = "BUS-RRG-202", city = "Dhari", pointType = "DROPPING", locationName = "Royal Route Office Dhari", landmark = "Station Road, Dhari", time = "07:30 AM", contactPhone = "9023377492")
            )
            boardingPointDao.insertPoints(boardingPoints)

            // Seed one initial sample confirmed booking for testing Track Booking with 9023377492
            val todayDate = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date())
            val sampleBooking = BookingEntity(
                bookingId = "RRG-902337-8812",
                pnrNumber = "RRG902337",
                busId = "BUS-RRG-101",
                busName = "Royal BharatBenz 2+1 AC Sleeper",
                busNumber = "GJ-14-RR-9023",
                fromCity = "Dhari",
                toCity = "Nathdwara",
                journeyDate = todayDate,
                departureTime = "08:30 PM",
                arrivalTime = "07:30 AM",
                boardingPoint = "Royal Route Travels Office, Dhari (08:45 PM)",
                droppingPoint = "Shrinathji Mandir VIP Gate, Nathdwara (07:00 AM)",
                seatNumbers = "U3, U4",
                passengersSummary = "Hiten Mehta (29, M - U3), Bhavna Mehta (27, F - U4)",
                primaryPassengerName = "Hiten Mehta",
                contactPhone = "9023377492",
                contactEmail = "royalroutegujarat@gmail.com",
                baseFare = 2300.0,
                taxAmount = 115.0,
                totalAmount = 2415.0,
                paymentMethod = "UPI (9023377492@upi)",
                paymentUpiId = "9023377492@upi",
                paymentStatus = "CONFIRMED",
                bookingTimestamp = System.currentTimeMillis() - 3600000L
            )
            bookingDao.insertBooking(sampleBooking)
        }
    }

    private fun generateSeatsForBus(busId: String, basePrice: Double): List<SeatEntity> {
        val seats = mutableListOf<SeatEntity>()

        // UPPER DECK: U1 to U15
        // Row 1: U1 (single left), Aisle, U2 (right inner), U3 (right window)
        // Row 2: U4 (single left), Aisle, U5 (right inner), U6 (right window)
        // Row 3: U7 (single left), Aisle, U8 (right inner), U9 (right window)
        // Row 4: U10 (single left), Aisle, U11 (right inner), U12 (right window)
        // Row 5: U13 (single left), Aisle, U14 (right inner), U15 (right window)
        for (r in 1..5) {
            val singleNum = (r - 1) * 3 + 1
            val doubleNum1 = singleNum + 1
            val doubleNum2 = singleNum + 2

            // Single Left Sleeper (premium window view)
            seats.add(
                SeatEntity(
                    id = "${busId}_U$singleNum",
                    busId = busId,
                    seatNumber = "U$singleNum",
                    deck = "UPPER",
                    berthType = "SINGLE_SLEEPER",
                    row = r,
                    column = 1,
                    price = basePrice + 100.0, // slight premium for single sleeper
                    status = if (singleNum == 1) "BOOKED" else if (singleNum == 7) "LADIES_RESERVED" else "AVAILABLE",
                    isLadiesSeat = (singleNum == 7)
                )
            )

            // Right Double Sleeper (inner)
            seats.add(
                SeatEntity(
                    id = "${busId}_U$doubleNum1",
                    busId = busId,
                    seatNumber = "U$doubleNum1",
                    deck = "UPPER",
                    berthType = "DOUBLE_SLEEPER",
                    row = r,
                    column = 3,
                    price = basePrice,
                    status = if (doubleNum1 == 2) "BOOKED" else "AVAILABLE",
                    isLadiesSeat = false
                )
            )

            // Right Double Sleeper (window)
            seats.add(
                SeatEntity(
                    id = "${busId}_U$doubleNum2",
                    busId = busId,
                    seatNumber = "U$doubleNum2",
                    deck = "UPPER",
                    berthType = "DOUBLE_SLEEPER",
                    row = r,
                    column = 4,
                    price = basePrice,
                    status = if (doubleNum2 == 3) "BOOKED" else "AVAILABLE",
                    isLadiesSeat = false
                )
            )
        }

        // LOWER DECK: L1 to L15
        for (r in 1..5) {
            val singleNum = (r - 1) * 3 + 1
            val doubleNum1 = singleNum + 1
            val doubleNum2 = singleNum + 2

            seats.add(
                SeatEntity(
                    id = "${busId}_L$singleNum",
                    busId = busId,
                    seatNumber = "L$singleNum",
                    deck = "LOWER",
                    berthType = "SINGLE_SLEEPER",
                    row = r,
                    column = 1,
                    price = basePrice + 50.0,
                    status = if (singleNum == 4) "BOOKED" else if (singleNum == 10) "LADIES_RESERVED" else "AVAILABLE",
                    isLadiesSeat = (singleNum == 10)
                )
            )

            seats.add(
                SeatEntity(
                    id = "${busId}_L$doubleNum1",
                    busId = busId,
                    seatNumber = "L$doubleNum1",
                    deck = "LOWER",
                    berthType = "DOUBLE_SLEEPER",
                    row = r,
                    column = 3,
                    price = basePrice - 50.0,
                    status = if (doubleNum1 == 8) "BOOKED" else "AVAILABLE",
                    isLadiesSeat = false
                )
            )

            seats.add(
                SeatEntity(
                    id = "${busId}_L$doubleNum2",
                    busId = busId,
                    seatNumber = "L$doubleNum2",
                    deck = "LOWER",
                    berthType = "DOUBLE_SLEEPER",
                    row = r,
                    column = 4,
                    price = basePrice - 50.0,
                    status = if (doubleNum2 == 9) "BOOKED" else "AVAILABLE",
                    isLadiesSeat = false
                )
            )
        }

        return seats
    }
}
