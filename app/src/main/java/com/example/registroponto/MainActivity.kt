package com.example.registroponto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.registroponto.ui.theme.RegistroPontoTheme
import com.example.registroponto.ui.RegistroPontoApp
import com.example.registroponto.viewmodel.RegistroPontoViewModel
import androidx.activity.viewModels


class MainActivity : ComponentActivity() {
    private val viewModel: RegistroPontoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegistroPontoTheme {
                RegistroPontoApp(viewModel = viewModel)
            }
        }
    }
}

