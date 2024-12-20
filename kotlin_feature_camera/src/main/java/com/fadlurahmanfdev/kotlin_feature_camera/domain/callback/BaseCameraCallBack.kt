package com.fadlurahmanfdev.kotlin_feature_camera.domain.callback

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import com.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraFlash
import com.fadlurahmanfdev.kotlin_feature_camera.domain.listener.CameraCaptureListener

interface BaseCameraCallBack {
    fun switchCameraFacing(cameraSelector: CameraSelector)

    // capture
    fun takePicture(listener: CameraCaptureListener)
    fun setFlashModeCapture(flashMode: FeatureCameraFlash)

    // analysis
    fun startAnalyze(analyzer: ImageAnalysis.Analyzer)
    fun stopAnalyze()
}