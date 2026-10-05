package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RouteConstants
import com.example.ui.components.RoyalQrCodeView
import com.example.ui.components.launchWhatsApp
import com.example.ui.theme.RoyalCrimson
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BookingViewModel

@Composable
fun TicketScreen(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeBooking by viewModel.activeBooking.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }

    if (activeBooking == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No Active Ticket Selected",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavyDark)
                ) {
                    Text("Go to Home")
                }
            }
        }
        return
    }

    val booking = activeBooking!!
    val isCancelled = booking.paymentStatus == "CANCELLED"

    val ticketShareText = """
        👑 ROYAL ROUTE GUJARAT - E-TICKET
        Website: ${RouteConstants.WEBSITE_URL}
        Booking ID: ${booking.bookingId}
        PNR: ${booking.pnrNumber}
        Status: ${booking.paymentStatus}
        
        🚌 Coach: ${booking.busName} (${booking.busNumber})
        Route: ${booking.fromCity} ➔ ${booking.toCity}
        Date: ${booking.journeyDate}
        Time: ${booking.departureTime}
        Seats: ${booking.seatNumbers}
        
        📍 Boarding: ${booking.boardingPoint}
        📍 Dropping: ${booking.droppingPoint}
        👤 Passenger: ${booking.passengersSummary}
        
        💳 Total Paid: ₹${booking.totalAmount.toInt()}
        Helpline / WhatsApp: ${RouteConstants.SUPPORT_PHONE}
    """.trimIndent()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Confirmation Banner
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isCancelled) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isCancelled) Icons.Default.Cancel else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isCancelled) RoyalCrimson else Color(0xFF2E7D32),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isCancelled) "Ticket Cancelled" else "Booking Confirmed & Verified!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isCancelled) RoyalCrimson else Color(0xFF2E7D32)
                        )
                        Text(
                            text = if (isCancelled) "Refund processed: ₹${booking.cancellationRefund.toInt()}" else "Show this digital QR code to bus conductor during boarding.",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }

        // Luxury E-Ticket Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Ticket Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RoyalNavyDark)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "👑", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ROYAL ROUTE GUJARAT",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = RoyalGold
                                    )
                                }
                                Text(
                                    text = RouteConstants.WEBSITE_URL,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isCancelled) RoyalCrimson else Color(0xFF2E7D32)
                            ) {
                                Text(
                                    text = booking.paymentStatus,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Ticket Body
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Booking ID & PNR Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "BOOKING ID", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text(text = booking.bookingId, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = RoyalNavyDark)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "PNR NUMBER", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text(text = booking.pnrNumber, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = RoyalNavyDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Journey Route Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF7F9FC),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = booking.departureTime, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = RoyalNavyDark)
                                    Text(text = booking.fromCity, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = booking.journeyDate, fontSize = 11.sp, color = Color.Gray)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsBus,
                                        contentDescription = null,
                                        tint = RoyalGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(text = "Direct Sleeper", fontSize = 9.sp, color = Color.Gray)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = booking.arrivalTime, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = RoyalNavyDark)
                                    Text(text = booking.toCity, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Next Morning", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Coach & Seat Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "COACH / BUS", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text(text = booking.busName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = booking.busNumber, fontSize = 11.sp, color = Color.DarkGray)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "SEAT BERTH(S)", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = RoyalNavyDark
                                ) {
                                    Text(
                                        text = booking.seatNumbers,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = RoyalGold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Boarding & Dropping Point details
                        Column {
                            Text(text = "BOARDING POINT", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(text = booking.boardingPoint, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "DROPPING POINT", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(text = booking.droppingPoint, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Passengers
                        Column {
                            Text(text = "PASSENGERS", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(text = booking.passengersSummary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Contact: ${booking.contactPhone} • ${booking.contactEmail}", fontSize = 11.sp, color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Scannable Conductor QR Code
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Conductor Check-In QR Code",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            RoyalQrCodeView(
                                dataString = "ROYAL_ROUTE_TICKET:${booking.bookingId}:${booking.pnrNumber}:${booking.seatNumbers}",
                                sizeDp = 150
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Scan to verify passenger identity",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Payment Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "PAYMENT METHOD", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text(text = booking.paymentMethod, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "TOTAL FARE", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text(text = "₹${booking.totalAmount.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = RoyalCrimson)
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons Row (WhatsApp, Share, Support)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // WhatsApp Share Button
                Button(
                    onClick = {
                        launchWhatsApp(
                            context,
                            RouteConstants.SUPPORT_PHONE,
                            "👑 ROYAL ROUTE GUJARAT TICKET CONFIRMATION:\n$ticketShareText"
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("share_whatsapp_ticket_btn")
                ) {
                    Text(text = "💬 Share Ticket on WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Android Share Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Royal Route Gujarat E-Ticket ${booking.bookingId}")
                                putExtra(Intent.EXTRA_TEXT, ticketShareText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Bus Ticket"))
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavyDark),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Share / Save", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${RouteConstants.SUPPORT_PHONE}")
                            }
                            context.startActivity(dialIntent)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavyMedium),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Helpline", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (!isCancelled) {
                    OutlinedButton(
                        onClick = { showCancelDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RoyalCrimson)
                    ) {
                        Text(text = "Cancel Ticket (15% Policy Charge)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Back to Home Screen", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showCancelDialog) {
        val refundPreview = booking.totalAmount * 0.85
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text(text = "Cancel Booking?") },
            text = {
                Text(
                    text = "As per Royal Route Gujarat policy, 85% refund (₹${refundPreview.toInt()}) will be returned to your original payment method (${RouteConstants.PAYMENT_PHONE}). Are you sure you want to cancel?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        viewModel.cancelActiveBooking(booking.bookingId) { refund ->
                            Toast.makeText(context, "Cancelled! Refund ₹${refund.toInt()} initiated.", Toast.LENGTH_LONG).show()
                        }
                    }
                ) {
                    Text("Confirm Cancellation", color = RoyalCrimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Ticket")
                }
            }
        )
    }
}
