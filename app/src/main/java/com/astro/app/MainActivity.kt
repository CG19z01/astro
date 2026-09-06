package com.astro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.astro.app.navigation.AstroApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bord à bord : le fond céleste doit passer sous les barres système.
        enableEdgeToEdge()
        val container = (application as AstroApplication).container
        setContent {
            AstroApp(container = container, modifier = Modifier.fillMaxSize())
        }
    }
}
