package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // === Users ===
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount): Long

    @Update
    suspend fun updateUser(user: UserAccount)

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserByIdFlow(id: Int): Flow<UserAccount?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): UserAccount?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccount?

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserAccount>>

    @Query("SELECT * FROM users WHERE role = 'DRIVER'")
    fun getAllDriversFlow(): Flow<List<UserAccount>>

    @Query("SELECT * FROM users WHERE role = 'DRIVER' AND isOnline = 1 AND isApproved = 1")
    fun getOnlineDriversFlow(): Flow<List<UserAccount>>

    @Query("SELECT * FROM users WHERE role = 'DRIVER' AND isOnline = 1 AND isApproved = 1")
    suspend fun getOnlineDrivers(): List<UserAccount>


    // === Trips ===
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long

    @Update
    suspend fun updateTrip(trip: Trip)

    @Query("SELECT * FROM trips WHERE id = :id")
    fun getTripByIdFlow(id: Int): Flow<Trip?>

    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getTripById(id: Int): Trip?

    @Query("SELECT * FROM trips WHERE clientId = :clientId ORDER BY timestamp DESC")
    fun getTripsForClientFlow(clientId: Int): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE driverId = :driverId ORDER BY timestamp DESC")
    fun getTripsForDriverFlow(driverId: Int): Flow<List<Trip>>

    @Query("SELECT * FROM trips ORDER BY timestamp DESC")
    fun getAllTripsFlow(): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE status = 'PENDING' ORDER BY timestamp ASC")
    fun getActivePendingTripsFlow(): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE clientId = :clientId AND status IN ('PENDING', 'ACCEPTED', 'IN_PROGRESS') LIMIT 1")
    fun getActiveTripForClientFlow(clientId: Int): Flow<Trip?>

    @Query("SELECT * FROM trips WHERE driverId = :driverId AND status IN ('ACCEPTED', 'IN_PROGRESS') LIMIT 1")
    fun getActiveTripForDriverFlow(driverId: Int): Flow<Trip?>


    // === Support Tickets ===
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportTicket(ticket: SupportTicket): Long

    @Update
    suspend fun updateSupportTicket(ticket: SupportTicket)

    @Query("SELECT * FROM support_tickets ORDER BY timestamp DESC")
    fun getAllSupportTicketsFlow(): Flow<List<SupportTicket>>


    // === Config ===
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: SystemConfig)

    @Query("SELECT * FROM system_config WHERE `key` = :key LIMIT 1")
    suspend fun getConfig(key: String): SystemConfig?

    @Query("SELECT * FROM system_config WHERE `key` = :key LIMIT 1")
    fun getConfigFlow(key: String): Flow<SystemConfig?>

    @Query("SELECT * FROM system_config")
    fun getAllConfigFlow(): Flow<List<SystemConfig>>
}
