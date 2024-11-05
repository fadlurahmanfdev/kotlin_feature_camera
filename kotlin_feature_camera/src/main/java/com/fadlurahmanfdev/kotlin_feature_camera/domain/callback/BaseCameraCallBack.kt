package co.id.fadlurahmanfdev.kotlin_feature_camera.domain.callback

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraFlash

interface BaseCameraCallBack {
    fun switchCameraFacing(cameraSelector: CameraSelector)

    // capture
    fun takePicture()
    fun setFlashModeCapture(flashMode: FeatureCameraFlash)

    // analysis
    fun startAnalyze(analyzer: ImageAnalysis.Analyzer)
    fun stopAnalyze()
}