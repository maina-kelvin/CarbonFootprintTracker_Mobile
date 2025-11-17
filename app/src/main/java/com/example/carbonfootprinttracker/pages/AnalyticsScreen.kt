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

val ChartGreen = Color(0xFF2E7D32)
val ChartYellow = Color(0xFFFBC02D)
val ChartRed = Color(0xFFD32F2F)

@Composable
fun AnalyticsScreen(
    db: AppDatabase,
    onBack: () -> Unit
) {
    val dao = db.carbonEmissionDao()
    val emissions by dao.getAllEmissions().collectAsState(initial = emptyList())

    val dateFormatter = SimpleDateFormat("EEE", Locale.getDefault())

    // WEEKLY BAR CHART DATA
    val weeklyData = emissions.groupBy { dateFormatter.format(it.timestamp) }
    val bars = weeklyData.map { (day, items) ->
        BarChartData.Bar(
            label = day,
            value = items.sumOf { it.carbonEmission }.toFloat(),
            color = Purple40
        )
    }

    // CATEGORY PIE CHART DATA
    val categoryData = emissions.groupBy { it.category }
    val slices = categoryData.map { (_, list) ->
        Slice(
            value = list.sumOf { it.carbonEmission }.toFloat(),
            color = listOf(ChartGreen, ChartYellow, ChartRed, Purple40).random()
        )
    }

    val pieLegend = categoryData.map { (cat, list) ->
        cat to listOf(ChartGreen, ChartYellow, ChartRed, Purple40).random()
    }

    val totalEmission = emissions.sumOf { it.carbonEmission }
    val avgEmission = if (emissions.isNotEmpty()) totalEmission / emissions.size else 0.0

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            AppHeader(
                title = "Analytics",
                subtitle = "Visual insights into your carbon footprint"
            )

            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Purple40)
            }

            SummaryCard("Total Emissions", "%.2f kg CO₂".format(totalEmission))
            SummaryCard("Average per Entry", "%.2f kg CO₂".format(avgEmission))

            // WEEKLY BAR CHART
            Text(
                "Weekly Emissions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Purple40
                )
            )
            if (bars.isNotEmpty()) {
                BarChart(
                    barChartData = BarChartData(bars = bars),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    animation = simpleChartAnimation(),
                    barDrawer = SimpleBarDrawer(),
                    xAxisDrawer = SimpleXAxisDrawer(),
                    yAxisDrawer = SimpleYAxisDrawer(),
                    labelDrawer = SimpleValueDrawer()
                )
            } else {
                EmptyChartMessage()
            }

            // CATEGORY PIE CHART
            Text(
                "Emissions by Category",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Purple40
                )
            )
            if (slices.isNotEmpty()) {
                PieChart(
                    pieChartData = PieChartData(slices = slices),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    animation = simpleChartAnimation(),
                    sliceDrawer = SimpleSliceDrawer()
                )

                // MANUAL LEGEND
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    pieLegend.forEach { (category, color) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(color = color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(category, color = PurpleGrey40)
                        }
                    }
                }
            } else {
                EmptyChartMessage()
            }

            Spacer(modifier = Modifier.height(50.dp))
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
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Purple40
                )
            )
        }
    }
}

@Composable
fun EmptyChartMessage() {
    Text(
        text = "No data available to display.",
        color = PurpleGrey40,
        modifier = Modifier.padding(8.dp)
    )
}
