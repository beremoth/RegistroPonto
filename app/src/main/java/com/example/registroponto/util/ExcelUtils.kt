package com.example.registroponto.util

import RegistroPonto
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun exportarParaExcel(context: Context, registros: List<RegistroPonto>) {
    try {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Registro de Ponto")

        val header = sheet.createRow(0)
        val headers = listOf("Data", "Entrada", "Pausa", "Saída Pausa", "Saída")

        val headerStyle = workbook.createCellStyle().apply {
            alignment = HorizontalAlignment.CENTER
        }

        headers.forEachIndexed { index, titulo ->
            val cell = header.createCell(index)
            cell.setCellValue(titulo)
            cell.cellStyle = headerStyle
        }

        registros.forEachIndexed { i, registro ->
            val row = sheet.createRow(i + 1)
            row.createCell(0).setCellValue(registro.data)
            row.createCell(1).setCellValue(registro.entrada ?: "")
            row.createCell(2).setCellValue(registro.pausa ?: "")
            row.createCell(3).setCellValue(registro.retorno ?: "")
            row.createCell(4).setCellValue(registro.saida ?: "")
        }

        // Ajustar largura das colunas
        for (i in 0..4) sheet.setColumnWidth(i, 20 * 256)

        // Diretório padrão Downloads
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName = "registro_ponto_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.xlsx"
        val file = File(downloadsDir, fileName)

        FileOutputStream(file).use { out ->
            workbook.write(out)
        }

        workbook.close()
        Toast.makeText(context, "Arquivo salvo em: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Erro ao exportar: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun importarRegistrosDoExcel(context: Context, uri: Uri): List<RegistroPonto> {
    val registros = mutableListOf<RegistroPonto>()
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)

        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue
            val data = getCellStringValue(row.getCell(0)) ?: continue
            val entrada = getCellStringValue(row.getCell(1))
            val pausa = getCellStringValue(row.getCell(2))
            val retorno = getCellStringValue(row.getCell(3))
            val saida = getCellStringValue(row.getCell(4))

            registros.add(
                RegistroPonto(
                    data = data,
                    entrada = entrada,
                    pausa = pausa,
                    retorno = retorno,
                    saida = saida
                )
            )
        }

        workbook.close()
        inputStream?.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return registros
}

fun getCellStringValue(cell: Cell?): String? {
    if (cell == null) return null
    return when (cell.cellType) {
        CellType.STRING -> cell.stringCellValue
        CellType.NUMERIC -> {
            val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            if (DateUtil.isCellDateFormatted(cell)) {
                format.format(cell.dateCellValue)
            } else {
                cell.numericCellValue.toString()
            }
        }
        CellType.BOOLEAN -> cell.booleanCellValue.toString()
        else -> null
    }
}