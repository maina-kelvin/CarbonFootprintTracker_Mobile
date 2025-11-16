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

                NavHost(navController = navController, startDestination = "login") {

                    // Route 1: Login
                    composable("login") {
                        // Your team member's LoginScreen
                        // For now, a placeholder button:
                        Button(onClick = {
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }) { Text("Go to Login Page") }
                    }

                    // Route 2: Dashboard
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

                    // Route 3: Your Data Input Form
                    composable("data_input") {
                        CarbonFootprintInput(
                            db = db,
                            onGoToHome = {
                                navController.navigate("dashboard") { launchSingleTop = true }
                            }
                        )
                    }

                    // Route 4: Analytics
                    composable("analytics") {
                        // Your team member's AnalyticsScreen
                        // For now, a placeholder:
                        Button(onClick = {
                            navController.navigate("dashboard") { launchSingleTop = true }
                        }) { Text("Analytics Page (Placeholder)") }
                    }

                    // Route 5: Recommendations
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