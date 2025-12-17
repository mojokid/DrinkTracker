package com.example.weeklyalcoholtracker.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drink_entries")
data class DrinkEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val timestamp: Long,
    val weekKey: String,

    val drinkName: String,
    val abvPercent: Double,

    // 🔑 canonical stored amount
    val volumeMl: Double
)