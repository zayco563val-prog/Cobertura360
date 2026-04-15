package com.cobertura360.app

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var savedFilesText: TextView
    private val uploadFolderName = "excel_uploads"

    private val openDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { saveExcelFile(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val selectFileButton: Button = findViewById(R.id.button_select_file)
        statusText = findViewById(R.id.text_status)
        savedFilesText = findViewById(R.id.text_files)

        selectFileButton.setOnClickListener {
            openDocumentLauncher.launch(arrayOf(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-excel",
                "application/octet-stream",
                "*/*"
            ))
        }

        refreshSavedFilesList()
    }

    private fun saveExcelFile(uri: Uri) {
        try {
            val contentResolver: ContentResolver = contentResolver
            val fileName = queryFileName(contentResolver, uri)
                ?: "base_excel_${System.currentTimeMillis()}.xlsx"
            val safeName = fileName.replace(Regex("[\\/:*?\"<>|]"), "_")
            val targetDir = File(filesDir, uploadFolderName)
            if (!targetDir.exists()) targetDir.mkdirs()

            val destination = File(targetDir, safeName)
            contentResolver.openInputStream(uri)?.use { inputStream ->
                destination.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            statusText.text = "Archivo guardado: ${destination.name}"
            refreshSavedFilesList()
        } catch (exception: Exception) {
            statusText.text = "Error guardando Excel: ${exception.message}"
        }
    }

    private fun queryFileName(resolver: ContentResolver, uri: Uri): String? {
        resolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                return cursor.getString(nameIndex)
            }
        }
        return null
    }

    private fun refreshSavedFilesList() {
        val targetDir = File(filesDir, uploadFolderName)
        val savedFiles = targetDir.listFiles()?.filter { it.isFile }?.map { it.name } ?: emptyList()
        savedFilesText.text = if (savedFiles.isEmpty()) {
            "No hay archivos guardados aún."
        } else {
            "Archivos guardados:\n" + savedFiles.joinToString(separator = "\n")
        }
    }
}
