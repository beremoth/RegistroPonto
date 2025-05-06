package com.example.registroponto.uiu

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.registroponto.util.exportarParaExcel
import com.example.registroponto.util.importarRegistrosDoExcel
import com.example.registroponto.viewmodel.RegistroPontoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import RegistroPonto
import RegistroPontoDao

@Composable
fun RegistroPontoScreen(viewModel: RegistroPontoViewModel = hiltViewModel(), modifier: Modifier = Modifier) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val hoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val registros by viewModel.registros.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            coroutineScope.launch {
                importarRegistrosDoExcel(context, it, registroPontoDao)
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

        val marcar = { tipo: String ->
            val hora = LocalTime.now().format(formatter)
            viewModel.marcarHorario(tipo, hoje, hora)
        }

        Button(onClick = { marcar("entrada") }) { Text("Marcar Entrada") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { marcar("pausa") }) { Text("Marcar Pausa") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { marcar("retorno") }) { Text("Marcar Retorno") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            marcar("saida")
            scope.launch {
                delay(300)
                val registrosDoDia =
                    registros.filter { it.data == hoje && it.entrada != null && it.saida != null }

                if (registrosDoDia.isNotEmpty()) {
                    val file = File(context.getExternalFilesDir(null), "registro_ponto.xlsx")
                    exportarParaExcel(context, file, registrosDoDia)
                }
            }
        }) { Text("Marcar Saída") }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            launcher.launch(arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        }) {
            Text("Importar Excel")}


        registros.filter { it.data == hoje }.forEach {
            it.entrada?.let { entrada -> Text("Entrada: $entrada") }
            it.pausa?.let { pausa -> Text("Pausa: $pausa") }
            it.retorno?.let { retorno -> Text("Retorno: $retorno") }
            it.saida?.let { saida -> Text("Saída: $saida") }
        }
    }
}
