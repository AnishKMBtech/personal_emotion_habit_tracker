package com.anish.echo.ui.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anish.echo.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Choose Theme",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Theme 1 - Dynamic/Grey
            ThemeOption(
                title = "Dynamic",
                subtitle = "Wallpaper colors (Android 12+)",
                isSelected = currentTheme == ThemeMode.DARK_MODE_1,
                previewColors = listOf(
                    Color(0xFF121212),
                    Color(0xFF1E1E1E),
                    Color(0xFFB0B0B0)  // Grey
                ),
                onClick = { onThemeSelected(ThemeMode.DARK_MODE_1) }
            )
            
            // Theme 2 - Warm Gold
            ThemeOption(
                title = "Warm Gold",
                subtitle = "Dark with gold accent",
                isSelected = currentTheme == ThemeMode.DARK_MODE_2,
                previewColors = listOf(
                    Color(0xFF121212),
                    Color(0xFF1C1C1C),
                    Color(0xFFF2D6B3)
                ),
                onClick = { onThemeSelected(ThemeMode.DARK_MODE_2) }
            )
            
            // Theme 3 - Cherry Mocha
            ThemeOption(
                title = "Cherry Mocha",
                subtitle = "Dark mocha with coral",
                isSelected = currentTheme == ThemeMode.CHERRY_MOCHA,
                previewColors = listOf(
                    Color(0xFF140A08),
                    Color(0xFF1C0F0D),
                    Color(0xFFF6B7AC)
                ),
                onClick = { onThemeSelected(ThemeMode.CHERRY_MOCHA) }
            )
            
            // Theme 4 - Light Cream
            ThemeOption(
                title = "Light Cream",
                subtitle = "Soft cream tones",
                isSelected = currentTheme == ThemeMode.LIGHT,
                previewColors = listOf(
                    Color(0xFFF5F0E8),  // Cream
                    Color(0xFFFAF7F2),  // Light cream
                    Color(0xFF3A3A3A)   // Dark grey text
                ),
                onClick = { onThemeSelected(ThemeMode.LIGHT) }
            )
            
            // Theme 5 - Warm Amber
            ThemeOption(
                title = "Warm Amber",
                subtitle = "Soft amber and cream",
                isSelected = currentTheme == ThemeMode.YELLOW,
                previewColors = listOf(
                    Color(0xFFFFF3D0),  // Soft cream-yellow
                    Color(0xFFFFECB3),  // Warm yellow
                    Color(0xFF3A3A3A)   // Dark text
                ),
                onClick = { onThemeSelected(ThemeMode.YELLOW) }
            )
        }
    }
}

@Composable
fun ThemeOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    previewColors: List<Color>,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    )
                } else Modifier
            ),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Color preview swatches
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    previewColors.forEach { color ->
                        Surface(
                            modifier = Modifier.size(24.dp),
                            color = color,
                            shape = MaterialTheme.shapes.small
                        ) {}
                    }
                }
                
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
