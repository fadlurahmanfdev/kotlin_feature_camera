package co.id.fadlurahmanfdev.kotlin_feature_camera.example.presentation

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraPurpose
import com.fadlurahmanfdev.kotlin_feature_camera.data.repository.FeatureCameraRepository
import com.fadlurahmanfdev.kotlin_feature_camera.data.repository.FeatureCameraRepositoryImpl
import com.fadlurahmanfdev.kotlin_feature_camera.domain.common.BaseCameraActivity
import com.fadlurahmanfdev.kotlin_feature_camera.domain.listener.CameraAnalysisListener
import co.id.fadlurahmanfdev.kotlin_feature_camera.example.R

class FaceCameraAnalysisActivity : BaseCameraActivity(), CameraAnalysisListener {
    lateinit var cameraPreview: PreviewView
    lateinit var ivFlash: ImageView
    lateinit var ivCamera: ImageView
    lateinit var ivStopCamera: ImageView
    lateinit var ivSwitch: ImageView
    override var cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    override var cameraPurpose: FeatureCameraPurpose = FeatureCameraPurpose.IMAGE_ANALYSIS
    private val cameraRepository: FeatureCameraRepository = FeatureCameraRepositoryImpl()

    override fun onCreateBaseCamera(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_face_camera)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        cameraPreview = findViewById<PreviewView>(R.id.cameraPreview)
        ivFlash = findViewById<ImageView>(R.id.iv_flash)
        ivCamera = findViewById<ImageView>(R.id.iv_camera)
        ivStopCamera = findViewById<ImageView>(R.id.iv_stop_camera)
        ivSwitch = findViewById<ImageView>(R.id.iv_switch_camera)

        ivFlash.setOnClickListener {
            enableTorch()
        }

        ivSwitch.setOnClickListener {

        }

        ivCamera.setOnClickListener {
            startAnalyze { imageProxy ->
                imageProxy.close()
            }
        }

        ivStopCamera.setOnClickListener {
            stopAnalyze()
        }

        addCameraAnalysisListener(this)
    }

    override fun setSurfaceProviderBaseCamera(preview: Preview) {
        preview.setSurfaceProvider(cameraPreview.surfaceProvider)
    }

    override fun onStartAnalyze() {
        ivCamera.visibility = View.GONE
        ivStopCamera.visibility = View.VISIBLE
    }

    override fun onStopAnalyze() {
        ivCamera.visibility = View.VISIBLE
        ivStopCamera.visibility = View.GONE
    }
}