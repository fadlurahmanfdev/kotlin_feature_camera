package co.id.fadlurahmanfdev.kotlin_feature_camera.domain.listener

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageProxy
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.exception.FeatureCameraException

interface CameraCaptureListener {
    /**
     * close [imageProxy] to capture another image
     */
    fun onCaptureSuccess(imageProxy: ImageProxy, cameraSelector: CameraSelector)
    fun onCaptureError(exception: FeatureCameraException)
}