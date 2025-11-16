package com.example.carbonfootprinttracker.pages

import android.content.Intent
import android.os.Bundle
import com.example.carbonfootprinttracker.R
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.example.carbonfootprinttracker.MainActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        /// FIXME: Edge-to-edge acting weird on some devices, use the correct ID
        val mainLayout = findViewById<android.widget.ScrollView>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()

    }

    private fun setupViews() {
        // Find views with explicit types
        // Had type inference issues earlier
        val signUpText: TextView = findViewById(R.id.signUpText)
        val loginButton: MaterialButton = findViewById(R.id.loginButton)
        // TODO: forgot password flow

        // Set up click listeners
        signUpText.setOnClickListener {
            // Navigate to Signup Activity
            // NOTE: Adding delay broke things during testing
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close LoginActivity so user can't go back
        }
    }
}