package com.example.pocketplan.utils

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageStorageHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Copies an image from a given Uri to the app's internal storage.
     * Returns the absolute path of the saved file, or null if it fails.
     */
    fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val fileName = "img_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Deletes a file from internal storage if it exists.
     */
    fun deleteImageFromInternalStorage(path: String) {
        try {
            val file = File(path)
            if (file.exists() && file.absolutePath.startsWith(context.filesDir.absolutePath)) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
