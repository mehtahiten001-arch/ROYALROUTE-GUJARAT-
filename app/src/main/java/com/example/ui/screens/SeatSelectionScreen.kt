package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AirlineSeatIndividualSuite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SleeperBerthView
import com.example.ui.theme.RoyalCrimson
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.SeatAvailable
import com.example.ui.theme.SeatBooked
import com.example.ui.theme.SeatLadies
import com.example.ui.theme.SeatSelected
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BookingViewModel

@Composable
fun SeatSelectionScreen(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val bus by viewModel.selectedBus.collectAsState()
    val seats by viewModel.currentBusSeats.collectAsState()
    val selectedSeats by viewModel.selectedSeats.collectAsState()

    var selectedDeckTab by remember { mutableIntStateOf(0) } // 0 = Lower Deck, 1 = Upper Deck
    val currentDeckName = if (selectedDeckTab == 0) "LOWER" else "UPPER"
    val deckSeats = seats.filter { it.deck == currentDeckName }

    val (baseFare, gst, total) = viewModel.calculateTotalFare()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FA))
    ) {
        // Bus Info Summary Header
        Surface(
            color = RoyalNavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = bus?.name ?: "Royal Sleeper Coach",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${bus?.fromCity} ➔ ${bus?.toCity}  •  ${bus?.departureTime}",
                        fontSize = 12.sp,
                        color = RoyalGold
                    )
                    Text(
                        text = "Max 6 seats",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Deck Tabs: LOWER DECK & UPPER DECK
        TabRow(
            selectedTabIndex = selectedDeckTab,
            containerColor = Color.White,
            contentColor = RoyalNavyDark,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedDeckTab]),
                    color = RoyalNavyDark,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedDeckTab == 0,
                onClick = { selectedDeckTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AirlineSeatIndividualSuite,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Lower Deck",
                            fontWeight = if (selectedDeckTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                },
                modifier = Modifier.testTag("tab_lower_deck")
            )

            Tab(
                selected = selectedDeckTab == 1,
                onClick = { selectedDeckTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AirlineSeatIndividualSuite,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Upper Deck",
                            fontWeight = if (selectedDeckTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                },
                modifier = Modifier.testTag("tab_upper_deck")
            )
        }

        // Seat Status Legend Bar
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = SeatAvailable, label = "Available")
                LegendItem(color = SeatSelected, label = "Selected")
                LegendItem(color = SeatLadies, label = "Ladies")
                LegendItem(color = SeatBooked, label = "Booked")
            }
        }

        // Bus Blueprint Layout
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Driver Cabin Section (Front of Bus)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF3F8),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🚪", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Entry / Exit Gate",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Gray
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Driver Cabin",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "🎛️", fontSize = 16.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sleeper Rows (1 to 5)
                        // Column 1: Single Window Sleeper | Aisle | Columns 3 & 4: Double Sleeper
                        for (r in 1..5) {
                            val rowSeats = deckSeats.filter { it.row == r }
                            val singleSeat = rowSeats.find { it.column == 1 }
                            val doubleInnerSeat = rowSeats.find { it.column == 3 }
                            val doubleWindowSeat = rowSeats.find { it.column == 4 }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left Single Berth
                                if (singleSeat != null) {
                                    val isSelected = selectedSeats.any { it.id == singleSeat.id }
                                    SleeperBerthView(
                                        seat = singleSeat,
                                        isSelected = isSelected,
                                        onSeatClick = { viewModel.toggleSeatSelection(it) }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(74.dp))
                                }

                                // Center Aisle Indicator
                                Column(
                                    modifier = Modifier.width(42.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "AISLE",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.LightGray,
                                        letterSpacing = 1.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(50.dp)
                                            .background(Color(0xFFE2E8F0))
                                    )
                                }

                                // Right Double Berths
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (doubleInnerSeat != null) {
                                        val isSelected = selectedSeats.any { it.id == doubleInnerSeat.id }
                                        SleeperBerthView(
                                            seat = doubleInnerSeat,
                                            isSelected = isSelected,
                                            onSeatClick = { viewModel.toggleSeatSelection(it) }
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.width(68.dp))
                                    }

                                    if (doubleWindowSeat != null) {
                                        val isSelected = selectedSeats.any { it.id == doubleWindowSeat.id }
                                        SleeperBerthView(
                                            seat = doubleWindowSeat,
                                            isSelected = isSelected,
                                            onSeatClick = { viewModel.toggleSeatSelection(it) }
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.width(68.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Rear of Bus
                        Text(
                            text = "BACK OF BUS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            letterSpacing = 1.2.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom Action Bar: Selected Seats & Continue CTA
        Surface(
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val seatCount = selectedSeats.size
                        Text(
                            text = if (seatCount > 0) {
                                "$seatCount Seat(s): ${selectedSeats.joinToString(", ") { it.seatNumber }}"
                            } else {
                                "No seat selected"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (seatCount > 0) RoyalNavyDark else Color.Gray
                        )
                        Text(
                            text = if (seatCount > 0) "Total: ₹${total.toInt()} (incl. GST)" else "Tap berths above to select",
                            fontSize = 12.sp,
                            color = if (seatCount > 0) RoyalCrimson else Color.Gray,
                            fontWeight = if (seatCount > 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.BOARDING_DROPPING) },
                        enabled = selectedSeats.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalNavyDark,
                            disabledContainerColor = Color.LightGray
                        ),
                        modifier = Modifier.testTag("continue_to_boarding_btn")
                    ) {
                        Text(
                            text = "Continue",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
                .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = label, fontSize = 11.sp, color = Color.DarkGray)
    }
}
