package com.example.carbonfootprinttracker.ui

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.carbonfootprinttracker.R
import com.example.carbonfootprinttracker.data.AppDatabase
import com.example.carbonfootprinttracker.data.CarbonEmission
import kotlinx.coroutines.launch
import java.util.*

class DataInputActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_input)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "carbon_db")
            .build()

        val inputMileage = findViewById<EditText>(R.id.inputMileage)
        val inputFuelType = findViewById<EditText>(R.id.inputFuelType)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val resultText = findViewById<TextView>(R.id.resultText)

        submitButton.setOnClickListener {
            val mileage = inputMileage.text.toString().toDoubleOrNull() ?: 0.0
            val fuelType = inputFuelType.text.toString()

            val emissionValue = mileage * 0.21 // Simple example

            resultText.text = "Carbon Emission: $emissionValue kg CO₂"

            val emission = CarbonEmission(
                category = "transport",
                carbonEmission = emissionValue,
                timestamp = Date(),
                mileage = mileage,
                fuelType = fuelType
            )

            lifecycleScope.launch {
                db.carbonEmissionDao().insertEmission(emission)
            }
        }
    }
}