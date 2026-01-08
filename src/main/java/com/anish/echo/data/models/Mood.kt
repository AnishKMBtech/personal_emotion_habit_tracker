package com.anish.echo.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moods")
data class Mood(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,     // "Happy", "Low", "Neutral"
    val icon: String       // emoji or vector ref
)
