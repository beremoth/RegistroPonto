package com.example.registroponto.util

import android.content.Context
import android.os.Environment
import android.widget.Toast
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import RegistroPonto

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

fun importarRegistrosDoExcel(context: Context): List<RegistroPonto> {
    val registros = mutableListOf<RegistroPonto>()
    try {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "registro_ponto.xlsx")
        if (!file.exists()) return registros

        val inputStream = FileInputStream(file)
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)

        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue

            val data = row.getCell(0)?.stringCellValue ?: continue
            val entrada = row.getCell(1)?.stringCellValue
            val pausa = row.getCell(2)?.stringCellValue
            val retorno = row.getCell(3)?.stringCellValue
            val saida = row.getCell(4)?.stringCellValue

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
        inputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return registros
}
