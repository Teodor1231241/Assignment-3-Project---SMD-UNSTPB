package com.phishguard.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreatDao {
    @Query("SELECT * FROM threat_entries ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ThreatEntry>>

    @Query("SELECT * FROM threat_entries WHERE url = :url LIMIT 1")
    suspend fun getByUrl(url: String): ThreatEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(threat: ThreatEntry)
}