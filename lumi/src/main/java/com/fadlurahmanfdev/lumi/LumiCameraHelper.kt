package com.fadlurahmanfdev.lumi

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy

class LumiCameraHelper {
    fun getBitmapFromImageProxy(imageProxy: ImageProxy): Bitmap {
        var bitmap = imageProxy.toBitmap()
        bitmap = rotateImage(bitmap, imageProxy.imageInfo.rotationDegrees.toFloat())
        return bitmap
    }

    fun rotateImage(bitmapImage: Bitmap, rotation: Float): Bitmap {
        val matrix = Matrix().apply {
            postRotate(rotation)
        }
        return Bitmap.createBitmap(
            bitmapImage,
            0,
            0,
            bitmapImage.width,
            bitmapImage.height,
            matrix,
            true
        )
    }

    fun mirrorHorizontalBitmap(bitmapImage: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(-1.0f, 1.0f)
        return Bitmap.createBitmap(
            bitmapImage,
            0,
            0,
            bitmapImage.width,
            bitmapImage.height,
            matrix,
            true
        )
    }
}