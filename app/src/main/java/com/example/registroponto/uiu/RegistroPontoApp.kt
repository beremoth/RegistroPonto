package com.example.registroponto.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.registroponto.uiu.BottomNavigationBar
import com.example.registroponto.uiu.NavigationGraph
import com.example.registroponto.viewmodel.RegistroPontoViewModel

@Composable
fun RegistroPontoApp(viewModel: RegistroPontoViewModel = viewModel()) {
    val navController = rememberNavController()


    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavigationGraph(
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
