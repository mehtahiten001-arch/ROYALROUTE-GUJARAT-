package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.SeatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeatDao {
    @Query("SELECT * FROM seats WHERE busId = :busId ORDER BY deck DESC, row ASC, column ASC")
    fun getSeatsForBus(busId: String): Flow<List<SeatEntity>>

    @Query("SELECT * FROM seats WHERE busId = :busId AND deck = :deck ORDER BY row ASC, column ASC")
    fun getSeatsByDeck(busId: String, deck: String): Flow<List<SeatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeats(seats: List<SeatEntity>)

    @Update
    suspend fun updateSeat(seat: SeatEntity)

    @Query("UPDATE seats SET status = :status WHERE busId = :busId AND seatNumber IN (:seatNumbers)")
    suspend fun updateSeatsStatus(busId: String, seatNumbers: List<String>, status: String)

    @Query("UPDATE seats SET status = 'AVAILABLE' WHERE busId = :busId AND seatNumber IN (:seatNumbers)")
    suspend fun releaseSeats(busId: String, seatNumbers: List<String>)
}
