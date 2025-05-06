package com.example.registroponto.uiu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.registroponto.viewmodel.RegistroPontoViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.Month
import java.util.*




@Composable
fun ResumoScreen(viewModel: RegistroPontoViewModel = viewModel(), modifier: Modifier = Modifier) {
    val registros by viewModel.registros.collectAsState()
    val meses = Month.entries.map { it.getDisplayName(TextStyle.FULL, Locale("pt", "BR")) }

    var tipoResumo by remember { mutableStateOf("Mensal") }
    var mesSelecionado by remember { mutableIntStateOf(LocalDate.now().monthValue - 1) }

    val formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val formatterHora = DateTimeFormatter.ofPattern("HH:mm")

    val registrosFiltrados = registros.filterNotNull().filter {
        try {
            val data = LocalDate.parse(it.data, formatterData)
            if (tipoResumo == "Semanal") {
                data.isAfter(LocalDate.now().minusDays(7))
            } else {
                data.monthValue == mesSelecionado + 1
            }
        } catch (e: Exception) {
            false
        }
    }

    val totalHoras = registrosFiltrados.mapNotNull {
        try {
            val entrada = it.entrada?.let { LocalTime.parse(it, formatterHora) }
            val saida = it.saida?.let { LocalTime.parse(it, formatterHora) }
            if (entrada != null && saida != null) Duration.between(entrada, saida) else null
        } catch (_: Exception) { null }
    }.fold(Duration.ZERO) { acc, dur -> acc.plus(dur) }

    val horasTotais = totalHoras.toHours()
    val minutosTotais = totalHoras.toMinutes() % 60

    Column(modifier = modifier.padding(16.dp)) {
        Text("Resumo de Registros", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tipo: ")
            Spacer(modifier = Modifier.width(8.dp))
            DropdownMenuSelector(listOf("Semanal", "Mensal"), tipoResumo) { tipoResumo = it }
        }

        if (tipoResumo == "Mensal") {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Mês: ")
                Spacer(modifier = Modifier.width(8.dp))
                DropdownMenuSelector(meses, meses[mesSelecionado]) {
                    mesSelecionado = meses.indexOf(it)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Total trabalhado: ${horasTotais}h ${minutosTotais}min", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (registrosFiltrados.isEmpty()) {
            Text("Nenhum registro encontrado para o período selecionado.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                items(registrosFiltrados) { registro ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Data: ${registro.data}", style = MaterialTheme.typography.titleSmall)
                            registro.entrada?.let { Text("Entrada: $it") }
                            registro.pausa?.let { Text("Pausa: $it") }
                            registro.retorno?.let { Text("Retorno: $it") }
                            registro.saida?.let { Text("Saída: $it") }
                        }
                    }
                }
            }
        }
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
        OutlinedButton(onClick = { expanded = true }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(selectedOption)
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
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