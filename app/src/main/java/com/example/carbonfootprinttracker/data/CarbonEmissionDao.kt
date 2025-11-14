package com.example.carbonfootprinttracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CarbonEmissionDao {

    // Insert or replace if it already exists
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmission(emission: CarbonEmission)

    // Get all emissions sorted by timestamp (newest first)
    @Query("SELECT * FROM carbon_data ORDER BY timestamp DESC")
    fun getAllEmissions(): Flow<List<CarbonEmission>>

    // Optional: delete a specific emission
    @Delete
    suspend fun deleteEmission(emission: CarbonEmission)

    // Optional: update an existing record
    @Update
    suspend fun updateEmission(emission: CarbonEmission)

    // Optional: clear all records
    @Query("DELETE FROM carbon_data")
    suspend fun deleteAll()
}