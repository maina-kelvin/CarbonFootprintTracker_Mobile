package com.example.carbonfootprinttracker.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.carbonfootprinttracker.data.AppDatabase
import com.example.carbonfootprinttracker.data.CarbonEmission
import kotlinx.coroutines.launch

// Added for the Dropdown Menu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults

// --- IMPORTS FOR ICONS ---
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign

import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class) // Required for ExposedDropdownMenuBox
@Composable
fun CarbonFootprintInput(db: AppDatabase, onGoToHome: () -> Unit) {

    // --- STATE ---
    var category by remember { mutableStateOf("Transport") }
    var result by remember { mutableStateOf<Double?>(null) }

    // Transport state
    var transportMode by remember { mutableStateOf("Vehicles") } // "Vehicles" or "Flights"
    var carType by remember { mutableStateOf("Compact Car") }
    var fuelType by remember { mutableStateOf("Petrol") }
    var mileage by remember { mutableStateOf("") }
    var flightDuration by remember { mutableStateOf("") }

    // Food state
    var meatMeals by remember { mutableStateOf("") }
    var localFoodPercent by remember { mutableStateOf("0") } // <-- CHANGED TO STRING
    var foodWaste by remember { mutableStateOf("") }

    // House state
    var electricityUsage by remember { mutableStateOf("") }
    var heatingType by remember { mutableStateOf("None") }
    var gasUsage by remember { mutableStateOf("") }
    var oilUsage by remember { mutableStateOf("") }
    var householdSize by remember { mutableStateOf("1") }

    val scope = rememberCoroutineScope()
    val dao = db.carbonEmissionDao()

    // --- Dropdown Options ---
    val transportModeOptions = listOf("Vehicles", "Flights")
    val carTypeOptions = listOf("Compact Car", "SUV", "Pick Up Truck", "Commercial Truck")
    val fuelTypeOptions = listOf("Petrol", "Diesel")
    val heatingTypeOptions = listOf("None", "Gas", "Oil", "Electric")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(65.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter Your Emissions", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(20.dp))

        // --- CATEGORY BUTTONS ---
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            listOf("Transport", "Food", "House").forEach { cat ->
                Button(
                    onClick = {
                        category = cat
                        result = null
                        localFoodPercent = "0" // <-- RESET STRING STATE
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (category == cat)
                            Color(0xFF4CAF50)
                        else MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(getCategoryIcon(cat), contentDescription = cat)
                        Spacer(Modifier.width(8.dp))
                        Text(cat)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // --- DYNAMIC INPUT FORM ---
        when (category) {
            "Transport" -> {
                // ... (Transport code is unchanged)
                DropdownMenuInput(
                    label = "Transport Type",
                    options = transportModeOptions,
                    selectedOption = transportMode,
                    onOptionSelected = { transportMode = it }
                )
                Spacer(Modifier.height(8.dp))

                if (transportMode == "Vehicles") {
                    DropdownMenuInput(
                        label = "Car Type",
                        options = carTypeOptions,
                        selectedOption = carType,
                        onOptionSelected = { carType = it }
                    )
                    Spacer(Modifier.height(8.dp))
                    DropdownMenuInput(
                        label = "Fuel Type",
                        options = fuelTypeOptions,
                        selectedOption = fuelType,
                        onOptionSelected = { fuelType = it }
                    )
                    Spacer(Modifier.height(8.dp))
                    NumericTextField(
                        label = "Enter distance (km)",
                        value = mileage,
                        onValueChange = { mileage = it }
                    )
                } else { // "Flights"
                    NumericTextField(
                        label = "Enter flight duration (hours)",
                        value = flightDuration,
                        onValueChange = { flightDuration = it }
                    )
                }
            }
            "Food" -> {
                NumericTextField(
                    label = "Enter meat meals (per day)",
                    value = meatMeals,
                    onValueChange = { meatMeals = it }
                )
                Spacer(Modifier.height(8.dp))

                // --- SLIDER + TEXTFIELD FOR LOCAL FOOD ---
                Text("Organic/Local Food (%)")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // This is the source of truth for the float value
                    val percentFloat = localFoodPercent.toFloatOrNull() ?: 0f

                    // 1. The Number Input
                    OutlinedTextField(
                        value = localFoodPercent,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty()) {
                                localFoodPercent = ""
                            } else if (newValue.matches(Regex("^\\d{1,3}\$"))) { // 1-3 digits
                                val num = newValue.toInt()
                                if (num in 0..100) { // Cap at 100
                                    localFoodPercent = newValue
                                }
                            }
                        },
                        modifier = Modifier.width(90.dp),
                        label = { Text("%") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End)
                    )

                    // 2. The Slider
                    Slider(
                        value = percentFloat,
                        onValueChange = {
                            // Update the string state from the slider
                            localFoodPercent = it.toInt().toString()
                        },
                        valueRange = 0f..100f,
                        modifier = Modifier.weight(1f) // Fills remaining space
                    )
                }
                // --- END OF SLIDER + TEXTFIELD ---

                Spacer(Modifier.height(8.dp))
                NumericTextField(
                    label = "Food Waste (kg per day)",
                    value = foodWaste,
                    onValueChange = { foodWaste = it }
                )
            }
            "House" -> {
                // ... (House code is unchanged)
                NumericTextField(
                    label = "Electricity Usage (kWh)",
                    value = electricityUsage,
                    onValueChange = { electricityUsage = it }
                )
                Spacer(Modifier.height(8.dp))
                NumericTextField(
                    label = "Household Size",
                    value = householdSize,
                    onValueChange = { householdSize = it }
                )
                Spacer(Modifier.height(8.dp))
                DropdownMenuInput(
                    label = "Heating Type",
                    options = heatingTypeOptions,
                    selectedOption = heatingType,
                    onOptionSelected = { heatingType = it }
                )

                if (heatingType == "Gas") {
                    Spacer(Modifier.height(8.dp))
                    NumericTextField(
                        label = "Gas Usage (kg)",
                        value = gasUsage,
                        onValueChange = { gasUsage = it }
                    )
                } else if (heatingType == "Oil") {
                    Spacer(Modifier.height(8.dp))
                    NumericTextField(
                        label = "Oil Usage (litres)",
                        value = oilUsage,
                        onValueChange = { oilUsage = it }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // --- CALCULATE BUTTON ---
        Button(onClick = {
            // --- 1. GET ALL VALUES FROM ALL FORMS ---
            val mileageNum = mileage.toDoubleOrNull() ?: 0.0
            val flightDurationNum = flightDuration.toDoubleOrNull() ?: 0.0
            val meatMealsNum = meatMeals.toDoubleOrNull() ?: 0.0
            val localFoodPercentNum = localFoodPercent.toDoubleOrNull() ?: 0.0 // <-- UPDATED
            val foodWasteNum = foodWaste.toDoubleOrNull() ?: 0.0
            val electricityNum = electricityUsage.toDoubleOrNull() ?: 0.0
            val gasNum = gasUsage.toDoubleOrNull() ?: 0.0
            val oilNum = oilUsage.toDoubleOrNull() ?: 0.0
            val householdSizeNum = max(1, householdSize.toIntOrNull() ?: 1)

            // --- 2. CALCULATE ALL THREE CATEGORIES ---
            val transportEmission = if (transportMode == "Vehicles") {
                val factor = getTransportFactor(carType, fuelType)
                mileageNum * factor
            } else { // "Flights"
                flightDurationNum * 110
            }

            val foodEmission = (meatMealsNum * 2.5) +
                    ((1 - (localFoodPercentNum / 100)) * 10) +
                    (foodWasteNum * 2)

            val electricityEmission = electricityNum * 0.233
            val gasEmission = gasNum * 5.3
            val oilEmission = oilNum * 2.52
            val heatingEmission = if (heatingType == "Electric") electricityNum * 0.1 else 0.0
            val houseEmission = (electricityEmission + gasEmission + oilEmission + heatingEmission) / householdSizeNum

            // --- 3. SUM THE TOTAL ---
            val totalEmission = transportEmission + foodEmission + houseEmission
            result = totalEmission

            // --- 4. SAVE ONE COMBINED ENTRY TO DB ---
            scope.launch {
                val emissionData = CarbonEmission(
                    category = "Total",
                    carbonEmission = totalEmission,

                    // All Transport fields
                    transportMode = transportMode,
                    fuelType = if (transportMode == "Vehicles") fuelType else null,
                    mileage = if (transportMode == "Vehicles") mileageNum else null,
                    flightDuration = if (transportMode == "Flights") flightDurationNum else null,

                    // All Food fields
                    meatMeals = meatMeals.toIntOrNull(),
                    localFoodPercent = localFoodPercent.toIntOrNull(), // <-- UPDATED
                    foodWaste = foodWasteNum,

                    // All House fields
                    electricityUsage = electricityNum,
                    gasUsage = gasNum,
                    oilUsage = oilNum,
                    householdSize = householdSizeNum
                )
                dao.insertEmission(emissionData)
            }
        }) {
            Text("Calculate & Save Data")
        }

        Spacer(Modifier.height(16.dp))

        // --- DASHBOARD BUTTON ---
        Button(
            onClick = onGoToHome,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            )
        ) {
            Text("Back to Dashboard")
        }


        Spacer(Modifier.height(32.dp))

        // --- RESULT TEXT ---
        result?.let {
            val resultColor = when {
                it <= 10 -> Color(0xFF4CAF50) // Green for low
                it <= 25 -> Color(0xFFFBC02D) // Yellow for medium
                else -> Color(0xFFD32F2F)     // Red for high
            }

            Text(
                text = "Total Emissions: %.2f kg CO₂".format(it),
                fontWeight = FontWeight.Bold,
                color = resultColor,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

/**
 * Helper function to get an icon for each category.
 */
@Composable
private fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Transport" -> Icons.Default.DirectionsCar
        "Food" -> Icons.Default.Fastfood
        "House" -> Icons.Default.Home
        else -> Icons.Default.DirectionsCar // Fallback
    }
}

/**
 * A reusable composable for a dropdown menu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuInput(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * A reusable composable for a text field that only accepts numbers.
 */
@Composable
fun NumericTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            // Filter to only allow digits and one decimal point
            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                onValueChange(newValue)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

/**
 * Helper function to get emission factor based on car and fuel type.
 * These are example values.
 */
private fun getTransportFactor(carType: String, fuelType: String): Double {
    return when (carType) {
        "Compact Car" -> when (fuelType) {
            "Petrol" -> 0.12
            "Diesel" -> 0.11
            else -> 0.12
        }
        "SUV" -> when (fuelType) {
            "Petrol" -> 0.18
            "Diesel" -> 0.17
            else -> 0.18
        }
        "Pick Up Truck" -> when (fuelType) {
            "Petrol" -> 0.22
            "Diesel" -> 0.20
            else -> 0.22
        }
        "Commercial Truck" -> when (fuelType) {
            "Petrol" -> 0.35
            "Diesel" -> 0.30
            else -> 0.30
        }
        else -> 0.15 // Default
    }
}