package com.anish.echo.ui.timer

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.anish.echo.EchoApplication
import com.anish.echo.services.TimerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    
    // We bind to the service to control it / read state
    private var timerService: TimerService? = null
    private var isBound = false
    
    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime = _elapsedTime.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()
    
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            isBound = true
            
            // Collect updates from Service
            viewModelScope.launch {
                launch {
                    timerService?.elapsedTime?.collect {
                         _elapsedTime.value = it
                     }
                }
                launch {
                    timerService?.isRunning?.collect {
                         _isRunning.value = it
                    }
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            timerService = null
        }
    }
    
    init {
        // Bind immediately
        val intent = Intent(application, TimerService::class.java)
        application.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun startTimer(habitName: String = "Habit") {
         timerService?.startTimer(habitName)
    }
    
    fun stopTimer() {
        timerService?.stopTimer()
    }
    
    fun pauseTimer() {
        timerService?.pauseTimer()
    }

    fun formatTime(millis: Long): String {
       val seconds = millis / 1000
       val m = seconds / 60
       val s = seconds % 60
       return String.format("%02d:%02d", m, s)
    }

    override fun onCleared() {
        super.onCleared()
        if (isBound) {
            getApplication<Application>().unbindService(connection)
            isBound = false
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EchoApplication)
                TimerViewModel(application)
            }
        }
    }
}
