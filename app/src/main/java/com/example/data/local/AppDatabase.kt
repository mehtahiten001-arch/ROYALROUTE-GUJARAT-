package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.BoardingPointDao
import com.example.data.local.dao.BookingDao
import com.example.data.local.dao.BusDao
import com.example.data.local.dao.SeatDao
import com.example.data.local.entity.BoardingPointEntity
import com.example.data.local.entity.BookingEntity
import com.example.data.local.entity.BusEntity
import com.example.data.local.entity.SeatEntity

@Database(
    entities = [
        BusEntity::class,
        SeatEntity::class,
        BookingEntity::class,
        BoardingPointEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun busDao(): BusDao
    abstract fun seatDao(): SeatDao
    abstract fun bookingDao(): BookingDao
    abstract fun boardingPointDao(): BoardingPointDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "royal_route_gujarat.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
