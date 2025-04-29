package com.example.registroponto.uiu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.registroponto.viewmodel.RegistroPontoViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun ResumoScreen(viewModel: RegistroPontoViewModel = viewModel(), modifier: Modifier = Modifier) {
    val registros by viewModel.registros.collectAsState()
    val meses = java.time.Month.values().map {
        it.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale("pt", "BR"))
    }

    var tipoResumo by remember { mutableStateOf("Semanal") }
    var mesSelecionado by remember { mutableStateOf(LocalDate.now().monthValue - 1) }

    Column(modifier = modifier.padding(16.dp)) {  // Usando o modifier recebido
        Text("Resumo de Registros", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tipo: ")
            Spacer(modifier = Modifier.width(8.dp))
            DropdownMenuSelector(
                options = listOf("Semanal", "Mensal"),
                selectedOption = tipoResumo,
                onOptionSelected = { tipoResumo = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Mês: ")
            Spacer(modifier = Modifier.width(8.dp))
            DropdownMenuSelector(
                options = meses,
                selectedOption = meses[mesSelecionado],
                onOptionSelected = { mesSelecionado = meses.indexOf(it) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Registros filtrados virão aqui...")
    }
}


@Composable
fun DropdownMenuSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(selectedOption)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
