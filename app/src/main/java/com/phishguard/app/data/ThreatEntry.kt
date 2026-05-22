package com.phishguard.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "threat_entries")
data class ThreatEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String,
    val riskLevel: String, // "HIGH", "MEDIUM", "LOW"
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)