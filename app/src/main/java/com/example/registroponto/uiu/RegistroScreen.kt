package com.example.registroponto.uiu

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.registroponto.ui.theme.RegistroPontoTheme
import com.example.registroponto.util.exportarParaExcel
import com.example.registroponto.viewmodel.RegistroPontoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.example.registroponto.util.getFileFromUri
import com.example.registroponto.util.importarRegistrosDoExcel
import java.io.File

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
    val hoje = LocalDate.now().toString()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            val file = getFileFromUri(context, it)
            file?.let {
                importarRegistrosDoExcel(context, uri) // Aqui você passa o registro atual
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(150.dp))
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
            val hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            val dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            // Marca no banco
            viewModel.marcarHorario(tipo = "saida", data = dataHoje, hora = hora)

            // Aguarda atualização e exporta
            scope.launch {
                delay(500) // pequeno atraso para garantir update
                val registrosHoje = viewModel.registros.value.find { it.data == dataHoje }
                registrosHoje?.let {
                    val file = File(context.getExternalFilesDir(null), "registro_ponto.xlsx")
                    exportarParaExcel(context, file, it) // Passa o registro do dia
                }
            }
        }) {
            Text("Marcar Saída")
        }


        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                launcher.launch(arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            },
        ) {
            Text("Importar Excel")
        }
    }
}
