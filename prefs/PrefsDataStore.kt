package com.example.weeklyalcoholtracker.prefs

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "weekly_alcohol_prefs")

class PrefsDataStore(private val context: Context) {

    object PrefKeys {
        val WEEK_START_DAY = intPreferencesKey("week_start_day")               // 1..7
        val WEEK_START_MINUTES = intPreferencesKey("week_start_minutes")       // 0..1439
        val WEEKLY_LIMIT = doublePreferencesKey("weekly_limit_servings")
        val USE_METRIC = booleanPreferencesKey("use_metric")
        val CARRY_OVER = booleanPreferencesKey("carry_over")
        val CARRY_PENALTY = intPreferencesKey("carry_penalty_percent")
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")

        // Last-used drink fields
        val LAST_DRINK_NAME = stringPreferencesKey("last_drink_name")
        val LAST_DRINK_ABV = doublePreferencesKey("last_drink_abv")
        val LAST_USE_METRIC = booleanPreferencesKey("last_use_metric")
    }

    val prefsFlow: Flow<UserPrefs> =
        context.dataStore.data.map { p ->
            val useMetric = p[PrefKeys.USE_METRIC] ?: true
            UserPrefs(
                weekStartDay = p[PrefKeys.WEEK_START_DAY] ?: 7,               // Sunday
                weekStartMinuteOfDay = p[PrefKeys.WEEK_START_MINUTES] ?: 120, // 2:00 AM
                weeklyServingsLimit = p[PrefKeys.WEEKLY_LIMIT] ?: 7.0,
                useMetric = useMetric,
                carryOverEnabled = p[PrefKeys.CARRY_OVER] ?: false,
                carryOverPenaltyPercent = p[PrefKeys.CARRY_PENALTY] ?: 50,
                notificationsEnabled = p[PrefKeys.NOTIFICATIONS] ?: true,

                lastDrinkName = p[PrefKeys.LAST_DRINK_NAME],
                lastDrinkAbvPercent = p[PrefKeys.LAST_DRINK_ABV],
                lastUseMetric = p[PrefKeys.LAST_USE_METRIC] ?: useMetric
            )
        }

    suspend fun update(transform: (UserPrefs) -> UserPrefs) {
        context.dataStore.edit { p ->
            val currentUseMetric = p[PrefKeys.USE_METRIC] ?: true
            val current = UserPrefs(
                weekStartDay = p[PrefKeys.WEEK_START_DAY] ?: 7,
                weekStartMinuteOfDay = p[PrefKeys.WEEK_START_MINUTES] ?: 120,
                weeklyServingsLimit = p[PrefKeys.WEEKLY_LIMIT] ?: 7.0,
                useMetric = currentUseMetric,
                carryOverEnabled = p[PrefKeys.CARRY_OVER] ?: false,
                carryOverPenaltyPercent = p[PrefKeys.CARRY_PENALTY] ?: 50,
                notificationsEnabled = p[PrefKeys.NOTIFICATIONS] ?: true,
                lastDrinkName = p[PrefKeys.LAST_DRINK_NAME],
                lastDrinkAbvPercent = p[PrefKeys.LAST_DRINK_ABV],
                lastUseMetric = p[PrefKeys.LAST_USE_METRIC] ?: currentUseMetric
            )

            val next = transform(current)

            p[PrefKeys.WEEK_START_DAY] = next.weekStartDay
            p[PrefKeys.WEEK_START_MINUTES] = next.weekStartMinuteOfDay
            p[PrefKeys.WEEKLY_LIMIT] = next.weeklyServingsLimit
            p[PrefKeys.USE_METRIC] = next.useMetric
            p[PrefKeys.CARRY_OVER] = next.carryOverEnabled
            p[PrefKeys.CARRY_PENALTY] = next.carryOverPenaltyPercent
            p[PrefKeys.NOTIFICATIONS] = next.notificationsEnabled

            // last drink fields
            if (next.lastDrinkName == null) p.remove(PrefKeys.LAST_DRINK_NAME) else p[PrefKeys.LAST_DRINK_NAME] = next.lastDrinkName
            if (next.lastDrinkAbvPercent == null) p.remove(PrefKeys.LAST_DRINK_ABV) else p[PrefKeys.LAST_DRINK_ABV] = next.lastDrinkAbvPercent
            p[PrefKeys.LAST_USE_METRIC] = next.lastUseMetric
        }
    }
}