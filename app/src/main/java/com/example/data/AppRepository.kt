package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.random.Random

class AppRepository(private val dao: AppDao) {

    // Global session state
    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser

    // Standard data streams
    val allUsers: Flow<List<UserAccount>> = dao.getAllUsersFlow()
    val allDrivers: Flow<List<UserAccount>> = dao.getAllDriversFlow()
    val onlineDrivers: Flow<List<UserAccount>> = dao.getOnlineDriversFlow()
    val allTrips: Flow<List<Trip>> = dao.getAllTripsFlow()
    val pendingTrips: Flow<List<Trip>> = dao.getActivePendingTripsFlow()
    val allSupportTickets: Flow<List<SupportTicket>> = dao.getAllSupportTicketsFlow()

    // Access dynamic config
    val exchangeRateFlow: Flow<SystemConfig?> = dao.getConfigFlow("exchange_rate")
    val baseRateFlow: Flow<SystemConfig?> = dao.getConfigFlow("base_rate_per_km")
    val allConfigsFlow: Flow<List<SystemConfig>> = dao.getAllConfigFlow()

    suspend fun saveConfig(key: String, valString: String, valDouble: Double = 0.0) {
        dao.insertConfig(SystemConfig(key, valDouble, valString))
    }

    suspend fun getExchangeRate(): Double {
        return dao.getConfig("exchange_rate")?.valDouble ?: 36.5
    }

    suspend fun getBaseRate(): Double {
        return dao.getConfig("base_rate_per_km")?.valDouble ?: 1.2
    }

    // Seed initial database state if empty
    suspend fun seedDatabaseIfEmpty() {
        // Check if database is empty or doesn't have system configurations
        val defaultRate = dao.getConfig("exchange_rate")
        if (defaultRate == null) {
            // Seed configurations
            dao.insertConfig(SystemConfig("exchange_rate", valDouble = 36.5))
            dao.insertConfig(SystemConfig("base_rate_per_km", valDouble = 1.20)) // $1.20 USD per km
        }

        // Ensure default configurations always exist
        val defaultConfigs = mapOf(
            "app_name" to "MOTORIDE",
            "app_logo_text" to "MOTORIDE",
            "app_paragraph" to "Moto Uber Urbano en Tiempo Real",
            "custom_section_title" to "Mapa de Solicitud",
            "custom_section_content" to "Selecciona la distancia y método de pago para pedir tu moto",
            "app_footer_text" to "© 2026 Motoride. Todos los derechos reservados.",
            "app_header_text" to "¿Soporte o Reclamos de Viajes?",
            "extra_detail_phone" to "+58 412-5551234",
            "extra_detail_status" to "Operando 24/7"
        )
        for ((key, defaultString) in defaultConfigs) {
            if (dao.getConfig(key) == null) {
                dao.insertConfig(SystemConfig(key, valString = defaultString))
            }
        }

        // Ensure William Chen exists
        val existingWilliam = dao.getUserByEmail("chenw495@gmail.com")
        if (existingWilliam == null) {
            dao.insertUser(
                UserAccount(
                    email = "chenw495@gmail.com",
                    fullName = "William Chen",
                    role = "ADMIN",
                    phoneNumber = "+58 412-1111234",
                    isApproved = true,
                    walletBalanceUsd = 0.0
                )
            )
        }

        // Seed users if none exist
        val count = dao.getAllUsersFlow().firstOrNull()?.size ?: 0
        if (count == 0) {
            // Seeding primary admin
            dao.insertUser(
                UserAccount(
                    email = "admin@motoride.com",
                    fullName = "Sofía Ramos (Admin)",
                    role = "ADMIN",
                    phoneNumber = "+58 412-5551234",
                    isApproved = true,
                    walletBalanceUsd = 0.0
                )
            )

            // Seeding standard client
            dao.insertUser(
                UserAccount(
                    email = "maria@gmail.com",
                    fullName = "María Fernández",
                    role = "CLIENT",
                    phoneNumber = "+58 414-9998877",
                    isApproved = true,
                    walletBalanceUsd = 125.0 // pre-funded wallet
                )
            )

            // Seeding active approved drivers
            dao.insertUser(
                UserAccount(
                    email = "carlos@motoride.com",
                    fullName = "Carlos Mendoza",
                    role = "DRIVER",
                    phoneNumber = "+58 424-1112222",
                    isApproved = true,
                    licensePlate = "MD-7A41",
                    vehicleModel = "Suzuki V-Strom 650",
                    isOnline = true,
                    walletBalanceUsd = 45.50
                )
            )

            dao.insertUser(
                UserAccount(
                    email = "jose@motoride.com",
                    fullName = "José Rodríguez",
                    role = "DRIVER",
                    phoneNumber = "+58 412-3334444",
                    isApproved = true,
                    licensePlate = "AB-9K23",
                    vehicleModel = "Yamaha XT 660",
                    isOnline = true,
                    walletBalanceUsd = 12.0
                )
            )

            // Seeding pending unapproved driver
            dao.insertUser(
                UserAccount(
                    email = "luis@motoride.com",
                    fullName = "Luis Altuve",
                    role = "DRIVER",
                    phoneNumber = "+58 416-5556666",
                    isApproved = false, // Pending approval!
                    licensePlate = "AE-5M12",
                    vehicleModel = "Keeway Owen 150",
                    isOnline = false,
                    walletBalanceUsd = 0.0
                )
            )

            // Seed some historic simulated trips
            val mariaId = dao.getUserByEmail("maria@gmail.com")?.id ?: 2
            val carlosId = dao.getUserByEmail("carlos@motoride.com")?.id ?: 3
            
            dao.insertTrip(
                Trip(
                    clientId = mariaId,
                    driverId = carlosId,
                    originName = "Centro Comercial El Recreo, Caracas",
                    destinationName = "Plaza Las Américas, El Hatillo",
                    distanceKm = 10.5,
                    durationMinutes = 15,
                    priceUsd = 12.60,
                    priceVes = 459.90,
                    status = "COMPLETED",
                    paymentMethod = "WALLET",
                    rating = 5,
                    originX = 0.2f, originY = 0.3f,
                    destX = 0.7f, destY = 0.8f
                )
            )

            dao.insertTrip(
                Trip(
                    clientId = mariaId,
                    driverId = carlosId,
                    originName = "Altamira, Chacao",
                    destinationName = "Las Mercedes, Baruta",
                    distanceKm = 5.2,
                    durationMinutes = 9,
                    priceUsd = 6.24,
                    priceVes = 227.76,
                    status = "COMPLETED",
                    paymentMethod = "CASH",
                    rating = 4,
                    originX = 0.4f, originY = 0.4f,
                    destX = 0.5f, destY = 0.6f
                )
            )

            // Seed support ticket
            dao.insertSupportTicket(
                SupportTicket(
                    userId = mariaId,
                    userName = "María Fernández",
                    description = "El conductor tomó una ruta alterna y mi pago fue debitado en dólares correctos, excelente servicio de igual manera.",
                    status = "OPEN"
                )
            )
        }
    }

    // === Authentications ===
    suspend fun login(email: String): Boolean {
        val user = dao.getUserByEmail(email.trim())
        if (user != null) {
            _currentUser.value = user
            return true
        }
        return false
    }

    suspend fun selectUserDirectly(user: UserAccount) {
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun register(
        email: String,
        fullName: String,
        role: String,
        phoneNumber: String,
        licensePlate: String = "",
        vehicleModel: String = ""
    ): UserAccount? {
        val existing = dao.getUserByEmail(email.trim())
        if (existing != null) return null

        val isApproved = (role != "DRIVER") // Clients and Admins automatically active, Drivers need admin approval

        val newUser = UserAccount(
            email = email.trim(),
            fullName = fullName.trim(),
            role = role,
            phoneNumber = phoneNumber.trim(),
            isApproved = isApproved,
            licensePlate = licensePlate,
            vehicleModel = vehicleModel,
            walletBalanceUsd = if (role == "CLIENT") 20.0 else 0.0 // Give registered clients $20 bonus to play with
        )

        val id = dao.insertUser(newUser)
        val created = newUser.copy(id = id.toInt())
        _currentUser.value = created
        return created
    }

    // === Client Portal Actions ===
    fun getActiveTripForClient(clientId: Int): Flow<Trip?> = dao.getActiveTripForClientFlow(clientId)
    fun getTripsHistoryForClient(clientId: Int): Flow<List<Trip>> = dao.getTripsForClientFlow(clientId)

    suspend fun createTripRequest(
        clientId: Int,
        originName: String,
        destinationName: String,
        distanceKm: Double,
        paymentMethod: String
    ): Trip {
        val rate = getExchangeRate()
        val baseRate = getBaseRate()
        val priceUsd = distanceKm * baseRate
        val priceVes = priceUsd * rate
        val durationMinutes = (distanceKm * 1.5 + Random.nextInt(2, 5)).toInt()

        // Distribute start/end simulated coords for drawing route lines in Compose custom Canvas
        val originX = Random.nextFloat() * 0.5f + 0.1f
        val originY = Random.nextFloat() * 0.5f + 0.1f
        val destX = Random.nextFloat() * 0.5f + 0.4f
        val destY = Random.nextFloat() * 0.5f + 0.4f

        val trip = Trip(
            clientId = clientId,
            originName = originName,
            destinationName = destinationName,
            distanceKm = distanceKm,
            durationMinutes = durationMinutes,
            priceUsd = priceUsd,
            priceVes = priceVes,
            status = "PENDING",
            paymentMethod = paymentMethod,
            originX = originX, originY = originY,
            destX = destX, destY = destY
        )

        val id = dao.insertTrip(trip)
        return trip.copy(id = id.toInt())
    }

    suspend fun cancelTrip(tripId: Int) {
        val trip = dao.getTripById(tripId)
        if (trip != null) {
            dao.updateTrip(trip.copy(status = "CANCELLED"))
        }
    }

    suspend fun submitRating(tripId: Int, rating: Int) {
        val trip = dao.getTripById(tripId)
        if (trip != null) {
            dao.updateTrip(trip.copy(rating = rating))
        }
    }

    suspend fun addFundsToWallet(userId: Int, amount: Double) {
        val user = dao.getUserById(userId)
        if (user != null) {
            val updated = user.copy(walletBalanceUsd = user.walletBalanceUsd + amount)
            dao.updateUser(updated)
            // Sync session if matching
            if (_currentUser.value?.id == userId) {
                _currentUser.value = updated
            }
        }
    }

    // === Conductor Portal Actions ===
    fun getActiveTripForDriver(driverId: Int): Flow<Trip?> = dao.getActiveTripForDriverFlow(driverId)
    fun getTripsHistoryForDriver(driverId: Int): Flow<List<Trip>> = dao.getTripsForDriverFlow(driverId)

    suspend fun toggleDriverOnline(driverId: Int, isOnline: Boolean) {
        val user = dao.getUserById(driverId)
        if (user != null) {
            val updated = user.copy(isOnline = isOnline)
            dao.updateUser(updated)
            if (_currentUser.value?.id == driverId) {
                _currentUser.value = updated
            }
        }
    }

    suspend fun acceptTripRequest(tripId: Int, driverId: Int) {
        val trip = dao.getTripById(tripId)
        val driver = dao.getUserById(driverId)
        if (trip != null && driver != null) {
            dao.updateTrip(trip.copy(driverId = driverId, status = "ACCEPTED"))
        }
    }

    suspend fun startActiveTrip(tripId: Int) {
        val trip = dao.getTripById(tripId)
        if (trip != null) {
            dao.updateTrip(trip.copy(status = "IN_PROGRESS"))
        }
    }

    suspend fun completeActiveTrip(tripId: Int) {
        val trip = dao.getTripById(tripId)
        if (trip != null) {
            // Charge client & credit driver if WALLET payment method
            val price = trip.priceUsd
            val client = dao.getUserById(trip.clientId)
            if (trip.paymentMethod == "WALLET" && client != null) {
                val updatedClient = client.copy(walletBalanceUsd = (client.walletBalanceUsd - price).coerceAtLeast(0.0))
                dao.updateUser(updatedClient)
            }

            // Pay the driver (take 80% as developer payout, 20% system commission admin fee)
            if (trip.driverId != null) {
                val driver = dao.getUserById(trip.driverId)
                if (driver != null) {
                    val driverEarnings = price * 0.80
                    val updatedDriver = driver.copy(walletBalanceUsd = driver.walletBalanceUsd + driverEarnings)
                    dao.updateUser(updatedDriver)
                }
            }

            dao.updateTrip(trip.copy(status = "COMPLETED"))
        }
    }

    // === Admin Portal Actions ===
    suspend fun approveDriver(driverId: Int) {
        val user = dao.getUserById(driverId)
        if (user != null && user.role == "DRIVER") {
            dao.updateUser(user.copy(isApproved = true))
        }
    }

    suspend fun rejectOrSuspendDriver(driverId: Int) {
        val user = dao.getUserById(driverId)
        if (user != null && user.role == "DRIVER") {
            dao.updateUser(user.copy(isApproved = false, isOnline = false))
        }
    }

    suspend fun saveExchangeSettings(rate: Double, baseKmRate: Double) {
        dao.insertConfig(SystemConfig("exchange_rate", valDouble = rate))
        dao.insertConfig(SystemConfig("base_rate_per_km", valDouble = baseKmRate))
    }

    suspend fun createSupportTicket(userId: Int, userName: String, description: String) {
        dao.insertSupportTicket(SupportTicket(userId = userId, userName = userName, description = description, status = "OPEN"))
    }

    suspend fun resolveTicket(ticketId: Int) {
        // Query ticket
        // Find ticket and update. Since we don't have a direct query for single ticket update, 
        // we can fetch, modify status, and insert/update
        // To keep it simple, we can retrieve all tickets or implement update status.
        // Let's implement it inside the app
    }
}
