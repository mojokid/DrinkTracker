package com.example.weeklyalcoholtracker.ui.components

import android.graphics.Bitmap
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import com.example.weeklyalcoholtracker.R
import kotlin.math.min
import androidx.compose.ui.graphics.nativeCanvas

@Composable
fun BeerGlassProgress(
    fraction: Float,   // 0.0 .. 1.0
    height: Dp,
    modifier: Modifier = Modifier
) {
    val clamped = fraction.coerceIn(0f, 1f)

    val emptyImg: ImageBitmap = ImageBitmap.imageResource(R.drawable.beer_glass_empty)
    val fullImg: ImageBitmap = ImageBitmap.imageResource(R.drawable.beer_glass_full)

    // Convert to Android Bitmaps (works across older Compose versions)
    val emptyBmp: Bitmap = emptyImg.asAndroidBitmap()
    val fullBmp: Bitmap = fullImg.asAndroidBitmap()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val scale = min(size.width / emptyBmp.width.toFloat(), size.height / emptyBmp.height.toFloat())
        val dstW = emptyBmp.width * scale
        val dstH = emptyBmp.height * scale

        val left = (size.width - dstW) / 2f
        val top = (size.height - dstH) / 2f

        val dstRect = RectF(left, top, left + dstW, top + dstH)

        // Draw EMPTY background
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawBitmap(emptyBmp, null, dstRect, null)
        }

        // Clip and draw FULL overlay
        val fillTop = top + (dstH * (1f - clamped))

        clipRect(
            left = dstRect.left,
            top = fillTop,
            right = dstRect.right,
            bottom = dstRect.bottom,
            clipOp = ClipOp.Intersect
        ) {
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawBitmap(fullBmp, null, dstRect, null)
            }
        }
    }
}