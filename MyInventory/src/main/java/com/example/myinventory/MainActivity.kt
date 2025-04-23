package com.example.myinventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.example.myinventory.data.DatabaseProvider
import com.example.myinventory.ui.graphView.GraphViewModel
import com.example.myinventory.ui.navigation.AppNavigation
import com.example.myinventory.ui.settings.SettingsViewModel
import com.example.myinventory.ui.theme.MyInventoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseProvider.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel = remember { SettingsViewModel(DatabaseProvider.db) }
            val graphViewModel = remember { GraphViewModel(DatabaseProvider.db) }

            MyInventoryTheme {
                AppNavigation(settingsViewModel, graphViewModel)
            }
        }
    }
}