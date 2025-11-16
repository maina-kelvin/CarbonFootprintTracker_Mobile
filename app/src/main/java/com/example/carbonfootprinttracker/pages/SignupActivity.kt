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

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        // Using same inset handling as login
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
    }

    private fun setupViews() {
        // Find views
        val loginText = findViewById<TextView>(R.id.loginText)
        val signupButton = findViewById<MaterialButton>(R.id.signupButton)

        // Set up click listeners
        loginText.setOnClickListener {
            // Navigate back to Login Activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        signupButton.setOnClickListener {
            // For demo purposes, just show a message
            println("Signup button clicked")
        }
    }
}