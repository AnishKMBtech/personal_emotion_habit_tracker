package com.anish.echo.data.db

import androidx.room.*
import com.anish.echo.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EchoDao {
    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM moods")
    fun getAllMoods(): Flow<List<Mood>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: Mood)

    @Insert
    suspend fun insertLog(log: LogEntry)

    @Query("SELECT * FROM logs WHERE timestamp >= :since")
    fun getLogsSince(since: Long): Flow<List<LogEntry>>

    @Transaction
    @Query("SELECT * FROM logs WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun getLogDetailsSince(since: Long): Flow<List<LogDetails>>
    
    @Delete
    suspend fun deleteLog(log: LogEntry)
}
