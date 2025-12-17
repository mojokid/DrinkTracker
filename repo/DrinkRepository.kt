package com.example.weeklyalcoholtracker.repo

import com.example.weeklyalcoholtracker.db.DrinkDao
import com.example.weeklyalcoholtracker.db.DrinkEntryEntity
import com.example.weeklyalcoholtracker.prefs.PrefsDataStore
import com.example.weeklyalcoholtracker.util.WeekUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.ZoneId

class DrinkRepository(
    private val dao: DrinkDao,
    private val prefs: PrefsDataStore
) {

    fun observeAll(): Flow<List<DrinkEntryEntity>> =
        dao.observeAll()

    suspend fun addDrink(
        drinkName: String,
        abvPercent: Double,
        volumeMl: Double
    ) {
        val p = prefs.prefsFlow.first()

        val weekKey = WeekUtils.weekStartKey(
            System.currentTimeMillis(),
            p.weekStartDay,
            p.weekStartMinuteOfDay,
            ZoneId.systemDefault()
        )

        dao.insert(
            DrinkEntryEntity(
                timestamp = System.currentTimeMillis(),
                weekKey = weekKey,
                drinkName = drinkName,
                abvPercent = abvPercent,
                volumeMl = volumeMl
            )
        )
    }
}