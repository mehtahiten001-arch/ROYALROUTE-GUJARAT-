package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BusItemCard
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.viewmodel.BookingViewModel

@Composable
fun BusSearchScreen(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val fromCity by viewModel.fromCity.collectAsState()
    val toCity by viewModel.toCity.collectAsState()
    val journeyDate by viewModel.journeyDate.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val allBuses by viewModel.allBuses.collectAsState()

    var filterType by remember { mutableStateOf("ALL") }
    var sortBy by remember { mutableStateOf("TIME") } // "TIME", "PRICE", "SEATS"

    // If direct search has results use it, or fallback to all buses matching route
    val baseList = if (searchResults.isNotEmpty()) searchResults else {
        allBuses.filter {
            it.fromCity.equals(fromCity, ignoreCase = true) &&
                    it.toCity.equals(toCity, ignoreCase = true)
        }.ifEmpty { allBuses }
    }

    val filteredList = baseList.filter { bus ->
        when (filterType) {
            "AC" -> bus.busType.contains("AC", ignoreCase = true)
            "VOLVO" -> bus.busType.contains("Volvo", ignoreCase = true) || bus.name.contains("Volvo", ignoreCase = true)
            "SLEEPER" -> bus.busType.contains("Sleeper", ignoreCase = true)
            else -> true
        }
    }.sortedWith(
        when (sortBy) {
            "PRICE" -> compareBy { it.sleeperPrice }
            "SEATS" -> compareByDescending { it.availableSeatsCount }
            else -> compareBy { it.departureTime }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FA))
    ) {
        // Route Bar Header
        Surface(
            color = RoyalNavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$fromCity  ➔  $toCity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Date: $journeyDate • ${filteredList.size} Luxury Coaches",
                            fontSize = 12.sp,
                            color = RoyalGold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Direct Express",
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Filters and Sort Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = filterType == "ALL",
                    onClick = { filterType = "ALL" },
                    label = { Text("All Buses") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalNavyDark,
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = filterType == "AC",
                    onClick = { filterType = "AC" },
                    label = { Text("AC Sleeper (2+1)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalNavyDark,
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = filterType == "VOLVO",
                    onClick = { filterType = "VOLVO" },
                    label = { Text("Volvo Multi-Axle") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalNavyDark,
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = sortBy == "PRICE",
                    onClick = { sortBy = if (sortBy == "PRICE") "TIME" else "PRICE" },
                    label = { Text("Cheapest First") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalGold,
                        selectedLabelColor = RoyalNavyDark
                    )
                )
            }
        }

        // Bus List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No direct buses on this route",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try searching for Dhari ⇄ Nathdwara or Amreli ⇄ Nathdwara routes.",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.setFromCity("Dhari")
                                viewModel.setToCity("Nathdwara")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavyDark)
                        ) {
                            Text("Switch to Dhari ➔ Nathdwara")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(filteredList, key = { it.id }) { bus ->
                    BusItemCard(
                        bus = bus,
                        onSelectBus = { viewModel.selectBus(it) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
