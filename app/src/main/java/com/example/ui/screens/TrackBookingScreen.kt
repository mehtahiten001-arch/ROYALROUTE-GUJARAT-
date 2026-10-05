package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BookingEntity
import com.example.data.model.RouteConstants
import com.example.ui.theme.RoyalCrimson
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BookingViewModel

@Composable
fun TrackBookingScreen(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.trackSearchQuery.collectAsState()
    val trackedBookings by viewModel.trackedBookings.collectAsState()
    var inputQuery by remember { mutableStateOf(searchQuery) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FA))
    ) {
        // Header
        Surface(
            color = RoyalNavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Track / Manage Your Booking",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = "Search by Booking ID or Mobile Number (${RouteConstants.SUPPORT_PHONE})",
                    fontSize = 12.sp,
                    color = RoyalGold
                )
            }
        }

        // Search Input Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = {
                        inputQuery = it
                        viewModel.setTrackSearchQuery(it)
                    },
                    label = { Text("Booking ID or Mobile Number") },
                    placeholder = { Text("e.g. 9023377492 or RRG-...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = RoyalNavyDark)
                    },
                    trailingIcon = {
                        if (inputQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                inputQuery = ""
                                viewModel.setTrackSearchQuery("")
                            }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("track_search_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEFF3F8),
                        modifier = Modifier
                            .clickable {
                                inputQuery = RouteConstants.SUPPORT_PHONE
                                viewModel.setTrackSearchQuery(RouteConstants.SUPPORT_PHONE)
                            }
                    ) {
                        Text(
                            text = "Test Phone: ${RouteConstants.SUPPORT_PHONE}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RoyalNavyDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Search Results List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (trackedBookings.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🎫", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Bookings Found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Check your Booking ID or enter mobile number 9023377492.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                items(trackedBookings, key = { it.bookingId }) { booking ->
                    TrackBookingItem(
                        booking = booking,
                        onViewDetails = { viewModel.viewBookingDetails(booking) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TrackBookingItem(
    booking: BookingEntity,
    onViewDetails: () -> Unit
) {
    val isCancelled = booking.paymentStatus == "CANCELLED"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetails() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = booking.bookingId,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = RoyalNavyDark
                    )
                    Text(
                        text = "Booked for: ${booking.journeyDate}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
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
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Route & Seats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${booking.fromCity} ➔ ${booking.toCity}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Seats: ${booking.seatNumbers}",
                    fontWeight = FontWeight.ExtraBold,
                    color = RoyalCrimson,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${booking.busName} (${booking.departureTime})",
                fontSize = 12.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Live Bus Status Bar
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF3F6FA),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isCancelled) Color.Gray else Color(0xFF2E7D32))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCancelled) "Ticket has been cancelled" else "Live Status: Coach scheduled on time from ${booking.fromCity}",
                        fontSize = 12.sp,
                        color = if (isCancelled) Color.Gray else RoyalNavyDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fare: ₹${booking.totalAmount.toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = RoyalNavyDark
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "View E-Ticket",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalGold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = RoyalGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
