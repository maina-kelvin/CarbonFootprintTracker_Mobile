package com.example.carbonfootprinttracker.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.carbonfootprinttracker.data.AppDatabase
import com.example.carbonfootprinttracker.ui.theme.Purple40
import com.example.carbonfootprinttracker.ui.theme.PurpleGrey40
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.renderer.bar.SimpleBarDrawer
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.bar.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.PieChartData.Slice
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import java.text.SimpleDateFormat
import java.util.*

val ChartColors = listOf(
    Color(0xFF2E7D32),
    Color(0xFFFBC02D),
    Color(0xFFD32F2F),
    Color(0xFF1976D2),
    Color(0xFF7B1FA2)
)

@Composable
fun AnalyticsScreen(db: AppDatabase, onBack: () -> Unit) {
    val dao = db.carbonEmissionDao()
    val emissions by dao.getAllEmissions().collectAsState(initial = emptyList())
    val dateFormatter = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())

    val totalEmission = emissions.sumOf { it.carbonEmission }
    val avgEmission = if (emissions.isNotEmpty()) totalEmission / emissions.size else 0.0
    val maxEmission = emissions.maxByOrNull { it.carbonEmission }?.carbonEmission ?: 0.0
    val mostUsedFuel = emissions.groupBy { it.fuelType }
        .maxByOrNull { it.value.size }?.key ?: "N/A"

    // --- WEEKLY BAR CHART ---
    val weeklyData = emissions.groupBy { dateFormatter.format(it.timestamp) }
    val weeklyBars = weeklyData.map { (day, list) ->
        BarChartData.Bar(label = day, value = list.sumOf { it.carbonEmission }.toFloat(), color = Purple40)
    }

    // --- CATEGORY PIE CHART ---
    val categoryData = emissions.groupBy { it.category }
    val categoryColorMap = categoryData.keys
        .withIndex()
        .associate { it.value to ChartColors[it.index % ChartColors.size] }
    val categorySlices = categoryData.map { (cat, list) ->
        Slice(value = list.sumOf { it.carbonEmission }.toFloat(), color = categoryColorMap[cat] ?: Purple40)
    }

    // --- FUEL TYPE PIE CHART ---
    val fuelData = emissions.groupBy { it.fuelType ?: "Unknown" }
    val fuelColorMap = fuelData.keys
        .withIndex()
        .associate { it.value to ChartColors[it.index % ChartColors.size] }
    val fuelSlices = fuelData.map { (fuel, list) ->
        Slice(value = list.sumOf { it.carbonEmission }.toFloat(), color = fuelColorMap[fuel] ?: Purple40)
    }

    // --- TOP 3 HIGH EMISSION DAYS ---
    val topDays = weeklyData.entries
        .sortedByDescending { it.value.sumOf { e -> e.carbonEmission } }
        .take(3)
        .map { "${it.key}: ${it.value.sumOf { e -> e.carbonEmission }} kg CO₂" }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Purple40)
            }

            // SUMMARY CARDS
            SummaryCard("Total Emissions", "%.2f kg CO₂".format(totalEmission))
            SummaryCard("Average per Entry", "%.2f kg CO₂".format(avgEmission))
            SummaryCard("Max Single Entry", "%.2f kg CO₂".format(maxEmission))
            SummaryCard("Most Used Fuel", mostUsedFuel)

            // WEEKLY BAR CHART
            Text("Weekly Emissions", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Purple40))
            if (weeklyBars.isNotEmpty()) {
                BarChart(
                    barChartData = BarChartData(bars = weeklyBars),
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    animation = simpleChartAnimation(),
                    barDrawer = SimpleBarDrawer(),
                    xAxisDrawer = SimpleXAxisDrawer(),
                    yAxisDrawer = SimpleYAxisDrawer(),
                    labelDrawer = SimpleValueDrawer()
                )
            } else EmptyChartMessage()

            // CATEGORY PIE CHART
            Text("Emissions by Category", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Purple40))
            if (categorySlices.isNotEmpty()) {
                PieChart(
                    pieChartData = PieChartData(slices = categorySlices),
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    animation = simpleChartAnimation(),
                    sliceDrawer = SimpleSliceDrawer()
                )
                Column {
                    categoryColorMap.forEach { (category, color) ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                            Box(modifier = Modifier.size(16.dp).background(color))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(category ?: "Other", color = PurpleGrey40)
                        }
                    }
                }
            } else EmptyChartMessage()

            // FUEL TYPE PIE CHART
            Text("Emissions by Fuel Type", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Purple40))
            if (fuelSlices.isNotEmpty()) {
                PieChart(
                    pieChartData = PieChartData(slices = fuelSlices),
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    animation = simpleChartAnimation(),
                    sliceDrawer = SimpleSliceDrawer()
                )
                Column {
                    fuelColorMap.forEach { (fuel, color) ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                            Box(modifier = Modifier.size(16.dp).background(color))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(fuel, color = PurpleGrey40)
                        }
                    }
                }
            } else EmptyChartMessage()

            // TOP 3 HIGH EMISSION DAYS
            Text("Top 3 High Emission Days", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Purple40))
            if (topDays.isNotEmpty()) topDays.forEach { SummaryCard("High Emission Day", it) } else EmptyChartMessage()
        }
    }
}

@Composable
fun SummaryCard(label: String, value: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, color = PurpleGrey40)
            Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Purple40))
        }
    }
}

@Composable
fun EmptyChartMessage() {
    Text("No data available to display.", color = PurpleGrey40, modifier = Modifier.padding(8.dp))
}
