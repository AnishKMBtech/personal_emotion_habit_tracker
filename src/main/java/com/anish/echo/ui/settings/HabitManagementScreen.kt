package com.anish.echo.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anish.echo.data.models.Habit
import com.anish.echo.ui.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory) // Reusing HomeViewModel since it has Habit list
) {
    val habits by viewModel.habits.collectAsState()
    var showEditSheet by remember { mutableStateOf(false) }
    var selectedHabit by remember { mutableStateOf<Habit?>(null) } // Null = New Habit

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Habits") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    selectedHabit = null
                    showEditSheet = true 
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(habits) { habit ->
                ListItem(
                    headlineContent = { Text(habit.name, fontWeight = FontWeight.Medium) },
                    supportingContent = { Text(if (habit.isTimed) "Timed" else "Simple Checkbox") },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { 
                                selectedHabit = habit
                                showEditSheet = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = { viewModel.deleteHabit(habit) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                )
                Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            }
        }
        
        if (showEditSheet) {
            EditHabitSheet(
                habit = selectedHabit,
                onDismiss = { showEditSheet = false },
                onSave = { name, isTimed, color ->
                    if (selectedHabit == null) {
                        // Create
                         viewModel.insertHabit(
                             Habit(name = name, isTimed = isTimed, color = color, createdAt = System.currentTimeMillis())
                         )
                    } else {
                        // Update (Not fully implemented in DAO/ViewModel yet, assumes ID is kept if we clone?)
                        // ACTUALLY: Habit data class is immutable. We need copy logic.
                        // And DAO insert OnConflict = REPLACE. So just need same ID.
                        viewModel.insertHabit(
                             selectedHabit!!.copy(name = name, isTimed = isTimed, color = color)
                        )
                    }
                }
            )
        }
    }
}
