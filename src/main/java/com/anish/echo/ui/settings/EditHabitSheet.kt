package com.anish.echo.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anish.echo.data.models.Habit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHabitSheet(
    habit: Habit?,
    onDismiss: () -> Unit,
    onSave: (String, Boolean, Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var name by remember { mutableStateOf(habit?.name ?: "") }
    var isTimed by remember { mutableStateOf(habit?.isTimed ?: false) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = if (habit == null) "New Habit" else "Edit Habit",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Habit Name") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text("Timed Habit?", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = isTimed, onCheckedChange = { isTimed = it })
            }
            Text(
                "Timed habits use the stopwatch. Uncheck for simple checkboxes.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { 
                    if (name.isNotBlank()) {
                         onSave(name, isTimed, 0xFFD0BCFF) // Default color for now
                         onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Save")
            }
        }
    }
}
