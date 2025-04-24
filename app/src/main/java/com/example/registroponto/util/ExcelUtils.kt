package com.example.registroponto.util

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class RegistroPonto(
    val data: String,
    val entrada: String? = null,
    val pausa: String? = null,
    val retorno: String? = null,
    val saida: String? = null
)

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

fun buscarArquivoNoMediaStore(context: Context, nomeArquivo: String): Uri? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val uriExterno = MediaStore.Downloads.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.Downloads._ID, MediaStore.Downloads.DISPLAY_NAME)
        val selection = "${MediaStore.Downloads.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(nomeArquivo)

        resolver.query(uriExterno, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID))
                return ContentUris.withAppendedId(uriExterno, id)
            }
        }
    }
    return null
}


fun importarRegistrosDoExcel(context: Context, uri: Uri): List<RegistroPonto> {
    val registros = mutableListOf<RegistroPonto>()

    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)

        // Itera sobre as linhas da planilha, começando da segunda linha (índice 1)
        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue
            // Verifica se a primeira célula está vazia
            if (row.getCell(0)?.stringCellValue.isNullOrEmpty()) continue
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

fun exportarParaExcel(context: Context, registro: RegistroPonto) {
    try {
        val fileName = "registro_ponto.xlsx"
        val mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        val headers = listOf("Data", "Entrada", "Pausa", "Retorno", "Saída")

        val workbook: XSSFWorkbook
        val sheet: Sheet

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        workbook = if (file.exists()) {
            FileInputStream(file).use { XSSFWorkbook(it) }
        } else {
            XSSFWorkbook()
        }

        sheet = if (workbook.numberOfSheets > 0) {
            workbook.getSheetAt(0)
        } else {
            workbook.createSheet("Registro de Ponto")
        }

        if (sheet.physicalNumberOfRows == 0) {
            val header = sheet.createRow(0)
            headers.forEachIndexed { i, titulo -> header.createCell(i).setCellValue(titulo) }
        }

        val novaLinha = sheet.createRow(sheet.physicalNumberOfRows)
        novaLinha.createCell(0).setCellValue(registro.data)
        novaLinha.createCell(1).setCellValue(registro.entrada ?: "")
        novaLinha.createCell(2).setCellValue(registro.pausa ?: "")
        novaLinha.createCell(3).setCellValue(registro.retorno ?: "")
        novaLinha.createCell(4).setCellValue(registro.saida ?: "")

        for (i in headers.indices) {
            sheet.setColumnWidth(i, 20 * 256)
        }

        FileOutputStream(file).use { output ->
            workbook.write(output)
        }

        workbook.close()
        Toast.makeText(context, "Registro adicionado em: ${file.absolutePath}", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Erro ao exportar: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

