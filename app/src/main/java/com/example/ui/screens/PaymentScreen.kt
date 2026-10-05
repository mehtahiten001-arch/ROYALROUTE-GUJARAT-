package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.ui.theme.RoyalCrimson
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BookingViewModel

@Composable
fun PaymentScreen(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bus by viewModel.selectedBus.collectAsState()
    val selectedSeats by viewModel.selectedSeats.collectAsState()
    val (baseFare, gst, total) = viewModel.calculateTotalFare()

    var paymentTab by remember { mutableIntStateOf(0) } // 0: UPI, 1: Card, 2: NetBanking, 3: Pay at Bus
    var isProcessing by remember { mutableStateOf(false) }

    // UPI Intent String to 9023377492
    val upiIntentUri = "upi://pay?pa=${RouteConstants.PAYMENT_UPI_ID}&pn=Royal%20Route%20Gujarat&am=${total.toInt()}&cu=INR&tn=RoyalRouteBusBooking"

    // Card dummy states
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }

    // Bank selection
    var selectedBank by remember { mutableStateOf("State Bank of India") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FA))
    ) {
        // Payment Amount Header
        Surface(
            color = RoyalNavyDark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AMOUNT PAYABLE",
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${total.toInt()}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RoyalGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${bus?.fromCity} ➔ ${bus?.toCity}  •  ${selectedSeats.size} Sleeper Berth(s)",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Payment Method Tabs
        TabRow(
            selectedTabIndex = paymentTab,
            containerColor = Color.White,
            contentColor = RoyalNavyDark,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[paymentTab]),
                    color = RoyalNavyDark,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = paymentTab == 0,
                onClick = { paymentTab = 0 },
                text = { Text("UPI (Instant)", fontWeight = if (paymentTab == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) }
            )
            Tab(
                selected = paymentTab == 1,
                onClick = { paymentTab = 1 },
                text = { Text("Card", fontWeight = if (paymentTab == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) }
            )
            Tab(
                selected = paymentTab == 2,
                onClick = { paymentTab = 2 },
                text = { Text("Net Banking", fontWeight = if (paymentTab == 2) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) }
            )
            Tab(
                selected = paymentTab == 3,
                onClick = { paymentTab = 3 },
                text = { Text("Pay at Bus", fontWeight = if (paymentTab == 3) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TAB 0: UPI Payment to 9023377492
            if (paymentTab == 0) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Official Verified Merchant Payment",
                                        color = Color(0xFF2E7D32),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Scan QR with Any UPI App",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = RoyalNavyDark
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Dynamic UPI QR code
                            RoyalQrCodeView(
                                dataString = upiIntentUri,
                                sizeDp = 180
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Payment ID & Copy Row
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF3F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "UPI ID / Number", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            text = "${RouteConstants.PAYMENT_PHONE} (${RouteConstants.PAYMENT_UPI_ID})",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = RoyalNavyDark
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", RouteConstants.PAYMENT_UPI_ID))
                                            Toast.makeText(context, "UPI ID Copied: ${RouteConstants.PAYMENT_UPI_ID}", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy UPI ID",
                                            tint = RoyalNavyDark
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Direct UPI App Launcher Button
                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiIntentUri))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "No UPI app found. Please copy UPI ID: ${RouteConstants.PAYMENT_UPI_ID}", Toast.LENGTH_LONG).show()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("launch_upi_app_btn")
                            ) {
                                Text(
                                    text = "Pay via GPay / PhonePe / Paytm",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // TAB 1: Card Payment
            if (paymentTab == 1) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Credit / Debit Card",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = RoyalNavyDark
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { cardNumber = it },
                                label = { Text("Card Number") },
                                placeholder = { Text("4111 2222 3333 4444") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.CreditCard, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = cardExpiry,
                                    onValueChange = { cardExpiry = it },
                                    label = { Text("MM/YY") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = cardCvv,
                                    onValueChange = { cardCvv = it },
                                    label = { Text("CVV") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
            }

            // TAB 2: Net Banking
            if (paymentTab == 2) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Select Bank",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = RoyalNavyDark
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            val banks = listOf(
                                "State Bank of India (SBI)",
                                "HDFC Bank",
                                "ICICI Bank",
                                "Bank of Baroda",
                                "Axis Bank",
                                "Kotak Mahindra Bank"
                            )

                            banks.forEach { bank ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedBank = bank }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedBank == bank,
                                        onClick = { selectedBank = bank },
                                        colors = RadioButtonDefaults.colors(selectedColor = RoyalNavyDark)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = bank, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            // TAB 3: Pay at Bus
            if (paymentTab == 3) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Pay Cash to Conductor / Conductor UPI",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = RoyalNavyDark
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "You can reserve your sleeper berths now and pay ₹${total.toInt()} directly to the Royal Route conductor upon boarding the coach at the bus stand or office.",
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFF8E1)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "⚠️", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Please reach boarding point 20 minutes before departure.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF8D6E63),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom CTA: Confirm & Issue Ticket
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
                        isProcessing = true
                        val paymentMethodName = when (paymentTab) {
                            0 -> "UPI (${RouteConstants.PAYMENT_PHONE})"
                            1 -> "Card Payment"
                            2 -> "Net Banking ($selectedBank)"
                            else -> "Pay at Bus (Cash)"
                        }

                        viewModel.completeBooking(paymentMethodName) { confirmedBooking ->
                            isProcessing = false
                            Toast.makeText(context, "Booking Confirmed! ID: ${confirmedBooking.bookingId}", Toast.LENGTH_LONG).show()
                            viewModel.navigateTo(AppScreen.TICKET)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavyDark),
                    enabled = !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_payment_btn")
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = RoyalGold,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Confirm & Generate E-Ticket",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
