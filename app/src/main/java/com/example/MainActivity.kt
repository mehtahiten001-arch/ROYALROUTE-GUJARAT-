package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.HeadsetMic
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RouteConstants
import com.example.ui.components.RoyalTopAppBar
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.BoardingDroppingScreen
import com.example.ui.screens.BusSearchScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PassengerDetailsScreen
import com.example.ui.screens.PaymentScreen
import com.example.ui.screens.SeatSelectionScreen
import com.example.ui.screens.SupportScreen
import com.example.ui.screens.TicketScreen
import com.example.ui.screens.TrackBookingScreen
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalRouteTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BookingViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BookingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoyalRouteTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: BookingViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle Android system back button properly
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        if (!viewModel.handleBack()) {
            viewModel.navigateTo(AppScreen.HOME)
        }
    }

    val topBarTitle = when (currentScreen) {
        AppScreen.HOME -> "Royal Route Gujarat"
        AppScreen.BUS_SEARCH -> "Available Buses"
        AppScreen.SEAT_SELECTION -> "Select Sleeper Berths"
        AppScreen.BOARDING_DROPPING -> "Boarding & Dropping Points"
        AppScreen.PASSENGER_DETAILS -> "Passenger Details"
        AppScreen.PAYMENT -> "Payment - 9023377492"
        AppScreen.TICKET -> "Royal E-Ticket"
        AppScreen.TRACK_BOOKING -> "Track Booking"
        AppScreen.SUPPORT -> "24x7 Royal Support"
        AppScreen.ADMIN -> "Admin Control Panel"
    }

    val topBarSubtitle = when (currentScreen) {
        AppScreen.HOME -> "${RouteConstants.WEBSITE_URL} • ${RouteConstants.SUPPORT_PHONE}"
        AppScreen.BUS_SEARCH -> "Dhari ⇄ Nathdwara Express"
        AppScreen.SEAT_SELECTION -> "Upper & Lower Berths"
        AppScreen.BOARDING_DROPPING -> "Select pickup & drop points"
        AppScreen.PASSENGER_DETAILS -> "Contact & Berth Confirmation"
        AppScreen.PAYMENT -> "Instant UPI & QR Code"
        AppScreen.TICKET -> "Official Ticket & Conductor QR"
        AppScreen.TRACK_BOOKING -> "Live Status & Ticket Lookup"
        AppScreen.SUPPORT -> "WhatsApp & Phone Assistance"
        AppScreen.ADMIN -> "Buses, Fares & Bookings"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            RoyalTopAppBar(
                title = topBarTitle,
                subtitle = topBarSubtitle,
                showBackButton = currentScreen != AppScreen.HOME,
                onBackClick = {
                    if (!viewModel.handleBack()) {
                        viewModel.navigateTo(AppScreen.HOME)
                    }
                }
            )
        },
        bottomBar = {
            // Main Bottom Navigation Bar
            NavigationBar(
                containerColor = RoyalNavyDark,
                contentColor = Color.White,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.HOME,
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalNavyDark,
                        selectedTextColor = RoyalGold,
                        indicatorColor = RoyalGold,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        unselectedTextColor = Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.testTag("nav_home")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.BUS_SEARCH || currentScreen == AppScreen.SEAT_SELECTION,
                    onClick = { viewModel.navigateTo(AppScreen.BUS_SEARCH) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.BUS_SEARCH) Icons.Filled.DirectionsBus else Icons.Outlined.DirectionsBus,
                            contentDescription = "Buses"
                        )
                    },
                    label = { Text("Buses", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalNavyDark,
                        selectedTextColor = RoyalGold,
                        indicatorColor = RoyalGold,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        unselectedTextColor = Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.testTag("nav_buses")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.TRACK_BOOKING || currentScreen == AppScreen.TICKET,
                    onClick = { viewModel.navigateTo(AppScreen.TRACK_BOOKING) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.TRACK_BOOKING) Icons.Filled.ConfirmationNumber else Icons.Outlined.ConfirmationNumber,
                            contentDescription = "My Tickets"
                        )
                    },
                    label = { Text("Tickets", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalNavyDark,
                        selectedTextColor = RoyalGold,
                        indicatorColor = RoyalGold,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        unselectedTextColor = Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.testTag("nav_tickets")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.SUPPORT,
                    onClick = { viewModel.navigateTo(AppScreen.SUPPORT) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.SUPPORT) Icons.Filled.HeadsetMic else Icons.Outlined.HeadsetMic,
                            contentDescription = "Support"
                        )
                    },
                    label = { Text("Support", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalNavyDark,
                        selectedTextColor = RoyalGold,
                        indicatorColor = RoyalGold,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        unselectedTextColor = Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.testTag("nav_support")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.ADMIN,
                    onClick = { viewModel.navigateTo(AppScreen.ADMIN) },
                    icon = {
                        Icon(
                            imageVector = if (currentScreen == AppScreen.ADMIN) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                            contentDescription = "Admin"
                        )
                    },
                    label = { Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalNavyDark,
                        selectedTextColor = RoyalGold,
                        indicatorColor = RoyalGold,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        unselectedTextColor = Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.testTag("nav_admin")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                    AppScreen.BUS_SEARCH -> BusSearchScreen(viewModel = viewModel)
                    AppScreen.SEAT_SELECTION -> SeatSelectionScreen(viewModel = viewModel)
                    AppScreen.BOARDING_DROPPING -> BoardingDroppingScreen(viewModel = viewModel)
                    AppScreen.PASSENGER_DETAILS -> PassengerDetailsScreen(viewModel = viewModel)
                    AppScreen.PAYMENT -> PaymentScreen(viewModel = viewModel)
                    AppScreen.TICKET -> TicketScreen(viewModel = viewModel)
                    AppScreen.TRACK_BOOKING -> TrackBookingScreen(viewModel = viewModel)
                    AppScreen.SUPPORT -> SupportScreen(viewModel = viewModel)
                    AppScreen.ADMIN -> AdminScreen(viewModel = viewModel)
                }
            }
        }
    }
}
