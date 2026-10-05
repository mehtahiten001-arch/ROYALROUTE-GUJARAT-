package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BusEntity
import com.example.data.local.entity.SeatEntity
import com.example.data.model.RouteConstants
import com.example.ui.theme.RoyalAmber
import com.example.ui.theme.RoyalCrimson
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldDark
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.theme.SeatAvailable
import com.example.ui.theme.SeatAvailableBorder
import com.example.ui.theme.SeatBooked
import com.example.ui.theme.SeatLadies
import com.example.ui.theme.SeatLadiesBorder
import com.example.ui.theme.SeatSelected
import com.example.ui.theme.SeatSelectedBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoyalTopAppBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable () -> Unit = {}
) {
    val context = LocalContext.current

    Surface(
        color = RoyalNavyDark,
        tonalElevation = 6.dp
    ) {
        Column {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = RoyalGold,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("top_bar_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back",
                                tint = Color.White
                            )
                        }
                    } else {
                        // Brand Icon Badge
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp, end = 4.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(RoyalGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "👑",
                                fontSize = 18.sp
                            )
                        }
                    }
                },
                actions = {
                    actions()
                    // Quick Call Helpline
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${RouteConstants.SUPPORT_PHONE}")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.testTag("top_bar_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call 9023377492",
                            tint = RoyalGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RoyalNavyDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
            // Golden accent divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(RoyalGold)
            )
        }
    }
}

@Composable
fun RoyalQrCodeView(
    dataString: String,
    modifier: Modifier = Modifier,
    sizeDp: Int = 180
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .size(sizeDp.dp)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size((sizeDp - 28).dp)) {
                val canvasSize = size.width
                val matrixSize = 25
                val blockSize = canvasSize / matrixSize

                // Background
                drawRect(color = Color.White, size = size)

                // Deterministic QR pattern generator based on hash
                val hash = dataString.hashCode()
                for (r in 0 until matrixSize) {
                    for (c in 0 until matrixSize) {
                        val isCornerPositionPattern =
                            (r < 7 && c < 7) || (r < 7 && c >= matrixSize - 7) || (r >= matrixSize - 7 && c < 7)

                        var drawDark = false
                        if (isCornerPositionPattern) {
                            // Draw Finder Patterns (7x7 corners)
                            val isOuter = (r == 0 || r == 6 || c == 0 || c == 6) && (r < 7 && c < 7)
                            val isTopRightOuter = (r == 0 || r == 6 || c == matrixSize - 7 || c == matrixSize - 1) && (r < 7 && c >= matrixSize - 7)
                            val isBottomLeftOuter = (r == matrixSize - 7 || r == matrixSize - 1 || c == 0 || c == 6) && (r >= matrixSize - 7 && c < 7)
                            val isCenter = (r in 2..4 && c in 2..4) ||
                                    (r in 2..4 && c in matrixSize - 5 until matrixSize - 2) ||
                                    (r in matrixSize - 5 until matrixSize - 2 && c in 2..4)

                            drawDark = isOuter || isTopRightOuter || isBottomLeftOuter || isCenter
                        } else {
                            // Timing patterns
                            if (r == 6 || c == 6) {
                                drawDark = (r + c) % 2 == 0
                            } else {
                                // Deterministic data bits
                                val bit = ((hash xor (r * 31 + c * 17)) and 0x1) == 1
                                val pseudoNoise = ((r * c + hash.ushr(4)) % 3 == 0)
                                drawDark = bit || pseudoNoise
                            }
                        }

                        if (drawDark) {
                            drawRect(
                                color = RoyalNavyDark,
                                topLeft = Offset(c * blockSize, r * blockSize),
                                size = Size(blockSize * 0.95f, blockSize * 0.95f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SleeperBerthView(
    seat: SeatEntity,
    isSelected: Boolean,
    onSeatClick: (SeatEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAvailable = seat.status == "AVAILABLE"
    val isBooked = seat.status == "BOOKED"
    val isLadies = seat.isLadiesSeat || seat.status == "LADIES_RESERVED"

    val (bgColor, borderColor, textColor) = when {
        isSelected -> Triple(SeatSelected, SeatSelectedBorder, Color.White)
        isBooked -> Triple(SeatBooked, Color(0xFF9E9E9E), Color(0xFF616161))
        isLadies -> Triple(SeatLadies.copy(alpha = 0.2f), SeatLadiesBorder, SeatLadiesBorder)
        else -> Triple(SeatAvailable, SeatAvailableBorder, RoyalNavyDark)
    }

    val isSingle = seat.berthType == "SINGLE_SLEEPER"
    val berthWidth = if (isSingle) 74.dp else 68.dp
    val berthHeight = 110.dp

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = modifier
            .width(berthWidth)
            .height(berthHeight)
            .border(
                width = if (isSelected) 2.5.dp else 1.2.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = !isBooked) {
                onSeatClick(seat)
            }
            .testTag("seat_${seat.seatNumber}")
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pillow indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(borderColor.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSingle) "WINDOW" else "DOUBLE",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Center Seat Number & Icon
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = seat.seatNumber,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = textColor
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                } else if (isLadies) {
                    Icon(
                        imageVector = Icons.Default.Female,
                        contentDescription = "Ladies seat",
                        tint = SeatLadiesBorder,
                        modifier = Modifier.size(16.dp)
                    )
                } else if (isBooked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Booked",
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Price badge
            Text(
                text = if (isBooked) "Booked" else "₹${seat.price.toInt()}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BusItemCard(
    bus: BusEntity,
    onSelectBus: (BusEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelectBus(bus) }
            .testTag("bus_card_${bus.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Bus Name & Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bus.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavyDark
                        )
                    )
                    Text(
                        text = "${bus.busType} • ${bus.busNumber}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2E7D32)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${bus.rating}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timing Row: Departure -> Duration -> Arrival
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = bus.departureTime,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = RoyalNavyDark
                    )
                    Text(
                        text = bus.fromCity,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = bus.duration,
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(RoyalGold)
                        )
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(2.dp)
                                .background(Color.LightGray)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "Royal Direct",
                        fontSize = 9.sp,
                        color = RoyalGoldDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = bus.arrivalTime,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = RoyalNavyDark
                    )
                    Text(
                        text = bus.toCity,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amenities chip line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("2+1 AC Sleeper", "GPS Tracking", "Clean Linen", "CCTV").forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Seats left & Price + Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Starts from ",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "₹${bus.sleeperPrice.toInt()}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = RoyalCrimson
                        )
                    }
                    Text(
                        text = "${bus.availableSeatsCount} Seats Left (Upper & Lower)",
                        fontSize = 11.sp,
                        color = if (bus.availableSeatsCount <= 5) RoyalCrimson else Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = RoyalNavyDark,
                    modifier = Modifier.clickable { onSelectBus(bus) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Seats",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

fun launchWhatsApp(context: Context, phoneNumber: String, message: String) {
    try {
        val cleanNumber = phoneNumber.replace("+", "").replace(" ", "").trim()
        val formattedNumber = if (cleanNumber.length == 10) "91$cleanNumber" else cleanNumber
        val url = "https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp not installed. Contact: $phoneNumber", Toast.LENGTH_LONG).show()
    }
}
