package com.example.registroponto.viewmodel


import RegistroPonto
import RegistroPontoDao
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registroponto.util.importarRegistrosDoExcel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroPontoViewModel @Inject constructor(
    private val dao: RegistroPontoDao
) : ViewModel() {

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
                dao.inserir(
                    RegistroPonto(
                        data = data,
                        entrada = if (tipo == "entrada") hora else null,
                        pausa = if (tipo == "pausa") hora else null,
                        retorno = if (tipo == "retorno") hora else null,
                        saida = if (tipo == "saida") hora else null
                    )
                )
            } else {
                dao.atualizar(
                    registrosDia.copy(
                        entrada = if (tipo == "entrada") hora else registrosDia.entrada,
                        pausa = if (tipo == "pausa") hora else registrosDia.pausa,
                        retorno = if (tipo == "retorno") hora else registrosDia.retorno,
                        saida = if (tipo == "saida") hora else registrosDia.saida
                    )
                )
            }
            carregarRegistros()
            onComplete?.invoke()
        }
    }

    fun inserirRegistro(registro: RegistroPonto) {
        viewModelScope.launch {
            dao.inserir(registro)
            carregarRegistros()
        }
    }
    fun importarDoExcel(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                importarRegistrosDoExcel(context, uri) { registro ->
                    dao.inserir(registro) // aqui estamos passando uma lambda suspensa compatível
                }
                carregarRegistros()
                Toast.makeText(context, "Importação concluída com sucesso!", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                Log.e("RegistroPontoViewModel", "Erro ao importar Excel", e)
                Toast.makeText(context, "Erro ao importar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}