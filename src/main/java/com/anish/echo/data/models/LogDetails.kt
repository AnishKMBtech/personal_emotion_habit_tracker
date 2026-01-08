package com.anish.echo.data.models

import androidx.room.Embedded
import androidx.room.Relation

data class LogDetails(
    @Embedded val log: LogEntry,
    @Relation(
        parentColumn = "habitId",
        entityColumn = "id"
    )
    val habit: Habit?,
    @Relation(
        parentColumn = "moodId",
        entityColumn = "id"
    )
    val mood: Mood?
)
