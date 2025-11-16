package com.example.carbonfootprinttracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CarbonEmission::class, ActivityEntry::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun carbonEmissionDao(): CarbonEmissionDao
    abstract fun activityEntryDao(): ActivityEntryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "carbon_tracker_db"
                )
                    .fallbackToDestructiveMigration() // clears DB on version mismatch
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}