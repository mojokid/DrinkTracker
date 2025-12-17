package com.example.weeklyalcoholtracker.util

import java.time.*

object WeekUtils {

    fun weekStartKey(
        nowMs: Long,
        weekStartIsoDay: Int,
        weekStartMinuteOfDay: Int,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): String {
        val now = Instant.ofEpochMilli(nowMs).atZone(zoneId)
        val start = currentWeekStart(now, weekStartIsoDay, weekStartMinuteOfDay)
        return start.toLocalDate().toString() // YYYY-MM-DD
    }

    fun nextWeekStartInstant(
        zoneId: ZoneId,
        weekStartIsoDay: Int,
        weekStartMinuteOfDay: Int
    ): Instant {
        val now = ZonedDateTime.now(zoneId)
        val currentStart = currentWeekStart(now, weekStartIsoDay, weekStartMinuteOfDay)
        val next = currentStart.plusDays(7)
        return next.toInstant()
    }

    private fun currentWeekStart(
        now: ZonedDateTime,
        weekStartIsoDay: Int,
        weekStartMinuteOfDay: Int
    ): ZonedDateTime {
        val startDay = DayOfWeek.of(weekStartIsoDay)
        val hour = (weekStartMinuteOfDay / 60).coerceIn(0, 23)
        val minute = (weekStartMinuteOfDay % 60).coerceIn(0, 59)

        // Candidate start in *this* calendar week (going backwards to startDay)
        var d = now.toLocalDate()
        while (d.dayOfWeek != startDay) d = d.minusDays(1)
        var candidate = d.atTime(hour, minute).atZone(now.zone)

        // If we haven't reached the start time yet, go back one week
        if (now.toInstant().isBefore(candidate.toInstant())) {
            candidate = candidate.minusDays(7)
        }
        return candidate
    }
}