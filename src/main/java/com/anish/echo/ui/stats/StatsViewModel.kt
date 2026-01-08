package com.anish.echo.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.anish.echo.EchoApplication
import com.anish.echo.data.db.EchoDao
import com.anish.echo.data.models.LogEntry
import com.anish.echo.data.models.LogDetails
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class DailyStat(
    val dayLabel: String,
    val totalCount: Int
)

class StatsViewModel(private val dao: EchoDao) : ViewModel() {

    // Get logs for the last 7 days
    private val sevenDaysAgo = LocalDate.now().minusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val weeklyStats: StateFlow<List<DailyStat>> = dao.getLogsSince(sevenDaysAgo)
        .map { logs ->
            // Group by day of week
            val dayCounts = logs.groupBy { 
                LocalDate.ofInstant(java.time.Instant.ofEpochMilli(it.timestamp), ZoneId.systemDefault()) 
            }
            
            // Ensure all past 7 days are represented, even if 0
            (0..6).map { i ->
                val date = LocalDate.now().minusDays((6 - i).toLong())
                val label = date.format(DateTimeFormatter.ofPattern("EEE"))
                val count = dayCounts[date]?.size ?: 0
                DailyStat(label, count)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentLogs = dao.getLogDetailsSince(sevenDaysAgo)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    fun deleteLog(log: LogEntry) {
        viewModelScope.launch {
            dao.deleteLog(log)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EchoApplication)
                StatsViewModel(application.database.dao())
            }
        }
    }
}
