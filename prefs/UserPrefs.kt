package com.example.weeklyalcoholtracker.prefs

data class UserPrefs(
    val weekStartDay: Int,               // 1=Mon ... 7=Sun (ISO)
    val weekStartMinuteOfDay: Int,       // 0..1439
    val weeklyServingsLimit: Double,
    val useMetric: Boolean,
    val carryOverEnabled: Boolean,
    val carryOverPenaltyPercent: Int,
    val notificationsEnabled: Boolean,

    // Remember last-used drink fields for prefill
    val lastDrinkName: String?,
    val lastDrinkAbvPercent: Double?,
    val lastUseMetric: Boolean
)