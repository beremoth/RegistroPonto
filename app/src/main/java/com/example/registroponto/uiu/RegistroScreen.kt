package com.example.registroponto.uiu

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.registroponto.util.exportarParaExcel
import com.example.registroponto.util.getFileFromUri
import com.example.registroponto.util.importarRegistrosDoExcel
import com.example.registroponto.viewmodel.RegistroPontoViewModel
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegistroPontoScreen(viewModel: RegistroPontoViewModel, modifier: Modifier = Modifier) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val hoje = LocalDate.now().toString()
    val registros by viewModel.registros.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            val file = getFileFromUri(context, it)
            file?.let {
                importarRegistrosDoExcel(context, uri)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(150.dp))

        Button(onClick = {
            val hora = java.time.LocalTime.now().format(formatter)
            viewModel.marcarHorario("entrada", hoje, hora)
        }) {
            Text("Marcar Entrada")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val hora = java.time.LocalTime.now().format(formatter)
            viewModel.marcarHorario("pausa", hoje, hora)
        }) {
            Text("Marcar Pausa")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val hora = java.time.LocalTime.now().format(formatter)
            viewModel.marcarHorario("retorno", hoje, hora)
        }) {
            Text("Marcar Retorno")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val hora = java.time.LocalTime.now().format(formatter)
            viewModel.marcarHorario("saida", hoje, hora)
            scope.launch {
                delay(500)
                val registrosHoje = viewModel.registros.value.find { it.data == hoje }
                registrosHoje?.let {
                    val file = java.io.File(context.getExternalFilesDir(null), "registro_ponto.xlsx")
                    exportarParaExcel(context, file, it)
                }
            }
        }) {
            Text("Marcar Saída")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            launcher.launch(arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        }) {
            Text("Importar Excel")
        }

        registros.filter { it.data == hoje }.forEach {
            it.entrada?.let { entrada -> Text("Entrada: $entrada") }
            it.pausa?.let { pausa -> Text("Pausa: $pausa") }
            it.retorno?.let { retorno -> Text("Retorno: $retorno") }
            it.saida?.let { saida -> Text("Saída: $saida") }
        }
    }
}

