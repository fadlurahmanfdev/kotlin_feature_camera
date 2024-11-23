package co.id.fadlurahmanfdev.kotlin_feature_camera.example.presentation

import android.os.Bundle
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraPurpose
import com.fadlurahmanfdev.kotlin_feature_camera.domain.common.BaseCameraActivity
import com.fadlurahmanfdev.kotlin_feature_camera.domain.listener.CameraAnalysisListener
import co.id.fadlurahmanfdev.kotlin_feature_camera.example.R

class CameraAnalysisActivity : BaseCameraActivity(), CameraAnalysisListener,
    BaseCameraActivity.CameraListener {
    lateinit var cameraPreview: PreviewView
    lateinit var ivFlash: ImageView
    lateinit var ivCamera: ImageView
    lateinit var ivSwitch: ImageView
    override var cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    override var cameraPurpose: FeatureCameraPurpose = FeatureCameraPurpose.IMAGE_ANALYSIS

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
            enableTorch()
        }

        ivSwitch.setOnClickListener {
            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                switchCameraFacing(CameraSelector.DEFAULT_FRONT_CAMERA)
            } else {
                switchCameraFacing(CameraSelector.DEFAULT_BACK_CAMERA)
            }
        }

        ivCamera.setOnClickListener {
            if (!isAnalyzing) {
                startAnalyze { imageProxy ->
                    imageProxy.close()
                }
            } else {
                stopAnalyze()
            }
        }

        addCameraListener(this)
        addCameraAnalysisListener(this)
    }

    /**
     * Configures the camera preview by setting the surface provider.
     *
     * This function should be used to set the [Preview.setSurfaceProvider] with the value of [PreviewView.mSurfaceProvider].
     */
    override fun setSurfaceProviderBaseCamera(preview: Preview) {
        preview.setSurfaceProvider(cameraPreview.surfaceProvider)
    }

    override fun onStartAnalyze() {
        ivCamera.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.baseline_stop_circle_24
            )
        )
    }

    override fun onStopAnalyze() {
        ivCamera.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.round_camera_alt_24))
    }

    override fun onFlashTorchChanged(isTorchTurnOn: Boolean) {
        if (isTorchTurnOn) {
            ivFlash.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.round_flash_on_24))
        } else {
            ivFlash.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.round_flash_off_24))
        }
    }
}