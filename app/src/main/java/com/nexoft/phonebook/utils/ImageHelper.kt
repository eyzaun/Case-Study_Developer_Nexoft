package com.nexoft.phonebook.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageHelper {

    private const val MAX_IMAGE_SIZE = 1024 * 1024 * 2 // 2MB
    private const val COMPRESSION_QUALITY = 80
    private const val MAX_WIDTH = 1024
    private const val MAX_HEIGHT = 1024

    suspend fun compressImage(
        context: Context,
        uri: Uri
    ): File? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val scaledBitmap = scaleBitmap(originalBitmap)

            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            var quality = COMPRESSION_QUALITY
            var streamLength: Long

            do {
                val bmpStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bmpStream)
                val bmpData = bmpStream.toByteArray()
                streamLength = bmpData.size.toLong()

                if (streamLength <= MAX_IMAGE_SIZE) {
                    outputStream.write(bmpData)
                    break
                }

                quality -= 10
            } while (quality > 10)

            outputStream.flush()
            outputStream.close()

            if (!originalBitmap.isRecycled) {
                originalBitmap.recycle()
            }
            if (!scaledBitmap.isRecycled && scaledBitmap != originalBitmap) {
                scaledBitmap.recycle()
            }

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun scaleBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= MAX_WIDTH && height <= MAX_HEIGHT) {
            return bitmap
        }

        val aspectRatio = width.toFloat() / height.toFloat()
        val targetWidth: Int
        val targetHeight: Int

        if (width > height) {
            targetWidth = MAX_WIDTH
            targetHeight = (MAX_WIDTH / aspectRatio).toInt()
        } else {
            targetHeight = MAX_HEIGHT
            targetWidth = (MAX_HEIGHT * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    fun createTempImageFile(context: Context): File {
        val timestamp = System.currentTimeMillis()
        return File(context.cacheDir, "camera_photo_$timestamp.jpg")
    }

    fun saveBitmapToFile(bitmap: Bitmap, file: File): Boolean {
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}