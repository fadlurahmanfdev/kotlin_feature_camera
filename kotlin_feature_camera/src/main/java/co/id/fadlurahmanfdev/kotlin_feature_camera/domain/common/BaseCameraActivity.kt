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
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraFacing
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraFacing.BACK
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraFacing.FRONT
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraFlash
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraPurpose
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraPurpose.IMAGE_ANALYSIS
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraPurpose.IMAGE_CAPTURE
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraRatio
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseCameraActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var executor: Executor
    private lateinit var camera: Camera
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    abstract var cameraFacing: FeatureCameraFacing
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var imageCapture: ImageCapture
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var analyzer: Analyzer
    private lateinit var preview: Preview
    abstract var cameraPurpose: FeatureCameraPurpose
    open var cameraRatio: FeatureCameraRatio = FeatureCameraRatio.RATIO_16_9
    private lateinit var useCaseGroup: UseCaseGroup

    val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onStartCreateBaseCamera(savedInstanceState)
        executor = ContextCompat.getMainExecutor(this)
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        onSetCameraFacing()
        onCreateBaseCamera(savedInstanceState)
    }

    private fun addImageAnalysisAnalyzer(analyzer: ImageAnalysis.Analyzer) {
        val resolution = ResolutionSelector.Builder()
            .setAllowedResolutionMode(ResolutionSelector.PREFER_CAPTURE_RATE_OVER_HIGHER_RESOLUTION)
            .build()
        imageAnalysis = ImageAnalysis.Builder()
            .setResolutionSelector(resolution)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(executor, analyzer)
        this.analyzer = analyzer
    }

    private fun onSetCameraFacing() {
        cameraSelector = when (cameraFacing) {
            FRONT -> {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            BACK -> {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
        }
    }

    fun setFeatureCameraFacing(cameraFacing: FeatureCameraFacing) {
        this.cameraFacing = cameraFacing
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

    fun isHaveCamera(facing: FeatureCameraFacing): Boolean {
        return when (facing) {
            FRONT -> {
                cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
            }

            BACK -> {
                cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
            }
        }
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
                    .setFlashMode(
                        when (cameraFlash) {
                            FeatureCameraFlash.ON -> ImageCapture.FLASH_MODE_ON
                            FeatureCameraFlash.AUTO -> ImageCapture.FLASH_MODE_AUTO
                            else -> ImageCapture.FLASH_MODE_OFF
                        }
                    )
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
                isTorchTurnOn = (state == TorchState.ON)
                captureListener?.isTorchChanged(state == TorchState.ON)
            }
            onAfterBindCameraToView()
        } catch (e: Throwable) {
            Log.e(
                BaseCameraActivity::class.java.simpleName,
                "error bind camera to view: ${e.message}"
            )
        }
    }

    @Deprecated("delete soon")
    abstract fun onAfterBindCameraToView()

    abstract fun setSurfaceProviderBaseCamera(preview: Preview)

    override fun onStop() {
        cameraProvider.unbindAll()
        cameraExecutor.shutdown()
        super.onStop()
    }

    fun hasFlashUnit(): Boolean {
        return camera.cameraInfo.hasFlashUnit() ?: false
    }

    private var cameraFlash: FeatureCameraFlash = FeatureCameraFlash.OFF
    fun switchFlash(cameraFlash: FeatureCameraFlash) {
        this.cameraFlash = cameraFlash
    }

    private var isTorchTurnOn: Boolean = false
    fun enableTorch() {
        if (cameraPurpose == FeatureCameraPurpose.IMAGE_ANALYSIS) {
            if (hasFlashUnit()) {
                if (!isTorchTurnOn) {
                    isTorchTurnOn = true
                    camera.cameraControl.enableTorch(isTorchTurnOn)
                } else {
                    isTorchTurnOn = false
                    camera.cameraControl.enableTorch(isTorchTurnOn)
                }
            }
        } else {
            Log.w(
                BaseCameraActivity::class.java.simpleName,
                "unable to turn on torch, cameraPurpose should be ImageAnalysis"
            )
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

    fun addCaptureListener(captureListener: CaptureListener) {
        if (this.captureListener != null) return
        this.captureListener = captureListener
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


    var isStartAnalysis: Boolean = false
    fun startAnalyze(analyzer: ImageAnalysis.Analyzer) {
        if (isStartAnalysis) {
            Log.w(BaseCameraActivity::class.java.simpleName, "camera already analysis")
            return
        }
        isStartAnalysis = true
        addImageAnalysisAnalyzer(analyzer)
        useCaseGroup.useCases.add(imageAnalysis)
        bindCameraToView()
        analyzeListener?.onStartAnalyze()
    }

    fun stopAnalyze() {
        imageAnalysis.clearAnalyzer()
        useCaseGroup.useCases.remove(imageAnalysis)
        isStartAnalysis = false
        analyzeListener?.onStopAnalyze()
    }

    interface CaptureListener {
        fun onCaptureSuccess(imageProxy: ImageProxy)
        fun onCaptureError(exception: FeatureCameraException)
        fun isTorchChanged(isTorch: Boolean)
    }

    interface AnalyzeListener {
        fun onStartAnalyze()
        fun onStopAnalyze()
    }
}