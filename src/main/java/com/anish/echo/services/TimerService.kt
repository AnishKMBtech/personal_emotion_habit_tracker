package com.anish.echo.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.anish.echo.R

class TimerService : Service() {

    private val binder = TimerBinder()
    private val scope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime = _elapsedTime.asStateFlow()

    private var startTime = 0L
    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    fun startTimer(habitName: String = "Habit") {
        if (_isRunning.value) return
        
        _isRunning.value = true
        startTime = System.currentTimeMillis() - _elapsedTime.value
        val notificationContent = "Timing: $habitName"
        if (Build.VERSION.SDK_INT >= 34) {
             startForeground(NOTIFICATION_ID, createNotification(notificationContent), android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
             startForeground(NOTIFICATION_ID, createNotification(notificationContent))
        }
        
        timerJob = scope.launch {
            while (_isRunning.value) {
                _elapsedTime.value = System.currentTimeMillis() - startTime
                delay(100L) // Update every 100ms
                updateNotification(_elapsedTime.value)
            }
        }
    }

    fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        // Keep foreground or update notification to "Paused"
        startForeground(NOTIFICATION_ID, createNotification("Paused"))
    }

    fun stopTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    fun resetTimer() {
        _elapsedTime.value = 0L
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Timer Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Echo Timer")
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher) // Ensure this resource exists or use generic
            .setOngoing(true)
            .build()
    }
    
    // Helper to format time (e.g., 00:05)
    private fun formatTime(millis: Long): String {
        val seconds = millis / 1000
        val m = seconds / 60
        val s = seconds % 60
        return String.format("%02d:%02d", m, s)
    }

    private fun updateNotification(millis: Long) {
        val notification = createNotification("Elapsed: ${formatTime(millis)}")
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "EchoTimerChannel"
        const val NOTIFICATION_ID = 1
    }
}
