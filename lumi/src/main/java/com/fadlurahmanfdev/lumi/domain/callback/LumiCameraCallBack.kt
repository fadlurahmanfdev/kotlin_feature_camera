package com.fadlurahmanfdev.lumi.domain.callback

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import com.fadlurahmanfdev.lumi.core.enums.LumiCameraFlash
import com.fadlurahmanfdev.lumi.domain.listener.LumiCameraCaptureListener

interface LumiCameraCallBack {
    fun switchCameraFacing(cameraSelector: CameraSelector)

    // capture
    fun takePicture(listener: LumiCameraCaptureListener)
    fun setFlashModeCapture(flashMode: LumiCameraFlash)

    // analysis
    fun startAnalyze(analyzer: ImageAnalysis.Analyzer)
    fun stopAnalyze()
}