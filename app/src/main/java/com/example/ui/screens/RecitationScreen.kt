package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Affirmation
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

@Composable
fun RecitationScreen(
    sessionType: SessionType,
    activeAffirmation: Affirmation?,
    currentCount: Int,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
    onSpeak: () -> Unit
) {
    BackHandler { onBack() }

    val targetCount = sessionType.count
    val isCompleted = currentCount >= targetCount
    val progressFraction = animateFloatAsState(
        targetValue = (currentCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 400),
        label = "recitation_progress"
    ).value

    // Subtle cosmic pulse animation for the sacred circle
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    var showBreathGuide by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("recitation_screen")
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0C081A),
                        Color(0xFF140F2E),
                        Color(0xFF0C081A)
                    )
                )
            )
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("recitation_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = sessionType.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldSolar
                    )
                )
                Text(
                    text = "Solfeggio ${sessionType.solfeggioHz.toInt()} Hz Tone",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onReset,
                modifier = Modifier.testTag("recitation_reset_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Affirmation Presentation Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("recitation_affirmation_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(
                    listOf(GoldSolar.copy(alpha = 0.7f), CelestialIndigo.copy(alpha = 0.5f))
                )
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
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

                    // TTS Speak Aloud button
                    Button(
                        onClick = onSpeak,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                        border = BorderStroke(1.dp, CelestialIndigo.copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("recitation_speak_aloud_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Listen Aloud",
                            tint = GoldSolar,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hear Cadence",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = GoldLight,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = GoldWarm.copy(alpha = 0.5f),
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = activeAffirmation?.text ?: "I am aligned with infinite possibilities and divine abundance.",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                if (!activeAffirmation?.intentionNote.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Anchor: ${activeAffirmation?.intentionNote}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic,
                            color = CelestialIndigo,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Large Sacred Counter Ring (Tap to recite)
        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(if (!isCompleted) pulseScale else 1f)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (!isCompleted) {
                        onIncrement()
                    }
                }
                .testTag("recitation_sacred_circle"),
            contentAlignment = Alignment.Center
        ) {
            // Background glowing circle
            Surface(
                shape = CircleShape,
                color = CosmicSurfaceVariant,
                border = BorderStroke(
                    2.dp,
                    if (isCompleted) EmeraldHarmony else GoldSolar.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxSize()
            ) {}

            // Circular progress indicator
            CircularProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 10.dp,
                color = if (isCompleted) EmeraldHarmony else GoldSolar,
                trackColor = CosmicBorder
            )

            // Inner circle content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = EmeraldHarmony,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "COMPLETED",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldHarmony,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "$targetCount / $targetCount Spoken",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "$currentCount",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = GoldSolar
                        )
                    )
                    Text(
                        text = "OF $targetCount REPETITIONS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CelestialIndigo,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap circle or button",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Big Primary Action Button: "I Said It Aloud (+1)"
        if (!isCompleted) {
            Button(
                onClick = onIncrement,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("recitation_i_spoke_it_button"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldSolar)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF1E1200),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "I Said It Out Loud  (${currentCount + 1}/$targetCount)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E1200)
                    )
                )
            }
        } else {
            // Completed Session Card Banner
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + scaleIn()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                    border = BorderStroke(1.dp, EmeraldHarmony.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✨ Ritual Sealed ✨",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldSolar
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "You have spoken your affirmation $targetCount times. Your intention resonates throughout your nervous system and the cosmos.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Return Home", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onReset,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant)
                            ) {
                                Text("Recite Again", color = GoldLight)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pacing & Breathing Guide Toggle
        OutlinedButton(
            onClick = { showBreathGuide = !showBreathGuide },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, CosmicBorder),
            modifier = Modifier.testTag("recitation_breath_guide_toggle")
        ) {
            Text(
                text = if (showBreathGuide) "Hide Pacing Tip" else "How to say it properly",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showBreathGuide) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurface.copy(alpha = 0.7f)),
                border = BorderStroke(0.5.dp, CosmicBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "3-Step 369 Recitation Technique:",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1. Inhale deeply: Center yourself in stillness.\n2. Speak with conviction: Say each word out loud with feeling, as if it is already your reality.\n3. Exhale & release: Feel deep gratitude in your chest.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
