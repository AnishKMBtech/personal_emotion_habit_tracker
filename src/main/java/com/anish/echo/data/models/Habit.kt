package com.anish.echo.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val color: Long,        // For UI
    val isTimed: Boolean,   // Timer or checkbox
    val createdAt: Long
)
