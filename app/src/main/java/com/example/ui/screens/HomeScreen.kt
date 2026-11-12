package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Affirmation
import com.example.model.DailyLog
import com.example.model.ReminderSettings
import com.example.model.SessionType
import com.example.ui.theme.CelestialDeep
import com.example.ui.theme.CelestialIndigo
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.EmeraldHarmony
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSolar
import com.example.ui.theme.GoldWarm
import com.example.ui.theme.SolfeggioRose
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    activeAffirmation: Affirmation?,
    todayLog: DailyLog?,
    streak: Int,
    reminderSettings: ReminderSettings,
    onStartSession: (SessionType) -> Unit,
    onQuickIncrement: (SessionType) -> Unit,
    onResetSession: (SessionType) -> Unit,
    onOpenAffirmations: () -> Unit,
    onOpenSettings: () -> Unit,
    onSpeakAffirmation: () -> Unit,
    onOpenJournal: () -> Unit
) {
    val dateDisplay = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    val totalRepetitions = todayLog?.totalRepetitions ?: 0
    val progressFraction = animateFloatAsState(
        targetValue = (totalRepetitions.toFloat() / 18f).coerceIn(0f, 1f),
        label = "daily_progress"
    ).value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "3-6-9",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = GoldSolar,
                                letterSpacing = 2.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MANIFESTATION",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CelestialIndigo,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                    Text(
                        text = dateDisplay,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Streak Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CosmicSurfaceVariant,
                        border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔥", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$streak Days",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("home_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Active Affirmation Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_affirmation_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                border = BorderStroke(
                    1.5.dp,
                    Brush.linearGradient(listOf(GoldSolar.copy(alpha = 0.8f), CelestialIndigo.copy(alpha = 0.4f)))
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldSolar.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, GoldSolar.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = (activeAffirmation?.category ?: "Abundance").uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldSolar,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onSpeakAffirmation,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("speak_affirmation_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak Aloud",
                                    tint = GoldSolar
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Switch",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = CelestialIndigo,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onOpenAffirmations() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = GoldWarm.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = activeAffirmation?.text ?: "I am aligned with the abundance of the universe.",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 24.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (!activeAffirmation?.intentionNote.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Intention: ${activeAffirmation?.intentionNote}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Daily Progress Indicator Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_progress_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                border = BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Manifestation Progress",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "$totalRepetitions / 18 Reps",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (totalRepetitions >= 18) EmeraldHarmony else GoldSolar
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (totalRepetitions >= 18) EmeraldHarmony else GoldSolar,
                        trackColor = MaterialTheme.colorScheme.surface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (totalRepetitions >= 18) "✨ Complete! Universe is listening."
                            else "${18 - totalRepetitions} affirmations remaining today",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(progressFraction * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // Section Title: 3-6-9 Rituals
        item {
            Text(
                text = "Today's 3-6-9 Sessions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Morning Session (3x)
        item {
            val morningCount = todayLog?.morningCount ?: 0
            val morningDone = morningCount >= 3
            val scheduledTime = String.format(Locale.getDefault(), "%02d:%02d", reminderSettings.morningHour, reminderSettings.morningMinute)

            SessionCard(
                sessionType = SessionType.MORNING,
                currentCount = morningCount,
                targetCount = 3,
                isDone = morningDone,
                icon = Icons.Default.WbSunny,
                iconColor = GoldSolar,
                scheduledTime = scheduledTime,
                solfeggio = "528 Hz",
                onStart = { onStartSession(SessionType.MORNING) },
                onQuickAdd = { onQuickIncrement(SessionType.MORNING) },
                onReset = { onResetSession(SessionType.MORNING) }
            )
        }

        // Afternoon Session (6x)
        item {
            val afternoonCount = todayLog?.afternoonCount ?: 0
            val afternoonDone = afternoonCount >= 6
            val scheduledTime = String.format(Locale.getDefault(), "%02d:%02d", reminderSettings.afternoonHour, reminderSettings.afternoonMinute)

            SessionCard(
                sessionType = SessionType.AFTERNOON,
                currentCount = afternoonCount,
                targetCount = 6,
                isDone = afternoonDone,
                icon = Icons.Default.Brightness5,
                iconColor = CelestialIndigo,
                scheduledTime = scheduledTime,
                solfeggio = "639 Hz",
                onStart = { onStartSession(SessionType.AFTERNOON) },
                onQuickAdd = { onQuickIncrement(SessionType.AFTERNOON) },
                onReset = { onResetSession(SessionType.AFTERNOON) }
            )
        }

        // Evening Session (9x)
        item {
            val eveningCount = todayLog?.eveningCount ?: 0
            val eveningDone = eveningCount >= 9
            val scheduledTime = String.format(Locale.getDefault(), "%02d:%02d", reminderSettings.eveningHour, reminderSettings.eveningMinute)

            SessionCard(
                sessionType = SessionType.EVENING,
                currentCount = eveningCount,
                targetCount = 9,
                isDone = eveningDone,
                icon = Icons.Default.Bedtime,
                iconColor = SolfeggioRose,
                scheduledTime = scheduledTime,
                solfeggio = "432 Hz",
                onStart = { onStartSession(SessionType.EVENING) },
                onQuickAdd = { onQuickIncrement(SessionType.EVENING) },
                onReset = { onResetSession(SessionType.EVENING) }
            )
        }

        // Journal & Reflection Prompt Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenJournal() }
                    .testTag("home_journal_prompt_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                border = BorderStroke(1.dp, CelestialDeep.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CelestialIndigo.copy(alpha = 0.2f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = CelestialIndigo
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Manifestation Reflection",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = if (!todayLog?.journalReflection.isNullOrBlank()) "Reflection logged • Tap to review"
                                else "Record synchronicities & daily signs",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = "Open",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldSolar
                        )
                    )
                }
            }
        }

        item {
            // Nikola Tesla's 369 Philosophy Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurface.copy(alpha = 0.6f)),
                border = BorderStroke(0.5.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "“If you only knew the magnificence of the 3, 6 and 9, then you would have a key to the universe.”",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "— Nikola Tesla",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldWarm,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun SessionCard(
    sessionType: SessionType,
    currentCount: Int,
    targetCount: Int,
    isDone: Boolean,
    icon: ImageVector,
    iconColor: Color,
    scheduledTime: String,
    solfeggio: String,
    onStart: () -> Unit,
    onQuickAdd: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("session_card_${sessionType.name.lowercase()}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) CosmicSurface.copy(alpha = 0.9f) else CosmicSurface
        ),
        border = BorderStroke(
            1.dp,
            if (isDone) EmeraldHarmony.copy(alpha = 0.6f) else CosmicBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = iconColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = sessionType.title,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            if (isDone) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = EmeraldHarmony,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = "Reminder: $scheduledTime • Solfeggio $solfeggio",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Count badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDone) EmeraldHarmony.copy(alpha = 0.2f) else iconColor.copy(alpha = 0.15f),
                    border = BorderStroke(
                        1.dp,
                        if (isDone) EmeraldHarmony.copy(alpha = 0.5f) else iconColor.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = "$currentCount / $targetCount",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDone) EmeraldHarmony else iconColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = sessionType.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("start_session_button_${sessionType.name.lowercase()}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDone) CelestialDeep else GoldWarm
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isDone) "Chant Again" else "Begin Ritual",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF150F02)
                    )
                }

                FilledTonalButton(
                    onClick = onQuickAdd,
                    enabled = currentCount < targetCount,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = CosmicSurfaceVariant
                    ),
                    modifier = Modifier.testTag("quick_add_button_${sessionType.name.lowercase()}")
                ) {
                    Text(
                        text = "+1 Spoken",
                        fontWeight = FontWeight.Bold,
                        color = GoldSolar
                    )
                }

                if (currentCount > 0) {
                    IconButton(
                        onClick = onReset,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("reset_button_${sessionType.name.lowercase()}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Count",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
