package com.example.registroponto.viewmodel

import RegistroPonto
import RegistroPontoDao
import AppDatabase
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

    fun marcarHorario(tipo: String, data: String, hora: String) {
        viewModelScope.launch {
            val registrosDia = dao.listarTodos().find { it.data == data }
            if (registrosDia == null) {
                val novo = RegistroPonto(
                    data = data,
                    entrada = if (tipo == "entrada") hora else null,
                    pausa = if (tipo == "pausa") hora else null,
                    retorno = if (tipo == "retorno") hora else null,
                    saida = if (tipo == "saida") hora else null
                )
                dao.inserir(novo)
            } else {
                val atualizado = registrosDia.copy(
                    entrada = if (tipo == "entrada") hora else registrosDia.entrada,
                    pausa = if (tipo == "pausa") hora else registrosDia.pausa,
                    retorno = if (tipo == "retorno") hora else registrosDia.retorno,
                    saida = if (tipo == "saida") hora else registrosDia.saida
                )
                dao.atualizar(atualizado)
            }
            carregarRegistros()
        }
    }
}
