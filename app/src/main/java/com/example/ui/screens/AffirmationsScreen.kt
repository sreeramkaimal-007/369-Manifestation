package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Affirmation
import com.example.ui.theme.CelestialDeep
import com.example.ui.theme.CelestialIndigo
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldSolar
import com.example.ui.theme.GoldWarm

@Composable
fun AffirmationsScreen(
    affirmations: List<Affirmation>,
    activeAffirmation: Affirmation?,
    onSelectActive: (Long) -> Unit,
    onAddAffirmation: (String, String, String, Boolean) -> Unit,
    onDeleteAffirmation: (Affirmation) -> Unit,
    onSpeak: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Abundance", "Love", "Health", "Career", "Inner Peace", "Custom")

    val filteredList = remember(affirmations, selectedCategory) {
        if (selectedCategory == "All") {
            affirmations
        } else {
            affirmations.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("affirmations_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Affirmations Library",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Select your daily focus or create your own custom 3-6-9 affirmation.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldSolar,
                                selectedLabelColor = Color(0xFF1B1200),
                                containerColor = CosmicSurfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) GoldSolar else CosmicBorder
                            )
                        )
                    }
                }
            }

            if (filteredList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No affirmations found in this category.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldWarm)
                            ) {
                                Text("Create Affirmation", color = Color(0xFF1B1200))
                            }
                        }
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { affirmation ->
                    val isActive = affirmation.id == activeAffirmation?.id

                    AffirmationCard(
                        affirmation = affirmation,
                        isActive = isActive,
                        onSetActive = { onSelectActive(affirmation.id) },
                        onDelete = { onDeleteAffirmation(affirmation) },
                        onSpeak = { onSpeak(affirmation.text) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }
        }

        // FAB to add affirmation
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = GoldSolar,
            contentColor = Color(0xFF1E1300),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_affirmation_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Affirmation")
        }
    }

    if (showAddDialog) {
        AddAffirmationDialog(
            initialCategory = if (selectedCategory != "All") selectedCategory else "Abundance",
            onDismiss = { showAddDialog = false },
            onConfirm = { text, category, note, makeActive ->
                onAddAffirmation(text, category, note, makeActive)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AffirmationCard(
    affirmation: Affirmation,
    isActive: Boolean,
    onSetActive: () -> Unit,
    onDelete: () -> Unit,
    onSpeak: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("affirmation_card_${affirmation.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
        border = BorderStroke(
            if (isActive) 1.5.dp else 1.dp,
            if (isActive) GoldSolar else CosmicBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CosmicSurfaceVariant,
                    border = BorderStroke(0.5.dp, CosmicBorder)
                ) {
                    Text(
                        text = affirmation.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CelestialIndigo
                        )
                    )
                }

                if (isActive) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldSolar.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, GoldSolar.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = GoldSolar,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ACTIVE RITUAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldSolar
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = affirmation.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                )
            )

            if (affirmation.intentionNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Anchor: ${affirmation.intentionNote}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card Bottom Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak Aloud",
                            tint = GoldWarm,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (!isActive) {
                    Button(
                        onClick = onSetActive,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceVariant),
                        border = BorderStroke(1.dp, GoldSolar.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "Set as Active",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddAffirmationDialog(
    initialCategory: String,
    onDismiss: () -> Unit,
    onConfirm: (text: String, category: String, note: String, makeActive: Boolean) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(initialCategory) }
    var intentionNote by remember { mutableStateOf("") }
    var makeActive by remember { mutableStateOf(true) }

    val categories = listOf("Abundance", "Love", "Health", "Career", "Inner Peace", "Custom")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create 369 Affirmation", fontWeight = FontWeight.Bold, color = GoldSolar)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Write in the present tense (e.g., 'I am attracting...'), with clarity and emotion.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Affirmation Text *") },
                    placeholder = { Text("I am deeply fulfilled and abundance flows freely to me...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = intentionNote,
                    onValueChange = { intentionNote = it },
                    label = { Text("Intention / Visualization Anchor") },
                    placeholder = { Text("Feeling joy, freedom, and inner certainty...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 12.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Set as active ritual today")
                    Switch(
                        checked = makeActive,
                        onCheckedChange = { makeActive = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onConfirm(text, category, intentionNote, makeActive)
                    }
                },
                enabled = text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldSolar)
            ) {
                Text("Add Affirmation", color = Color(0xFF1B1200), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
