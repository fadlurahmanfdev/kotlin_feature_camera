package co.id.fadlurahmanfdev.kotlin_feature_camera.example.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraPurpose
import com.fadlurahmanfdev.kotlin_feature_camera.data.exception.FeatureCameraException
import com.fadlurahmanfdev.kotlin_feature_camera.data.repository.FeatureCameraRepository
import com.fadlurahmanfdev.kotlin_feature_camera.data.repository.FeatureCameraRepositoryImpl
import com.fadlurahmanfdev.kotlin_feature_camera.domain.common.BaseCameraActivity
import com.fadlurahmanfdev.kotlin_feature_camera.domain.listener.CameraCaptureListener
import co.id.fadlurahmanfdev.kotlin_feature_camera.example.R
import co.id.fadlurahmanfdev.kotlin_feature_camera.example.other.CameraSharedModel

class EktpCameraActivity : BaseCameraActivity(),
    BaseCameraActivity.CameraListener {
    lateinit var cameraPreview: PreviewView
    lateinit var ivFlash: ImageView
    lateinit var ivCamera: ImageView
    lateinit var ivSwitch: ImageView
    override var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    override var cameraPurpose: FeatureCameraPurpose = FeatureCameraPurpose.IMAGE_CAPTURE
    private val cameraRepository: FeatureCameraRepository = FeatureCameraRepositoryImpl()

    override fun onCreateBaseCamera(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_ektp_camera)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        cameraPreview = findViewById<PreviewView>(R.id.cameraPreview)
        ivFlash = findViewById<ImageView>(R.id.iv_flash)
        ivCamera = findViewById<ImageView>(R.id.iv_camera)
        ivSwitch = findViewById<ImageView>(R.id.iv_switch_camera)
        ivFlash.setOnClickListener {}

        ivSwitch.setOnClickListener {}

        ivCamera.setOnClickListener {
            takePicture(object : CameraCaptureListener {
                override fun onCaptureSuccess(
                    imageProxy: ImageProxy,
                    cameraSelector: CameraSelector
                ) {


                    val bitmap = cameraRepository.getBitmapFromImageProxy(imageProxy)
                    CameraSharedModel.bitmapImage = bitmap
                    val intent =
                        Intent(this@EktpCameraActivity, PreviewEKTPImageActivity::class.java)
                    startActivity(intent)


                }

                override fun onCaptureError(exception: FeatureCameraException) {
                    Toast.makeText(
                        this@EktpCameraActivity,
                        "Capture Error: ${exception.enumError}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        addCameraListener(this)
    }

    override fun setSurfaceProviderBaseCamera(preview: Preview) {
        preview.setSurfaceProvider(cameraPreview.surfaceProvider)
    }
}