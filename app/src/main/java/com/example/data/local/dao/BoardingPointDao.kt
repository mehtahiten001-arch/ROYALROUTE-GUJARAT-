package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.BoardingPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardingPointDao {
    @Query("SELECT * FROM boarding_points WHERE busId = :busId AND pointType = :pointType")
    fun getPointsForBus(busId: String, pointType: String): Flow<List<BoardingPointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<BoardingPointEntity>)

    @Query("DELETE FROM boarding_points WHERE busId = :busId")
    suspend fun deletePointsForBus(busId: String)
}
