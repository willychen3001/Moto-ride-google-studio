package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Trip
import com.example.data.UserAccount
import com.example.ui.theme.*

@Composable
fun MapSimulator(
    modifier: Modifier = Modifier,
    activeTrip: Trip?,
    onlineDrivers: List<UserAccount>,
    animationProgress: Float
) {
    // Pulse animation for simulated coordinates
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(VibrantMapBg)
            .border(1.dp, VibrantBorder, RoundedCornerShape(16.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // --- Draw Grid Background (Streets simulation) ---
            val gridSpacing = 40.dp.toPx()
            val strokeWidth = 1.dp.toPx()
            val streetColor = VibrantPurple.copy(alpha = 0.20f)

            // Vertical streets
            var x = 0f
            while (x < width) {
                drawLine(
                    color = streetColor,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = strokeWidth
                )
                x += gridSpacing
            }

            // Horizontal streets
            var y = 0f
            while (y < height) {
                drawLine(
                    color = streetColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = strokeWidth
                )
                y += gridSpacing
            }

            // Draw a diagonal arterial avenue for styling
            drawLine(
                color = VibrantLilac,
                start = Offset(0f, 0f),
                end = Offset(width, height),
                strokeWidth = 4.dp.toPx()
            )

            // --- Draw Static Online Drivers (Lime dots with circles) ---
            if (activeTrip == null) {
                onlineDrivers.forEachIndexed { index, driver ->
                    // Determine simulated spread coordinates
                    val dx = when (index) {
                        0 -> width * 0.25f
                        1 -> width * 0.75f
                        else -> width * 0.45f
                    }
                    val dy = when (index) {
                        0 -> height * 0.35f
                        1 -> height * 0.65f
                        else -> height * 0.15f
                    }

                    // Green outer glowing pulse
                    drawCircle(
                        color = NeonLime.copy(alpha = 0.2f * pulseAlpha),
                        radius = 16.dp.toPx(),
                        center = Offset(dx, dy)
                    )
                    // Green solid center
                    drawCircle(
                        color = NeonLime,
                        radius = 5.dp.toPx(),
                        center = Offset(dx, dy)
                    )
                }
            }

            // --- Draw Active Trip Details ---
            if (activeTrip != null) {
                val startX = activeTrip.originX * width
                val startY = activeTrip.originY * height
                val endX = activeTrip.destX * width
                val endY = activeTrip.destY * height

                // Route representation line (dashed if accepted, solid cyan if IN_PROGRESS)
                val routeColor = if (activeTrip.status == "IN_PROGRESS") NeonCyan else NeonLime.copy(alpha = 0.7f)
                val pathEffect = if (activeTrip.status == "PENDING") {
                    PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                } else null

                drawLine(
                    color = routeColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 3.dp.toPx(),
                    pathEffect = pathEffect
                )

                // Origin green pulse
                drawCircle(
                    color = NeonLime.copy(alpha = 0.3f * pulseAlpha),
                    radius = 14.dp.toPx(),
                    center = Offset(startX, startY)
                )
                drawCircle(
                    color = NeonLime,
                    radius = 6.dp.toPx(),
                    center = Offset(startX, startY)
                )

                // Destination orange pulse
                drawCircle(
                    color = SoftOrange.copy(alpha = 0.3f * pulseAlpha),
                    radius = 14.dp.toPx(),
                    center = Offset(endX, endY)
                )
                drawCircle(
                    color = SoftOrange,
                    radius = 6.dp.toPx(),
                    center = Offset(endX, endY)
                )

                // Animated Driver Motorcycle avatar
                if (activeTrip.status == "ACCEPTED" || activeTrip.status == "IN_PROGRESS") {
                    // Position depends on state and progress
                    // Status ACCEPTED means the driver is driving towards the ORIGIN (Client)
                    // Status IN_PROGRESS means the driver has picked up the Client and is driving to DESTINATION
                    val bikeX: Float
                    val bikeY: Float
                    val iconColor: Color

                    if (activeTrip.status == "ACCEPTED") {
                        // Driving towards Origin
                        // Start point: offscreen-left, ending point: Client origin
                        val mockDriverStartX = width * 0.05f
                        val mockDriverStartY = height * 0.95f
                        bikeX = mockDriverStartX + (startX - mockDriverStartX) * animationProgress
                        bikeY = mockDriverStartY + (startY - mockDriverStartY) * animationProgress
                        iconColor = NeonLime
                    } else {
                        // IN_PROGRESS: Driving to Destination
                        bikeX = startX + (endX - startX) * animationProgress
                        bikeY = startY + (endY - startY) * animationProgress
                        iconColor = NeonCyan
                    }

                    // Render moving pointer glow
                    drawCircle(
                        color = iconColor.copy(alpha = 0.3f),
                        radius = 20.dp.toPx(),
                        center = Offset(bikeX, bikeY)
                    )
                }
            }
        }

        // --- Custom Compose Overlays for Status Text ---
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .border(0.5.dp, VibrantBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            val label = when (activeTrip?.status) {
                "PENDING" -> "Buscando Conductor..."
                "ACCEPTED" -> "Conductor en Camino"
                "IN_PROGRESS" -> "Viaje en Progreso"
                "COMPLETED" -> "Llegada al Destino"
                "CANCELLED" -> "Viaje Cancelado"
                else -> "Simulador de Tráfico Activo"
            }
            val color = when (activeTrip?.status) {
                "PENDING" -> SoftOrange
                "ACCEPTED" -> NeonLime
                "IN_PROGRESS" -> NeonCyan
                "COMPLETED" -> Color.Green
                else -> NeonLime
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color, RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantText
                )
            }
        }

        // Floating Map Marker / Navigation Graphic
        if (activeTrip != null && (activeTrip.status == "ACCEPTED" || activeTrip.status == "IN_PROGRESS")) {
            val textDisplay = if (activeTrip.status == "ACCEPTED") "Arribo: ~3 min" else "ETA: ~${(activeTrip.durationMinutes * (1f - animationProgress)).toInt().coerceAtLeast(1)} min"
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                    .border(0.5.dp, VibrantBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = VibrantPurple,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = textDisplay,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantText
                    )
                }
            }
        }
    }
}
