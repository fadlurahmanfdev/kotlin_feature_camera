package com.fadlurahmanfdev.lumi.domain.listener

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageProxy
import com.fadlurahmanfdev.lumi.core.exception.LumiCameraException

interface LumiCameraCaptureListener {
    /**
     * close [imageProxy] to capture another image
     */
    fun onCaptureSuccess(imageProxy: ImageProxy, cameraSelector: CameraSelector)
    fun onCaptureError(exception: LumiCameraException)
}