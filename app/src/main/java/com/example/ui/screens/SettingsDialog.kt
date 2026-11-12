package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ReminderSettings
import com.example.model.SessionType
import com.example.ui.theme.CelestialDeep
import com.example.ui.theme.CelestialIndigo
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.EmeraldHarmony
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSolar
import com.example.ui.theme.GoldWarm
import com.example.ui.theme.SolfeggioRose
import java.util.Locale

@Composable
fun SettingsDialog(
    initialSettings: ReminderSettings,
    onDismiss: () -> Unit,
    onSave: (ReminderSettings) -> Unit,
    onSendTestNotification: (SessionType) -> Unit
) {
    var morningHour by remember { mutableIntStateOf(initialSettings.morningHour) }
    var morningMinute by remember { mutableIntStateOf(initialSettings.morningMinute) }
    var morningEnabled by remember { mutableStateOf(initialSettings.morningEnabled) }

    var afternoonHour by remember { mutableIntStateOf(initialSettings.afternoonHour) }
    var afternoonMinute by remember { mutableIntStateOf(initialSettings.afternoonMinute) }
    var afternoonEnabled by remember { mutableStateOf(initialSettings.afternoonEnabled) }

    var eveningHour by remember { mutableIntStateOf(initialSettings.eveningHour) }
    var eveningMinute by remember { mutableIntStateOf(initialSettings.eveningMinute) }
    var eveningEnabled by remember { mutableStateOf(initialSettings.eveningEnabled) }

    var soundEnabled by remember { mutableStateOf(initialSettings.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(initialSettings.vibrationEnabled) }
    var challengeDays by remember { mutableIntStateOf(initialSettings.challengeDays) }

    var showTestNotificationToast by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = GoldSolar
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ritual Timing & Alerts",
                    fontWeight = FontWeight.Bold,
                    color = GoldSolar
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .testTag("settings_dialog_content"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Configure when you are reminded to speak your affirmations out loud.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Morning Reminder Card
                TimePickerRow(
                    label = "🌅 Morning (3x)",
                    hour = morningHour,
                    minute = morningMinute,
                    enabled = morningEnabled,
                    accentColor = GoldSolar,
                    onHourChange = { morningHour = it },
                    onMinuteChange = { morningMinute = it },
                    onEnabledChange = { morningEnabled = it }
                )

                // Afternoon Reminder Card
                TimePickerRow(
                    label = "☀️ Afternoon (6x)",
                    hour = afternoonHour,
                    minute = afternoonMinute,
                    enabled = afternoonEnabled,
                    accentColor = CelestialIndigo,
                    onHourChange = { afternoonHour = it },
                    onMinuteChange = { afternoonMinute = it },
                    onEnabledChange = { afternoonEnabled = it }
                )

                // Evening Reminder Card
                TimePickerRow(
                    label = "🌙 Evening (9x)",
                    hour = eveningHour,
                    minute = eveningMinute,
                    enabled = eveningEnabled,
                    accentColor = SolfeggioRose,
                    onHourChange = { eveningHour = it },
                    onMinuteChange = { eveningMinute = it },
                    onEnabledChange = { eveningEnabled = it }
                )

                // Test Notification Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = GoldLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Test Notifications Instantly",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onSendTestNotification(SessionType.MORNING)
                                    showTestNotificationToast = "Morning notification sent!"
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Morning", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    onSendTestNotification(SessionType.AFTERNOON)
                                    showTestNotificationToast = "Afternoon notification sent!"
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Noon", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    onSendTestNotification(SessionType.EVENING)
                                    showTestNotificationToast = "Evening notification sent!"
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Evening", fontSize = 11.sp)
                            }
                        }
                        if (showTestNotificationToast != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = showTestNotificationToast!!,
                                style = MaterialTheme.typography.labelSmall.copy(color = EmeraldHarmony)
                            )
                        }
                    }
                }

                // Sensory Toggles
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                    border = BorderStroke(1.dp, CosmicBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Solfeggio Sound Chime", style = MaterialTheme.typography.bodyMedium)
                            Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Haptic Feedback on Taps", style = MaterialTheme.typography.bodyMedium)
                            Switch(checked = vibrationEnabled, onCheckedChange = { vibrationEnabled = it })
                        }
                    }
                }

                // Challenge Length
                Column {
                    Text(
                        text = "Manifestation Challenge Goal",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = challengeDays == 33,
                            onClick = { challengeDays = 33 },
                            label = { Text("33 Days (Classic)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldSolar,
                                selectedLabelColor = Color(0xFF1B1200)
                            )
                        )
                        FilterChip(
                            selected = challengeDays == 45,
                            onClick = { challengeDays = 45 },
                            label = { Text("45 Days (Intense)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldSolar,
                                selectedLabelColor = Color(0xFF1B1200)
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        ReminderSettings(
                            morningHour = morningHour,
                            morningMinute = morningMinute,
                            morningEnabled = morningEnabled,
                            afternoonHour = afternoonHour,
                            afternoonMinute = afternoonMinute,
                            afternoonEnabled = afternoonEnabled,
                            eveningHour = eveningHour,
                            eveningMinute = eveningMinute,
                            eveningEnabled = eveningEnabled,
                            soundEnabled = soundEnabled,
                            vibrationEnabled = vibrationEnabled,
                            challengeDays = challengeDays
                        )
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldSolar),
                modifier = Modifier.testTag("save_settings_button")
            ) {
                Text("Save Settings", color = Color(0xFF1B1200), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TimePickerRow(
    label: String,
    hour: Int,
    minute: Int,
    enabled: Boolean,
    accentColor: Color,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onEnabledChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
        border = BorderStroke(1.dp, if (enabled) accentColor.copy(alpha = 0.5f) else CosmicBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (enabled) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange
                )
            }

            if (enabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Hour Stepper
                    Stepper(
                        value = hour,
                        min = 0,
                        max = 23,
                        format = "%02d",
                        onValueChange = onHourChange
                    )

                    Text(
                        text = " : ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    // Minute Stepper
                    Stepper(
                        value = minute,
                        min = 0,
                        max = 59,
                        step = 5,
                        format = "%02d",
                        onValueChange = onMinuteChange
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    val amPm = if (hour < 12) "AM" else "PM"
                    val displayHour12 = when {
                        hour == 0 -> 12
                        hour > 12 -> hour - 12
                        else -> hour
                    }
                    Text(
                        text = String.format(Locale.getDefault(), "(%d:%02d %s)", displayHour12, minute, amPm),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun Stepper(
    value: Int,
    min: Int,
    max: Int,
    step: Int = 1,
    format: String,
    onValueChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(CosmicSurface, RoundedCornerShape(8.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        IconButton(
            onClick = {
                val newVal = if (value - step < min) max - ((max - min) % step) else value - step
                onValueChange(newVal)
            },
            modifier = Modifier.size(32.dp)
        ) {
            Text("-", fontWeight = FontWeight.Black, fontSize = 18.sp, color = GoldLight)
        }

        Text(
            text = String.format(Locale.getDefault(), format, value),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        IconButton(
            onClick = {
                val newVal = if (value + step > max) min else value + step
                onValueChange(newVal)
            },
            modifier = Modifier.size(32.dp)
        ) {
            Text("+", fontWeight = FontWeight.Black, fontSize = 18.sp, color = GoldLight)
        }
    }
}
