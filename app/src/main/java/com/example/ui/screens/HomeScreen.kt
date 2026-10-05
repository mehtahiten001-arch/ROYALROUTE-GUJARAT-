package com.example.ui.screens

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RouteConstants
import com.example.ui.components.launchWhatsApp
import com.example.ui.theme.RoyalAmber
import com.example.ui.theme.RoyalCrimson
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fromCity by viewModel.fromCity.collectAsState()
    val toCity by viewModel.toCity.collectAsState()
    val journeyDate by viewModel.journeyDate.collectAsState()

    var showFromDropdown by remember { mutableStateOf(false) }
    var showToDropdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FA))
    ) {
        // Luxury Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(RoyalNavyDark, RoyalNavyMedium, Color(0xFF1E3557))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👑", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ROYAL ROUTE GUJARAT",
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.2.sp,
                                    fontSize = 18.sp,
                                    color = RoyalGold
                                )
                            }
                            Text(
                                text = RouteConstants.WEBSITE_URL,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // WhatsApp Quick action
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF25D366),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    launchWhatsApp(
                                        context,
                                        RouteConstants.SUPPORT_PHONE,
                                        "Hello Royal Route Gujarat! I would like to book a luxury sleeper ticket."
                                    )
                                }
                                .testTag("hero_whatsapp_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💬", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "WhatsApp",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Luxury Sleeper Journey\nDhari ⇄ Nathdwara Direct",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Upper & Lower Berths • Shrinathji Darshan Timings • GPS Tracking",
                        fontSize = 13.sp,
                        color = Color(0xFFDCE4EC)
                    )
                }
            }
        }

        // Primary Search Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Book Your Luxury Sleeper Bus",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // From - To with Swap button
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            // FROM CITY
                            Box {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF3F5F9),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showFromDropdown = true }
                                        .testTag("select_from_city")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = RoyalCrimson,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = "FROM", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                            Text(text = fromCity, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = RoyalNavyDark)
                                        }
                                    }
                                }

                                DropdownMenu(
                                    expanded = showFromDropdown,
                                    onDismissRequest = { showFromDropdown = false }
                                ) {
                                    RouteConstants.CITIES.forEach { city ->
                                        DropdownMenuItem(
                                            text = { Text(city, fontWeight = if (city == fromCity) FontWeight.Bold else FontWeight.Normal) },
                                            onClick = {
                                                viewModel.setFromCity(city)
                                                showFromDropdown = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // TO CITY
                            Box {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF3F5F9),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showToDropdown = true }
                                        .testTag("select_to_city")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = "TO", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                            Text(text = toCity, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = RoyalNavyDark)
                                        }
                                    }
                                }

                                DropdownMenu(
                                    expanded = showToDropdown,
                                    onDismissRequest = { showToDropdown = false }
                                ) {
                                    RouteConstants.CITIES.forEach { city ->
                                        DropdownMenuItem(
                                            text = { Text(city, fontWeight = if (city == toCity) FontWeight.Bold else FontWeight.Normal) },
                                            onClick = {
                                                viewModel.setToCity(city)
                                                showToDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Swap Button in between
                        IconButton(
                            onClick = { viewModel.swapCities() },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(RoyalGold)
                                .border(2.dp, Color.White, CircleShape)
                                .testTag("swap_cities_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap Cities",
                                tint = RoyalNavyDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Date Selection
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF3F5F9),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val pickedCal = Calendar.getInstance()
                                        pickedCal.set(year, month, dayOfMonth)
                                        val pickedStr = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(pickedCal.time)
                                        viewModel.setJourneyDate(pickedStr)
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                            .testTag("journey_date_picker")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = RoyalNavyDark,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "JOURNEY DATE", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                    Text(text = journeyDate, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RoyalNavyDark)
                                }
                            }

                            Row {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White,
                                    modifier = Modifier
                                        .clickable { viewModel.setDateToday() }
                                        .padding(end = 6.dp)
                                ) {
                                    Text(
                                        text = "Today",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalNavyDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White,
                                    modifier = Modifier.clickable { viewModel.setDateTomorrow() }
                                ) {
                                    Text(
                                        text = "Tomorrow",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalNavyDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Primary Search Button
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.BUS_SEARCH) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("search_bus_cta"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavyDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SEARCH LUXURY BUSES",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Quick Popular Route Chips
        item {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                Text(
                    text = "Popular Royal Routes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = RoyalNavyDark,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val popularPairs = listOf(
                        Pair("Dhari", "Nathdwara"),
                        Pair("Nathdwara", "Dhari"),
                        Pair("Amreli", "Nathdwara"),
                        Pair("Ahmedabad", "Nathdwara")
                    )

                    items(popularPairs) { pair ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (fromCity == pair.first && toCity == pair.second)
                                    RoyalNavyDark else Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.clickable {
                                viewModel.setFromCity(pair.first)
                                viewModel.setToCity(pair.second)
                                viewModel.navigateTo(AppScreen.BUS_SEARCH)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🚌  ${pair.first} ➔ ${pair.second}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (fromCity == pair.first && toCity == pair.second)
                                        RoyalGold else RoyalNavyDark
                                )
                            }
                        }
                    }
                }
            }
        }

        // Direct Helpline & Payment Information Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF4FA)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(RoyalNavyDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = RoyalGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Direct Booking & Helpline",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = RoyalNavyDark
                            )
                            Text(
                                text = "Contact & Payment Number: ${RouteConstants.SUPPORT_PHONE}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalCrimson
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${RouteConstants.SUPPORT_PHONE}")
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavyDark)
                        ) {
                            Text(text = "Call ${RouteConstants.SUPPORT_PHONE}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                launchWhatsApp(
                                    context,
                                    RouteConstants.SUPPORT_PHONE,
                                    "Hello, I need booking assistance for Royal Route Gujarat bus."
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                        ) {
                            Text(text = "WhatsApp Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Luxury Travel Highlights
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "Why Travel With Royal Route Gujarat?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = RoyalNavyDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                val features = listOf(
                    Triple("🛏️", "2+1 Luxury Sleeper Berths", "Spacious upper & lower beds with premium mattress & privacy curtains"),
                    Triple("⏱️", "Darshan Friendly Timings", "Departs Dhari at night and arrives at Nathdwara fresh for Mangla & Shringar"),
                    Triple("📍", "Live GPS & Real-time Tracking", "Share live bus location with your family members anytime"),
                    Triple("💳", "Instant UPI Booking (9023377492)", "Secure payment directly to official merchant account with zero hassle")
                )

                features.forEach { (icon, title, desc) ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = icon, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RoyalNavyDark)
                                Text(text = desc, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        // Bottom Quick Nav Cards (Track Ticket & Admin Access)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.TRACK_BOOKING) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "🔎 Track Booking", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.ADMIN) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "🔐 Admin Portal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
