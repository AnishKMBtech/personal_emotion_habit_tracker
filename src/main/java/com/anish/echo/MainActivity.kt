package com.anish.echo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anish.echo.ui.home.HomeScreen
import com.anish.echo.ui.logging.LogSheet
import com.anish.echo.ui.settings.HabitManagementScreen
import com.anish.echo.ui.settings.SettingsScreen
import com.anish.echo.ui.settings.ThemeScreen
import com.anish.echo.ui.stats.StatsScreen
import com.anish.echo.ui.theme.EchoTheme
import com.anish.echo.ui.theme.ThemeMode
import com.anish.echo.ui.timer.TimerScreen
import com.anish.echo.data.SettingsPreferences
import com.anish.echo.ui.CurvedBottomNavigation
import com.anish.echo.ui.echoNavItems

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Load persisted theme on start
            val settingsPrefs = remember { SettingsPreferences(this) }
            var currentTheme by remember { mutableStateOf(settingsPrefs.themeMode) }
            
            EchoTheme(themeMode = currentTheme) {
                val navController = rememberNavController()
                
                // State for Log Sheet
                var showLogSheet by remember { mutableStateOf(false) }
                var logSheetDuration by remember { mutableLongStateOf(0L) }
                var logSheetHabitId by remember { mutableStateOf<Int?>(null) }
                var logSheetHabitName by remember { mutableStateOf<String?>(null) }
                val sheetState = rememberModalBottomSheetState()
                
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val isTimerScreen = currentRoute?.startsWith("timer") == true
                
                Scaffold(
                    bottomBar = {
                        // Hide bottom nav during timer for fullscreen experience
                        if (!isTimerScreen) {
                            CurvedBottomNavigation(
                                items = echoNavItems,
                                currentRoute = currentRoute,
                                onItemClick = { item ->
                                    navController.navigate(item.route) { launchSingleTop = true }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        // Don't apply padding to timer screen for true fullscreen
                        modifier = if (isTimerScreen) Modifier else Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                onLogMoodClick = { 
                                    navController.navigate("habits")
                                },
                                onStartTimerClick = { id, name ->
                                    navController.navigate("timer/$id/$name")
                                },
                                onHabitCheck = { id, name ->
                                    logSheetDuration = 0L
                                    logSheetHabitId = id
                                    logSheetHabitName = name
                                    showLogSheet = true
                                },
                                onSettingsClick = {
                                    navController.navigate("settings")
                                },
                                onManageHabitsClick = {
                                    navController.navigate("habits")
                                }
                            )
                        }
                        composable(
                            "timer/{habitId}/{habitName}",
                            arguments = listOf(
                                androidx.navigation.navArgument("habitId") { type = androidx.navigation.NavType.IntType },
                                androidx.navigation.navArgument("habitName") { type = androidx.navigation.NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val habitId = backStackEntry.arguments?.getInt("habitId") ?: 0
                            val habitName = backStackEntry.arguments?.getString("habitName") ?: "Habit"
                            
                            TimerScreen(
                                habitName = habitName,
                                habitId = habitId,
                                onFinish = { duration, hId ->
                                    logSheetDuration = duration
                                    logSheetHabitId = hId
                                    logSheetHabitName = habitName
                                    showLogSheet = true
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("stats") {
                            StatsScreen()
                        }
                        composable("settings") {
                            SettingsScreen(
                                onNavigateToHabits = { navController.navigate("habits") },
                                onNavigateToTheme = { navController.navigate("theme") },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("habits") {
                            HabitManagementScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("theme") {
                            ThemeScreen(
                                currentTheme = currentTheme,
                                onThemeSelected = { newTheme -> 
                                    currentTheme = newTheme
                                    settingsPrefs.themeMode = newTheme  // Persist the change
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
                
                if (showLogSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showLogSheet = false },
                        sheetState = sheetState
                    ) {
                        LogSheet(
                            initialDuration = logSheetDuration,
                            initialHabitId = logSheetHabitId,
                            initialHabitName = logSheetHabitName,
                            onDismiss = { showLogSheet = false }
                        )
                    }
                }
            }
        }
    }
}

