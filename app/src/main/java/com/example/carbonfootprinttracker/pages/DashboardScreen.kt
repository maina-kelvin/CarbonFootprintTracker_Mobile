package com.example.carbonfootprinttracker.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carbonfootprinttracker.data.AppDatabase
import com.example.carbonfootprinttracker.data.CarbonEmission
import com.example.carbonfootprinttracker.ui.theme.Purple40
import com.example.carbonfootprinttracker.ui.theme.PurpleGrey40
import java.text.SimpleDateFormat
import java.util.*

val AccentGreen = Color(0xFF2E7D32)

@Composable
fun DashboardScreen(
    db: AppDatabase,
    onNavigateToInput: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToRecommendations: () -> Unit = {}
) {
    val dao = db.carbonEmissionDao()
    val emissions by dao.getAllEmissions().collectAsState(initial = emptyList())

    val totalEmissions = emissions.sumOf { it.carbonEmission }
    val averageEmissions = if (emissions.isNotEmpty()) totalEmissions / emissions.size else 0.0
    val recentEmissions = emissions.take(5)
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header
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
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Welcome to your dashboard",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Purple40
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Track and understand your carbon footprint",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PurpleGrey40
                            )
                        )
                    }
                }
            }

            // 2 Statistics cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    StatCard(
                        title = "Total Entries",
                        value = "${emissions.size}",
                        accent = Purple40,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Average",
                        value = "%.2f".format(averageEmissions),
                        subtitle = "kg CO₂",
                        accent = AccentGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Total Emission card
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        Text(
                            text = "Total Carbon Emissions",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Purple40
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "%.2f kg CO₂".format(totalEmissions),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = when {
                                totalEmissions <= 50 -> AccentGreen
                                totalEmissions <= 150 -> Color(0xFFFBC02D)
                                else -> Color(0xFFD32F2F)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        //  purple-green line
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Purple40, AccentGreen)
                                    )
                                )
                        )
                    }
                }
            }

            // Buttons
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onNavigateToInput,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple40
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Entry", color = Color.White)
                        }
                    }
                    
                    Button(
                        onClick = onNavigateToAnalytics,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Analytics, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analytics", color = Color.White)
                        }
                    }
                    
                    Button(
                        onClick = onNavigateToRecommendations,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple40
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Recommendations", color = Color.White)
                        }
                    }
                }
            }

            // Recent Entries Title
            item {
                Text(
                    text = "Recent Entries",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Purple40
                    ),
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            // Empty state
            if (recentEmissions.isEmpty()) {
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No entries yet",
                                style = MaterialTheme.typography.titleMedium.copy(color = Purple40)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Tap 'Add Entry' to begin tracking.",
                                color = PurpleGrey40
                            )
                        }
                    }
                }
            } else {
                items(recentEmissions) { emission ->
                    EmissionItemCard(emission, dateFormat)
                }
            }
        }
    }
}

// ---- Components ---- //

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    accent: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = PurpleGrey40)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
            )
            if (subtitle != null) {
                Text(subtitle, color = PurpleGrey40, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun EmissionItemCard(emission: CarbonEmission, formatter: SimpleDateFormat) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(
                        when {
                            emission.carbonEmission <= 10 -> AccentGreen
                            emission.carbonEmission <= 25 -> Color(0xFFFBC02D)
                            else -> Color(0xFFD32F2F)
                        }
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(emission.category, fontWeight = FontWeight.Bold, color = Purple40)
                Text(formatter.format(emission.timestamp), color = PurpleGrey40, fontSize = 13.sp)
            }

            Text(
                "%.2f kg".format(emission.carbonEmission),
                color = AccentGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
