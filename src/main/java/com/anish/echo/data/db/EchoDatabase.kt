package com.anish.echo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.anish.echo.data.models.Habit
import com.anish.echo.data.models.Mood
import com.anish.echo.data.models.LogEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Habit::class, Mood::class, LogEntry::class], version = 2, exportSchema = false)
abstract class EchoDatabase : RoomDatabase() {
    abstract fun dao(): EchoDao

    companion object {
        @Volatile
        private var INSTANCE: EchoDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): EchoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EchoDatabase::class.java,
                    "echo-db"
                )
                .fallbackToDestructiveMigration() // For dev: clears DB on schema change
                .addCallback(EchoDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class EchoDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Synchronous, raw SQL insertion to guarantee data existence on creation
            db.beginTransaction()
            try {
                // IDs match MoodSelection.MOODS (1..5)
                db.execSQL("INSERT OR IGNORE INTO moods (id, label, icon) VALUES (1, 'Great', '😄')")
                db.execSQL("INSERT OR IGNORE INTO moods (id, label, icon) VALUES (2, 'Good', '🙂')")
                db.execSQL("INSERT OR IGNORE INTO moods (id, label, icon) VALUES (3, 'Okay', '😐')")
                db.execSQL("INSERT OR IGNORE INTO moods (id, label, icon) VALUES (4, 'Low', '😔')")
                db.execSQL("INSERT OR IGNORE INTO moods (id, label, icon) VALUES (5, 'Bad', '😫')")
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            // Synchronous, raw SQL insertion to guarantee data existence
            db.beginTransaction()
            try {
                // IDs match MoodSelection.MOODS (1..5)
                db.execSQL("INSERT OR IGNORE INTO moods (id, label, icon) VALUES (1, 'Great', '😄')")
                db.execSQL("INSERT OR IGNORE INTO moods (id, label, icon) VALUES (2, 'Good', '🙂')")
                db.execSQL("INSERT OR IGNORE INTO moods (id, label, icon) VALUES (3, 'Okay', '😐')")
                db.execSQL("INSERT OR IGNORE INTO moods (id, label, icon) VALUES (4, 'Low', '😔')")
                db.execSQL("INSERT OR IGNORE INTO moods (id, label, icon) VALUES (5, 'Bad', '😫')")
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }

        // Removed suspend populateDatabase as we use execSQL now
            // Pre-populate Moods

    }
}
