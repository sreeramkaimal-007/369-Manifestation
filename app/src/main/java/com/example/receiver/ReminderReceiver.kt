package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.model.SessionType
import com.example.util.ReminderManager

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            val reminderManager = ReminderManager(context)
            reminderManager.scheduleAllReminders()
            return
        }

        if (action == ReminderManager.ACTION_REMINDER) {
            val sessionTypeName = intent.getStringExtra(ReminderManager.EXTRA_SESSION_TYPE) ?: SessionType.MORNING.name
            val sessionType = try {
                SessionType.valueOf(sessionTypeName)
            } catch (_: Exception) {
                SessionType.MORNING
            }

            val affirmationText = intent.getStringExtra(ReminderManager.EXTRA_AFFIRMATION_TEXT)
                ?: "I am a powerful creator and everything I desire is flowing into my life."

            showNotification(context, sessionType, affirmationText)

            // Reschedule next occurrence for this session
            val reminderManager = ReminderManager(context)
            reminderManager.scheduleSessionReminder(sessionType)
        }
    }

    private fun showNotification(context: Context, sessionType: SessionType, affirmationText: String) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(ReminderManager.EXTRA_SESSION_TYPE, sessionType.name)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            sessionType.ordinal + 100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, subtext) = when (sessionType) {
            SessionType.MORNING -> "🌅 Morning Ritual: Say aloud 3 times" to "Begin your day by setting your intention into the universe."
            SessionType.AFTERNOON -> "☀️ Afternoon Focus: Say aloud 6 times" to "Re-center and amplify your manifestation energy."
            SessionType.EVENING -> "🌙 Evening Grounding: Say aloud 9 times" to "Lock your affirmation into your subconscious before sleep."
        }

        val notification = NotificationCompat.Builder(context, ReminderManager.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText("\"$affirmationText\"")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("\"$affirmationText\"\n\n$subtext")
                    .setSummaryText(sessionType.timeLabel)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(sessionType.ordinal + 10, notification)
        } catch (_: SecurityException) {
            // Handled if POST_NOTIFICATIONS is not yet granted
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderManager.CHANNEL_ID,
                "369 Affirmation Rituals",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to say your affirmations 3x morning, 6x afternoon, and 9x evening"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }
}
