package com.example.weeklyalcoholtracker.workers

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weeklyalcoholtracker.R
import com.example.weeklyalcoholtracker.prefs.PrefsDataStore
import kotlinx.coroutines.flow.first

class WeeklyResetWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        // We “reset” by doing nothing destructive: the app shows current week by weekKey.
        // But we can notify the user and reschedule the next reset.

        val prefs = PrefsDataStore(applicationContext).prefsFlow.first()
        if (prefs.notificationsEnabled) {
            sendResetNotification(applicationContext)
        }

        // Always reschedule next week reset.
        WorkScheduler.scheduleWeeklyReset(applicationContext)
        return Result.success()
    }

    private fun sendResetNotification(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notif = NotificationCompat.Builder(context, NotificationChannels.DEFAULT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New week started")
            .setContentText("Your weekly count has reset.")
            .setAutoCancel(true)
            .build()

        nm.notify(2001, notif)
    }
}