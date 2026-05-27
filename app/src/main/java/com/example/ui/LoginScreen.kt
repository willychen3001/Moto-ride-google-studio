package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserAccount
import com.example.ui.theme.*

@Composable
fun LoginScreenView(
    viewModel: MotorideViewModel,
    onLoginSuccess: () -> Unit
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val configs by viewModel.appConfigs.collectAsState()
    val appNameVal = configs["app_name"] ?: "MOTORIDE"
    val appParagraphVal = configs["app_paragraph"] ?: "Moto Uber Urbano en Tiempo Real"
    
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") } // Mocked security check
    var currentScreen by remember { mutableStateOf("LOGIN") } // LOGIN vs REGISTER

    // Registration inputs
    var regFullName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regRole by remember { mutableStateOf("CLIENT") } // CLIENT or DRIVER
    var regLicensePlate by remember { mutableStateOf("") }
    var regVehicleModel by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // --- Brand Header Logo ---
        Box(
            modifier = Modifier
                .size(68.dp)
                .background(
                    Brush.radialGradient(listOf(NeonLime, Color.Transparent)),
                    shape = RoundedCornerShape(20.dp)
                )
                .border(2.dp, NeonLime, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ElectricBike,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = appNameVal,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = VibrantPurple,
            letterSpacing = 2.sp
        )
        Text(
            text = appParagraphVal,
            fontSize = 13.sp,
            color = VibrantGrayText,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Switch between auth screens
        if (currentScreen == "LOGIN") {
            // === LOGIN SCREEN ===
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Inicio de Sesión Rol-Based",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NeonLime
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Correo Electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeonLime) },
                        modifier = Modifier.fillMaxWidth().testTag("email_login_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonLime,
                            unfocusedBorderColor = Color(0xFF2D3139)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Contraseña (Simulada)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeonLime) },
                        modifier = Modifier.fillMaxWidth().testTag("password_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonLime,
                            unfocusedBorderColor = Color(0xFF2D3139)
                        )
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (emailInput.isNotBlank()) {
                                if (emailInput.trim().equals("chenw495@gmail.com", ignoreCase = true)) {
                                    if (passwordInput != "chen2112") {
                                        errorMessage = "Contraseña incorrecta para el Administrador William Chen."
                                        return@Button
                                    }
                                }
                                viewModel.login(emailInput) { success ->
                                    if (success) {
                                        onLoginSuccess()
                                    } else {
                                        errorMessage = "Correo no registrado. Registra uno nuevo o usa los accesos directos de abajo."
                                    }
                                }
                            } else {
                                errorMessage = "Por favor ingresa un correo."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                        modifier = Modifier.fillMaxWidth().testTag("login_submit_btn")
                    ) {
                        Text("INGRESAR AL SISTEMA", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "¿No tienes cuenta? Regístrate aquí",
                        fontSize = 12.sp,
                        color = NeonCyan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentScreen = "REGISTER"
                                errorMessage = ""
                            },
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // === REGISTER SCREEN ===
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SlateGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Registrar Nueva Cuenta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = regFullName,
                        onValueChange = { regFullName = it },
                        label = { Text("Nombre Completo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan) },
                        modifier = Modifier.fillMaxWidth().testTag("reg_name_field"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("Correo Electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeonCyan) },
                        modifier = Modifier.fillMaxWidth().testTag("reg_email_field"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regPhone,
                        onValueChange = { regPhone = it },
                        label = { Text("Número de Teléfono") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = NeonCyan) },
                        modifier = Modifier.fillMaxWidth().testTag("reg_phone_field"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Role Radio selectors
                    Text("Selecciona tu rol:", fontSize = 12.sp, color = VibrantGrayText)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = regRole == "CLIENT",
                                onClick = { regRole = "CLIENT" },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                            )
                            Text("Cliente", fontSize = 13.sp, color = VibrantText)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = regRole == "DRIVER",
                                onClick = { regRole = "DRIVER" },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                            )
                            Text("Conductor (Piloto)", fontSize = 13.sp, color = VibrantText)
                        }
                    }

                    // Bike-specific attributes if regRole == DRIVER
                    if (regRole == "DRIVER") {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = regVehicleModel,
                            onValueChange = { regVehicleModel = it },
                            label = { Text("Modelo de Moto (ej. Yamaha, Suzuki)") },
                            leadingIcon = { Icon(Icons.Default.ElectricBike, contentDescription = null, tint = NeonCyan) },
                            modifier = Modifier.fillMaxWidth().testTag("reg_vehicle_field"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = regLicensePlate,
                            onValueChange = { regLicensePlate = it },
                            label = { Text("Número de Placa") },
                            leadingIcon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = NeonCyan) },
                            modifier = Modifier.fillMaxWidth().testTag("reg_plate_field"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan)
                        )
                    }

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMessage, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (regFullName.isBlank() || regEmail.isBlank() || regPhone.isBlank()) {
                                errorMessage = "Por favor llena los campos requeridos."
                                return@Button
                            }
                            if (regRole == "DRIVER" && (regVehicleModel.isBlank() || regLicensePlate.isBlank())) {
                                errorMessage = "Para conducir requieres moto y placa válida."
                                return@Button
                            }

                            viewModel.register(
                                email = regEmail,
                                fullName = regFullName,
                                role = regRole,
                                phoneNumber = regPhone,
                                licensePlate = regLicensePlate,
                                vehicleModel = regVehicleModel
                            ) { createdUser ->
                                if (createdUser != null) {
                                    onLoginSuccess()
                                } else {
                                    errorMessage = "Este correo ya se encuentra registrado."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                        modifier = Modifier.fillMaxWidth().testTag("register_submit_btn")
                    ) {
                        Text("COMPLETAR REGISTRO", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "¿Ya tienes cuenta? Ingresa aquí",
                        fontSize = 12.sp,
                        color = NeonLime,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentScreen = "LOGIN"
                                errorMessage = ""
                            },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Sandbox Presets / Easy Evaluation Panels ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SlateGray),
            border = BorderStroke(1.dp, VibrantBorder)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SmartButton, contentDescription = null, tint = VibrantPurple, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "ACCESO RÁPIDO DE EVALUACIÓN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = VibrantPurple,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Pulsa cualquiera de estas cuentas pre-generadas para saltar directo a su portal correspondiente y probar el servicio:",
                    fontSize = 11.sp,
                    color = VibrantGrayText,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                val evaluators = listOf(
                    Triple("Cliente: María", "maria@gmail.com", Icons.Default.Person),
                    Triple("Conductor Aprobado: Carlos", "carlos@motoride.com", Icons.Default.ElectricBike),
                    Triple("Conductor Pendiente: Luis", "luis@motoride.com", Icons.Default.HourglassEmpty),
                    Triple("Admin: William Chen", "chenw495@gmail.com", Icons.Default.Security)
                )

                evaluators.forEach { (label, email, icon) ->
                    Button(
                        onClick = {
                            emailInput = email
                            if (email == "chenw495@gmail.com") {
                                passwordInput = "chen2112"
                            } else {
                                passwordInput = "12345"
                            }
                            viewModel.login(email) { onLoginSuccess() }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("quick_login_${email.replace("@", "_")}"),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, contentDescription = null, tint = VibrantPurple, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(label, fontSize = 12.sp, color = VibrantDarkPurple, fontWeight = FontWeight.Bold)
                            }
                            Text(email, fontSize = 10.sp, color = VibrantGrayText, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}
