package com.example.registroponto.util

import android.content.Context
import android.net.Uri
import android.widget.Toast
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import RegistroPonto
import java.util.Calendar


fun getFileFromUri(context: Context, uri: Uri): File? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val file = File(context.cacheDir, "registro_ponto_importado.xlsx")
    FileOutputStream(file).use { output -> inputStream.copyTo(output) }
    return file
}

fun getCellStringValue(cell: Cell?): String? {
    if (cell == null) return null
    return when (cell.cellType) {
        CellType.STRING -> cell.stringCellValue
        CellType.NUMERIC -> {
            if (DateUtil.isCellDateFormatted(cell)) {
                val calendar = Calendar.getInstance().apply { time = cell.dateCellValue }
                return if (calendar.get(Calendar.HOUR_OF_DAY) == 0 &&
                    calendar.get(Calendar.MINUTE) == 0 &&
                    calendar.get(Calendar.SECOND) == 0
                ) {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cell.dateCellValue)
                } else {
                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(cell.dateCellValue)
                }
            } else {
                cell.numericCellValue.toString()
            }
        }
        CellType.BOOLEAN -> cell.booleanCellValue.toString()
        CellType.FORMULA -> cell.toString()
        else -> null
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

fun exportarParaExcel(context: Context, file: File, registro: RegistroPonto) {
    try {
        val headers = listOf("Data", "Entrada", "Pausa", "Retorno", "Saída")
        val workbook: XSSFWorkbook
        val sheet: Sheet

        workbook = FileInputStream(file).use { XSSFWorkbook(it) }
        sheet = workbook.getOrCreateSheet("Registro de Ponto", headers)

        val novaLinha = sheet.createRow(sheet.physicalNumberOfRows)
        novaLinha.createCell(0).setCellValue(registro.data)
        novaLinha.createCell(1).setCellValue(registro.entrada ?: "")
        novaLinha.createCell(2).setCellValue(registro.pausa ?: "")
        novaLinha.createCell(3).setCellValue(registro.retorno ?: "")
        novaLinha.createCell(4).setCellValue(registro.saida ?: "")

        FileOutputStream(file).use { output -> workbook.write(output) }

        workbook.close()
        Toast.makeText(context, "Registro adicionado em: ${file.absolutePath}", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Erro ao exportar: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

fun XSSFWorkbook.getOrCreateSheet(sheetName: String, headers: List<String>): Sheet {
    var sheet = getSheet(sheetName)
    if (sheet == null) {
        sheet = createSheet(sheetName)
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            headerRow.createCell(index).setCellValue(header)
        }
    }
    return sheet
}
