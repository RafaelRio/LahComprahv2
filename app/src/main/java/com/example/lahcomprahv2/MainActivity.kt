package com.example.lahcomprahv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.lahcomprahv2.ui.screens.list.ListaProductosScreen
import com.example.lahcomprahv2.ui.theme.LahComprahV2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            LahComprahV2Theme(content = {
                ListaProductosScreen()
            })
        }
    }
}