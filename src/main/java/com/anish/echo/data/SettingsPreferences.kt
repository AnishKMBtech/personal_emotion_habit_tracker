package com.anish.echo.data

import android.content.Context
import android.content.SharedPreferences
import com.anish.echo.ui.theme.ThemeMode

/**
 * Simple SharedPreferences wrapper for app settings persistence.
 */
class SettingsPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, 
        Context.MODE_PRIVATE
    )
    
    var themeMode: ThemeMode
        get() {
            val ordinal = prefs.getInt(KEY_THEME_MODE, ThemeMode.DARK_MODE_2.ordinal)
            return ThemeMode.entries.getOrElse(ordinal) { ThemeMode.DARK_MODE_2 }
        }
        set(value) {
            prefs.edit().putInt(KEY_THEME_MODE, value.ordinal).apply()
        }
    
    companion object {
        private const val PREFS_NAME = "echo_settings"
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
