package com.phishguard.app.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ThreatEntry::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun threatDao(): ThreatDao
}