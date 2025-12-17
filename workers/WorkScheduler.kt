package com.example.weeklyalcoholtracker.workers

import android.content.Context
import androidx.work.*
import com.example.weeklyalcoholtracker.prefs.PrefsDataStore
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import java.time.Instant

object WorkScheduler {

    private const val UNIQUE_WEEKLY_RESET = "weekly_reset_work"

    fun scheduleWeeklyReset(context: Context) {
        // Schedule a one-time worker for the next week start.
        // After it runs, the worker schedules the next one again.
        val workManager = WorkManager.getInstance(context)

        val req = OneTimeWorkRequestBuilder<WeeklyResetWorker>()
            .setInitialDelay(nextResetDelayMillis(context), TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniqueWork(
            UNIQUE_WEEKLY_RESET,
            ExistingWorkPolicy.REPLACE,
            req
        )
    }

    private fun nextResetDelayMillis(context: Context): Long {
        val zone = ZoneId.systemDefault()
        val prefs = kotlinx.coroutines.runBlocking {
            PrefsDataStore(context).prefsFlow.first()
        }

        val nextInstant = com.example.weeklyalcoholtracker.util.WeekUtils.nextWeekStartInstant(
            zoneId = zone,
            weekStartIsoDay = prefs.weekStartDay,
            weekStartMinuteOfDay = prefs.weekStartMinuteOfDay
        )

        val now = Instant.now()
        val d = Duration.between(now, nextInstant).toMillis()
        return d.coerceAtLeast(5_000L)
    }
}