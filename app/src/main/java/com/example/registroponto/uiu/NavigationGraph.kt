package com.example.registroponto.uiu

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.registroponto.viewmodel.RegistroPontoViewModel

@Composable
fun NavigationGraph(navController: NavHostController,viewModel: RegistroPontoViewModel,modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Registro.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Registro.route) {
            RegistroPontoScreen(viewModel = viewModel)
        }
        composable(BottomNavItem.Resumo.route) {
            ResumoScreen(viewModel = viewModel)
        }
    }
}

