package com.example.carbonfootprinttracker.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.carbonfootprinttracker.R
import com.example.carbonfootprinttracker.ui.theme.Purple40

@Composable
fun AppHeader(
    title: String = "Carbon Footprint Tracker",
    subtitle: String = "Track Your Environmental Impact"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Leaf Icon
        Icon(
            painter = painterResource(id = R.drawable.ic_leaf),
            contentDescription = "Leaf icon representing environmental tracking",
            modifier = Modifier.size(80.dp),
            tint = Purple40
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Purple40
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtitle
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )
    }
}

