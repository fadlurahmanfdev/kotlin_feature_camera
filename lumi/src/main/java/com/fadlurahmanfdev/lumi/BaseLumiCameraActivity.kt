package com.fadlurahmanfdev.lumi

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.Analyzer
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.fadlurahmanfdev.lumi.core.enums.LumiCameraFlash
import com.fadlurahmanfdev.lumi.core.enums.LumiCameraPurpose
import com.fadlurahmanfdev.lumi.core.enums.LumiCameraPurpose.IMAGE_ANALYSIS
import com.fadlurahmanfdev.lumi.core.enums.LumiCameraPurpose.IMAGE_CAPTURE
import com.fadlurahmanfdev.lumi.core.exception.LumiCameraException
import com.fadlurahmanfdev.lumi.domain.callback.LumiCameraCallBack
import com.fadlurahmanfdev.lumi.domain.listener.LumiCameraAnalysisListener
import com.fadlurahmanfdev.lumi.domain.listener.LumiCameraCaptureListener
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseLumiCameraActivity : AppCompatActivity(), LumiCameraCallBack {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var executor: Executor
    private lateinit var camera: Camera
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    abstract var cameraSelector: CameraSelector
    private lateinit var imageCapture: ImageCapture
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var analyzer: Analyzer
    private lateinit var preview: Preview
    abstract var cameraPurpose: LumiCameraPurpose
    private var cameraFlash: LumiCameraFlash = LumiCameraFlash.OFF
    private lateinit var useCaseGroup: UseCaseGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executor = ContextCompat.getMainExecutor(this)
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        onCreateBaseCamera(savedInstanceState)
    }

    abstract fun onCreateBaseCamera(savedInstanceState: Bundle?)

    /**
     * The place set of [Preview.setSurfaceProvider]
     * the value is [PreviewView.mSurfaceProvider]
     * */
    abstract fun setSurfaceProviderBaseCamera(preview: Preview)

    override fun onResume() {
        super.onResume()
        cameraExecutor = Executors.newSingleThreadExecutor()
        onListenCameraProvider()
    }

    private fun onListenCameraProvider() {
        cameraProviderFuture.addListener(
            {
                onStartCamera()
            },
            executor,
        )
    }

    open fun getResolutionSelector():ResolutionSelector {
        return when(cameraPurpose){
            IMAGE_CAPTURE -> getDefaultCaptureCameraResolutionSelector()
            IMAGE_ANALYSIS -> getDefaultAnalysisCameraResolutionSelector()
        }
    }

    private fun getDefaultCaptureCameraResolutionSelector():ResolutionSelector{
        return ResolutionSelector.Builder()
            .setAllowedResolutionMode(ResolutionSelector.PREFER_HIGHER_RESOLUTION_OVER_CAPTURE_RATE)
            .setResolutionStrategy(ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY)
            .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
            .build()
    }

    private fun getDefaultAnalysisCameraResolutionSelector():ResolutionSelector{
        return ResolutionSelector.Builder()
            .setAllowedResolutionMode(ResolutionSelector.PREFER_CAPTURE_RATE_OVER_HIGHER_RESOLUTION)
            .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
            .build()
    }

    private fun onStartCamera() {
        cameraProvider = cameraProviderFuture.get()

        val resolutionSelector = getResolutionSelector()

        preview = Preview.Builder().apply {
            setResolutionSelector(resolutionSelector)
        }.build().apply {
            setSurfaceProviderBaseCamera(this)
        }

        when (cameraPurpose) {
            IMAGE_CAPTURE -> {
                imageCapture = ImageCapture.Builder()
                    .setFlashMode(getImageCaptureFlashMode(cameraFlash))
                    .setResolutionSelector(resolutionSelector)
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                useCaseGroup = UseCaseGroup.Builder().apply {
                    addUseCase(preview)
                    addUseCase(imageCapture)
                }.build()
            }

            IMAGE_ANALYSIS -> {
                useCaseGroup = UseCaseGroup.Builder().apply {
                    addUseCase(preview)
                }.build()
            }
        }
        bindCameraToView()
        onCameraStarted()
    }

    abstract fun onCameraStarted()

    private fun bindCameraToView() {
        try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, useCaseGroup)
            camera.cameraInfo.torchState.observe(this) { state ->
                _isTorchTurnOn = (state == TorchState.ON)
                _cameraListener?.onFlashTorchChanged(state == TorchState.ON)
            }
        } catch (e: Throwable) {
            Log.e(
                BaseLumiCameraActivity::class.java.simpleName,
                "error bind camera to view: ${e.message}"
            )
        }
    }

    override fun onStop() {
        cameraProvider.unbindAll()
        cameraExecutor.shutdown()
        super.onStop()
    }

    private fun hasFlashUnit(): Boolean {
        return camera.cameraInfo.hasFlashUnit()
    }

    private var _isTorchTurnOn: Boolean = false
    val isTorchTurnOn: Boolean get() = _isTorchTurnOn
    fun enableTorch() {
        if (cameraPurpose == LumiCameraPurpose.IMAGE_ANALYSIS) {
            if (hasFlashUnit()) {
                if (!_isTorchTurnOn) {
                    _isTorchTurnOn = true
                    camera.cameraControl.enableTorch(_isTorchTurnOn)
                } else {
                    _isTorchTurnOn = false
                    camera.cameraControl.enableTorch(_isTorchTurnOn)
                }
            }
        } else {
            Log.w(
                BaseLumiCameraActivity::class.java.simpleName,
                "unable to turn on torch, cameraPurpose should be ImageAnalysis"
            )
        }
    }

    override fun switchCameraFacing(cameraSelector: CameraSelector) {
        if (this.cameraSelector == cameraSelector) return
        if (cameraProvider.hasCamera(cameraSelector)) {
            setFlashModeCapture(LumiCameraFlash.OFF)
            this.cameraSelector = cameraSelector
            bindCameraToView()
            return
        }
    }

    interface CameraListener {
        fun onFlashModeChanged(flashMode: LumiCameraFlash) {}
        fun onFlashTorchChanged(isTorchTurnOn: Boolean) {}
    }

    private var _cameraListener: CameraListener? = null
    fun addCameraListener(listener: CameraListener) {
        if (_cameraListener != null) return
        _cameraListener = listener
    }

    private var _isCapturing: Boolean = false
    override fun takePicture(listener: LumiCameraCaptureListener) {
        if (_isCapturing) {
            Log.i(
                this::class.java.simpleName,
                "camera currently capturing image"
            )
            return
        }
        _isCapturing = true
        imageCapture.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    Log.d(BaseLumiCameraActivity::class.java.simpleName, "onCaptureSuccess")
                    _isCapturing = false
                    listener.onCaptureSuccess(image, cameraSelector)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e(
                        BaseLumiCameraActivity::class.java.simpleName,
                        "onCaptureError: ${exception.message}"
                    )
                    _isCapturing = false
                    listener.onCaptureError(
                        LumiCameraException(
                            enumError = "ERR_GENERAL_CAPTURE",
                            message = "An error occurred while capturing the image."
                        )
                    )
                }
            })
    }

    override fun setFlashModeCapture(flashMode: LumiCameraFlash) {
        if (cameraPurpose == LumiCameraPurpose.IMAGE_CAPTURE) {
            if (!hasFlashUnit()) {
                Log.d(BaseLumiCameraActivity::class.java.simpleName, "camera didn't has flash unit")
                return
            }
            imageCapture.flashMode = getImageCaptureFlashMode(flashMode)
            _cameraListener?.onFlashModeChanged(flashMode)
        }
    }

    private fun getImageCaptureFlashMode(flashMode: LumiCameraFlash): Int {
        return when (flashMode) {
            LumiCameraFlash.ON -> ImageCapture.FLASH_MODE_ON
            LumiCameraFlash.AUTO -> ImageCapture.FLASH_MODE_AUTO
            else -> ImageCapture.FLASH_MODE_OFF
        }
    }


    // --- Analyze Camera Method ---
    private var _Lumi_cameraAnalysisListener: LumiCameraAnalysisListener? = null
    fun addCameraAnalysisListener(listener: LumiCameraAnalysisListener) {
        if (_Lumi_cameraAnalysisListener != null) return
        _Lumi_cameraAnalysisListener = listener
    }


    private var _isAnalyzing: Boolean = false
    val isAnalyzing: Boolean get() = _isAnalyzing
    override fun startAnalyze(analyzer: ImageAnalysis.Analyzer) {
        if (_isAnalyzing) {
            Log.w(BaseLumiCameraActivity::class.java.simpleName, "camera already analysis")
            return
        }
        _isAnalyzing = true
        addImageAnalysisAnalyzer(analyzer)
        useCaseGroup.useCases.add(imageAnalysis)
        bindCameraToView()
        _Lumi_cameraAnalysisListener?.onStartAnalyze()
    }

    override fun stopAnalyze() {
        try {
            imageAnalysis.clearAnalyzer()
            useCaseGroup.useCases.remove(imageAnalysis)
        } catch (e: Throwable) {
            Log.e(
                this::class.java.simpleName,
                "failed stop for analyze clear analyzer: ${e.message}"
            )
        }
        _Lumi_cameraAnalysisListener?.onStopAnalyze()
        _isAnalyzing = false
    }

    private fun addImageAnalysisAnalyzer(analyzer: ImageAnalysis.Analyzer) {
        val resolution = getResolutionSelector()
        imageAnalysis = ImageAnalysis.Builder()
            .setResolutionSelector(resolution)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(executor, analyzer)
        this.analyzer = analyzer
    }
}

