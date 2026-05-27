package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Trip
import com.example.data.UserAccount
import com.example.ui.theme.*

@Composable
fun DriverPortalView(
    viewModel: MotorideViewModel,
    user: UserAccount
) {
    val activeTrip by viewModel.activeDriverTrip.collectAsState()
    val pendingTrips by viewModel.pendingTrips.collectAsState()
    val allTrips by viewModel.allTrips.collectAsState()
    val exchangeRate by viewModel.exchangeRate.collectAsState()
    val animProgress by viewModel.driverAnimProgress.collectAsState()

    // Filter relevant driver trip logs
    val driverHistory = remember(allTrips, user) {
        allTrips.filter { it.driverId == user.id && it.status == "COMPLETED" }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Driver Dashboard Header ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(VibrantLilac, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBike,
                                    contentDescription = "Driver",
                                    tint = VibrantDarkPurple
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = VibrantText
                                )
                                Text(
                                    text = "Moto: ${user.vehicleModel} (${user.licensePlate})",
                                    fontSize = 11.sp,
                                    color = VibrantGrayText
                                )
                            }
                        }

                        // Logout Icon
                        IconButton(
                            onClick = { viewModel.logout() },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = DarkGray)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Log Out",
                                tint = VibrantGrayText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Color(0xFF2D3139), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Document Status Check - Driver approvals
                    if (!user.isApproved) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0x33FF5252)),
                            border = BorderStroke(1.dp, Color(0xFFFF5252))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF5252))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "DOCUMENTOS EXPIRADOS O SIN APROBAR",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFFFF5252)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Tu cuenta de piloto requiere validación del Panel Administrativo de Motoride para recibir viajes de clientes.",
                                    fontSize = 11.sp,
                                    color = VibrantGrayText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.approveDriver(user.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                    modifier = Modifier.fillMaxWidth().height(34.dp).testTag("quick_approve_btn")
                                ) {
                                    Text("Simular Auto-Aprobación Al Instante", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    } else {
                        // Switch online / offline
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(if (user.isOnline) Color.Green else Color.Gray, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (user.isOnline) "OPERADOR DISPONIBLE" else "DESCONECTADO (Offline)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (user.isOnline) Color(0xFF006A6A) else VibrantGrayText
                                    )
                                }
                                Text("Recibe viajes cercanos en vivo", fontSize = 11.sp, color = VibrantGrayText)
                            }

                            Switch(
                                checked = user.isOnline,
                                onCheckedChange = { viewModel.toggleOnlineStatus(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = VibrantPurple,
                                    checkedTrackColor = VibrantLilac
                                ),
                                modifier = Modifier.testTag("driver_online_switch")
                            )
                        }
                    }
                }
            }
        }

        // --- Earnings Wallet Card ---
        if (user.isApproved) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "MIS GANANCIAS ACUMULADAS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantGrayText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = String.format("$%.2f USD", user.walletBalanceUsd),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = VibrantPurple
                                )
                                Text(
                                    text = String.format("%.2f VES", user.walletBalanceUsd * exchangeRate),
                                    fontSize = 12.sp,
                                    color = VibrantGrayText
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = VibrantPurple,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- Integrated Navigation Map Box ---
        if (user.isApproved && user.isOnline) {
            item {
                Text(
                    text = "Mapa de Navegación",
                    fontWeight = FontWeight.Bold,
                    color = VibrantText,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                MapSimulator(
                    activeTrip = activeTrip,
                    onlineDrivers = if (user.isOnline) listOf(user) else emptyList(),
                    animationProgress = animProgress
                )
            }
        }

        // --- Active Journey Monitor Screen ---
        if (activeTrip != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateGray),
                    border = BorderStroke(1.dp, NeonCyan)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Map, contentDescription = null, tint = NeonCyan)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "VIAJE ACTIVO #MTR-${activeTrip!!.id}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = VibrantText
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .background(VibrantLilac, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = activeTrip!!.status,
                                    color = VibrantDarkPurple,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Divider(color = VibrantBorder, modifier = Modifier.padding(vertical = 12.dp))

                        // Routing directions simulation
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, tint = VibrantPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Orígen (Recogida):", fontSize = 10.sp, color = VibrantGrayText)
                                Text(activeTrip!!.originName, color = VibrantText, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = SoftOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Destino (Entrega):", fontSize = 10.sp, color = VibrantGrayText)
                                Text(activeTrip!!.destinationName, color = VibrantText, fontSize = 12.sp)
                            }
                        }

                        Divider(color = VibrantBorder, modifier = Modifier.padding(vertical = 12.dp))

                        // Price structures
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("DISTANCIA / ETA", fontSize = 10.sp, color = VibrantGrayText)
                                Text("${activeTrip!!.distanceKm} km (~${activeTrip!!.durationMinutes} min)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VibrantText)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("TARIFA BRUTA", fontSize = 10.sp, color = VibrantGrayText)
                                Text(String.format("$%.2f USD", activeTrip!!.priceUsd), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = VibrantText)
                                Text(String.format("%.2f VES", activeTrip!!.priceVes), fontSize = 11.sp, color = VibrantGrayText)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("TU GANANCIA (80%)", fontSize = 10.sp, color = VibrantGrayText)
                                Text(String.format("$%.2f USD", activeTrip!!.priceUsd * 0.8), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF006A6A))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Next step button mapping
                        if (activeTrip!!.status == "ACCEPTED") {
                            Button(
                                onClick = { viewModel.startTrip(activeTrip!!.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = VibrantPurple),
                                modifier = Modifier.fillMaxWidth().testTag("start_ride_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("INICIAR RUTA (Pasajero Aborda)", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else if (activeTrip!!.status == "IN_PROGRESS") {
                            Button(
                                onClick = { viewModel.completeTrip(activeTrip!!.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006A6A)),
                                modifier = Modifier.fillMaxWidth().testTag("complete_ride_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DoneOutline, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("COMPLETAR VIAJE (Cobrar Cliente)", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else if (user.isApproved && user.isOnline) {
            // --- Incoming requests offer screen ---
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RssFeed, contentDescription = null, tint = VibrantPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ofertas de Viajes Pendientes",
                        fontWeight = FontWeight.Bold,
                        color = VibrantText,
                        fontSize = 14.sp
                    )
                }
            }

            if (pendingTrips.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SlateGray)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.ElectricBike, contentDescription = null, tint = VibrantGrayText, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No hay solicitudes pendientes en rango.",
                                color = VibrantGrayText,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Consejo: Cambia al portal Cliente para solicitar un viaje.",
                                color = VibrantPurple.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                items(pendingTrips) { trip ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SlateGray),
                        border = BorderStroke(1.dp, VibrantPurple.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "¡NUEVO PEDIDO #MTR-${trip.id}!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = VibrantPurple
                                )
                                Text(
                                    "${trip.distanceKm} km (~${trip.durationMinutes} min)",
                                    fontSize = 12.sp,
                                    color = VibrantGrayText
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Recoger en: ${trip.originName}", fontSize = 12.sp, color = VibrantText)
                            Text("Entrega en: ${trip.destinationName}", fontSize = 12.sp, color = VibrantText)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Ganancia neta (80%):", fontSize = 10.sp, color = VibrantGrayText)
                                    Row {
                                        Text(
                                            String.format("$%.2f USD", trip.priceUsd * 0.8),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF006A6A)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            String.format("(%.2f VES)", trip.priceVes * 0.8),
                                            fontSize = 12.sp,
                                            color = VibrantGrayText
                                        )
                                    }
                                }

                                Button(
                                    onClick = { viewModel.acceptTrip(trip.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = VibrantPurple),
                                    modifier = Modifier.testTag("accept_trip_button")
                                ) {
                                    Text("ACEPTAR VIAJE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Driver Completed trip histories lists ---
        if (user.isApproved) {
            item {
                Text(
                    text = "Historial de Rutas Completadas",
                    fontWeight = FontWeight.Bold,
                    color = VibrantText,
                    fontSize = 14.sp
                )
            }

            if (driverHistory.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No registras viajes finalizados hoy.", color = VibrantGrayText, fontSize = 12.sp)
                    }
                }
            } else {
                items(driverHistory) { trip ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SlateGray)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("MTR-${trip.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = VibrantText)
                                Text("COMPLETADO", fontSize = 11.sp, color = Color(0xFF006A6A), fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("De: ${trip.originName}", fontSize = 11.sp, color = VibrantGrayText)
                            Text("A: ${trip.destinationName}", fontSize = 11.sp, color = VibrantGrayText)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${trip.distanceKm} km", fontSize = 11.sp, color = VibrantGrayText)
                                Text(
                                    String.format("Ganaste: $%.2f USD", trip.priceUsd * 0.8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF006A6A)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
