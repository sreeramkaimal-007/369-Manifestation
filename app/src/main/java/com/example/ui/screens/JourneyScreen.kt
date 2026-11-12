package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyLog
import com.example.model.ReminderSettings
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

@Composable
fun JourneyScreen(
    todayLog: DailyLog?,
    allLogs: List<DailyLog>,
    streak: Int,
    completedDaysCount: Int,
    reminderSettings: ReminderSettings,
    onSaveJournal: (String, String) -> Unit
) {
    var reflectionText by remember(todayLog?.journalReflection) {
        mutableStateOf(todayLog?.journalReflection.orEmpty())
    }
    var selectedMood by remember(todayLog?.moodTag) {
        mutableStateOf(todayLog?.moodTag?.ifBlank { "Aligned" } ?: "Aligned")
    }
    var isSavedNotification by remember { mutableStateOf(false) }

    val challengeTotalDays = reminderSettings.challengeDays
    val totalRepetitionsAllTime = remember(allLogs) {
        allLogs.sumOf { it.totalRepetitions }
    }

    val moodOptions = listOf(
        "✨ Aligned",
        "🔥 High Vibe",
        "🌿 Peaceful",
        "💖 Love",
        "🧘 Zen",
        "🌟 Miraculous"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("journey_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Manifestation Journey",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "Track your Tesla 369 streak, challenge milestones, and synchronicities.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Challenge Roadmap Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("challenge_roadmap_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                border = BorderStroke(1.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = GoldSolar,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tesla $challengeTotalDays-Day Challenge",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldSolar.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, GoldSolar.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "Day $completedDaysCount of $challengeTotalDays",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldSolar
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val challengeProgress = (completedDaysCount.toFloat() / challengeTotalDays.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { challengeProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = GoldSolar,
                        trackColor = CosmicSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Milestone Pills
                    val milestones = listOf(3, 6, 9, 18, 27, 33)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        milestones.forEach { milestoneDay ->
                            val isReached = completedDaysCount >= milestoneDay
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isReached) EmeraldHarmony else CosmicSurfaceVariant,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isReached) EmeraldHarmony else CosmicBorder
                                    ),
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (isReached) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color(0xFF0C1F15),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "$milestoneDay",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "D$milestoneDay",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = if (isReached) EmeraldHarmony else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Stats Overview Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Streak Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = SolfeggioRose,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Streak",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$streak Days",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        )
                    }
                }

                // Completed Days
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldHarmony,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Full Days",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$completedDaysCount Days",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldHarmony
                            )
                        )
                    }
                }

                // Total Repetitions
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = CelestialIndigo,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Spoken",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalRepetitionsAllTime",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = CelestialIndigo
                            )
                        )
                    }
                }
            }
        }

        // Today's Synchronicities & Journal Reflection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("journal_reflection_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                border = BorderStroke(1.dp, CelestialIndigo.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = GoldSolar,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Today's Synchronicities & Signs",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Notice what signs appeared today: 11:11, 333, unexpected opportunities, peaceful feelings, or coincidences.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vibration Selector
                    Text(
                        text = "Today's Energy State:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(moodOptions) { mood ->
                            val isSelected = selectedMood == mood
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedMood = mood },
                                label = { Text(mood, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CelestialDeep,
                                    selectedLabelColor = Color.White,
                                    containerColor = CosmicSurfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = reflectionText,
                        onValueChange = {
                            reflectionText = it
                            isSavedNotification = false
                        },
                        placeholder = {
                            Text("e.g. Saw 333 on the clock, felt a sudden wave of deep confidence, received an unexpected message...")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reflection_text_input"),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSavedNotification) "✓ Reflection Saved!" else "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EmeraldHarmony,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Button(
                            onClick = {
                                onSaveJournal(reflectionText, selectedMood)
                                isSavedNotification = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldWarm),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("save_reflection_button")
                        ) {
                            Text("Save Reflection", color = Color(0xFF1E1200), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Past Logs Section
        item {
            Text(
                text = "Past Ritual History",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (allLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface)
                ) {
                    Text(
                        text = "Start speaking your affirmations today to populate your history log.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(allLogs.take(15)) { log ->
                PastLogCard(log = log)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PastLogCard(log: DailyLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
        border = BorderStroke(
            1.dp,
            if (log.isDayComplete) EmeraldHarmony.copy(alpha = 0.4f) else CosmicBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.date,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (log.isDayComplete) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldHarmony.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "18 / 18 Complete",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldHarmony
                                )
                            )
                        }
                    } else {
                        Text(
                            text = "${log.totalRepetitions} / 18 Spoken",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Repetition breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🌅 M: ${log.morningCount}/3",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (log.isMorningComplete) GoldSolar else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "☀️ A: ${log.afternoonCount}/6",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (log.isAfternoonComplete) CelestialIndigo else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "🌙 E: ${log.eveningCount}/9",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (log.isEveningComplete) SolfeggioRose else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (log.journalReflection.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${log.journalReflection}\"",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
