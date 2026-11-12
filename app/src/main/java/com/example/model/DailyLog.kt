package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey
    val date: String, // "YYYY-MM-DD"
    val morningCount: Int = 0, // Target: 3
    val afternoonCount: Int = 0, // Target: 6
    val eveningCount: Int = 0, // Target: 9
    val morningCompletedAt: Long? = null,
    val afternoonCompletedAt: Long? = null,
    val eveningCompletedAt: Long? = null,
    val journalReflection: String = "",
    val moodTag: String = "Aligned",
    val activeAffirmationText: String = ""
) {
    val totalRepetitions: Int
        get() = morningCount + afternoonCount + eveningCount

    val isMorningComplete: Boolean
        get() = morningCount >= 3

    val isAfternoonComplete: Boolean
        get() = afternoonCount >= 6

    val isEveningComplete: Boolean
        get() = eveningCount >= 9

    val isDayComplete: Boolean
        get() = isMorningComplete && isAfternoonComplete && isEveningComplete

    val progressFraction: Float
        get() = totalRepetitions.toFloat() / 18f
}
