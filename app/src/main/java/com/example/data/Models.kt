package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val fullName: String,
    val role: String, // CLIENT, DRIVER, ADMIN
    val phoneNumber: String,
    val isApproved: Boolean = false, // Drivers must be approved by Admin
    val licensePlate: String = "",
    val vehicleModel: String = "",
    val isOnline: Boolean = false, // Active/Inactive for drivers
    val walletBalanceUsd: Double = 0.0
)

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientId: Int,
    val driverId: Int? = null,
    val originName: String,
    val destinationName: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val priceUsd: Double,
    val priceVes: Double,
    val status: String, // PENDING, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
    val paymentMethod: String, // CASH, PAGO_MOVIL, WALLET
    val rating: Int? = null,
    val timestamp: Long = System.currentTimeMillis(),
    // Simulated coordinate boundaries for simple route drawing
    val originX: Float,
    val originY: Float,
    val destX: Float,
    val destY: Float
)

@Entity(tableName = "system_config")
data class SystemConfig(
    @PrimaryKey val key: String,
    val valDouble: Double = 0.0,
    val valString: String = ""
)

@Entity(tableName = "support_tickets")
data class SupportTicket(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val userName: String,
    val description: String,
    val status: String, // OPEN, RESOLVED
    val timestamp: Long = System.currentTimeMillis()
)
