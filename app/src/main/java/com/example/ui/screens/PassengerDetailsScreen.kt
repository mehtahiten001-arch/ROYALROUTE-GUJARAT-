package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Passenger
import com.example.data.model.RouteConstants
import com.example.ui.theme.RoyalCrimson
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BookingViewModel

@Composable
fun PassengerDetailsScreen(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bus by viewModel.selectedBus.collectAsState()
    val selectedSeats by viewModel.selectedSeats.collectAsState()
    val passengers by viewModel.passengers.collectAsState()
    val contactPhone by viewModel.contactPhone.collectAsState()
    val contactEmail by viewModel.contactEmail.collectAsState()

    var phoneInput by remember { mutableStateOf(contactPhone) }
    var emailInput by remember { mutableStateOf(contactEmail) }

    val (baseFare, gst, total) = viewModel.calculateTotalFare()

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
                    text = "Passenger & Contact Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
                Text(
                    text = "${selectedSeats.size} Sleeper Berth(s): ${selectedSeats.joinToString(", ") { it.seatNumber }}",
                    fontSize = 12.sp,
                    color = RoyalGold
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Passenger inputs per selected seat
            items(passengers.size) { index ->
                val passenger = passengers[index]
                val seat = selectedSeats.getOrNull(index)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Passenger ${index + 1}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = RoyalNavyDark
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = RoyalNavyDark
                            ) {
                                Text(
                                    text = "Seat: ${passenger.seatNumber} (${if (seat?.deck == "UPPER") "Upper" else "Lower"})",
                                    color = RoyalGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Name
                        OutlinedTextField(
                            value = passenger.name,
                            onValueChange = { newName ->
                                viewModel.updatePassenger(index, newName, passenger.age, passenger.gender)
                            },
                            label = { Text("Full Name *") },
                            placeholder = { Text("e.g. Ramesh Patel") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = RoyalNavyDark)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("passenger_name_$index"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Age
                            OutlinedTextField(
                                value = if (passenger.age > 0) passenger.age.toString() else "",
                                onValueChange = { newAgeStr ->
                                    val newAge = newAgeStr.filter { it.isDigit() }.toIntOrNull() ?: 0
                                    viewModel.updatePassenger(index, passenger.name, newAge, passenger.gender)
                                },
                                label = { Text("Age *") },
                                placeholder = { Text("28") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(0.45f)
                                    .testTag("passenger_age_$index"),
                                singleLine = true
                            )

                            // Gender selector
                            Row(
                                modifier = Modifier.weight(0.55f),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilterChip(
                                    selected = passenger.gender.equals("Male", ignoreCase = true),
                                    onClick = {
                                        viewModel.updatePassenger(index, passenger.name, passenger.age, "Male")
                                    },
                                    label = { Text("Male") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = RoyalNavyDark,
                                        selectedLabelColor = Color.White
                                    )
                                )

                                FilterChip(
                                    selected = passenger.gender.equals("Female", ignoreCase = true),
                                    onClick = {
                                        viewModel.updatePassenger(index, passenger.name, passenger.age, "Female")
                                    },
                                    label = { Text("Female") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFC2185B),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Primary Contact Info Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ticket Delivery & Contact Info",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = RoyalNavyDark
                        )
                        Text(
                            text = "E-Ticket & live bus tracking SMS will be sent here.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = {
                                phoneInput = it
                                viewModel.updateContactInfo(it, emailInput)
                            },
                            label = { Text("Mobile Number (WhatsApp) *") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = RoyalNavyDark)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_phone_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = {
                                emailInput = it
                                viewModel.updateContactInfo(phoneInput, it)
                            },
                            label = { Text("Email Address (Optional)") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = RoyalNavyDark)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_email_input"),
                            singleLine = true
                        )
                    }
                }
            }

            // Fare Breakdown Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFC)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Fare Breakdown",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = RoyalNavyDark
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Base Sleeper Fare (${selectedSeats.size} berths)", fontSize = 13.sp, color = Color.DarkGray)
                            Text(text = "₹${baseFare.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "GST (5%)", fontSize = 13.sp, color = Color.DarkGray)
                            Text(text = "₹${gst.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Total Payable Amount", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = RoyalNavyDark)
                            Text(text = "₹${total.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, color = RoyalCrimson)
                        }
                    }
                }
            }
        }

        // Bottom Proceed CTA
        Surface(
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        val uncompleted = passengers.any { it.name.isBlank() || it.age <= 0 }
                        if (uncompleted) {
                            Toast.makeText(context, "Please enter all passenger names and valid ages", Toast.LENGTH_SHORT).show()
                        } else if (phoneInput.length < 10) {
                            Toast.makeText(context, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.navigateTo(AppScreen.PAYMENT)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavyDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("proceed_to_payment_btn")
                ) {
                    Text(
                        text = "Proceed to Online Payment (₹${total.toInt()})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
