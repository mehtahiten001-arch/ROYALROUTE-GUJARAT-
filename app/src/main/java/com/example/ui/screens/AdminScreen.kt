package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BookingEntity
import com.example.data.local.entity.BusEntity
import com.example.data.local.entity.SeatEntity
import com.example.data.model.RouteConstants
import com.example.ui.components.SleeperBerthView
import com.example.ui.theme.RoyalCrimson
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.viewmodel.BookingViewModel
import kotlin.random.Random

@Composable
fun AdminScreen(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsState()
    val adminBuses by viewModel.adminBuses.collectAsState()
    val allBookings by viewModel.allBookings.collectAsState()

    var passcode by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Fleet, 1: Bookings, 2: Seat Inventory, 3: Reports

    // Add Bus Dialog State
    var showAddBusDialog by remember { mutableStateOf(false) }

    if (!isAdminUnlocked) {
        // Login Screen
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF6F8FA))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(RoyalNavyDark, RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Royal Route Admin Portal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = RoyalNavyDark
                    )

                    Text(
                        text = "Manage buses, routes, fares, and bookings",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = passcode,
                        onValueChange = { passcode = it },
                        label = { Text("Enter Admin PIN / Passcode") },
                        placeholder = { Text("Hint: 9023") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (viewModel.unlockAdmin(passcode.trim())) {
                                Toast.makeText(context, "Welcome, Admin!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Invalid PIN. Use 9023 or admin", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavyDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_login_btn")
                    ) {
                        Text(text = "Unlock Admin Portal", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = {
                            passcode = "9023"
                            viewModel.unlockAdmin("9023")
                        }
                    ) {
                        Text(text = "Quick Access (PIN: 9023)", fontSize = 12.sp, color = RoyalGold)
                    }
                }
            }
        }
        return
    }

    // Unlocked Admin Panel
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FA))
    ) {
        // Admin Top Bar
        Surface(
            color = RoyalNavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "👑 Royal Admin Control",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Official Route & Booking Management",
                        fontSize = 11.sp,
                        color = RoyalGold
                    )
                }

                IconButton(onClick = { viewModel.lockAdmin() }) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = "Lock Admin",
                        tint = Color.White
                    )
                }
            }
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = RoyalNavyDark,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = RoyalNavyDark,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Fleet & Routes (${adminBuses.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Bookings (${allBookings.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Seat Inventory", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Reports & Revenue", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        // Tab Content
        when (selectedTab) {
            0 -> AdminFleetTab(
                buses = adminBuses,
                onAddBus = { showAddBusDialog = true },
                onDeleteBus = { viewModel.deleteBus(it) }
            )
            1 -> AdminBookingsTab(bookings = allBookings)
            2 -> AdminSeatInventoryTab(viewModel = viewModel, buses = adminBuses)
            3 -> AdminReportsTab(bookings = allBookings, buses = adminBuses)
        }
    }

    if (showAddBusDialog) {
        AddBusDialog(
            onDismiss = { showAddBusDialog = false },
            onConfirm = { newBus ->
                viewModel.addNewBus(newBus)
                showAddBusDialog = false
                Toast.makeText(context, "New Bus Added: ${newBus.name}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun AdminFleetTab(
    buses: List<BusEntity>,
    onAddBus: () -> Unit,
    onDeleteBus: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = onAddBus,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("admin_add_bus_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavyDark)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = RoyalGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add New Luxury Coach / Route", fontWeight = FontWeight.Bold)
            }
        }

        items(buses, key = { it.id }) { bus ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = bus.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = RoyalNavyDark)
                            Text(text = "${bus.busType} • ${bus.busNumber}", fontSize = 12.sp, color = Color.Gray)
                        }

                        IconButton(onClick = { onDeleteBus(bus.id) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoyalCrimson)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${bus.fromCity} ➔ ${bus.toCity}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${bus.departureTime} - ${bus.arrivalTime}",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Fare: ₹${bus.sleeperPrice.toInt()}",
                            fontWeight = FontWeight.Bold,
                            color = RoyalCrimson,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Seats left: ${bus.availableSeatsCount}",
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AdminBookingsTab(bookings: List<BookingEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Passenger Manifest & Recent Bookings (${bookings.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = RoyalNavyDark
            )
        }

        if (bookings.isEmpty()) {
            item {
                Text(text = "No bookings registered yet.", color = Color.Gray, modifier = Modifier.padding(top = 20.dp))
            }
        } else {
            items(bookings, key = { it.bookingId }) { booking ->
                val isCancelled = booking.paymentStatus == "CANCELLED"
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = booking.bookingId, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RoyalNavyDark)
                                Text(text = booking.primaryPassengerName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCancelled) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                            ) {
                                Text(
                                    text = booking.paymentStatus,
                                    color = if (isCancelled) RoyalCrimson else Color(0xFF2E7D32),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${booking.fromCity} ➔ ${booking.toCity}  •  ${booking.journeyDate}",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "Seats: ${booking.seatNumbers}  •  Paid: ₹${booking.totalAmount.toInt()} via ${booking.paymentMethod}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RoyalNavyDark
                        )
                        Text(
                            text = "Contact: ${booking.contactPhone}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AdminSeatInventoryTab(
    viewModel: BookingViewModel,
    buses: List<BusEntity>
) {
    var selectedBusId by remember { mutableStateOf(buses.firstOrNull()?.id ?: "") }
    val seats by viewModel.currentBusSeats.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Live Coach Seat Inventory & Blocking",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = RoyalNavyDark
            )
            Text(
                text = "Tap any berth to toggle status (Available ⇄ Booked ⇄ Ladies)",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Current Coach Berths Overview",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    seats.take(12).forEach { seat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Berth ${seat.seatNumber} (${seat.deck})",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (seat.status) {
                                    "AVAILABLE" -> Color(0xFFE8F5E9)
                                    "BOOKED" -> Color(0xFFFFEBEE)
                                    else -> Color(0xFFFCE4EC)
                                },
                                modifier = Modifier.clickable {
                                    val nextStatus = when (seat.status) {
                                        "AVAILABLE" -> "BOOKED"
                                        "BOOKED" -> "LADIES_RESERVED"
                                        else -> "AVAILABLE"
                                    }
                                    viewModel.toggleSeatStatus(seat, nextStatus)
                                }
                            ) {
                                Text(
                                    text = "${seat.status} (Tap to change)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (seat.status) {
                                        "AVAILABLE" -> Color(0xFF2E7D32)
                                        "BOOKED" -> RoyalCrimson
                                        else -> Color(0xFFC2185B)
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AdminReportsTab(
    bookings: List<BookingEntity>,
    buses: List<BusEntity>
) {
    val totalRevenue = bookings.filter { it.paymentStatus != "CANCELLED" }.sumOf { it.totalAmount }
    val totalConfirmed = bookings.count { it.paymentStatus != "CANCELLED" }
    val totalCancelled = bookings.count { it.paymentStatus == "CANCELLED" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Financial & Operations Report",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = RoyalNavyDark
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = RoyalNavyDark),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Total Revenue", color = Color.LightGray, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "₹${totalRevenue.toInt()}", color = RoyalGold, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Confirmed Tickets", color = Color.Gray, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "$totalConfirmed", color = Color(0xFF2E7D32), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Route Performance", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "• Dhari ➔ Nathdwara: 88% Average Occupancy", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "• Nathdwara ➔ Dhari: 92% Average Occupancy", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "• Amreli ➔ Nathdwara: 81% Average Occupancy", fontSize = 13.sp)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Merchant Account Settlement", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Settlement Number: ${RouteConstants.PAYMENT_PHONE}", fontSize = 13.sp, color = RoyalCrimson, fontWeight = FontWeight.SemiBold)
                    Text(text = "UPI Merchant ID: ${RouteConstants.PAYMENT_UPI_ID}", fontSize = 12.sp, color = Color.Gray)
                    Text(text = "Direct bank credit frequency: Daily at 11:59 PM", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AddBusDialog(
    onDismiss: () -> Unit,
    onConfirm: (BusEntity) -> Unit
) {
    var name by remember { mutableStateOf("Royal Luxury 2+1 AC Sleeper") }
    var busNumber by remember { mutableStateOf("GJ-14-RR-${Random.nextInt(1000, 9999)}") }
    var fromCity by remember { mutableStateOf("Dhari") }
    var toCity by remember { mutableStateOf("Nathdwara") }
    var departureTime by remember { mutableStateOf("09:00 PM") }
    var arrivalTime by remember { mutableStateOf("07:30 AM") }
    var fareStr by remember { mutableStateOf("1150") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add New Luxury Bus") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Bus Name") }, singleLine = true)
                OutlinedTextField(value = busNumber, onValueChange = { busNumber = it }, label = { Text("Bus Number Plate") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = fromCity, onValueChange = { fromCity = it }, label = { Text("From") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = toCity, onValueChange = { toCity = it }, label = { Text("To") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = departureTime, onValueChange = { departureTime = it }, label = { Text("Dep Time") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = arrivalTime, onValueChange = { arrivalTime = it }, label = { Text("Arr Time") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                OutlinedTextField(value = fareStr, onValueChange = { fareStr = it }, label = { Text("Sleeper Fare (₹)") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val fare = fareStr.toDoubleOrNull() ?: 1150.0
                    val newBus = BusEntity(
                        id = "BUS-RRG-${System.currentTimeMillis() % 10000}",
                        name = name,
                        busNumber = busNumber,
                        busType = "AC Sleeper (2+1)",
                        fromCity = fromCity,
                        toCity = toCity,
                        departureTime = departureTime,
                        arrivalTime = arrivalTime,
                        duration = "10h 30m",
                        sleeperPrice = fare,
                        seaterPrice = fare - 250.0,
                        availableSeatsCount = 30,
                        rating = 4.9,
                        amenities = "WiFi,Charging Point,Blanket,Water Bottle,Reading Lamp,CCTV,Emergency Exit,GPS Tracking"
                    )
                    onConfirm(newBus)
                }
            ) {
                Text("Add Coach", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
