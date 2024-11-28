package com.fadlurahmanfdev.kotlin_feature_camera.domain.common

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
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraFlash
import com.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraPurpose
import com.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraPurpose.IMAGE_ANALYSIS
import com.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraPurpose.IMAGE_CAPTURE
import com.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraRatio
import com.fadlurahmanfdev.kotlin_feature_camera.data.exception.FeatureCameraException
import com.fadlurahmanfdev.kotlin_feature_camera.domain.callback.BaseCameraCallBack
import com.fadlurahmanfdev.kotlin_feature_camera.domain.listener.CameraAnalysisListener
import com.fadlurahmanfdev.kotlin_feature_camera.domain.listener.CameraCaptureListener
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseCameraActivity : AppCompatActivity(), BaseCameraCallBack {
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
    abstract var cameraPurpose: FeatureCameraPurpose
    open var cameraRatio: FeatureCameraRatio = FeatureCameraRatio.RATIO_16_9
    private var cameraFlash: FeatureCameraFlash = FeatureCameraFlash.OFF
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

    private fun onStartCamera() {
        cameraProvider = cameraProviderFuture.get()
        val resolutionSelector = ResolutionSelector.Builder()
            .setAspectRatioStrategy(if (cameraRatio == FeatureCameraRatio.RATIO_4_3) AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY else AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
            .build()

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
    }

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
                BaseCameraActivity::class.java.simpleName,
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
        if (cameraPurpose == FeatureCameraPurpose.IMAGE_ANALYSIS) {
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
                BaseCameraActivity::class.java.simpleName,
                "unable to turn on torch, cameraPurpose should be ImageAnalysis"
            )
        }
    }

    override fun switchCameraFacing(cameraSelector: CameraSelector) {
        if (this.cameraSelector == cameraSelector) return
        if (cameraProvider.hasCamera(cameraSelector)) {
            setFlashModeCapture(FeatureCameraFlash.OFF)
            this.cameraSelector = cameraSelector
            bindCameraToView()
            return
        }
    }

    interface CameraListener {
        fun onFlashModeChanged(flashMode: FeatureCameraFlash) {}
        fun onFlashTorchChanged(isTorchTurnOn: Boolean) {}
    }

    private var _cameraListener: CameraListener? = null
    fun addCameraListener(listener: CameraListener) {
        if (_cameraListener != null) return
        _cameraListener = listener
    }

    private var _isCapturing: Boolean = false
    override fun takePicture(listener: CameraCaptureListener) {
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
                    Log.d(BaseCameraActivity::class.java.simpleName, "onCaptureSuccess")
                    _isCapturing = false
                    listener.onCaptureSuccess(image, cameraSelector)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e(
                        BaseCameraActivity::class.java.simpleName,
                        "onCaptureError: ${exception.message}"
                    )
                    _isCapturing = false
                    listener.onCaptureError(
                        FeatureCameraException(
                            enumError = "ERR_GENERAL_CAPTURE",
                            message = "An error occurred while capturing the image."
                        )
                    )
                }
            })
    }

    override fun setFlashModeCapture(flashMode: FeatureCameraFlash) {
        if (cameraPurpose == FeatureCameraPurpose.IMAGE_CAPTURE) {
            if (!hasFlashUnit()) {
                Log.d(BaseCameraActivity::class.java.simpleName, "camera didn't has flash unit")
                return
            }
            imageCapture.flashMode = getImageCaptureFlashMode(flashMode)
            _cameraListener?.onFlashModeChanged(flashMode)
        }
    }

    private fun getImageCaptureFlashMode(flashMode: FeatureCameraFlash): Int {
        return when (flashMode) {
            FeatureCameraFlash.ON -> ImageCapture.FLASH_MODE_ON
            FeatureCameraFlash.AUTO -> ImageCapture.FLASH_MODE_AUTO
            else -> ImageCapture.FLASH_MODE_OFF
        }
    }


    // --- Analyze Camera Method ---
    private var _cameraAnalysisListener: CameraAnalysisListener? = null
    fun addCameraAnalysisListener(listener: CameraAnalysisListener) {
        if (_cameraAnalysisListener != null) return
        _cameraAnalysisListener = listener
    }


    private var _isAnalyzing: Boolean = false
    val isAnalyzing: Boolean get() = _isAnalyzing
    override fun startAnalyze(analyzer: ImageAnalysis.Analyzer) {
        if (_isAnalyzing) {
            Log.w(BaseCameraActivity::class.java.simpleName, "camera already analysis")
            return
        }
        _isAnalyzing = true
        addImageAnalysisAnalyzer(analyzer)
        useCaseGroup.useCases.add(imageAnalysis)
        bindCameraToView()
        _cameraAnalysisListener?.onStartAnalyze()
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
        _cameraAnalysisListener?.onStopAnalyze()
        _isAnalyzing = false
    }

    private fun addImageAnalysisAnalyzer(analyzer: ImageAnalysis.Analyzer) {
        val resolution = ResolutionSelector.Builder()
            .setAspectRatioStrategy(if (cameraRatio == FeatureCameraRatio.RATIO_4_3) AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY else AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
            .setAllowedResolutionMode(ResolutionSelector.PREFER_CAPTURE_RATE_OVER_HIGHER_RESOLUTION)
            .build()
        imageAnalysis = ImageAnalysis.Builder()
            .setResolutionSelector(resolution)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(executor, analyzer)
        this.analyzer = analyzer
    }
}

