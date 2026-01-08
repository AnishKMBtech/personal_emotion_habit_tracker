package com.anish.echo.ui.logging

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anish.echo.data.db.EchoDao
import com.anish.echo.data.models.LogEntry
import com.anish.echo.ui.home.HomeViewModel // Reusing logic or create LogViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogSheet(
    initialDuration: Long = 0L,
    initialHabitId: Int? = null,
    initialHabitName: String? = null,
    onDismiss: () -> Unit,
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory) 
) {
    val scope = rememberCoroutineScope()
    
    var selectedMoodId by remember { mutableIntStateOf(-1) } // -1 = none
    var note by remember { mutableStateOf("") }
    
    // Bottom Sheet Content
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(), 
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Log Session",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (initialDuration > 0) {
           Text(
               text = "Duration: ${initialDuration / 1000}s",
               style = MaterialTheme.typography.bodyLarge
           )
           Spacer(modifier = Modifier.height(16.dp))
        }

        Text("How do you feel?", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        val moods by homeViewModel.moods.collectAsState()

        MoodSelection(
            moods = moods,
            selectedMoodId = if (selectedMoodId == -1) null else selectedMoodId,
            onMoodSelected = { selectedMoodId = it }
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                scope.launch {
                    val log = LogEntry(
                        habitId = initialHabitId,
                        habitName = initialHabitName, // Save snapshot
                        moodId = if (selectedMoodId == -1) null else selectedMoodId,
                        duration = if (initialDuration > 0) initialDuration else null,
                        note = note.ifBlank { null },
                        timestamp = System.currentTimeMillis()
                    )
                    homeViewModel.insertLog(log) 
                    onDismiss()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Save Log")
        }
    }
}
