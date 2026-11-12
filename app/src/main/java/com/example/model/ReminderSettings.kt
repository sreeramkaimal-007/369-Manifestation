package com.example.model

data class ReminderSettings(
    val morningHour: Int = 8,
    val morningMinute: Int = 0,
    val morningEnabled: Boolean = true,
    val afternoonHour: Int = 13,
    val afternoonMinute: Int = 0,
    val afternoonEnabled: Boolean = true,
    val eveningHour: Int = 21,
    val eveningMinute: Int = 0,
    val eveningEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val challengeDays: Int = 33
)
