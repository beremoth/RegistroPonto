package com.example.registroponto.util

import RegistroPonto
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


fun Cell.getStringValue(): String? {
    return when (cellType) {
        CellType.STRING -> stringCellValue
        CellType.NUMERIC -> if (DateUtil.isCellDateFormatted(this)) {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dateCellValue)
        } else {
            numericCellValue.toInt().toString()
        }
        CellType.BLANK -> null
        else -> null
    }
}


fun exportarParaExcel(context: Context, file: File, registros: List<RegistroPonto>) {
    try {
        val workbook = if (file.exists()) {
            XSSFWorkbook(file.inputStream())
        } else {
            XSSFWorkbook()
        }

        val sheet = workbook.getSheet("Registros") ?: workbook.createSheet("Registros")

        // Cria cabeçalho se estiver vazio
        if (sheet.physicalNumberOfRows == 0) {
            val header = sheet.createRow(0)
            header.createCell(0).setCellValue("Data")
            header.createCell(1).setCellValue("Entrada")
            header.createCell(2).setCellValue("Pausa")
            header.createCell(3).setCellValue("Retorno")
            header.createCell(4).setCellValue("Saída")
        }

        // Coleta as datas já existentes no arquivo para evitar duplicatas
        val datasExistentes = mutableSetOf<String>()
        for (i in 1..sheet.lastRowNum) {
            val dataCell = sheet.getRow(i)?.getCell(0)
            val dataStr = dataCell?.stringCellValue
            if (!dataStr.isNullOrBlank()) {
                datasExistentes.add(dataStr)
            }
        }

        // Adiciona novos registros
        var rowIndex = sheet.lastRowNum + 1
        registros.forEach { registro ->
            if (registro.data !in datasExistentes) {
                val row = sheet.createRow(rowIndex++)
                row.createCell(0).setCellValue(registro.data)
                row.createCell(1).setCellValue(registro.entrada ?: "")
                row.createCell(2).setCellValue(registro.pausa ?: "")
                row.createCell(3).setCellValue(registro.retorno ?: "")
                row.createCell(4).setCellValue(registro.saida ?: "")
            }
        }

        file.outputStream().use { workbook.write(it) }
        workbook.close()
        Toast.makeText(context, "Exportação concluída com sucesso!", Toast.LENGTH_SHORT).show()

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Erro ao exportar para Excel", Toast.LENGTH_LONG).show()
    }
}



suspend fun importarRegistrosDoExcel(
    context: Context,
    uri: Uri,
    inserirRegistro: suspend (RegistroPonto) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val workbook = XSSFWorkbook(input)
                val sheet = workbook.getSheetAt(0)

                for (rowIndex in 1..sheet.lastRowNum) {
                    val row = sheet.getRow(rowIndex)
                    if (row != null) {
                        val data = row.getCell(0)?.getStringValue() ?: continue
                        val entrada = row.getCell(1)?.getStringValue()
                        val pausa = row.getCell(2)?.getStringValue()
                        val retorno = row.getCell(3)?.getStringValue()
                        val saida = row.getCell(4)?.getStringValue()

                        val registro = RegistroPonto(
                            data = data,
                            entrada = entrada,
                            pausa = pausa,
                            retorno = retorno,
                            saida = saida
                        )

                        inserirRegistro(registro)  // Chama via ViewModel
                    }
                }

                workbook.close()

            }
        } catch (e: Exception) {
            Log.e("ExcelUtils", "Erro ao importar Excel", e)
            throw e // Deixa o ViewModel lidar com o erro
        }
    }
}



