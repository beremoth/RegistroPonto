package com.example.registroponto.uiu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Registro : BottomNavItem("registro", "Registro", Icons.Default.AccessTime)
    object Resumo : BottomNavItem("resumo", "Resumo", Icons.Default.Assessment)
}

