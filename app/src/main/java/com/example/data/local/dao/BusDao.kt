package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusDao {
    @Query("SELECT * FROM buses WHERE isActive = 1")
    fun getAllActiveBuses(): Flow<List<BusEntity>>

    @Query("SELECT * FROM buses WHERE fromCity = :fromCity AND toCity = :toCity AND isActive = 1")
    fun searchBuses(fromCity: String, toCity: String): Flow<List<BusEntity>>

    @Query("SELECT * FROM buses WHERE id = :busId LIMIT 1")
    suspend fun getBusById(busId: String): BusEntity?

    @Query("SELECT * FROM buses")
    fun getAllBusesForAdmin(): Flow<List<BusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBus(bus: BusEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuses(buses: List<BusEntity>)

    @Update
    suspend fun updateBus(bus: BusEntity)

    @Query("DELETE FROM buses WHERE id = :busId")
    suspend fun deleteBus(busId: String)
}
