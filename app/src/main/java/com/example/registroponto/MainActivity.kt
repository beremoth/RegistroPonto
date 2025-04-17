package com.example.registroponto

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.registroponto.ui.theme.RegistroPontoTheme
import com.example.registroponto.viewmodel.RegistroPontoViewModel
import com.example.registroponto.util.exportarParaExcel
import com.example.registroponto.util.importarRegistrosDoExcel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private val viewModel: RegistroPontoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegistroPontoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RegistroPontoScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun RegistroPontoScreen(viewModel: RegistroPontoViewModel) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val registros by viewModel.registros.collectAsState()
    val hoje = LocalDate.now().toString()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { selectedUri: Uri? ->
        selectedUri?.let { uri ->
            val registrosImportados = importarRegistrosDoExcel(context, uri)
            registrosImportados.forEach { registro -> viewModel.inserirRegistro(registro) }
            Toast.makeText(context, "${registrosImportados.size} registros importados!", Toast.LENGTH_SHORT).show()
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Button(onClick = {
            val hora = LocalTime.now().format(formatter)
            viewModel.marcarHorario(tipo = "entrada", data = hoje, hora = hora)
        }) {
            Text("Marcar Entrada")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val hora = LocalTime.now().format(formatter)
            viewModel.marcarHorario(tipo = "pausa", data = hoje, hora = hora)
        }) {
            Text("Marcar Pausa")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val hora = LocalTime.now().format(formatter)
            viewModel.marcarHorario(tipo = "retorno", data = hoje, hora = hora)
        }) {
            Text("Marcar Retorno")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val hora = LocalTime.now().format(formatter)
            viewModel.marcarHorario(tipo = "saida", data = hoje, hora = hora)
            scope.launch {
                delay(500)
                val registrosAtualizados = viewModel.registros.value
                exportarParaExcel(context, registrosAtualizados)
                Toast.makeText(context, "Excel exportado com sucesso!", Toast.LENGTH_SHORT).show()
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


        Spacer(modifier = Modifier.height(16.dp))

        Text("Registros de Hoje:")

        registros.filter { it.data == hoje }.forEach {
            it.entrada?.let { entrada -> Text("Entrada: $entrada") }
            it.pausa?.let { pausa -> Text("Pausa: $pausa") }
            it.retorno?.let { retorno -> Text("Retorno: $retorno") }
            it.saida?.let { saida -> Text("Saída: $saida") }
        }
    }
}