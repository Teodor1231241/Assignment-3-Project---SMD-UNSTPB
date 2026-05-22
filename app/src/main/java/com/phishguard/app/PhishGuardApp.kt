package com.phishguard.app

import android.app.Application
import androidx.room.Room
import com.phishguard.app.data.AppDatabase

class PhishGuardApp : Application() {
    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "phishguard-db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}