package com.example.carbonfootprinttracker.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.carbonfootprinttracker.data.AppDatabase
import com.example.carbonfootprinttracker.data.CarbonEmission
import com.example.carbonfootprinttracker.pages.AppHeader

data class Recommendation(
    val category: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val priority: Priority
)

enum class Priority {
    HIGH, MEDIUM, LOW
}

@Composable
fun RecommendationsScreen(
    db: AppDatabase,
    onNavigateToDashboard: () -> Unit
) {
    val dao = db.carbonEmissionDao()
    val emissions by dao.getAllEmissions().collectAsState(initial = emptyList())

    // Analyze emissions and generate recommendations
    val recommendations = remember(emissions) {
        generateRecommendations(emissions)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Header
        item {
            Text(
                text = "Personalized Recommendations",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Introduction
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (emissions.isEmpty()) {
                            "Start tracking your carbon footprint to receive personalized recommendations!"
                        } else {
                            "Based on your ${emissions.size} entry${if (emissions.size != 1) "s" else ""}, here are ways to reduce your carbon footprint:"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Recommendations
        if (recommendations.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No recommendations yet",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add more entries to get personalized advice",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            // Group by priority
            val highPriority = recommendations.filter { it.priority == Priority.HIGH }
            val mediumPriority = recommendations.filter { it.priority == Priority.MEDIUM }
            val lowPriority = recommendations.filter { it.priority == Priority.LOW }

            if (highPriority.isNotEmpty()) {
                item {
                    Text(
                        text = "High Priority",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(highPriority) { recommendation ->
                    RecommendationCard(recommendation = recommendation)
                }
            }

            if (mediumPriority.isNotEmpty()) {
                item {
                    Text(
                        text = "Medium Priority",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBC02D),
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                }
                items(mediumPriority) { recommendation ->
                    RecommendationCard(recommendation = recommendation)
                }
            }

            if (lowPriority.isNotEmpty()) {
                item {
                    Text(
                        text = "Low Priority",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                }
                items(lowPriority) { recommendation ->
                    RecommendationCard(recommendation = recommendation)
                }
            }
        }

        // Back to Dashboard Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToDashboard,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE0E0E0)
                )
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    tint = Color(0xFF424242)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back to Dashboard", color = Color(0xFF424242))
            }
        }
    }
    }
}

@Composable
fun RecommendationCard(recommendation: Recommendation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = recommendation.icon,
                contentDescription = recommendation.category,
                modifier = Modifier.size(32.dp),
                tint = when (recommendation.priority) {
                    Priority.HIGH -> Color(0xFFD32F2F)
                    Priority.MEDIUM -> Color(0xFFFBC02D)
                    Priority.LOW -> Color(0xFF4CAF50)
                }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recommendation.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

fun generateRecommendations(emissions: List<CarbonEmission>): List<Recommendation> {
    if (emissions.isEmpty()) return emptyList()

    val recommendations = mutableListOf<Recommendation>()

    // Analyze transport
    val transportEmissions = emissions.filter { 
        it.transportMode != null || it.mileage != null || it.flightDuration != null 
    }
    
    if (transportEmissions.isNotEmpty()) {
        val avgMileage = transportEmissions.mapNotNull { it.mileage }.average()
        val hasFlights = transportEmissions.any { it.flightDuration != null && it.flightDuration!! > 0 }
        val highMileage = avgMileage > 50

        if (highMileage) {
            recommendations.add(
                Recommendation(
                    category = "Transport",
                    title = "Reduce Daily Commute",
                    description = "Your average daily distance is ${"%.1f".format(avgMileage)} km. Consider carpooling, public transport, or cycling for shorter trips to reduce emissions.",
                    icon = Icons.Default.DirectionsCar,
                    priority = if (avgMileage > 100) Priority.HIGH else Priority.MEDIUM
                )
            )
        }

        // General vehicle recommendation based on high mileage
        if (highMileage && transportEmissions.any { it.transportMode == "Vehicles" }) {
            recommendations.add(
                Recommendation(
                    category = "Transport",
                    title = "Consider More Efficient Vehicles",
                    description = "With your high daily mileage, consider switching to a more fuel-efficient vehicle or hybrid/electric car to significantly reduce emissions.",
                    icon = Icons.Default.DirectionsCar,
                    priority = Priority.MEDIUM
                )
            )
        }

        if (hasFlights) {
            recommendations.add(
                Recommendation(
                    category = "Transport",
                    title = "Reduce Air Travel",
                    description = "Flights are one of the highest sources of carbon emissions. Consider video conferencing for business meetings or train travel for shorter distances.",
                    icon = Icons.Default.FlightTakeoff,
                    priority = Priority.HIGH
                )
            )
        }

        val hasDiesel = transportEmissions.any { it.fuelType == "Diesel" }
        if (hasDiesel) {
            recommendations.add(
                Recommendation(
                    category = "Transport",
                    title = "Switch to Electric or Hybrid",
                    description = "Consider switching to an electric or hybrid vehicle. They produce significantly fewer emissions and can save money on fuel costs.",
                    icon = Icons.Default.EvStation,
                    priority = Priority.MEDIUM
                )
            )
        }
    }

    // Analyze food
    val foodEmissions = emissions.filter { 
        it.meatMeals != null || it.localFoodPercent != null || it.foodWaste != null 
    }

    if (foodEmissions.isNotEmpty()) {
        val avgMeatMeals = foodEmissions.mapNotNull { it.meatMeals }.average()
        val avgLocalFood = foodEmissions.mapNotNull { it.localFoodPercent }.average()
        val avgFoodWaste = foodEmissions.mapNotNull { it.foodWaste }.average()

        if (avgMeatMeals > 2) {
            recommendations.add(
                Recommendation(
                    category = "Food",
                    title = "Reduce Meat Consumption",
                    description = "You're consuming ${"%.1f".format(avgMeatMeals)} meat meals per day on average. Try having 1-2 meat-free days per week. Plant-based proteins have much lower carbon footprints.",
                    icon = Icons.Default.Restaurant,
                    priority = if (avgMeatMeals > 3) Priority.HIGH else Priority.MEDIUM
                )
            )
        }

        if (avgLocalFood < 30) {
            recommendations.add(
                Recommendation(
                    category = "Food",
                    title = "Buy More Local & Organic Food",
                    description = "Only ${"%.0f".format(avgLocalFood)}% of your food is local/organic. Buying local reduces transportation emissions and supports local farmers.",
                    icon = Icons.Default.ShoppingCart,
                    priority = Priority.MEDIUM
                )
            )
        }

        if (avgFoodWaste > 0.5) {
            recommendations.add(
                Recommendation(
                    category = "Food",
                    title = "Reduce Food Waste",
                    description = "You're wasting ${"%.1f".format(avgFoodWaste)} kg of food per day. Plan meals, use leftovers creatively, and compost food scraps to reduce waste.",
                    icon = Icons.Default.Delete,
                    priority = Priority.MEDIUM
                )
            )
        }
    }

    // Analyze house
    val houseEmissions = emissions.filter { 
        it.electricityUsage != null || it.gasUsage != null || it.oilUsage != null 
    }

    if (houseEmissions.isNotEmpty()) {
        val avgElectricity = houseEmissions.mapNotNull { it.electricityUsage }.average()
        val avgGas = houseEmissions.mapNotNull { it.gasUsage }.average()
        val avgOil = houseEmissions.mapNotNull { it.oilUsage }.average()
        val avgHouseholdSize = houseEmissions.mapNotNull { it.householdSize }.average().toInt()

        if (avgElectricity > 30) {
            recommendations.add(
                Recommendation(
                    category = "House",
                    title = "Reduce Electricity Usage",
                    description = "Your average daily electricity usage is ${"%.1f".format(avgElectricity)} kWh. Switch to LED bulbs, unplug devices when not in use, and use energy-efficient appliances.",
                    icon = Icons.Default.Lightbulb,
                    priority = if (avgElectricity > 50) Priority.HIGH else Priority.MEDIUM
                )
            )
        }

        if (avgGas > 0) {
            recommendations.add(
                Recommendation(
                    category = "House",
                    title = "Improve Home Insulation",
                    description = "You're using ${"%.1f".format(avgGas)} kg of gas for heating. Improve insulation, seal windows and doors, and lower your thermostat by 1-2°C to reduce gas usage.",
                    icon = Icons.Default.Home,
                    priority = Priority.MEDIUM
                )
            )
        }

        if (avgOil > 0) {
            recommendations.add(
                Recommendation(
                    category = "House",
                    title = "Switch to Renewable Heating",
                    description = "Consider switching from oil heating to a heat pump or solar heating system. This can reduce your heating emissions by up to 70%.",
                    icon = Icons.Default.Home,
                    priority = Priority.HIGH
                )
            )
        }

        if (avgHouseholdSize == 1 && avgElectricity > 20) {
            recommendations.add(
                Recommendation(
                    category = "House",
                    title = "Optimize for Single Occupancy",
                    description = "As a single-person household, focus on using smaller appliances, efficient heating zones, and smart home devices to reduce unnecessary energy consumption.",
                    icon = Icons.Default.Person,
                    priority = Priority.LOW
                )
            )
        }
    }

    // General recommendations based on total emissions
    val totalEmissions = emissions.sumOf { it.carbonEmission }
    val avgDailyEmissions = totalEmissions / emissions.size

    if (avgDailyEmissions > 25) {
        recommendations.add(
            Recommendation(
                category = "General",
                title = "High Overall Carbon Footprint",
                description = "Your average daily emissions are ${"%.1f".format(avgDailyEmissions)} kg CO₂, which is above the recommended 20 kg/day. Focus on the high-priority recommendations above.",
                icon = Icons.Default.Warning,
                priority = Priority.HIGH
            )
        )
    } else if (avgDailyEmissions < 10) {
        recommendations.add(
            Recommendation(
                category = "General",
                title = "Great Job!",
                description = "Your average daily emissions are ${"%.1f".format(avgDailyEmissions)} kg CO₂, which is excellent! Keep up the good work and continue making sustainable choices.",
                icon = Icons.Default.CheckCircle,
                priority = Priority.LOW
            )
        )
    }

    return recommendations.distinctBy { it.title }
}

