package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserAccount
import com.example.ui.theme.*

@Composable
fun AdminPortalView(
    viewModel: MotorideViewModel,
    user: UserAccount
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val allDrivers by viewModel.allDrivers.collectAsState()
    val allTrips by viewModel.allTrips.collectAsState()
    val exchangeRate by viewModel.exchangeRate.collectAsState()
    val baseRate by viewModel.baseRate.collectAsState()
    val supportTickets by viewModel.allSupportTickets.collectAsState()
    val configs by viewModel.appConfigs.collectAsState()

    // Tab state
    var activeTab by remember { mutableStateOf("OPERATIONS") } // OPERATIONS vs SETTINGS

    // Base rate and conversion inputs
    var inputExchangeRate by remember(exchangeRate) { mutableStateOf(exchangeRate.toString()) }
    var inputBaseRate by remember(baseRate) { mutableStateOf(baseRate.toString()) }
    var configSaveMessage by remember { mutableStateOf("") }

    // Dynamic config inputs
    var customAppName by remember(configs) { mutableStateOf(configs["app_name"] ?: "MOTORIDE") }
    var customLogoText by remember(configs) { mutableStateOf(configs["app_logo_text"] ?: "MOTORIDE") }
    var customParagraph by remember(configs) { mutableStateOf(configs["app_paragraph"] ?: "Moto Uber Urbano en Tiempo Real") }
    var customSecTitle by remember(configs) { mutableStateOf(configs["custom_section_title"] ?: "Mapa de Solicitud") }
    var customSecContent by remember(configs) { mutableStateOf(configs["custom_section_content"] ?: "Selecciona la distancia y método de pago para pedir tu moto") }
    var customHeaderText by remember(configs) { mutableStateOf(configs["app_header_text"] ?: "¿Soporte o Reclamos de Viajes?") }
    var customFooterText by remember(configs) { mutableStateOf(configs["app_footer_text"] ?: "© 2026 Motoride") }
    var customPhoneText by remember(configs) { mutableStateOf(configs["extra_detail_phone"] ?: "+58 412-5551234") }
    var customStatusText by remember(configs) { mutableStateOf(configs["extra_detail_status"] ?: "Operando 24/7") }
    var settingsSaveMessage by remember { mutableStateOf("") }

    // Computations
    val totalVolumeUsd = remember(allTrips) {
        allTrips.filter { it.status == "COMPLETED" }.sumOf { it.priceUsd }
    }
    val systemRevenueUsd = remember(allTrips) {
        allTrips.filter { it.status == "COMPLETED" }.sumOf { it.priceUsd * 0.20 }
    }
    val totalUsers = allUsers.size
    val totalDrivers = allDrivers.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Admin Header ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateGray)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {},
                            colors = IconButtonDefaults.iconButtonColors(containerColor = VibrantPurple)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Panel Administrativo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = VibrantText
                            )
                            Text(
                                text = "Sesión: ${user.fullName}",
                                fontSize = 11.sp,
                                color = VibrantGrayText
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.logout() },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = DarkGray)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = "Log Out", tint = VibrantGrayText)
                    }
                }
            }
        }

        // --- Interactive Segmented Navigation Row ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateGray, RoundedCornerShape(8.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { activeTab = "OPERATIONS" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == "OPERATIONS") VibrantPurple else Color.Transparent,
                        contentColor = if (activeTab == "OPERATIONS") Color.White else VibrantGrayText
                    ),
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Operaciones", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { activeTab = "SETTINGS" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == "SETTINGS") VibrantPurple else Color.Transparent,
                        contentColor = if (activeTab == "SETTINGS") Color.White else VibrantGrayText
                    ),
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Configuración", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (activeTab == "OPERATIONS") {
            // === OPERATIONS DASHBOARD TAB ===
            
            // --- Platform Statistics Summary Grid ---
            item {
                Text(
                    "Métricas Globales de Operación",
                    fontWeight = FontWeight.Bold,
                    color = VibrantText,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Stat 1: Revenue
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = SlateGray)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("COMISIÓN PLATAFORMA (20%)", fontSize = 9.sp, color = VibrantGrayText, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    String.format("$%.2f USD", systemRevenueUsd),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = VibrantPurple
                                )
                                Text(
                                    String.format("%.2f VES", systemRevenueUsd * exchangeRate),
                                    fontSize = 11.sp,
                                    color = VibrantGrayText
                                )
                            }
                        }

                        // Stat 2: Total volume
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = SlateGray)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("VOLUMEN TRANSADO", fontSize = 9.sp, color = VibrantGrayText, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    String.format("$%.2f USD", totalVolumeUsd),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = VibrantPurple
                                )
                                Text(
                                    String.format("%.2f VES", totalVolumeUsd * exchangeRate),
                                    fontSize = 11.sp,
                                    color = VibrantGrayText
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Stat 3: Users
                        Card(
                            modifier = Modifier.weight(1.0f),
                            colors = CardDefaults.cardColors(containerColor = SlateGray)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("REGISTRADOS", fontSize = 9.sp, color = VibrantGrayText, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$totalUsers Usuarios", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = VibrantText)
                                Text("$totalDrivers Conductores", fontSize = 11.sp, color = VibrantGrayText)
                            }
                        }

                        // Stat 4: Trips
                        Card(
                            modifier = Modifier.weight(1.0f),
                            colors = CardDefaults.cardColors(containerColor = SlateGray)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("VIAJES COMPLETADOS", fontSize = 9.sp, color = VibrantGrayText, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${allTrips.count { it.status == "COMPLETED" }} Completados", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = VibrantText)
                                Text("${allTrips.count { it.status == "CANCELLED" }} Cancelados", fontSize = 11.sp, color = VibrantGrayText)
                            }
                        }
                    }
                }
            }

            // --- Driver Approval Management Queue ---
            item {
                Text(
                    text = "Solicitudes de Conductores (${allDrivers.size})",
                    fontWeight = FontWeight.Bold,
                    color = VibrantText,
                    fontSize = 14.sp
                )
            }

            if (allDrivers.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No hay conductores registrados.", color = VibrantGrayText, fontSize = 12.sp)
                    }
                }
            } else {
                items(allDrivers) { driver ->
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
                                Column {
                                    Text(driver.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = VibrantText)
                                    Text("Placa: ${driver.licensePlate}  •  ${driver.vehicleModel}", fontSize = 11.sp, color = VibrantGrayText)
                                    Text("Tel: ${driver.phoneNumber}", fontSize = 11.sp, color = VibrantGrayText)
                                }

                                // Approval status tag
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (driver.isApproved) Color(0xFFE0F2F1) else Color(0xFFFFEBEE),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (driver.isApproved) "APROBADO" else "PENDIENTE",
                                        color = if (driver.isApproved) Color(0xFF006A6A) else Color(0xFFC62828),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = VibrantBorder, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Billetera: " + String.format("$%.2f USD", driver.walletBalanceUsd),
                                    fontSize = 12.sp,
                                    color = VibrantPurple,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (!driver.isApproved) {
                                        Button(
                                            onClick = { viewModel.approveDriver(driver.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006A6A)),
                                            modifier = Modifier.height(34.dp).testTag("approve_driver_${driver.id}")
                                        ) {
                                            Text("Aprobar", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = { viewModel.suspendDriver(driver.id) },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                                            border = BorderStroke(1.dp, Color(0xFFC62828)),
                                            modifier = Modifier.height(34.dp).testTag("suspend_driver_${driver.id}")
                                        ) {
                                            Text("Suspender", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- Live Support Tickets Log ---
            item {
                Text(
                    text = "Reportes de Soporte Técnico (${supportTickets.size})",
                    fontWeight = FontWeight.Bold,
                    color = VibrantText,
                    fontSize = 14.sp
                )
            }

            if (supportTickets.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text("No hay boletos de soporte abiertos.", color = VibrantGrayText, fontSize = 12.sp)
                    }
                }
            } else {
                items(supportTickets) { ticket ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SlateGray)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Boleto #${ticket.id}  •  ${ticket.userName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = VibrantPurple
                                )
                                Text(
                                    text = ticket.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (ticket.status == "OPEN") SoftOrange else VibrantGrayText
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = ticket.description,
                                fontSize = 12.sp,
                                color = VibrantGrayText
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        } else {
            // === CONFIGURATION AND BRANDING PANEL TAB ===
            item {
                Text(
                    "Configuración Visual y Funcional de la App",
                    fontWeight = FontWeight.Bold,
                    color = VibrantText,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Section A: Core Attributes (Name, Logo Slogan)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateGray)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SettingsSystemDaydream, contentDescription = null, tint = VibrantPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "IDENTIFICACIÓN Y LOGO",
                                fontWeight = FontWeight.Bold,
                                color = VibrantPurple,
                                fontSize = 11.sp
                            )
                        }

                        OutlinedTextField(
                            value = customAppName,
                            onValueChange = { customAppName = it },
                            label = { Text("Nombre del Sistema", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("config_app_name"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            )
                        )

                        OutlinedTextField(
                            value = customLogoText,
                            onValueChange = { customLogoText = it },
                            label = { Text("Logotipo Texto", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("config_logo_text"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            )
                        )

                        OutlinedTextField(
                            value = customParagraph,
                            onValueChange = { customParagraph = it },
                            label = { Text("Eslogan o Párrafo Introductorio", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("config_paragraph"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            )
                        )
                    }
                }
            }

            // Section B: Custom Sections & Support Questions
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateGray)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FeaturedPlayList, contentDescription = null, tint = VibrantPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CONTENIDOS DINÁMICOS & SECCIONES",
                                fontWeight = FontWeight.Bold,
                                color = VibrantPurple,
                                fontSize = 11.sp
                            )
                        }

                        OutlinedTextField(
                            value = customSecTitle,
                            onValueChange = { customSecTitle = it },
                            label = { Text("Título de Sección Principal", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("config_section_title"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            )
                        )

                        OutlinedTextField(
                            value = customSecContent,
                            onValueChange = { customSecContent = it },
                            label = { Text("Instrucción del Servicio", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("config_section_content"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            )
                        )

                        OutlinedTextField(
                            value = customHeaderText,
                            onValueChange = { customHeaderText = it },
                            label = { Text("Pregunta de la Cabecera de Soporte", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("config_header_text"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            )
                        )
                    }
                }
            }

            // Section C: Rates and Tariffs Configuration
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateGray)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = VibrantPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SISTEMA DE TASAS Y TARIFAS",
                                fontWeight = FontWeight.Bold,
                                color = VibrantPurple,
                                fontSize = 11.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = inputExchangeRate,
                                onValueChange = { inputExchangeRate = it },
                                label = { Text("Tasa (VES / USD)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f).testTag("config_exchange_rate"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VibrantPurple,
                                    unfocusedBorderColor = VibrantBorder
                                )
                            )

                            OutlinedTextField(
                                value = inputBaseRate,
                                onValueChange = { inputBaseRate = it },
                                label = { Text("Tarifa ($ / KM)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f).testTag("config_base_rate"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VibrantPurple,
                                    unfocusedBorderColor = VibrantBorder
                                )
                            )
                        }
                    }
                }
            }

            // Section D: Extra details & Footer Signature
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SlateGray)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Assignment, contentDescription = null, tint = VibrantPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "INFORMACIÓN EXTRA Y PIE DE PÁGINA",
                                fontWeight = FontWeight.Bold,
                                color = VibrantPurple,
                                fontSize = 11.sp
                            )
                        }

                        OutlinedTextField(
                            value = customPhoneText,
                            onValueChange = { customPhoneText = it },
                            label = { Text("Teléfono de Soporte del Sistema", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("config_phone_extra"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            )
                        )

                        OutlinedTextField(
                            value = customStatusText,
                            onValueChange = { customStatusText = it },
                            label = { Text("Estado del Servidor / Operativo", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("config_status_extra"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            )
                        )

                        OutlinedTextField(
                            value = customFooterText,
                            onValueChange = { customFooterText = it },
                            label = { Text("Texto Pie de Página (Copyright)", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("config_footer"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = VibrantBorder
                            )
                        )
                    }
                }
            }

            // Action / Save Area
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 80.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (settingsSaveMessage.isNotEmpty()) {
                        Text(
                            text = settingsSaveMessage,
                            color = Color(0xFF006A6A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(1.dp).weight(1f))
                    }

                    Button(
                        onClick = {
                            viewModel.updateConfig("app_name", customAppName)
                            viewModel.updateConfig("app_logo_text", customLogoText)
                            viewModel.updateConfig("app_paragraph", customParagraph)
                            viewModel.updateConfig("custom_section_title", customSecTitle)
                            viewModel.updateConfig("custom_section_content", customSecContent)
                            viewModel.updateConfig("app_header_text", customHeaderText)
                            viewModel.updateConfig("app_footer_text", customFooterText)
                            viewModel.updateConfig("extra_detail_phone", customPhoneText)
                            viewModel.updateConfig("extra_detail_status", customStatusText)

                            val rate = inputExchangeRate.toDoubleOrNull()
                            val base = inputBaseRate.toDoubleOrNull()
                            if (rate != null && base != null) {
                                viewModel.updateRates(rate, base)
                                settingsSaveMessage = "Configuración Actualizada Exitosamente"
                            } else {
                                settingsSaveMessage = "Contenidos guardados (tasas inválidas omitidas)"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006A6A)),
                        modifier = Modifier.testTag("save_all_configs_btn"),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar Todo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
