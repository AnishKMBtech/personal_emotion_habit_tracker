package com.anish.echo.ui.stats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.DismissValue
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.ExperimentalMaterial3Api
import com.anish.echo.data.models.LogDetails

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel(factory = StatsViewModel.Factory)
) {
    val stats by viewModel.weeklyStats.collectAsState()
    val logs by viewModel.recentLogs.collectAsState()
    
    // State for log details dialog
    var selectedLog by remember { mutableStateOf<LogDetails?>(null) }

    // Log Details Dialog
    if (selectedLog != null) {
        LogDetailsDialog(
            logDetails = selectedLog!!,
            onDismiss = { selectedLog = null },
            onDelete = { 
                viewModel.deleteLog(selectedLog!!.log)
                selectedLog = null
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom nav/fab
        ) {
            item {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "Stats",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Last 7 Days",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Bar Chart
            item {
                if (stats.isNotEmpty()) {
                    BarChart(
                        data = stats,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                } else {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                Spacer(modifier = Modifier.height(48.dp))
            }

            // History Header
            item {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Log Items
            if (logs.isEmpty()) {
                item {
                    Text(
                        text = "No logs in the last 7 days.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(logs, key = { it.log.id }) { logDetails ->
                    SwipeToDeleteLogItem(
                        logDetails = logDetails,
                        onDelete = { viewModel.deleteLog(logDetails.log) },
                        onShowDetails = { selectedLog = logDetails },
                        onClick = { selectedLog = logDetails }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteLogItem(
    logDetails: com.anish.echo.data.models.LogDetails,
    onDelete: () -> Unit,
    onShowDetails: () -> Unit,
    onClick: () -> Unit = {}
) {
    val dismissState = rememberDismissState(
        confirmValueChange = {
            when (it) {
                DismissValue.DismissedToStart -> {
                    onDelete()
                    true
                }
                DismissValue.DismissedToEnd -> {
                    onShowDetails()
                    false // Don't dismiss, just trigger details popup
                }
                else -> false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color = when (dismissState.targetValue) {
                DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.primary // Blue for Details
                DismissValue.DismissedToStart -> MaterialTheme.colorScheme.error // Red for Delete
                else -> Color.Transparent
            }
            
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color, MaterialTheme.shapes.medium)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (direction == DismissDirection.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                 if (direction == DismissDirection.StartToEnd) {
                     Icon(androidx.compose.material.icons.Icons.Default.Info, contentDescription = "Details", tint = Color.White)
                 } else {
                     Icon(androidx.compose.material.icons.Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                 }
            }
        },
        dismissContent = {
            LogHistoryItem(logDetails = logDetails, onClick = onClick)
        }
    )
}

@Composable
fun LogHistoryItem(
    logDetails: com.anish.echo.data.models.LogDetails,
    onClick: () -> Unit = {}
) {
    val date = java.time.Instant.ofEpochMilli(logDetails.log.timestamp)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDateTime()
    val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("MMM d, h:mm a")
    
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Emoji display - from mood or from note (for mood check-ins)
                val displayEmoji = when {
                    logDetails.mood != null -> logDetails.mood.icon
                    logDetails.log.note != null && logDetails.log.note.contains("|") -> 
                        logDetails.log.note.substringBefore("|").trim()
                    else -> null
                }
                
                if (displayEmoji != null) {
                    Text(
                        text = displayEmoji,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
                
                Column {
                    Text(
                        text = logDetails.log.habitName ?: "Mood Log",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Show note/phrase for mood check-ins
                    val notePhrase = if (logDetails.log.note != null && logDetails.log.note.contains("|")) {
                        logDetails.log.note.substringAfter("|").trim()
                    } else {
                        null
                    }
                    
                    if (notePhrase != null) {
                        Text(
                            text = notePhrase,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    
                    Text(
                        text = date.format(timeFormatter),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Duration or check
            val duration = logDetails.log.duration ?: 0L
            if (duration > 0) {
                 Text(
                    text = formatDuration(duration),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (logDetails.habit != null) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun formatDuration(millis: Long): String {
    val minutes = millis / 1000 / 60
    return "${minutes}m"
}

@Composable
fun LogDetailsDialog(
    logDetails: LogDetails,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val date = java.time.Instant.ofEpochMilli(logDetails.log.timestamp)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDateTime()
    val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")
    val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("h:mm a")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Emoji display
                val displayEmoji = when {
                    logDetails.mood != null -> logDetails.mood.icon
                    logDetails.log.note != null && logDetails.log.note.contains("|") -> 
                        logDetails.log.note.substringBefore("|").trim()
                    else -> null
                }
                if (displayEmoji != null) {
                    Text(
                        text = displayEmoji,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
                Text(
                    text = logDetails.log.habitName ?: "Mood Check-in",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        text = {
            Column {
                // Date & Time
                Text(
                    text = date.format(dateFormatter),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = date.format(timeFormatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Duration if present
                val duration = logDetails.log.duration ?: 0L
                if (duration > 0) {
                    Row {
                        Text(
                            text = "Duration: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatDuration(duration),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Note if present
                val noteText = logDetails.log.note
                if (!noteText.isNullOrBlank()) {
                    // Check if it's a mood check-in format (emoji | phrase)
                    val displayNote = if (noteText.contains("|")) {
                        noteText.substringAfter("|").trim()
                    } else {
                        noteText
                    }
                    
                    if (displayNote.isNotBlank()) {
                        Text(
                            text = "Note:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = displayNote,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        }
    )
}


@Composable
fun BarChart(
    data: List<DailyStat>,
    modifier: Modifier = Modifier
) {
    val barColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    // Find max for scaling
    val maxCount = remember(data) { data.maxOfOrNull { it.totalCount }?.takeIf { it > 0 } ?: 1 }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { stat ->
            // Animate height on entry
            var animationPlayed by remember { mutableStateOf(false) }
            val targetHeight = if (stat.totalCount > 0) {
                (stat.totalCount.toFloat() / maxCount).coerceIn(0.15f, 1f)
            } else {
                0.08f // Minimum visible bar for empty days
            }
            
            val heightFraction by animateFloatAsState(
                targetValue = if (animationPlayed) targetHeight else 0f,
                animationSpec = tween(1000),
                label = "barHeight"
            )

            LaunchedEffect(Unit) {
                animationPlayed = true
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Bar container - takes most of the height
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // The actual bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .fillMaxHeight(heightFraction)
                            .background(
                                if (stat.totalCount > 0) barColor else trackColor.copy(alpha = 0.3f),
                                shape = MaterialTheme.shapes.small
                            )
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stat.dayLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor
                )
            }
        }
    }
}
