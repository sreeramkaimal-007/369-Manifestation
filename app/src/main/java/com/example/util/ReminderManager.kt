package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.model.ReminderSettings
import com.example.model.SessionType
import com.example.receiver.ReminderReceiver
import java.util.Calendar

class ReminderManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREFS_NAME = "369_reminder_settings"
        const val CHANNEL_ID = "affirmation_369_rituals"
        const val ACTION_REMINDER = "com.aistudio.affirmation369.ACTION_REMINDER"
        const val EXTRA_SESSION_TYPE = "extra_session_type"
        const val EXTRA_AFFIRMATION_TEXT = "extra_affirmation_text"

        private const val KEY_MORNING_HOUR = "morning_hour"
        private const val KEY_MORNING_MINUTE = "morning_minute"
        private const val KEY_MORNING_ENABLED = "morning_enabled"

        private const val KEY_AFTERNOON_HOUR = "afternoon_hour"
        private const val KEY_AFTERNOON_MINUTE = "afternoon_minute"
        private const val KEY_AFTERNOON_ENABLED = "afternoon_enabled"

        private const val KEY_EVENING_HOUR = "evening_hour"
        private const val KEY_EVENING_MINUTE = "evening_minute"
        private const val KEY_EVENING_ENABLED = "evening_enabled"

        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_CHALLENGE_DAYS = "challenge_days"
    }

    fun getSettings(): ReminderSettings {
        return ReminderSettings(
            morningHour = prefs.getInt(KEY_MORNING_HOUR, 8),
            morningMinute = prefs.getInt(KEY_MORNING_MINUTE, 0),
            morningEnabled = prefs.getBoolean(KEY_MORNING_ENABLED, true),
            afternoonHour = prefs.getInt(KEY_AFTERNOON_HOUR, 13),
            afternoonMinute = prefs.getInt(KEY_AFTERNOON_MINUTE, 0),
            afternoonEnabled = prefs.getBoolean(KEY_AFTERNOON_ENABLED, true),
            eveningHour = prefs.getInt(KEY_EVENING_HOUR, 21),
            eveningMinute = prefs.getInt(KEY_EVENING_MINUTE, 0),
            eveningEnabled = prefs.getBoolean(KEY_EVENING_ENABLED, true),
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            vibrationEnabled = prefs.getBoolean(KEY_VIBRATION_ENABLED, true),
            challengeDays = prefs.getInt(KEY_CHALLENGE_DAYS, 33)
        )
    }

    fun saveSettings(settings: ReminderSettings) {
        prefs.edit().apply {
            putInt(KEY_MORNING_HOUR, settings.morningHour)
            putInt(KEY_MORNING_MINUTE, settings.morningMinute)
            putBoolean(KEY_MORNING_ENABLED, settings.morningEnabled)
            putInt(KEY_AFTERNOON_HOUR, settings.afternoonHour)
            putInt(KEY_AFTERNOON_MINUTE, settings.afternoonMinute)
            putBoolean(KEY_AFTERNOON_ENABLED, settings.afternoonEnabled)
            putInt(KEY_EVENING_HOUR, settings.eveningHour)
            putInt(KEY_EVENING_MINUTE, settings.eveningMinute)
            putBoolean(KEY_EVENING_ENABLED, settings.eveningEnabled)
            putBoolean(KEY_SOUND_ENABLED, settings.soundEnabled)
            putBoolean(KEY_VIBRATION_ENABLED, settings.vibrationEnabled)
            putInt(KEY_CHALLENGE_DAYS, settings.challengeDays)
            apply()
        }
        scheduleAllReminders()
    }

    fun scheduleAllReminders() {
        scheduleSessionReminder(SessionType.MORNING)
        scheduleSessionReminder(SessionType.AFTERNOON)
        scheduleSessionReminder(SessionType.EVENING)
    }

    fun scheduleSessionReminder(sessionType: SessionType, activeAffirmationText: String? = null) {
        val settings = getSettings()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val (hour, minute, enabled) = when (sessionType) {
            SessionType.MORNING -> Triple(settings.morningHour, settings.morningMinute, settings.morningEnabled)
            SessionType.AFTERNOON -> Triple(settings.afternoonHour, settings.afternoonMinute, settings.afternoonEnabled)
            SessionType.EVENING -> Triple(settings.eveningHour, settings.eveningMinute, settings.eveningEnabled)
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_REMINDER
            putExtra(EXTRA_SESSION_TYPE, sessionType.name)
            if (!activeAffirmationText.isNullOrBlank()) {
                putExtra(EXTRA_AFFIRMATION_TEXT, activeAffirmationText)
            }
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sessionType.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!enabled) {
            alarmManager.cancel(pendingIntent)
            return
        }

        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If time already passed today, schedule for tomorrow
        if (calendar.before(now)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val triggerAtMillis = calendar.timeInMillis

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // Fallback gracefully
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    fun sendInstantTestNotification(sessionType: SessionType, affirmationText: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "369 Affirmation Rituals",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to say your affirmations 3x morning, 6x afternoon, and 9x evening"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_SESSION_TYPE, sessionType.name)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            sessionType.ordinal + 500,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, subtext) = when (sessionType) {
            SessionType.MORNING -> "🌅 Morning Ritual: Say aloud 3 times" to "Begin your day by setting your intention into the universe."
            SessionType.AFTERNOON -> "☀️ Afternoon Focus: Say aloud 6 times" to "Re-center and amplify your manifestation energy."
            SessionType.EVENING -> "🌙 Evening Grounding: Say aloud 9 times" to "Lock your affirmation into your subconscious before sleep."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("[Test] $title")
            .setContentText("\"$affirmationText\"")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("\"$affirmationText\"\n\n$subtext\n(This is a test notification from Affirmation 369)")
                    .setSummaryText(sessionType.timeLabel)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(999, notification)
        } catch (_: SecurityException) {}
    }
}
