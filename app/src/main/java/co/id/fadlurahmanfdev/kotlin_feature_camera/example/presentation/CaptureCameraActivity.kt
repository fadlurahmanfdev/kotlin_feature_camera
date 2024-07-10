package co.id.fadlurahmanfdev.kotlin_feature_camera.example.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraFlash
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraPurpose
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.exception.FeatureCameraException
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.repository.FeatureCameraRepository
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.repository.FeatureCameraRepositoryImpl
import co.id.fadlurahmanfdev.kotlin_feature_camera.domain.common.BaseCameraActivity
import co.id.fadlurahmanfdev.kotlin_feature_camera.domain.common.BaseCameraV2Activity
import co.id.fadlurahmanfdev.kotlin_feature_camera.example.R
import co.id.fadlurahmanfdev.kotlin_feature_camera.example.other.CameraSharedModel
import co.id.fadlurahmanfdev.kotlin_feature_camera.other.utility.FeatureCameraUtility

class CaptureCameraActivity : BaseCameraV2Activity(),
    BaseCameraV2Activity.CaptureListener, BaseCameraV2Activity.CameraListener {
    lateinit var cameraPreview: PreviewView
    lateinit var ivFlash: ImageView
    lateinit var ivCamera: ImageView
    lateinit var ivSwitch: ImageView
    override var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    override var cameraPurpose: FeatureCameraPurpose = FeatureCameraPurpose.IMAGE_CAPTURE
    private var currentFlashMode: FeatureCameraFlash = FeatureCameraFlash.OFF
    private var currentCameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var repository: FeatureCameraRepository = FeatureCameraRepositoryImpl()

    override fun onCreateBaseCamera(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_single_camera)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        cameraPreview = findViewById<PreviewView>(R.id.cameraPreview)
        ivFlash = findViewById<ImageView>(R.id.iv_flash)
        ivCamera = findViewById<ImageView>(R.id.iv_camera)
        ivSwitch = findViewById<ImageView>(R.id.iv_switch_camera)

        ivFlash.setOnClickListener {
            if (currentFlashMode == FeatureCameraFlash.OFF) {
                setFlashMode(FeatureCameraFlash.ON)
            } else {
                setFlashMode(FeatureCameraFlash.OFF)
            }
        }

        ivSwitch.setOnClickListener {
            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                switchCameraFacing(CameraSelector.DEFAULT_FRONT_CAMERA)
            } else {
                switchCameraFacing(CameraSelector.DEFAULT_BACK_CAMERA)
            }
        }

        ivCamera.setOnClickListener {
            takePicture()
        }

        addCameraListener(this)
        addCaptureListener(this)
    }

    override fun setSurfaceProviderBaseCamera(preview: Preview) {
        preview.setSurfaceProvider(cameraPreview.surfaceProvider)
    }

    override fun onCaptureSuccess(imageProxy: ImageProxy, cameraSelector: CameraSelector) {
        CameraSharedModel.bitmapImage = repository.getBitmapFromImageProxy(imageProxy)
        if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
            CameraSharedModel.bitmapImage =
                repository.mirrorHorizontalBitmap(CameraSharedModel.bitmapImage)
        }
        val intent = Intent(this, PreviewFaceImageActivity::class.java)
        startActivity(intent)
    }

    override fun onCaptureError(exception: FeatureCameraException) {
        println("MASUK ERROR CAPTURE: ${exception.enumError}")
    }

    override fun onFlashModeChanged(flashMode: FeatureCameraFlash) {
        super.onFlashModeChanged(flashMode)
        currentFlashMode = flashMode
        when (currentFlashMode) {
            FeatureCameraFlash.ON, FeatureCameraFlash.AUTO -> {
                ivFlash.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.round_flash_on_24
                    )
                )
            }

            else -> {
                ivFlash.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.round_flash_off_24
                    )
                )
            }
        }
    }
}