package co.id.fadlurahmanfdev.kotlin_feature_camera.domain.common

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.core.content.ContextCompat
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.exception.FeatureCameraException
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.type.FeatureCameraFacing
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.type.FeatureCameraFacing.*
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.type.FeatureCameraPurpose
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.type.FeatureCameraPurpose.*
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseCameraActivity : AppCompatActivity() {
    lateinit var cameraExecutor: ExecutorService
    lateinit var executor: Executor
    lateinit var camera: Camera
    lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    lateinit var cameraProvider: ProcessCameraProvider
    var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    lateinit var imageCapture: ImageCapture
    lateinit var imageAnalysis: ImageAnalysis
    lateinit var analyzer: Analyzer
    lateinit var preview: Preview
    lateinit var cameraPurpose: FeatureCameraPurpose
    lateinit var useCaseGroup: UseCaseGroup

    val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onStartCreateBaseCamera(savedInstanceState)
        executor = ContextCompat.getMainExecutor(this)
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        onSetCameraPurpose()
        onSetCameraFacing()
        onCreateBaseCamera(savedInstanceState)
    }

    /**
     * [setCameraPurposeCapture] for purposes
     * */
    abstract fun onSetCameraPurpose()

    fun setCameraPurposeCapture() {
        cameraPurpose = IMAGE_CAPTURE
    }

    fun setCameraPurposeAnalysis(analyzer: ImageAnalysis.Analyzer) {
        cameraPurpose = IMAGE_ANALYSIS
        this.analyzer = analyzer
    }

    abstract fun onSetCameraFacing()

    fun setCameraFacing(cameraFacing: FeatureCameraFacing) {
        when (cameraFacing) {
            FRONT -> {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            }

            BACK -> {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            }
        }
    }

    abstract fun onStartCreateBaseCamera(savedInstanceState: Bundle?)

    abstract fun onCreateBaseCamera(savedInstanceState: Bundle?)

    override fun onResume() {
        super.onResume()
        cameraExecutor = Executors.newSingleThreadExecutor()
        onListenCameraProvider()
    }

    open fun onListenCameraProvider() {
        cameraProviderFuture.addListener({
            onStartCamera()
        }, executor)
    }

    private fun onStartCamera() {
        cameraProvider = cameraProviderFuture.get()
        val resolutionSelector = ResolutionSelector.Builder()
            .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
            .build()


        preview = Preview.Builder().apply {
            setResolutionSelector(resolutionSelector)
        }.build().apply {
            setSurfaceProviderBaseCamera(this)
        }

        when (cameraPurpose) {
            IMAGE_CAPTURE -> {
                imageCapture = ImageCapture.Builder().setFlashMode(ImageCapture.FLASH_MODE_ON)
                    .setResolutionSelector(resolutionSelector)
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                useCaseGroup = UseCaseGroup.Builder().apply {
                    addUseCase(preview)
                    addUseCase(imageCapture)
                }.build()
            }

            IMAGE_ANALYSIS -> {
                val resolution = ResolutionSelector.Builder()
                    .setAllowedResolutionMode(ResolutionSelector.PREFER_CAPTURE_RATE_OVER_HIGHER_RESOLUTION)
                    .build()
                imageAnalysis = ImageAnalysis.Builder()
                    .setResolutionSelector(resolution)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                imageAnalysis.setAnalyzer(executor, analyzer)
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
            onBindCameraToView()
        } catch (e: Throwable) {
            Log.e(
                BaseCameraActivity::class.java.simpleName,
                "error bind camera to view: ${e.message}"
            )
        }
    }

    abstract fun onBindCameraToView()

    abstract fun setSurfaceProviderBaseCamera(preview: Preview)

    override fun onStop() {
        cameraProvider.unbindAll()
        cameraExecutor.shutdown()
        super.onStop()
    }

    fun hasFlashUnit(): Boolean {
        return camera.cameraInfo.hasFlashUnit()
    }

    var isTorchTurnOn: Boolean = false
    fun enableTorch() {
        if (hasFlashUnit()) {
            if (!isTorchTurnOn) {
                isTorchTurnOn = true
                camera.cameraControl.enableTorch(isTorchTurnOn)
            } else {
                isTorchTurnOn = false
                camera.cameraControl.enableTorch(isTorchTurnOn)
            }
        }
    }

    fun switchCamera() {
        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            val isHaveFrontCamera = cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
            if (!isHaveFrontCamera) {
                return
            }
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        } else if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
            val isHaveBackCamera = cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
            if (!isHaveBackCamera) {
                return
            }
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }

        bindCameraToView()
    }

    private var captureListener: CaptureListener? = null

    /**
     * addCaptureListener inside function [onBindCameraToView]
     * */
    fun addCaptureListener(captureListener: CaptureListener) {
        if (this.captureListener != null) return
        this.captureListener = captureListener
        camera.cameraInfo.torchState.observe(this) { state ->
            isTorchTurnOn = (state == TorchState.ON)
            this.captureListener?.isTorchChanged(state == TorchState.ON)
        }
    }

    fun takePicture() {
        imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                Log.d(BaseCameraActivity::class.java.simpleName, "onCaptureSuccess")
                captureListener?.onCaptureSuccess(image)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                captureListener?.onCaptureError(
                    FeatureCameraException(
                        enumError = "ERR_GENERAL_CAPTURE",
                        message = "An error occurred while capturing the image."
                    )
                )
            }
        })
    }

    private var analyzeListener: AnalyzeListener? = null
    fun addAnalyzeListener(analyzeListener: AnalyzeListener) {
        if (this.analyzeListener != null) return
        this.analyzeListener = analyzeListener
    }


    fun startAnalyze() {
        useCaseGroup.useCases.add(imageAnalysis)
        bindCameraToView()
        analyzeListener?.onStartAnalyze()
    }

    fun stopAnalyze() {
        imageAnalysis.clearAnalyzer()
    }

    interface CaptureListener {
        fun onCaptureSuccess(imageProxy: ImageProxy)
        fun onCaptureError(exception: FeatureCameraException)
        fun isTorchChanged(isTorch: Boolean)
    }

    interface AnalyzeListener {
        fun onStartAnalyze()
    }
}