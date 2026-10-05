package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RouteConstants
import com.example.ui.components.launchWhatsApp
import com.example.ui.theme.RoyalCrimson
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.viewmodel.BookingViewModel

@Composable
fun SupportScreen(
    viewModel: BookingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Support Header
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalNavyDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "👑", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Royal Customer Support",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Text(
                        text = "24x7 Dedicated Passenger Assistance",
                        fontSize = 12.sp,
                        color = RoyalGold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("support_call_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = RoyalNavyDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Call ${RouteConstants.SUPPORT_PHONE}", color = RoyalNavyDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                launchWhatsApp(
                                    context,
                                    RouteConstants.SUPPORT_PHONE,
                                    "Hello Royal Route Gujarat! I need assistance with bus booking or inquiries."
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("support_whatsapp_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                        ) {
                            Text(text = "💬 WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Official Links Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Official Web & Payment Channels",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = RoyalNavyDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Website
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF3F6FA),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://${RouteConstants.WEBSITE_URL}"))
                                context.startActivity(intent)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = RoyalNavyDark)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Official Website", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Text(text = RouteConstants.WEBSITE_URL, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalNavyDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Phone & Payment Number
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF3F6FA),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = RoyalCrimson)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Primary Helpline & Payment Number", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Text(text = RouteConstants.SUPPORT_PHONE, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalCrimson)
                            }
                        }
                    }
                }
            }
        }

        // Branch Offices
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Royal Route Bus Branch Offices",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = RoyalNavyDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OfficeItem(
                        city = "Dhari Head Office (Gujarat)",
                        address = "Royal Route Travels, Station Road, Opp. ST Depot, Dhari, Dist. Amreli, Gujarat - 365640",
                        phone = RouteConstants.SUPPORT_PHONE
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OfficeItem(
                        city = "Nathdwara Branch (Rajasthan)",
                        address = "Near Shrinathji Temple Bus Stand, VIP Darshan Gate Road, Nathdwara, Rajasthan - 313301",
                        phone = RouteConstants.SUPPORT_PHONE
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OfficeItem(
                        city = "Ahmedabad Booking Office",
                        address = "CTM Express Highway Cross Road, Near Toll Plaza, Ahmedabad, Gujarat",
                        phone = RouteConstants.SUPPORT_PHONE
                    )
                }
            }
        }

        // FAQs
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Frequently Asked Questions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = RoyalNavyDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FaqItem(
                        q = "What is the difference between Upper and Lower berths?",
                        a = "Upper berths offer enhanced personal privacy and single window options, while lower berths provide easy access and extra headroom."
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FaqItem(
                        q = "What are the Shrinathji Temple arrival timings?",
                        a = "Our luxury sleeper coaches depart Dhari in the evening and arrive at Nathdwara around 07:00 AM to 07:30 AM, ideal for Mangla and Shringar Darshan."
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FaqItem(
                        q = "How do I cancel my ticket and get a refund?",
                        a = "You can cancel anytime up to 2 hours before departure via the Track Booking screen. An 85% refund is automatically credited back to your UPI or account."
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OfficeItem(city: String, address: String, phone: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = city, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = RoyalNavyDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = address, fontSize = 11.sp, color = Color.Gray, lineHeight = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Helpline: $phone", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalCrimson)
        }
    }
}

@Composable
private fun FaqItem(q: String, a: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Q: $q", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = RoyalNavyDark)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = a, fontSize = 12.sp, color = Color.DarkGray, lineHeight = 16.sp)
    }
}
