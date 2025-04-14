package com.example.myinventory.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myinventory.R

sealed class Screen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val labelResId: Int) {
    data object Devices : Screen("devices", Icons.Default.Home, R.string.devices)
    data object AddDevice : Screen("add_device", Icons.Default.Home, R.string.add_device)
    data object Settings : Screen("settings", Icons.Default.Settings, R.string.settings)
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(Screen.Devices, Screen.Settings)
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = stringResource(screen.labelResId)) },
                label = { Text(stringResource(screen.labelResId)) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            // Избегаем создания множества копий экрана в стеке
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Восстанавливаем состояние при возврате на экран
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
} 