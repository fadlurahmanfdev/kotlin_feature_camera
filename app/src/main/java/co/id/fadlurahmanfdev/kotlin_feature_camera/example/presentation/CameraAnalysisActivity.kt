package co.id.fadlurahmanfdev.kotlin_feature_camera.example.presentation

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.type.FeatureCameraFacing
import co.id.fadlurahmanfdev.kotlin_feature_camera.domain.common.BaseCameraActivity
import co.id.fadlurahmanfdev.kotlin_feature_camera.example.R

class CameraAnalysisActivity : BaseCameraActivity(), BaseCameraActivity.AnalyzeListener {
    lateinit var cameraPreview: PreviewView
    lateinit var ivFlash: ImageView
    lateinit var ivCamera: ImageView
    lateinit var ivSwitch: ImageView
    override fun onStartCreateBaseCamera(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_camera_analysis)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        cameraPreview = findViewById<PreviewView>(R.id.cameraPreview)
        ivFlash = findViewById<ImageView>(R.id.iv_flash)
        ivCamera = findViewById<ImageView>(R.id.iv_camera)
        ivSwitch = findViewById<ImageView>(R.id.iv_switch_camera)
        addAnalyzeListener(this)
    }

    override fun onCreateBaseCamera(savedInstanceState: Bundle?) {
        ivFlash.setOnClickListener {
            enableTorch()
        }

        ivSwitch.setOnClickListener {
            switchCamera()
        }

        ivCamera.setOnClickListener {
            startAnalyze()
        }
    }

    override fun setSurfaceProviderBaseCamera(preview: Preview) {
        preview.setSurfaceProvider(cameraPreview.surfaceProvider)
    }

    override fun onSetCameraPurpose() {
        setCameraPurposeAnalysis {
            println("MASUK SINI IMAGE PROXY ACTIVITY LUAR")
        }
    }

    override fun onSetCameraFacing() {
        setCameraFacing(FeatureCameraFacing.FRONT)
    }

    override fun onStartAnalyze() {
        ivCamera.visibility = View.INVISIBLE
    }
}