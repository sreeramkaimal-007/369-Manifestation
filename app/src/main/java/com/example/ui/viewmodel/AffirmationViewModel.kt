package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.model.Affirmation
import com.example.model.DailyLog
import com.example.model.ReminderSettings
import com.example.model.SessionType
import com.example.util.ChimePlayer
import com.example.util.ReminderManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AffirmationViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val affirmationDao = database.affirmationDao()
    private val dailyLogDao = database.dailyLogDao()
    val reminderManager = ReminderManager(application)
    val chimePlayer = ChimePlayer(application)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayDateString: String = dateFormat.format(Date())

    val allAffirmations: StateFlow<List<Affirmation>> = affirmationDao.getAllAffirmations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAffirmation: StateFlow<Affirmation?> = affirmationDao.getActiveAffirmation()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayLog: StateFlow<DailyLog?> = dailyLogDao.getLogForDate(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allLogs: StateFlow<List<DailyLog>> = dailyLogDao.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedDaysCount: StateFlow<Int> = dailyLogDao.getCompletedDaysCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _reminderSettings = MutableStateFlow(reminderManager.getSettings())
    val reminderSettings: StateFlow<ReminderSettings> = _reminderSettings.asStateFlow()

    // Calculated streak from all completed daily logs
    val currentStreak: StateFlow<Int> = allLogs.combine(todayLog) { logs, today ->
        calculateStreak(logs, today)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // Ensure today's log row exists
        viewModelScope.launch {
            val existing = dailyLogDao.getLogForDateSync(todayDateString)
            if (existing == null) {
                dailyLogDao.insertOrUpdate(
                    DailyLog(
                        date = todayDateString,
                        activeAffirmationText = activeAffirmation.value?.text.orEmpty()
                    )
                )
            }
        }
    }

    private fun calculateStreak(logs: List<DailyLog>, today: DailyLog?): Int {
        val completedDates = logs.filter { it.isDayComplete }.map { it.date }.toSet()
        if (completedDates.isEmpty() && (today == null || !today.isDayComplete)) return 0

        val cal = Calendar.getInstance()
        var streak = 0

        // If today is complete, count today and go backwards; if today is not complete yet, start checking from yesterday
        val todayStr = dateFormat.format(cal.time)
        if (completedDates.contains(todayStr)) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (true) {
            val dateStr = dateFormat.format(cal.time)
            if (completedDates.contains(dateStr)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    fun incrementSessionCount(sessionType: SessionType) {
        viewModelScope.launch {
            val current = dailyLogDao.getLogForDateSync(todayDateString)
                ?: DailyLog(date = todayDateString, activeAffirmationText = activeAffirmation.value?.text.orEmpty())

            val now = System.currentTimeMillis()
            val updated = when (sessionType) {
                SessionType.MORNING -> {
                    val newCount = (current.morningCount + 1).coerceAtMost(3)
                    current.copy(
                        morningCount = newCount,
                        morningCompletedAt = if (newCount >= 3) now else current.morningCompletedAt,
                        activeAffirmationText = activeAffirmation.value?.text ?: current.activeAffirmationText
                    )
                }
                SessionType.AFTERNOON -> {
                    val newCount = (current.afternoonCount + 1).coerceAtMost(6)
                    current.copy(
                        afternoonCount = newCount,
                        afternoonCompletedAt = if (newCount >= 6) now else current.afternoonCompletedAt,
                        activeAffirmationText = activeAffirmation.value?.text ?: current.activeAffirmationText
                    )
                }
                SessionType.EVENING -> {
                    val newCount = (current.eveningCount + 1).coerceAtMost(9)
                    current.copy(
                        eveningCount = newCount,
                        eveningCompletedAt = if (newCount >= 9) now else current.eveningCompletedAt,
                        activeAffirmationText = activeAffirmation.value?.text ?: current.activeAffirmationText
                    )
                }
            }
            dailyLogDao.insertOrUpdate(updated)

            val settings = _reminderSettings.value
            if (settings.soundEnabled) {
                chimePlayer.playChime(sessionType.solfeggioHz, viewModelScope)
            }
            if (settings.vibrationEnabled) {
                chimePlayer.playHapticFeedback()
            }
        }
    }

    fun resetSessionCount(sessionType: SessionType) {
        viewModelScope.launch {
            val current = dailyLogDao.getLogForDateSync(todayDateString) ?: return@launch
            val updated = when (sessionType) {
                SessionType.MORNING -> current.copy(morningCount = 0, morningCompletedAt = null)
                SessionType.AFTERNOON -> current.copy(afternoonCount = 0, afternoonCompletedAt = null)
                SessionType.EVENING -> current.copy(eveningCount = 0, eveningCompletedAt = null)
            }
            dailyLogDao.insertOrUpdate(updated)
        }
    }

    fun saveJournalReflection(reflection: String, moodTag: String) {
        viewModelScope.launch {
            val current = dailyLogDao.getLogForDateSync(todayDateString)
                ?: DailyLog(date = todayDateString)
            val updated = current.copy(
                journalReflection = reflection,
                moodTag = moodTag
            )
            dailyLogDao.insertOrUpdate(updated)
        }
    }

    fun setActiveAffirmation(id: Long) {
        viewModelScope.launch {
            affirmationDao.setActiveAffirmation(id)
            val active = affirmationDao.getActiveAffirmationSync()
            if (active != null) {
                reminderManager.scheduleSessionReminder(SessionType.MORNING, active.text)
                reminderManager.scheduleSessionReminder(SessionType.AFTERNOON, active.text)
                reminderManager.scheduleSessionReminder(SessionType.EVENING, active.text)
            }
        }
    }

    fun addCustomAffirmation(text: String, category: String, intentionNote: String, makeActive: Boolean) {
        viewModelScope.launch {
            val id = affirmationDao.insertAffirmation(
                Affirmation(
                    text = text.trim(),
                    category = category.trim().ifBlank { "Custom" },
                    intentionNote = intentionNote.trim(),
                    isActive = false
                )
            )
            if (makeActive) {
                affirmationDao.setActiveAffirmation(id)
            }
        }
    }

    fun deleteAffirmation(affirmation: Affirmation) {
        viewModelScope.launch {
            affirmationDao.deleteAffirmation(affirmation)
            // If deleted was active, make first available active
            if (affirmation.isActive) {
                val list = affirmationDao.getCount()
                if (list > 0) {
                    val remaining = affirmationDao.getAllAffirmations()
                    // Dao query can set first active
                }
            }
        }
    }

    fun saveReminderSettings(settings: ReminderSettings) {
        _reminderSettings.value = settings
        reminderManager.saveSettings(settings)
        activeAffirmation.value?.let { active ->
            reminderManager.scheduleSessionReminder(SessionType.MORNING, active.text)
            reminderManager.scheduleSessionReminder(SessionType.AFTERNOON, active.text)
            reminderManager.scheduleSessionReminder(SessionType.EVENING, active.text)
        }
    }

    fun sendTestNotification(sessionType: SessionType) {
        val text = activeAffirmation.value?.text ?: "I am attracting abundance and harmony into my life."
        reminderManager.sendInstantTestNotification(sessionType, text)
    }

    fun speakAffirmation(text: String? = null) {
        val toSpeak = text ?: activeAffirmation.value?.text
        if (!toSpeak.isNullOrBlank()) {
            chimePlayer.speakAffirmation(toSpeak)
        }
    }

    fun stopSpeaking() {
        chimePlayer.stopSpeaking()
    }

    override fun onCleared() {
        super.onCleared()
        chimePlayer.release()
    }
}
