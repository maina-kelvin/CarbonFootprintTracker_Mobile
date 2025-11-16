package com.example.carbonfootprinttracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityEntryDao {

    // Insert or replace if it already exists
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityEntry(entry: ActivityEntry)

    // Get all activity entries sorted by timestamp (newest first)
    @Query("SELECT * FROM activity_entries ORDER BY timestamp DESC")
    fun getAllActivityEntries(): Flow<List<ActivityEntry>>

    // Get activity entries by category
    @Query("SELECT * FROM activity_entries WHERE category = :category ORDER BY timestamp DESC")
    fun getActivityEntriesByCategory(category: String): Flow<List<ActivityEntry>>

    // Delete a specific activity entry
    @Delete
    suspend fun deleteActivityEntry(entry: ActivityEntry)

    // Update an existing record
    @Update
    suspend fun updateActivityEntry(entry: ActivityEntry)

    // Clear all activity entries
    @Query("DELETE FROM activity_entries")
    suspend fun deleteAllActivityEntries()

    // Get activity entry by ID
    @Query("SELECT * FROM activity_entries WHERE id = :id")
    suspend fun getActivityEntryById(id: String): ActivityEntry?
}

