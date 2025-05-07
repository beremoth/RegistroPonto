package com.example.registroponto.viewmodel


import AppDatabase
import RegistroPonto
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.registroponto.util.importarRegistrosDoExcel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistroPontoViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).registroPontoDao()

    private val _registros = MutableStateFlow<List<RegistroPonto>>(emptyList())
    val registros: StateFlow<List<RegistroPonto>> = _registros

    init {
        carregarRegistros()
    }

    private fun carregarRegistros() {
        viewModelScope.launch {
            _registros.value = dao.listarTodos()
        }
    }

    fun marcarHorario(tipo: String, data: String, hora: String, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            val registrosDia = dao.listarTodos().find { it.data == data }
            if (registrosDia == null) {
                val novo = RegistroPonto(
                    data = data,
                    entrada = if (tipo == "entrada") hora else null,
                    pausa   = if (tipo == "pausa") hora else null,
                    retorno = if (tipo == "retorno") hora else null,
                    saida   = if (tipo == "saida") hora else null
                )
                dao.inserir(novo)
            } else {
                val atualizado = registrosDia.copy(
                    entrada = if (tipo == "entrada") hora else registrosDia.entrada,
                    pausa   = if (tipo == "pausa") hora else registrosDia.pausa,
                    retorno = if (tipo == "retorno") hora else registrosDia.retorno,
                    saida   = if (tipo == "saida") hora else registrosDia.saida
                )
                dao.atualizar(atualizado)
            }
            carregarRegistros()
            onComplete?.invoke()
        }
    }

    fun importarDoExcel(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                importarRegistrosDoExcel(context, uri) { registro ->
                    dao.inserir(registro)
                }
                carregarRegistros()
                Toast.makeText(context, "Importação concluída com sucesso!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao importar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

}