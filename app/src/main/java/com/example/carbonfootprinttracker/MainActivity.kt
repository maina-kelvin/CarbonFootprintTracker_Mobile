package com.example.carbonfootprinttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carbonfootprinttracker.data.AppDatabase
import android.content.Intent

import com.example.carbonfootprinttracker.pages.CarbonFootprintInput
import com.example.carbonfootprinttracker.pages.LoginActivity
import com.example.carbonfootprinttracker.pages.DashboardScreen
import com.example.carbonfootprinttracker.pages.RecommendationsScreen

// Import your team's other screens here

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getDatabase(this)

        setContent {
            MaterialTheme {
                // This NavHost will control your whole app
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "dashboard") {

                    // Route 1: Dashboard
                    composable("dashboard") {
                        DashboardScreen(
                            db = db,
                            onNavigateToInput = {
                                navController.navigate("data_input")
                            },
                            onNavigateToAnalytics = {
                                navController.navigate("analytics")
                            },
                            onNavigateToRecommendations = {
                                navController.navigate("recommendations")
                            }
                        )
                    }

                    // Route 2: Your Data Input Form
                    composable("data_input") {
                        CarbonFootprintInput(
                            db = db,
                            onGoToHome = {
                                navController.navigate("dashboard") { launchSingleTop = true }
                            }
                        )
                    }

                    // Route 3: Analytics
                    composable("analytics") {
                        // Your team member's AnalyticsScreen
                        // For now, a placeholder:
                        Button(onClick = {
                            navController.navigate("dashboard") { launchSingleTop = true }
                        }) { Text("Analytics Page (Placeholder)") }
                    }

                    // Route 4: Recommendations
                    composable("recommendations") {
                        RecommendationsScreen(
                            db = db,
                            onNavigateToDashboard = {
                                navController.navigate("dashboard") { launchSingleTop = true }
                            }
                        )
                    }
                }
            }
        }
    }
}