package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MotorideViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    val currentUser: StateFlow<UserAccount?>
    val allUsers: StateFlow<List<UserAccount>>
    val allDrivers: StateFlow<List<UserAccount>>
    val onlineDrivers: StateFlow<List<UserAccount>>
    val allTrips: StateFlow<List<Trip>>
    val pendingTrips: StateFlow<List<Trip>>
    val allSupportTickets: StateFlow<List<SupportTicket>>

    // Flow for active client trip
    private val _activeClientTrip = MutableStateFlow<Trip?>(null)
    val activeClientTrip: StateFlow<Trip?> = _activeClientTrip

    // Flow for active driver trip
    private val _activeDriverTrip = MutableStateFlow<Trip?>(null)
    val activeDriverTrip: StateFlow<Trip?> = _activeDriverTrip

    private val _exchangeRate = MutableStateFlow(36.5)
    val exchangeRate: StateFlow<Double> = _exchangeRate
    
    private val _appConfigs = MutableStateFlow<Map<String, String>>(emptyMap())
    val appConfigs: StateFlow<Map<String, String>> = _appConfigs

    private val _baseRate = MutableStateFlow(1.20)
    val baseRate: StateFlow<Double> = _baseRate

    // Current map coordinate simulations for visual tracking animation
    private val _driverAnimProgress = MutableStateFlow(0f)
    val driverAnimProgress: StateFlow<Float> = _driverAnimProgress

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AppRepository(db.dao())

        currentUser = repository.currentUser
        allUsers = repository.allUsers.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        allDrivers = repository.allDrivers.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        onlineDrivers = repository.onlineDrivers.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        allTrips = repository.allTrips.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        pendingTrips = repository.pendingTrips.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        allSupportTickets = repository.allSupportTickets.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        viewModelScope.launch {
            repository.allConfigsFlow.collect { configs ->
                val map = configs.associate { it.key to it.valString }
                _appConfigs.value = map
            }
        }

        viewModelScope.launch {
            // Seed defaults
            repository.seedDatabaseIfEmpty()
            
            // Collect initial rates
            repository.exchangeRateFlow.collect { rateConfig ->
                _exchangeRate.value = rateConfig?.valDouble ?: 36.5
            }
        }

        viewModelScope.launch {
            repository.baseRateFlow.collect { baseConfig ->
                _baseRate.value = baseConfig?.valDouble ?: 1.20
            }
        }

        // React to login changes to bind client or driver active trip
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    if (user.role == "CLIENT") {
                        repository.getActiveTripForClient(user.id).collect { trip ->
                            _activeClientTrip.value = trip
                            // Reset animation progress on new trip
                            if (trip?.status == "ACCEPTED" || trip?.status == "IN_PROGRESS") {
                                triggerDriverSimulatedMovement()
                            }
                        }
                    } else if (user.role == "DRIVER") {
                        repository.getActiveTripForDriver(user.id).collect { trip ->
                            _activeDriverTrip.value = trip
                        }
                    }
                } else {
                    _activeClientTrip.value = null
                    _activeDriverTrip.value = null
                }
            }
        }
    }

    // Trigger simulation of drivers traveling or arriving
    private fun triggerDriverSimulatedMovement() {
        viewModelScope.launch {
            _driverAnimProgress.value = 0f
            // Progressively increase driver trace on map in ticks
            for (i in 1..20) {
                kotlinx.coroutines.delay(1000)
                _driverAnimProgress.value = i / 20f
            }
        }
    }

    // === Authentication Operations ===
    fun login(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.login(email)
            onResult(success)
        }
    }

    fun selectUserDirectly(user: UserAccount) {
        viewModelScope.launch {
            repository.selectUserDirectly(user)
        }
    }

    fun logout() {
        repository.logout()
    }

    fun register(
        email: String,
        fullName: String,
        role: String,
        phoneNumber: String,
        licensePlate: String = "",
        vehicleModel: String = "",
        onResult: (UserAccount?) -> Unit
    ) {
        viewModelScope.launch {
            val user = repository.register(email, fullName, role, phoneNumber, licensePlate, vehicleModel)
            onResult(user)
        }
    }

    // === Client Portal Operations ===
    fun requestTrip(
        origin: String,
        destination: String,
        distance: Double,
        paymentMethod: String,
        onCreated: (Trip) -> Unit
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val trip = repository.createTripRequest(
                clientId = user.id,
                originName = origin,
                destinationName = destination,
                distanceKm = distance,
                paymentMethod = paymentMethod
            )
            onCreated(trip)
        }
    }

    fun cancelActiveTrip(tripId: Int) {
        viewModelScope.launch {
            repository.cancelTrip(tripId)
        }
    }

    fun rateActiveTrip(tripId: Int, stars: Int) {
        viewModelScope.launch {
            repository.submitRating(tripId, stars)
        }
    }

    fun addWalletBalance(amount: Double) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.addFundsToWallet(user.id, amount)
        }
    }

    fun fileSupportTicket(description: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.createSupportTicket(user.id, user.fullName, description)
        }
    }

    // === Conductor Portal Operations ===
    fun toggleOnlineStatus(isOnline: Boolean) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleDriverOnline(user.id, isOnline)
        }
    }

    fun acceptTrip(tripId: Int) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.acceptTripRequest(tripId, user.id)
        }
    }

    fun startTrip(tripId: Int) {
        viewModelScope.launch {
            repository.startActiveTrip(tripId)
        }
    }

    fun completeTrip(tripId: Int) {
        viewModelScope.launch {
            repository.completeActiveTrip(tripId)
        }
    }

    // === Admin Operations ===
    fun approveDriver(driverId: Int) {
        viewModelScope.launch {
            repository.approveDriver(driverId)
        }
    }

    fun suspendDriver(driverId: Int) {
        viewModelScope.launch {
            repository.rejectOrSuspendDriver(driverId)
        }
    }

    fun updateRates(exchangeRate: Double, baseKmRate: Double) {
        viewModelScope.launch {
            repository.saveExchangeSettings(exchangeRate, baseKmRate)
            _exchangeRate.value = exchangeRate
            _baseRate.value = baseKmRate
        }
    }

    fun updateConfig(key: String, value: String) {
        viewModelScope.launch {
            repository.saveConfig(key, value)
        }
    }
}
