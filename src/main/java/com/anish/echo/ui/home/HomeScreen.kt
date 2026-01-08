package com.anish.echo.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anish.echo.data.models.Habit
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    onLogMoodClick: () -> Unit = {},
    onStartTimerClick: (Int, String) -> Unit = { _, _ -> },
    onHabitCheck: (Int, String) -> Unit = { _, _ -> },
    onSettingsClick: () -> Unit = {}, // Settings callback
    onManageHabitsClick: () -> Unit = {} // Manage Habits callback
) {
    val habitStates by viewModel.habitStates.collectAsState()
    val hasLoggedMoodToday by viewModel.hasLoggedMoodToday.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Top Bar with Settings Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Menu placeholder
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
                
                // Settings gear
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mood Selector Card - Shows once, disappears after logging
            // Random creative phrases
            val moodPhrases = listOf(
                "How's your force feeling today?",
                "Are we in calm mode or survival mode today?",
                "Are you holding it together, or barely?",
                "What chapter are you in today?",
                "Are you winning today… internally?",
                "Are you at peace or powering up today?",
                "Is today a training arc or a breakdown arc?",
                "How heavy is your heart today?",
                "Are you fighting or healing today?",
                "What arc are you in right now?",
                "Are you okay… like really okay?",
                "Is today a fun episode or a filler one?",
                "How chaotic is your brain today?",
                "Are we calm SpongeBob or feral SpongeBob today?",
                "Is your brain being nice to you today?",
                "Are you surviving or thriving today?",
                "How close are you to your villain arc today?",
                "Is today a breakdown or a breakthrough?",
                "Are you holding the line today?",
                "How's the internal monologue today?"
            )
            val currentPhrase by remember { mutableStateOf(moodPhrases.random()) }
            
            if (!hasLoggedMoodToday) {
                val moods = listOf("😢", "😕", "😐", "🙂", "😊")
                val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = currentPhrase,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 28.sp
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Mood Icons Row - Click to log
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            moods.forEach { emoji ->
                                Surface(
                                    onClick = {
                                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                        // Log the mood with emoji AND the phrase
                                        viewModel.logMood(emoji, currentPhrase)
                                    },
                                    modifier = Modifier.size(52.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = emoji, style = MaterialTheme.typography.headlineMedium)
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Section Header
            Text(
                text = "TODAY'S RHYTHM",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                letterSpacing = 1.5.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Habit List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(habitStates) { uiState ->
                    HabitItem(
                        habit = uiState.habit, 
                        isCompleted = uiState.isCompletedToday,
                        onCheck = { onHabitCheck(uiState.habit.id, uiState.habit.name) },
                        onStartTimer = onStartTimerClick
                    )
                }
                if (habitStates.isEmpty()) {
                    item {
                        Text(
                            text = "No habits yet. Add one?",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                }
                
                // Quote footer starts here (just keeping the spacer for layout consistency if needed, or removing button entirely)
                
                // Manage Habits Button
                item {
                    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            onManageHabitsClick()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Manage Habits")
                    }
                }
                
                // Quote footer
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "\"Echoes fade, but patterns remain.\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
fun HabitItem(
    habit: Habit,
    isCompleted: Boolean,
    onCheck: () -> Unit,
    onStartTimer: (Int, String) -> Unit
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    
    val subtitle = if (habit.isTimed) "Timed" else if (isCompleted) "Completed" else "Tap to log"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Name and Subtitle - Plain, no icon
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Action: Circular checkbox or Start button
            if (habit.isTimed) {
                FilledTonalButton(
                    onClick = {
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onStartTimer(habit.id, habit.name)
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Start")
                }
            } else {
                // Circular checkbox
                Surface(
                    onClick = {
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onCheck()
                    },
                    modifier = Modifier.size(32.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = if (isCompleted) MaterialTheme.colorScheme.primary 
                           else MaterialTheme.colorScheme.surface,
                    border = if (!isCompleted) androidx.compose.foundation.BorderStroke(
                        2.dp, 
                        MaterialTheme.colorScheme.outline
                    ) else null
                ) {
                    if (isCompleted) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

