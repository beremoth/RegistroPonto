package com.example.registroponto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.navigation.NavController
import com.example.registroponto.viewmodel.RegistroPontoViewModel



@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Registro,
        BottomNavItem.Resumo
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = navController.currentBackStackEntryAsState().value?.destination?.route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Popula a pilha de navegação de acordo com o item
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
                        BottomNavigationBar(navController)
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