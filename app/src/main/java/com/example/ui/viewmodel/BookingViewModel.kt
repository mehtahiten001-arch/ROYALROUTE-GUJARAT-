package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.BoardingPointEntity
import com.example.data.local.entity.BookingEntity
import com.example.data.local.entity.BusEntity
import com.example.data.local.entity.SeatEntity
import com.example.data.model.Passenger
import com.example.data.model.RouteConstants
import com.example.data.repository.BusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class AppScreen {
    HOME,
    BUS_SEARCH,
    SEAT_SELECTION,
    BOARDING_DROPPING,
    PASSENGER_DETAILS,
    PAYMENT,
    TICKET,
    TRACK_BOOKING,
    SUPPORT,
    ADMIN
}

class BookingViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = BusRepository(database)

    // Navigation state
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<AppScreen>()

    // Search query parameters
    private val _fromCity = MutableStateFlow("Dhari")
    val fromCity: StateFlow<String> = _fromCity.asStateFlow()

    private val _toCity = MutableStateFlow("Nathdwara")
    val toCity: StateFlow<String> = _toCity.asStateFlow()

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    private val _journeyDate = MutableStateFlow(dateFormat.format(Date()))
    val journeyDate: StateFlow<String> = _journeyDate.asStateFlow()

    private val _busFilter = MutableStateFlow("ALL") // "ALL", "AC", "NON_AC", "SLEEPER"
    val busFilter: StateFlow<String> = _busFilter.asStateFlow()

    // Bus search results
    val searchResults: StateFlow<List<BusEntity>> = _fromCity.flatMapLatest { from ->
        _toCity.flatMapLatest { to ->
            repository.searchBuses(from, to)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBuses: StateFlow<List<BusEntity>> = repository.allActiveBuses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminBuses: StateFlow<List<BusEntity>> = repository.allBusesForAdmin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookings: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently selected bus for booking
    private val _selectedBus = MutableStateFlow<BusEntity?>(null)
    val selectedBus: StateFlow<BusEntity?> = _selectedBus.asStateFlow()

    // Seats for the selected bus
    val currentBusSeats: StateFlow<List<SeatEntity>> = _selectedBus.flatMapLatest { bus ->
        if (bus != null) {
            repository.getSeatsForBus(bus.id)
        } else {
            MutableStateFlow(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected seats by user
    private val _selectedSeats = MutableStateFlow<List<SeatEntity>>(emptyList())
    val selectedSeats: StateFlow<List<SeatEntity>> = _selectedSeats.asStateFlow()

    // Boarding and Dropping points
    val boardingPoints: StateFlow<List<BoardingPointEntity>> = _selectedBus.flatMapLatest { bus ->
        if (bus != null) repository.getBoardingPoints(bus.id) else MutableStateFlow(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val droppingPoints: StateFlow<List<BoardingPointEntity>> = _selectedBus.flatMapLatest { bus ->
        if (bus != null) repository.getDroppingPoints(bus.id) else MutableStateFlow(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedBoardingPoint = MutableStateFlow<BoardingPointEntity?>(null)
    val selectedBoardingPoint: StateFlow<BoardingPointEntity?> = _selectedBoardingPoint.asStateFlow()

    private val _selectedDroppingPoint = MutableStateFlow<BoardingPointEntity?>(null)
    val selectedDroppingPoint: StateFlow<BoardingPointEntity?> = _selectedDroppingPoint.asStateFlow()

    // Passengers list
    private val _passengers = MutableStateFlow<List<Passenger>>(emptyList())
    val passengers: StateFlow<List<Passenger>> = _passengers.asStateFlow()

    // Contact info
    private val _contactPhone = MutableStateFlow(RouteConstants.SUPPORT_PHONE)
    val contactPhone: StateFlow<String> = _contactPhone.asStateFlow()

    private val _contactEmail = MutableStateFlow("mehtahiten001@gmail.com")
    val contactEmail: StateFlow<String> = _contactEmail.asStateFlow()

    // Completed / active e-ticket
    private val _activeBooking = MutableStateFlow<BookingEntity?>(null)
    val activeBooking: StateFlow<BookingEntity?> = _activeBooking.asStateFlow()

    // Track booking
    private val _trackSearchQuery = MutableStateFlow("")
    val trackSearchQuery: StateFlow<String> = _trackSearchQuery.asStateFlow()

    val trackedBookings: StateFlow<List<BookingEntity>> = _trackSearchQuery.flatMapLatest { q ->
        if (q.isBlank()) repository.allBookings else repository.findBookings(q.trim())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin unlocked state
    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun handleBack(): Boolean {
        return if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
            true
        } else {
            false
        }
    }

    fun setFromCity(city: String) {
        _fromCity.value = city
    }

    fun setToCity(city: String) {
        _toCity.value = city
    }

    fun swapCities() {
        val temp = _fromCity.value
        _fromCity.value = _toCity.value
        _toCity.value = temp
    }

    fun setJourneyDate(dateStr: String) {
        _journeyDate.value = dateStr
    }

    fun setDateToday() {
        _journeyDate.value = dateFormat.format(Date())
    }

    fun setDateTomorrow() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        _journeyDate.value = dateFormat.format(cal.time)
    }

    fun setBusFilter(filter: String) {
        _busFilter.value = filter
    }

    fun selectBus(bus: BusEntity) {
        _selectedBus.value = bus
        _selectedSeats.value = emptyList()
        _selectedBoardingPoint.value = null
        _selectedDroppingPoint.value = null
        _passengers.value = emptyList()
        navigateTo(AppScreen.SEAT_SELECTION)
    }

    fun toggleSeatSelection(seat: SeatEntity) {
        if (seat.status == "BOOKED") return

        val current = _selectedSeats.value.toMutableList()
        val exists = current.find { it.id == seat.id }
        if (exists != null) {
            current.remove(exists)
        } else {
            if (current.size >= 6) {
                return // Max 6 seats per booking
            }
            current.add(seat)
        }
        _selectedSeats.value = current

        // Sync passenger input list
        val updatedPassengers = current.mapIndexed { index, s ->
            val existing = _passengers.value.getOrNull(index)
            Passenger(
                name = existing?.name ?: "",
                age = existing?.age ?: 25,
                gender = existing?.gender ?: if (s.isLadiesSeat) "Female" else "Male",
                seatNumber = s.seatNumber,
                berthType = if (s.deck == "UPPER") "Upper Berth" else "Lower Berth"
            )
        }
        _passengers.value = updatedPassengers
    }

    fun selectBoardingPoint(point: BoardingPointEntity) {
        _selectedBoardingPoint.value = point
    }

    fun selectDroppingPoint(point: BoardingPointEntity) {
        _selectedDroppingPoint.value = point
    }

    fun updatePassenger(index: Int, name: String, age: Int, gender: String) {
        val current = _passengers.value.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(name = name, age = age, gender = gender)
            _passengers.value = current
        }
    }

    fun updateContactInfo(phone: String, email: String) {
        _contactPhone.value = phone
        _contactEmail.value = email
    }

    fun calculateTotalFare(): Triple<Double, Double, Double> {
        val bus = _selectedBus.value ?: return Triple(0.0, 0.0, 0.0)
        val baseFare = _selectedSeats.value.sumOf { it.price }
        val gst = baseFare * 0.05 // 5% GST
        val total = baseFare + gst
        return Triple(baseFare, gst, total)
    }

    fun completeBooking(paymentMethod: String, onCompleted: (BookingEntity) -> Unit) {
        val bus = _selectedBus.value ?: return
        val seats = _selectedSeats.value
        if (seats.isEmpty()) return

        val (base, gst, total) = calculateTotalFare()
        val randomSuffix = Random.nextInt(1000, 9999)
        val bookingId = "RRG-${RouteConstants.PAYMENT_PHONE.takeLast(6)}-$randomSuffix"
        val pnr = "RRG${Random.nextInt(100000, 999999)}"

        val seatNumbersStr = seats.joinToString(", ") { it.seatNumber }
        val passengersSummaryStr = _passengers.value.joinToString("; ") {
            "${it.name} (${it.age}, ${it.gender.take(1)} - ${it.seatNumber})"
        }
        val primaryPassenger = _passengers.value.firstOrNull()?.name?.ifBlank { "Royal Passenger" } ?: "Royal Passenger"

        val bp = _selectedBoardingPoint.value?.let { "${it.locationName} (${it.time})" }
            ?: "${bus.fromCity} Bus Stand (${bus.departureTime})"
        val dp = _selectedDroppingPoint.value?.let { "${it.locationName} (${it.time})" }
            ?: "${bus.toCity} Bus Stand (${bus.arrivalTime})"

        val newBooking = BookingEntity(
            bookingId = bookingId,
            pnrNumber = pnr,
            busId = bus.id,
            busName = bus.name,
            busNumber = bus.busNumber,
            fromCity = bus.fromCity,
            toCity = bus.toCity,
            journeyDate = _journeyDate.value,
            departureTime = bus.departureTime,
            arrivalTime = bus.arrivalTime,
            boardingPoint = bp,
            droppingPoint = dp,
            seatNumbers = seatNumbersStr,
            passengersSummary = passengersSummaryStr,
            primaryPassengerName = primaryPassenger,
            contactPhone = _contactPhone.value,
            contactEmail = _contactEmail.value,
            baseFare = base,
            taxAmount = gst,
            totalAmount = total,
            paymentMethod = paymentMethod,
            paymentUpiId = RouteConstants.PAYMENT_UPI_ID,
            paymentStatus = "CONFIRMED"
        )

        viewModelScope.launch {
            repository.confirmBooking(newBooking, seats.map { it.seatNumber })
            _activeBooking.value = newBooking
            _selectedSeats.value = emptyList()
            onCompleted(newBooking)
        }
    }

    fun viewBookingDetails(booking: BookingEntity) {
        _activeBooking.value = booking
        navigateTo(AppScreen.TICKET)
    }

    fun cancelActiveBooking(bookingId: String, onRefundCalculated: (Double) -> Unit) {
        viewModelScope.launch {
            val refund = repository.cancelBooking(bookingId)
            _activeBooking.value = _activeBooking.value?.copy(
                paymentStatus = "CANCELLED",
                cancellationRefund = refund
            )
            onRefundCalculated(refund)
        }
    }

    fun setTrackSearchQuery(query: String) {
        _trackSearchQuery.value = query
    }

    fun unlockAdmin(passcode: String): Boolean {
        if (passcode == "9023" || passcode == "admin" || passcode == "royal") {
            _isAdminUnlocked.value = true
            return true
        }
        return false
    }

    fun lockAdmin() {
        _isAdminUnlocked.value = false
    }

    fun addNewBus(bus: BusEntity) {
        viewModelScope.launch {
            repository.insertBus(bus)
        }
    }

    fun updateBusDetails(bus: BusEntity) {
        viewModelScope.launch {
            repository.updateBus(bus)
        }
    }

    fun deleteBus(busId: String) {
        viewModelScope.launch {
            repository.deleteBus(busId)
        }
    }

    fun toggleSeatStatus(seat: SeatEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateSeatStatus(seat, newStatus)
        }
    }
}
