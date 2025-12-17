package com.example.weeklyalcoholtracker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DrinkEntryEntity::class],
    version = 2, // 🔺 bump version
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun drinkDao(): DrinkDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "drinks.db"
                )
                    .fallbackToDestructiveMigration() // 🔑 dev-safe
                    .build()
                    .also { INSTANCE = it }
            }
    }
}