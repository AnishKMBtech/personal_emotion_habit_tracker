package com.anish.echo.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "logs",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Mood::class,
            parentColumns = ["id"],
            childColumns = ["moodId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId"]), Index(value = ["moodId"])]
)
data class LogEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int?,     // nullable for mood-only logs
    val habitName: String? = null, // Snapshot of habit name at log time
    val moodId: Int?,      // nullable for habit-only logs
    val duration: Long?,  // millis (only if timed)
    val note: String?,
    val timestamp: Long
)
