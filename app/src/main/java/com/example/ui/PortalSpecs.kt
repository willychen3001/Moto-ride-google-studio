package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepBlack
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.SoftOrange

@Composable
fun PortalSpecsView(onDismiss: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Base de Datos", "Flujo Secuencial", "Stack Tecnológico", "Endpoints API")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Specs",
                    tint = NeonLime,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Blueprints & Arquitectura",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Diseño de Sistemas - Motoride",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }

        // Horizontal tabs for specifications
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = NeonLime,
            edgePadding = 0.dp,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Specifications Contents
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF2D3139), RoundedCornerShape(12.dp))
                .background(Color(0xFF141517))
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> DatabaseArchitectureTab()
                1 -> SequenceFlowTab()
                2 -> TechStackTab()
                3 -> ApiEndpointsTab()
            }
        }
    }
}

@Composable
fun DatabaseArchitectureTab() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Storage, contentDescription = "BD", tint = NeonLime)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Esquema Relacional (Base de Datos)",
                color = NeonLime,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Diseñado con PostgreSQL (compatible con CockroachDB / Spanner para escalabilidad geográfica en tiempo real):",
            color = Color.LightGray,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Table 1: Users
        TableSchemaBox(
            tableName = "users (Usuarios de la Plataforma)",
            columns = listOf(
                "id : UUID (Primary Key)",
                "email : VARCHAR(255) (Unique / Index)",
                "password_hash : VARCHAR(255) (Bcrypt / JWT auth)",
                "full_name : VARCHAR(100)",
                "phone_number : VARCHAR(20)",
                "role : VARCHAR(20) [CLIENT, DRIVER, ADMIN]",
                "is_approved : BOOLEAN (Default: false, required for Drivers)",
                "license_plate : VARCHAR(20) (Drivers only)",
                "vehicle_model : VARCHAR(100) (Drivers only)",
                "is_online : BOOLEAN (Default: false)",
                "wallet_balance_usd : DECIMAL(10,2) (Default: 0.00)",
                "rating : DECIMAL(3,2) (Default: 5.00)"
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Table 2: Trips
        TableSchemaBox(
            tableName = "trips (Servicios / Viajes)",
            columns = listOf(
                "id : UUID (Primary Key)",
                "client_id : UUID (Foreign Key -> users.id)",
                "driver_id : UUID (Foreign Key -> users.id, Nullable)",
                "origin_name : VARCHAR(255)",
                "destination_name : VARCHAR(255)",
                "origin_geom : GEOMETRY(Point, 4326) (Spatially Index)",
                "destination_geom : GEOMETRY(Point, 4326)",
                "distance_km : DECIMAL(5,2)",
                "duration_minutes : INT",
                "price_usd : DECIMAL(8,2) (Base price stored in USD)",
                "price_ves : DECIMAL(12,2) (Calculated at creation rate)",
                "exchange_rate : DECIMAL(8,2) (Saved rate at booking)",
                "payment_method : VARCHAR(20) [CASH, PAGO_MOVIL, WALLET]",
                "status : VARCHAR(30) [PENDING, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED]",
                "rating : INT (1-5, Nullable)",
                "created_at : TIMESTAMP (Default: NOW())",
                "completed_at : TIMESTAMP (Nullable)"
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Table 3: System Configurations & Exchange Rates
        TableSchemaBox(
            tableName = "system_configs (Configuraciones de Sistema)",
            columns = listOf(
                "key : VARCHAR(50) (Primary Key) [eg. 'exchange_rate', 'base_rate_km']",
                "val_double : DECIMAL(12,4)",
                "val_string : TEXT",
                "updated_at : TIMESTAMP (Default: CURRENT_TIMESTAMP)"
            )
        )
    }
}

@Composable
fun SequenceFlowTab() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Troubleshoot, contentDescription = "Flow", tint = NeonCyan)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Flujo Lógico del Servicio (Motoride)",
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Ciclo de vida de una solicitud de transporte de motos en tiempo real:",
            color = Color.LightGray,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        val flowSteps = listOf(
            Triple("1. Cotización y Búsqueda", "El Cliente introduce origen y destino. El backend calcula distancia mediante Ruteo Geográfico, cotiza en USD, calcula el precio equivalente en VES (usando la tasa de cambio vigente) y muestra conductores activos en rango con índices postGIS.", NeonLime),
            Triple("2. Solicitud Activa (PENDING)", "El Cliente confirma la solicitud. El pedido se inserta con estado 'PENDING'. Se emite un evento Pub/Sub vía Websockets (o SSE) a todos los Drivers disponibles calificados que estén online dentro de un radio de 3 km.", Color.White),
            Triple("3. Aceptación del Driver (ACCEPTED)", "El Driver recibe notificación de viaje con ruta trazada. El primero en pulsar 'Aceptar Viaje' cambia el estado a 'ACCEPTED', bloqueando el viaje. El cliente recibe confirmación en tiempo real con la ficha y matrícula del motorizado.", SoftOrange),
            Triple("4. Abordaje e Inicia Ruta (IN_PROGRESS)", "El motorizado llega al origen. Al recoger al cliente, activa el switch 'Iniciar Viaje', cambiando el estado a 'IN_PROGRESS'. Se actualizan las coordenadas GPS dinámicamente cada 5 segundos.", NeonCyan),
            Triple("5. Finalización y Pago (COMPLETED)", "El Driver marca el viaje como 'Completado'. Si el pago es Wallet, se debita del cliente y se dispersa el 80% al Driver y 20% al Admin. Si es Pago Móvil o Efectivo, se realiza la transacción directa y se descuenta comisión de la billetera del conductor. Ambos pueden calificarse mutuamente.", Color.Green)
        )

        flowSteps.forEach { step ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .background(Color(0xFF1E2022).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF2D3139), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(step.third, RoundedCornerShape(5.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step.first,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = step.second,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun TechStackTab() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Dns, contentDescription = "Stack", tint = SoftOrange)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Sugerencia de Stack Tecnológico",
                color = SoftOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Arquitectura corporativa capaz de soportar alta concurrencia, geolocalización continua y transacciones de billetera segura:",
            color = Color.LightGray,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        val techSpecs = listOf(
            "MÓVIL (Client & Driver)" to "Jetpack Compose (Android nativo fluido) o Flutter si se requiere multiplataforma nativo. Integración con Mapbox SDK o Google Maps APIs para renderizado vectorial off-thread.",
            "BACKEND ENGINE" to "Node.js (TypeScript) con Fastify o NestJS para APIs de baja latencia y alta E/S, o Go (Golang) para el microservicio crítico de emparejamiento geográfico.",
            "WEBSOCKET CONNECTIONS" to "Socket.io o WebSockets nativos de Go para conectar clientes/conductores en tiempo real con tolerancia a desconexión y latencias menores a 100ms.",
            "COMUNICACIÓN Y MENSAJERÍA" to "Redis Pub/Sub para coordinar eventos geográficos rápidos. Kafka / RabbitMQ como bus de eventos duraderos para auditorías de transacciones financieras.",
            "BASES DE DATOS" to "PostgreSQL con PostGIS para almacenamiento duradero de perfiles y ruteo geoespacial rápido. Redis para caché y almacenamiento de última coordenada en vivo de conductores.",
            "INFRAESTRUCTURA" to "Docker habilitado en clusters Kubernetes (EKS/GKE). Gateway de pagos integrada para soporte de divisas (Binance Pay P2P API, Stripe, y conciliador automático para Pago Móvil / Banco Central)."
        )

        techSpecs.forEach { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = item.first,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = SoftOrange,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.second,
                    fontSize = 13.sp,
                    color = Color.LightGray,
                    lineHeight = 18.sp
                )
                Divider(color = Color(0xFF2D3139), thickness = 0.5.dp, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
fun ApiEndpointsTab() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Terminal, contentDescription = "API", tint = NeonLime)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Diseño de la API REST (Principales Endpoints)",
                color = NeonLime,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Formato unificado JSON. Autenticación robusta Bearer JWT requerida.",
            color = Color.Gray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Endpoint 1: Auth
        EndpointBox(
            method = "POST",
            route = "/api/v1/auth/login",
            desc = "Autentica al usuario devolviendo token JWT y de acuerdo con su rol se efectúa la redirección.",
            body = "{\n  \"email\": \"maria@gmail.com\",\n  \"password\": \"********\"\n}",
            response = "{\n  \"status\": \"success\",\n  \"token\": \"eyJhbGciOi...\",\n  \"user\": {\n    \"id\": \"c60fa2d0...\",\n    \"fullName\": \"María\",\n    \"role\": \"CLIENT\"\n  }\n}"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Endpoint 2: Create Trip
        EndpointBox(
            method = "POST",
            route = "/api/v1/trips/request",
            desc = "El cliente inicia la reserva de un viaje calculando precio en USD y VES.",
            body = "{\n  \"originText\": \"Altamira, Caracas\",\n  \"destText\": \"Las Mercedes\",\n  \"originLat\": 10.4939,\n  \"originLng\": -66.8373,\n  \"destLat\": 10.4815,\n  \"destLng\": -66.8590,\n  \"paymentMethod\": \"WALLET\"\n}",
            response = "{\n  \"tripId\": \"t_883a9de0...\",\n  \"status\": \"PENDING\",\n  \"distanceKm\": 5.2,\n  \"priceUsd\": 6.24,\n  \"priceVes\": 227.76,\n  \"exchangeRateAtBooking\": 36.50\n}"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Endpoint 3: Driver Locations
        EndpointBox(
            method = "GET",
            route = "/api/v1/drivers/nearby",
            desc = "Obtiene conductores activos y aprobados en un radio geográfico (PostGIS).",
            body = "Query Params: ?lat=10.4939&lng=-66.8373&radius_meters=3000",
            response = "{\n  \"count\": 2,\n  \"drivers\": [\n    {\n      \"driverId\": \"d_553b821\",\n      \"name\": \"Carlos Mendoza\",\n      \"lat\": 10.4912,\n      \"lng\": -66.8385,\n      \"vehicle\": \"Suzuki V-Strom\"\n    }\n  ]\n}"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Endpoint 4: Accept Trip
        EndpointBox(
            method = "PUT",
            route = "/api/v1/trips/{id}/accept",
            desc = "El conductor acepta una solicitud de viaje pendiente.",
            body = "{\n  \"driverId\": \"d_553b821\"\n}",
            response = "{\n  \"tripId\": \"t_883a9de0\",\n  \"status\": \"ACCEPTED\",\n  \"matchedDriver\": {\n    \"fullName\": \"Carlos Mendoza\",\n    \"licensePlate\": \"MD-7A41\"\n  }\n}"
        )
    }
}

@Composable
fun TableSchemaBox(tableName: String, columns: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF2D3139), RoundedCornerShape(8.dp))
            .background(DeepBlack)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.TableChart, contentDescription = null, tint = NeonLime, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Tabla: $tableName",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        columns.forEach { col ->
            Text(
                text = "  •  $col",
                fontSize = 12.sp,
                color = Color.LightGray,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(vertical = 1.dp)
            )
        }
    }
}

@Composable
fun EndpointBox(method: String, route: String, desc: String, body: String, response: String) {
    val methodColor = when (method) {
        "POST" -> NeonLime
        "GET" -> NeonCyan
        "PUT" -> SoftOrange
        else -> Color.Gray
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF2D3139), RoundedCornerShape(8.dp))
            .background(DeepBlack)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .background(methodColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = method,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = route,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = desc,
            color = Color.LightGray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("JSON Body:", fontSize = 11.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF141517), RoundedCornerShape(4.dp))
                .border(0.5.dp, Color(0xFF2D3139), RoundedCornerShape(4.dp))
                .padding(8.dp)
        ) {
            Text(
                text = body,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Color(0xFFA5C261)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        Text("Example Response:", fontSize = 11.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF141517), RoundedCornerShape(4.dp))
                .border(0.5.dp, Color(0xFF2D3139), RoundedCornerShape(4.dp))
                .padding(8.dp)
        ) {
            Text(
                text = response,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = NeonCyan
            )
        }
    }
}
