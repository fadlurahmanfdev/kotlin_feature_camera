package co.id.fadlurahmanfdev.kotlin_feature_camera.other.utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream


object FeatureCameraUtility {
    lateinit var bitmapImage: Bitmap
    lateinit var base64Image: String
    var rotationDegree: Float = 0f

    fun getBase64FromBitmap(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
//        val newBitmap =
//            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun getBitmapFromImageProxy(imageProxy: ImageProxy): Bitmap {
        val planeProxy: ImageProxy.PlaneProxy = imageProxy.planes.first()
        val buffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer[bytes]
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun mirrorBitmapImage(bitmapImage: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(1.0f, -1.0f)
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