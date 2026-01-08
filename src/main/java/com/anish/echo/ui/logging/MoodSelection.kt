package com.anish.echo.ui.logging

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anish.echo.data.models.Mood

@Composable
fun MoodSelection(
    moods: List<Mood>,
    selectedMoodId: Int?,
    onMoodSelected: (Int) -> Unit
) {
    if (moods.isEmpty()) {
        Text("No moods available", style = MaterialTheme.typography.bodySmall)
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier.fillMaxWidth().height(80.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(moods) { mood ->
            MoodItem(
                mood = mood,
                isSelected = mood.id == selectedMoodId,
                onClick = { onMoodSelected(mood.id) }
            )
        }
    }
}

@Composable
fun MoodItem(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(4.dp)
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent, CircleShape)
            .padding(8.dp)
    ) {
        Text(text = mood.icon, style = MaterialTheme.typography.headlineMedium)
    }
}
