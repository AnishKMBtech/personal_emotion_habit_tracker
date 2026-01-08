package com.anish.echo

import android.app.Application
import com.anish.echo.data.db.EchoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class EchoApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { EchoDatabase.getDatabase(this, applicationScope) }
}
