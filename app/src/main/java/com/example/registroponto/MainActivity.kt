package com.example.registroponto.viewmodel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.registroponto.ui.theme.RegistroPontoTheme
import com.example.registroponto.uiu.BottomNavItem
import com.example.registroponto.uiu.RegistroPontoScreen
import com.example.registroponto.uiu.ResumoScreen





class MainActivity : ComponentActivity() {
    private val viewModel: RegistroPontoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegistroPontoTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        BottomNavigation {
                            val items = listOf(
                                BottomNavItem.Registro,
                                BottomNavItem.Resumo
                            )
                            items.forEach { item ->
                                BottomNavigationItem(
                                    icon = { Icon(item.icon, contentDescription = item.label) },
                                    label = { Text(item.label) },
                                    selected = currentRoute == item.route,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.Registro.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(BottomNavItem.Registro.route) {
                            RegistroPontoScreen(viewModel)
                        }
                        composable(BottomNavItem.Resumo.route) {
                            ResumoScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}
