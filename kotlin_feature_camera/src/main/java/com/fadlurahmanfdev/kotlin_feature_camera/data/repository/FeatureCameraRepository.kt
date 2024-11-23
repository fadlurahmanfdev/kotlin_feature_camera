package com.fadlurahmanfdev.kotlin_feature_camera.data.repository

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy

interface FeatureCameraRepository {
    fun getBitmapFromImageProxy(imageProxy: ImageProxy): Bitmap
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
    fun mirrorHorizontalBitmap(bitmapImage: Bitmap): Bitmap
}