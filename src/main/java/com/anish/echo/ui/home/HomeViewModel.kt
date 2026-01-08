package com.anish.echo.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.anish.echo.EchoApplication
import com.anish.echo.data.db.EchoDao
import com.anish.echo.data.models.Habit
import com.anish.echo.data.models.LogEntry
import com.anish.echo.data.models.Mood
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val dao: EchoDao) : ViewModel() {

    // Hot flow of habits, always updated
    val habits: StateFlow<List<Habit>> = dao.getAllHabits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val moods: StateFlow<List<Mood>> = dao.getAllMoods()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val startOfToday = java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    
    // Combining Habits with their completion status for Today
    val habitStates: StateFlow<List<HabitUIState>> = kotlinx.coroutines.flow.combine(
        dao.getAllHabits(),
        dao.getLogsSince(startOfToday)
    ) { habits, logs ->
        habits.map { habit ->
            HabitUIState(
                habit = habit,
                isCompletedToday = logs.any { it.habitId == habit.id }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Check if any mood-only log was created today
    val hasLoggedMoodToday: StateFlow<Boolean> = dao.getLogsSince(startOfToday)
        .map { logs ->
            logs.any { it.habitId == null && it.habitName == "Mood Check-in" }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun completeHabit(habit: Habit) {
        viewModelScope.launch {
            // Simple toggle logic for now: just log it.
            // In a real toggle we might check if logged today, but for v1 explicit logging is fine.
            // Or we check if there is a log for today.
            // For "Check logic", let's assume tapping checkbox = create a log for NOW.
            
            val log = LogEntry(
                habitId = habit.id,
                habitName = habit.name, // Save snapshot
                moodId = null, 
                timestamp = System.currentTimeMillis(),
                duration = null,
                note = null
            )
            try {
                dao.insertLog(log) 
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    suspend fun insertLog(log: LogEntry) {
        try {
            dao.insertLog(log)
            android.util.Log.d("EchoApp", "Log saved successfully: $log")
        } catch (e: Exception) {
            android.util.Log.e("EchoApp", "Failed to save log", e)
            e.printStackTrace()
        }
    }
    
    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            dao.deleteHabit(habit)
        }
    }
    
    fun insertHabit(habit: Habit) {
        viewModelScope.launch {
            dao.insertHabit(habit)
        }
    }
    
    fun deleteLog(log: LogEntry) {
        viewModelScope.launch {
            dao.deleteLog(log)
        }
    }
    
    fun logMood(moodEmoji: String, phrase: String) {
        viewModelScope.launch {
            val log = LogEntry(
                habitId = null,
                habitName = "Mood Check-in",
                moodId = null,
                timestamp = System.currentTimeMillis(),
                duration = null,
                note = "$moodEmoji | $phrase"  // Store emoji and phrase together
            )
            try {
                dao.insertLog(log)
                android.util.Log.d("EchoApp", "Mood logged: $moodEmoji - $phrase")
            } catch (e: Exception) {
                android.util.Log.e("EchoApp", "Failed to log mood", e)
            }
        }
    }
    
    // Factory for creating HomeViewModel with DAO
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EchoApplication)
                HomeViewModel(application.database.dao())
            }
        }
    }
}

data class HabitUIState(
    val habit: Habit,
    val isCompletedToday: Boolean
)
