package com.etiennelawlor.eggday.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.ZoneId

/**
 * Refreshes the widget when the day changes.
 *
 * Uses an inexact, non-wakeup alarm, so no exact-alarm permission is needed. It may run a few
 * minutes after midnight, or when the screen next turns on, which is when the widget is visible.
 */
object MidnightScheduler {

    const val ACTION_MIDNIGHT = "com.etiennelawlor.eggday.action.MIDNIGHT"

    fun schedule(context: Context) {
        val nextMidnight = LocalDate.now()
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        context.getSystemService(AlarmManager::class.java)
            .set(AlarmManager.RTC, nextMidnight, pendingIntent(context))
    }

    fun cancel(context: Context) {
        context.getSystemService(AlarmManager::class.java).cancel(pendingIntent(context))
    }

    private fun pendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, DateChangeReceiver::class.java).setAction(ACTION_MIDNIGHT),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
}
