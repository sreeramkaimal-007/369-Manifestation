package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.SessionType
import com.example.ui.screens.AffirmationsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JourneyScreen
import com.example.ui.screens.RecitationScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.theme.Affirmation369Theme
import com.example.ui.theme.CelestialDeep
import com.example.ui.theme.CelestialIndigo
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSolar
import com.example.ui.viewmodel.AffirmationViewModel
import com.example.util.ReminderManager

class MainActivity : ComponentActivity() {

    private val viewModel: AffirmationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle initial notification extra if app was launched via notification
        val initialSessionType = intent?.getStringExtra(ReminderManager.EXTRA_SESSION_TYPE)?.let {
            try {
                SessionType.valueOf(it)
            } catch (_: Exception) {
                null
            }
        }

        setContent {
            Affirmation369Theme {
                MainAppContent(
                    viewModel = viewModel,
                    initialSession = initialSessionType
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

@Composable
fun MainAppContent(
    viewModel: AffirmationViewModel,
    initialSession: SessionType?
) {
    val activeAffirmation by viewModel.activeAffirmation.collectAsStateWithLifecycle()
    val allAffirmations by viewModel.allAffirmations.collectAsStateWithLifecycle()
    val todayLog by viewModel.todayLog.collectAsStateWithLifecycle()
    val allLogs by viewModel.allLogs.collectAsStateWithLifecycle()
    val completedDaysCount by viewModel.completedDaysCount.collectAsStateWithLifecycle()
    val streak by viewModel.currentStreak.collectAsStateWithLifecycle()
    val reminderSettings by viewModel.reminderSettings.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var activeRecitationSession by remember { mutableStateOf<SessionType?>(initialSession) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Request Notification permission on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission result handled
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Full-screen recitation view
    if (activeRecitationSession != null) {
        val session = activeRecitationSession!!
        val currentCount = when (session) {
            SessionType.MORNING -> todayLog?.morningCount ?: 0
            SessionType.AFTERNOON -> todayLog?.afternoonCount ?: 0
            SessionType.EVENING -> todayLog?.eveningCount ?: 0
        }

        RecitationScreen(
            sessionType = session,
            activeAffirmation = activeAffirmation,
            currentCount = currentCount,
            onIncrement = { viewModel.incrementSessionCount(session) },
            onReset = { viewModel.resetSessionCount(session) },
            onBack = { activeRecitationSession = null },
            onSpeak = { viewModel.speakAffirmation() }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    containerColor = CosmicSurface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_bottom_nav_bar"),
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Rituals"
                            )
                        },
                        label = { Text("Ritual", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E1300),
                            selectedTextColor = GoldSolar,
                            indicatorColor = GoldSolar,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_ritual")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = "Affirmations"
                            )
                        },
                        label = { Text("Affirmations", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E1300),
                            selectedTextColor = GoldSolar,
                            indicatorColor = GoldSolar,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_affirmations")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Journey"
                            )
                        },
                        label = { Text("Journey", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E1300),
                            selectedTextColor = GoldSolar,
                            indicatorColor = GoldSolar,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_journey")
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0F0B1E),
                                Color(0xFF161033),
                                Color(0xFF0F0B1E)
                            )
                        )
                    )
                    .padding(paddingValues)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        0 -> HomeScreen(
                            activeAffirmation = activeAffirmation,
                            todayLog = todayLog,
                            streak = streak,
                            reminderSettings = reminderSettings,
                            onStartSession = { session -> activeRecitationSession = session },
                            onQuickIncrement = { session -> viewModel.incrementSessionCount(session) },
                            onResetSession = { session -> viewModel.resetSessionCount(session) },
                            onOpenAffirmations = { selectedTab = 1 },
                            onOpenSettings = { showSettingsDialog = true },
                            onSpeakAffirmation = { viewModel.speakAffirmation() },
                            onOpenJournal = { selectedTab = 2 }
                        )
                        1 -> AffirmationsScreen(
                            affirmations = allAffirmations,
                            activeAffirmation = activeAffirmation,
                            onSelectActive = { id -> viewModel.setActiveAffirmation(id) },
                            onAddAffirmation = { text, category, note, makeActive ->
                                viewModel.addCustomAffirmation(text, category, note, makeActive)
                            },
                            onDeleteAffirmation = { affirmation ->
                                viewModel.deleteAffirmation(affirmation)
                            },
                            onSpeak = { text -> viewModel.speakAffirmation(text) }
                        )
                        2 -> JourneyScreen(
                            todayLog = todayLog,
                            allLogs = allLogs,
                            streak = streak,
                            completedDaysCount = completedDaysCount,
                            reminderSettings = reminderSettings,
                            onSaveJournal = { reflection, mood ->
                                viewModel.saveJournalReflection(reflection, mood)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            initialSettings = reminderSettings,
            onDismiss = { showSettingsDialog = false },
            onSave = { updated -> viewModel.saveReminderSettings(updated) },
            onSendTestNotification = { sessionType -> viewModel.sendTestNotification(sessionType) }
        )
    }
}
