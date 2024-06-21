package co.id.fadlurahmanfdev.kotlin_feature_camera.other.utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Base64
import com.google.ar.core.ImageFormat
import java.io.ByteArrayOutputStream

object FeatureCameraUtility {
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

//    fun getBitmapFromImage(image:Image): Bitmap {
//        val yBuffer = image.planes[0].buffer // Y
//        val vuBuffer = image.planes[2].buffer // VU
//
//        val ySize = yBuffer.remaining()
//        val vuSize = vuBuffer.remaining()
//
//        val nv21 = ByteArray(ySize + vuSize)
//
//        yBuffer.get(nv21, 0, ySize)
//        vuBuffer.get(nv21, ySize, vuSize)
//
//        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
//        val out = ByteArrayOutputStream()
//        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
//        val imageBytes = out.toByteArray()
//        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//    }
}