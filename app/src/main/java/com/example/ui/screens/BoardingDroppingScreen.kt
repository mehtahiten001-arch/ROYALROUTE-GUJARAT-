package com.example.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BoardingPointEntity
import com.example.ui.theme.RoyalCrimson
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BookingViewModel

@Composable
fun BoardingDroppingScreen(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val bus by viewModel.selectedBus.collectAsState()
    val boardingPoints by viewModel.boardingPoints.collectAsState()
    val droppingPoints by viewModel.droppingPoints.collectAsState()
    val selectedBoarding by viewModel.selectedBoardingPoint.collectAsState()
    val selectedDropping by viewModel.selectedDroppingPoint.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Boarding, 1 = Dropping

    // Preselect first if not yet chosen
    if (selectedBoarding == null && boardingPoints.isNotEmpty()) {
        viewModel.selectBoardingPoint(boardingPoints.first())
    }
    if (selectedDropping == null && droppingPoints.isNotEmpty()) {
        viewModel.selectDroppingPoint(droppingPoints.first())
    }

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
                    text = "Select Boarding & Dropping Points",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
                Text(
                    text = "${bus?.fromCity} ➔ ${bus?.toCity}  •  ${bus?.busNumber}",
                    fontSize = 12.sp,
                    color = RoyalGold
                )
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.White,
            contentColor = RoyalNavyDark,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = RoyalNavyDark,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "1. Boarding Point",
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                        selectedBoarding?.let {
                            Text(text = it.time, fontSize = 11.sp, color = RoyalCrimson, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            )

            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "2. Dropping Point",
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                        selectedDropping?.let {
                            Text(text = it.time, fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            )
        }

        // Points List
        val currentPoints = if (activeTab == 0) boardingPoints else droppingPoints
        val currentSelected = if (activeTab == 0) selectedBoarding else selectedDropping

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentPoints.isEmpty()) {
                item {
                    // Fallback point if specific bus points list is empty
                    val defaultPoint = BoardingPointEntity(
                        busId = bus?.id ?: "",
                        city = if (activeTab == 0) bus?.fromCity ?: "" else bus?.toCity ?: "",
                        pointType = if (activeTab == 0) "BOARDING" else "DROPPING",
                        locationName = if (activeTab == 0) "${bus?.fromCity} ST Bus Stand & Royal Travels" else "${bus?.toCity} Private Bus Stand & Mandir Gate",
                        landmark = "Main Highway Junction",
                        time = if (activeTab == 0) bus?.departureTime ?: "08:30 PM" else bus?.arrivalTime ?: "07:30 AM"
                    )

                    PointItemCard(
                        point = defaultPoint,
                        isSelected = true,
                        onSelect = {
                            if (activeTab == 0) viewModel.selectBoardingPoint(defaultPoint)
                            else viewModel.selectDroppingPoint(defaultPoint)
                        }
                    )
                }
            } else {
                items(currentPoints) { point ->
                    val isSelected = currentSelected?.id == point.id || currentSelected?.locationName == point.locationName
                    PointItemCard(
                        point = point,
                        isSelected = isSelected,
                        onSelect = {
                            if (activeTab == 0) {
                                viewModel.selectBoardingPoint(point)
                                activeTab = 1 // Auto advance to dropping point!
                            } else {
                                viewModel.selectDroppingPoint(point)
                            }
                        }
                    )
                }
            }
        }

        // Bottom CTA
        Surface(
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.PASSENGER_DETAILS) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavyDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("continue_to_passengers_btn")
                ) {
                    Text(
                        text = "Continue to Passenger Details",
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

@Composable
private fun PointItemCard(
    point: BoardingPointEntity,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF3F7FC) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) RoyalNavyDark else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(14.dp)
            )
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = RoyalNavyDark)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = point.locationName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = RoyalNavyDark
                    )
                    Text(
                        text = point.time,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = RoyalCrimson
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = point.landmark,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
