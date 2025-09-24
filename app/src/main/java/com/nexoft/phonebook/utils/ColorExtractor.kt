package com.nexoft.phonebook.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ColorExtractor {

    suspend fun extractDominantColor(
        context: Context,
        imageUrl: String?,
        defaultColor: Color = Color.Gray
    ): Color = withContext(Dispatchers.IO) {
        if (imageUrl.isNullOrEmpty()) {
            return@withContext defaultColor
        }

        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
                bitmap?.let {
                    val palette = Palette.from(it).generate()
                    val dominantSwatch = palette.dominantSwatch
                        ?: palette.vibrantSwatch
                        ?: palette.mutedSwatch

                    dominantSwatch?.let { swatch ->
                        return@withContext Color(swatch.rgb)
                    }
                }
            }
            defaultColor
        } catch (e: Exception) {
            defaultColor
        }
    }

    fun createShadowColor(dominantColor: Color): Color {
        return dominantColor.copy(alpha = 0.4f)
    }

    fun darkenColor(color: Color, factor: Float = 0.2f): Color {
        val r = (color.red * (1 - factor)).coerceIn(0f, 1f)
        val g = (color.green * (1 - factor)).coerceIn(0f, 1f)
        val b = (color.blue * (1 - factor)).coerceIn(0f, 1f)
        return Color(r, g, b, color.alpha)
    }
}