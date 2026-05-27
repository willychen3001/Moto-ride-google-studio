package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ClientPortalView(
    viewModel: MotorideViewModel,
    user: UserAccount
) {
    val activeTrip by viewModel.activeClientTrip.collectAsState()
    val onlineDrivers by viewModel.onlineDrivers.collectAsState()
    val allTrips by viewModel.allTrips.collectAsState()
    val exchangeRate by viewModel.exchangeRate.collectAsState()
    val configs by viewModel.appConfigs.collectAsState()

    // Config extraction
    val logoTextVal = configs["app_logo_text"] ?: "MOTORIDE"
    val customSectionTitleVal = configs["custom_section_title"] ?: "Mapa de Solicitud"
    val customSectionContentVal = configs["custom_section_content"] ?: "Selecciona la distancia y método de pago para pedir tu moto"
    val appHeaderTextVal = configs["app_header_text"] ?: "¿Soporte o Reclamos de Viajes?"
    val appFooterTextVal = configs["app_footer_text"] ?: "© 2026 Motoride. Todos los derechos reservados."
    val baseRate by viewModel.baseRate.collectAsState()
    val animProgress by viewModel.driverAnimProgress.collectAsState()

    // Form inputs
    var originText by remember { mutableStateOf("") }
    var destText by remember { mutableStateOf("") }
    var selectedDistance by remember { mutableStateOf(5.0) } // Default 5 km
    var paymentMethod by remember { mutableStateOf("WALLET") } // WALLET, CASH, PAGO_MOVIL
    var showReportSheet by remember { mutableStateOf(false) }
    var supportText by remember { mutableStateOf("") }
    var isRatingActive by remember { mutableStateOf(false) }
    var customRating by remember { mutableStateOf(5) }

    // Client history
    val clientHistory = remember(allTrips, user) {
        allTrips.filter { it.clientId == user.id }
    }

    // Dynamic price helper
    val estimatedUsd = selectedDistance * baseRate
    val estimatedVes = estimatedUsd * exchangeRate

    // Clean trigger for rating popup when trip completes
    LaunchedEffect(activeTrip?.status) {
        if (activeTrip?.status == "COMPLETED" && activeTrip?.rating == null) {
            isRatingActive = true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Client Header ---
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
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User",
                                    tint = VibrantDarkPurple
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = VibrantText
                                )
                                Text(
                                    text = "Cliente Registrado",
                                    fontSize = 12.sp,
                                    color = VibrantGrayText
                                )
                            }
                        }

                        // Logout / Change role button
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

                    // Billet/Wallet section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Billetera Digital",
                                fontSize = 11.sp,
                                color = VibrantGrayText,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format("$%.2f USD", user.walletBalanceUsd),
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp,
                                color = VibrantPurple
                            )
                            Text(
                                text = String.format("%.2f VES (Ref)", user.walletBalanceUsd * exchangeRate),
                                fontSize = 12.sp,
                                color = VibrantGrayText
                            )
                        }

                        // Fast Refill buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.addWalletBalance(15.0) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkGray),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp).testTag("refill_15")
                            ) {
                                Text("+$15", fontSize = 11.sp, color = VibrantPurple)
                            }
                            Button(
                                onClick = { viewModel.addWalletBalance(50.0) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkGray),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp).testTag("refill_50")
                            ) {
                                Text("+$50", fontSize = 11.sp, color = VibrantPurple)
                            }
                        }
                    }
                }
            }
        }

        // --- Active Maps and Route Section ---
        item {
            Text(
                text = "Mapa de Solicitud",
                fontWeight = FontWeight.Bold,
                color = VibrantText,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            MapSimulator(
                activeTrip = activeTrip,
                onlineDrivers = onlineDrivers,
                animationProgress = animProgress
            )
        }

        // --- Active Trip Controller Card ---
        if (activeTrip != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateGray),
                    border = BorderStroke(1.dp, NeonLime.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "VIAJE SOLICITADO #MTR-${activeTrip!!.id}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NeonLime
                            )

                            // Status badge
                            val badgeColor = when (activeTrip!!.status) {
                                "PENDING" -> SoftOrange
                                "ACCEPTED" -> NeonLime
                                "IN_PROGRESS" -> NeonCyan
                                else -> Color.Green
                            }
                            Box(
                                modifier = Modifier
                                    .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = activeTrip!!.status,
                                    color = badgeColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Route details
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, tint = VibrantPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(activeTrip!!.originName, color = VibrantText, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = SoftOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(activeTrip!!.destinationName, color = VibrantText, fontSize = 13.sp)
                        }

                        Divider(color = VibrantBorder, modifier = Modifier.padding(vertical = 12.dp))

                        // Price and stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("DISTANCIA / ETA", fontSize = 10.sp, color = VibrantGrayText)
                                Text("${activeTrip!!.distanceKm} km (~${activeTrip!!.durationMinutes} min)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = VibrantText)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("TARIFA", fontSize = 10.sp, color = VibrantGrayText)
                                Text(String.format("$%.2f USD", activeTrip!!.priceUsd), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = VibrantPurple)
                                Text(String.format("%.2f VES", activeTrip!!.priceVes), fontSize = 11.sp, color = VibrantGrayText)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("PAGO", fontSize = 10.sp, color = VibrantGrayText)
                                Text(activeTrip!!.paymentMethod, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VibrantPurple)
                            }
                        }

                        // Driver assignment description
                        if (activeTrip!!.status == "PENDING") {
                            Spacer(modifier = Modifier.height(14.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(4.dp),
                                color = VibrantPurple,
                                trackColor = DarkGray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Esperando que un conductor cercano apruebe la solicitud en moto...",
                                fontSize = 11.sp,
                                color = VibrantGrayText
                            )
                        } else if (activeTrip!!.driverId != null) {
                            // Driver info card
                            Spacer(modifier = Modifier.height(14.dp))
                            Text("SU PILOTO ASIGNADO", fontSize = 10.sp, color = VibrantPurple, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DarkGray, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ElectricBike, contentDescription = null, tint = VibrantPurple, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Operador #0${activeTrip!!.driverId}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VibrantText)
                                    Text("Moto de seguridad asignada", fontSize = 11.sp, color = VibrantGrayText)
                                }
                            }
                        }

                        // Cancellation triggers
                        if (activeTrip!!.status == "PENDING" || activeTrip!!.status == "ACCEPTED") {
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedButton(
                                onClick = { viewModel.cancelActiveTrip(activeTrip!!.id) },
                                modifier = Modifier.fillMaxWidth().testTag("cancel_ride"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                                border = BorderStroke(1.dp, Color(0xFFFF5252))
                            ) {
                                Text("Cancelar Solicitud de Viaje")
                            }
                        }
                    }
                }
            }
        } else {
            // --- Custom dynamic informational banner ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { },
                    colors = CardDefaults.cardColors(containerColor = SlateGray.copy(alpha = 0.5f)),
                    border = BorderStroke(0.5.dp, VibrantBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = customSectionTitleVal.uppercase(),
                            color = VibrantPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = customSectionContentVal,
                            color = VibrantText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }

            // --- Normal Booking Form Block ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SOLICITAR " + logoTextVal.uppercase() + " YA",
                            fontWeight = FontWeight.Bold,
                            color = NeonLime,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Origin pick
                        val prebuiltOrigins = listOf(
                            "Plaza Venezuela, Caracas",
                            "Altamira, Chacao",
                            "Estación Metro Chacaíto",
                            "Centro Comercial Sambil, Chacao"
                        )
                        var originExpanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = originExpanded,
                            onExpandedChange = { originExpanded = !originExpanded }
                        ) {
                            OutlinedTextField(
                                value = originText,
                                onValueChange = { originText = it },
                                label = { Text("Punto de Partida (Origen)") },
                                leadingIcon = { Icon(Icons.Default.MyLocation, contentDescription = null, tint = NeonLime) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonLime,
                                    unfocusedBorderColor = Color(0xFF2D3139)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = originExpanded,
                                onDismissRequest = { originExpanded = false }
                            ) {
                                prebuiltOrigins.forEach { origin ->
                                    DropdownMenuItem(
                                        text = { Text(origin) },
                                        onClick = {
                                            originText = origin
                                            originExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Destination pick
                        val prebuiltDests = listOf(
                            "Las Mercedes, Baruta",
                            "Centro Comercial Ciudad Tamanaco (CCCT)",
                            "Plaza Bolívar de Hatillo",
                            "Plaza Bolívar de Caracas"
                        )
                        var destExpanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = destExpanded,
                            onExpandedChange = { destExpanded = !destExpanded }
                        ) {
                            OutlinedTextField(
                                value = destText,
                                onValueChange = { destText = it },
                                label = { Text("Destino de Llegada") },
                                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = SoftOrange) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonLime,
                                    unfocusedBorderColor = Color(0xFF2D3139)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = destExpanded,
                                onDismissRequest = { destExpanded = false }
                            ) {
                                prebuiltDests.forEach { dest ->
                                    DropdownMenuItem(
                                        text = { Text(dest) },
                                        onClick = {
                                            destText = dest
                                            destExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Simulated Distance slider
                        Column(modifier = Modifier.padding(vertical = 12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Distancia del Recorrido", fontSize = 12.sp, color = VibrantGrayText)
                                Text(String.format("%.1f km", selectedDistance), fontWeight = FontWeight.Bold, color = VibrantPurple)
                            }
                            Slider(
                                value = selectedDistance.toFloat(),
                                onValueChange = { selectedDistance = it.toDouble() },
                                valueRange = 1.0f..25.0f,
                                colors = SliderDefaults.colors(
                                    thumbColor = VibrantPurple,
                                    activeTrackColor = VibrantPurple,
                                    inactiveTrackColor = DarkGray
                                )
                            )
                        }

                        // Payment Methods row selector
                        Text("Método de Pago", fontSize = 12.sp, color = VibrantGrayText, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val methods = listOf(
                                Triple("WALLET", "Mi Wallet", Icons.Default.AccountBalanceWallet),
                                Triple("PAGO_MOVIL", "Pago Móvil", Icons.Default.QrCode),
                                Triple("CASH", "Efectivo", Icons.Default.AttachMoney)
                            )

                            methods.forEach { (m, label, icon) ->
                                val selected = paymentMethod == m
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(if (selected) DarkGray else Color.Transparent, RoundedCornerShape(8.dp))
                                        .border(
                                            1.dp,
                                            if (selected) VibrantPurple else VibrantBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { paymentMethod = m }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(icon, contentDescription = null, tint = if (selected) VibrantPurple else Color.Gray, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selected) VibrantDarkPurple else Color.Gray)
                                    }
                                }
                            }
                        }

                        // Error checks
                        val errorMsg = when {
                            originText.isEmpty() -> "Elija origen"
                            destText.isEmpty() -> "Elija destino"
                            paymentMethod == "WALLET" && user.walletBalanceUsd < estimatedUsd -> "Fondos insuficientes en Wallet"
                            onlineDrivers.isEmpty() -> "No hay choferes cercanos disponibles en este momento"
                            else -> null
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color(0xFF2D3139), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Fare estimate display
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Previsualización Tarifa:", fontSize = 11.sp, color = VibrantGrayText)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = String.format("$%.2f USD", estimatedUsd),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = VibrantPurple
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = String.format("(%.2f VES)", estimatedVes),
                                        fontSize = 12.sp,
                                        color = VibrantGrayText
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.requestTrip(originText, destText, selectedDistance, paymentMethod) {
                                        // Auto clear inputs
                                    }
                                },
                                enabled = errorMsg == null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VibrantPurple,
                                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.testTag("submit_ride_request")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ElectricBike, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("PEDIR MOTO", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (errorMsg != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(errorMsg, fontSize = 11.sp, color = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        // --- Rating Dialog Alert (Success modal) ---
        if (isRatingActive) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateGray),
                    border = BorderStroke(1.dp, Color.Green.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("¡VIAJE COMPLETADO CON ÉXITO!", fontWeight = FontWeight.Bold, color = VibrantText, fontSize = 15.sp)
                        Text("Por favor, califique al motorizado de seguridad:", fontSize = 12.sp, color = VibrantGrayText)
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Row of stars
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (star in 1..5) {
                                val selected = star <= customRating
                                Icon(
                                    imageVector = if (selected) Icons.Default.Star else Icons.Default.StarOutline,
                                    contentDescription = null,
                                    tint = if (selected) VibrantPurple else Color.Gray,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { customRating = star }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                clientHistory.firstOrNull { it.status == "COMPLETED" && it.rating == null }?.let {
                                    viewModel.rateActiveTrip(it.id, customRating)
                                }
                                isRatingActive = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPurple),
                            modifier = Modifier.fillMaxWidth().testTag("submit_rating_btn")
                        ) {
                            Text("Guardar Opinión", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- Supportive Report Dialog trigger ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showReportSheet = !showReportSheet },
                colors = CardDefaults.cardColors(containerColor = SlateGray)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = VibrantPurple)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(appHeaderTextVal, fontWeight = FontWeight.Bold, color = VibrantText, fontSize = 13.sp)
                            Text("Reporta incidentes o fallas directo aquí", fontSize = 11.sp, color = VibrantGrayText)
                        }
                    }
                    Icon(
                        imageVector = if (showReportSheet) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = VibrantPurple
                    )
                }
            }
        }

        if (showReportSheet) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Iniciar Reporte de Soporte", fontWeight = FontWeight.Bold, color = VibrantText, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = supportText,
                            onValueChange = { supportText = it },
                            placeholder = { Text("Escribe reclamo o consulta aquí...", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (supportText.isNotBlank()) {
                                    viewModel.fileSupportTicket(supportText)
                                    supportText = ""
                                    showReportSheet = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPurple),
                            enabled = supportText.isNotBlank(),
                            modifier = Modifier.align(Alignment.End).testTag("send_support_ticket")
                        ) {
                            Text("Enviar Boleto", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // --- Client Journey History Lists ---
        item {
            Text(
                text = "Historial de Viajes",
                fontWeight = FontWeight.Bold,
                color = VibrantText,
                fontSize = 14.sp
            )
        }

        if (clientHistory.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay registros de viajes previos.", color = VibrantGrayText, fontSize = 12.sp)
                }
            }
        } else {
            items(clientHistory) { trip ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateGray)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "MTR-${trip.id}  •  ${trip.status}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (trip.status == "COMPLETED") Color(0xFF006A6A) else VibrantGrayText
                            )

                            // Rating stars display
                            if (trip.rating != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = VibrantPurple, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("${trip.rating}", fontSize = 11.sp, color = VibrantText)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Origen: ${trip.originName}", fontSize = 11.sp, color = VibrantGrayText)
                        Text("Destino: ${trip.destinationName}", fontSize = 11.sp, color = VibrantGrayText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${trip.distanceKm} km",
                                fontSize = 11.sp,
                                color = VibrantGrayText
                            )
                            Text(
                                String.format("$%.2f USD (%.2f VES)", trip.priceUsd, trip.priceVes),
                                fontSize = 11.sp,
                                color = VibrantPurple,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = appFooterTextVal,
                    color = VibrantGrayText,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp)) // Safe bottom spacing
        }
    }
}
