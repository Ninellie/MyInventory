package com.example.myinventory.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myinventory.ui.devices.AddDeviceScreen
import com.example.myinventory.ui.devices.DevicesScreen
import com.example.myinventory.ui.graphView.GraphScreen
import com.example.myinventory.ui.graphView.GraphViewModel
import com.example.myinventory.ui.settings.SettingsScreen
import com.example.myinventory.ui.settings.SettingsViewModel

@Composable
fun AppNavigation(
    settings: SettingsViewModel,
    graph: GraphViewModel
) {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Devices.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Devices.route) {
                DevicesScreen(
                    viewModel = settings,
                    onNavigateToAddDevice = {
                        navController.navigate(Screen.AddDevice.route)
                    }
                )
            }
            
            composable(Screen.AddDevice.route) {
                AddDeviceScreen(
                    viewModel = settings,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(settings)
            }

            composable(Screen.Graph.route) {
                GraphScreen(graph)
            }
        }
    }
} 