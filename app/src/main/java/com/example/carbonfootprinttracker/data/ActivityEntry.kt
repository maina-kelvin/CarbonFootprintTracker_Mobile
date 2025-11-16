package com.example.carbonfootprinttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "activity_entries")
data class ActivityEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val timestamp: Long,
    val category: String,
    val co2Kg: Double,
    val note: String? = null
)

