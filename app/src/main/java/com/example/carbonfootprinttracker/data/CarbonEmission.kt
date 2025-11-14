package com.example.carbonfootprinttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

// Use TypeConverter for Date (Room can't store Date directly)
@Entity(tableName = "carbon_data")
@TypeConverters(DateConverter::class)
data class CarbonEmission(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val category: String,
    val carbonEmission: Double,
    val timestamp: Date = Date(),

    // Optional fields
    val transportMode: String? = null,
    val fuelType: String? = null,
    val mileage: Double? = null,
    val flightDuration: Double? = null,

    val meatMeals: Int? = null,
    val localFoodPercent: Int? = null,
    val foodWaste: Double? = null,

    val electricityUsage: Double? = null,
    val gasUsage: Double? = null,
    val oilUsage: Double? = null,
    val householdSize: Int? = null
)