package co.id.fadlurahmanfdev.kotlin_feature_camera.example.presentation

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.exception.FeatureCameraException
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.type.FeatureCameraFacing
import co.id.fadlurahmanfdev.kotlin_feature_camera.data.type.FeatureCameraPurpose
import co.id.fadlurahmanfdev.kotlin_feature_camera.domain.common.BaseCameraActivity
import co.id.fadlurahmanfdev.kotlin_feature_camera.example.R
import co.id.fadlurahmanfdev.kotlin_feature_camera.other.utility.FeatureCameraUtility

class EktpCameraActivity : BaseCameraActivity(), BaseCameraActivity.CaptureListener {
    lateinit var cameraPreview: PreviewView
    lateinit var ivFlash: ImageView
    lateinit var ivCamera: ImageView
    lateinit var ivSwitch: ImageView
    override fun onStartCreateBaseCamera(savedInstanceState: Bundle?) {
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
    }

    override fun onCreateBaseCamera(savedInstanceState: Bundle?) {
        ivFlash.setOnClickListener {
            enableTorch()
        }

        ivSwitch.setOnClickListener {
            switchCamera()
        }

        ivCamera.setOnClickListener {
            takePicture()
        }
    }

    override fun onBindCameraToView() {
        addCaptureListener(this)
    }

    override fun setSurfaceProviderBaseCamera(preview: Preview) {
        preview.setSurfaceProvider(cameraPreview.surfaceProvider)
    }

    override fun onSetCameraPurpose() {
        setCameraPurposeCapture()
    }

    override fun onSetCameraFacing() {
        setCameraFacing(FeatureCameraFacing.BACK)
    }

    override fun onCaptureSuccess(imageProxy: ImageProxy) {
        val buffer = imageProxy.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer[bytes]
        val bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val base64Image = FeatureCameraUtility.getBase64FromBitmap(bitmapImage)
        if (base64Image != null) {
            FeatureCameraUtility.base64Image = base64Image
            FeatureCameraUtility.rotationDegree = imageProxy.imageInfo.rotationDegrees.toFloat()
            val intent = Intent(this, PreviewEKTPImageActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCaptureError(exception: FeatureCameraException) {
        println("MASUK ERROR CAPTURE: ${exception.enumError}")
    }

    override fun isTorchChanged(isTorch: Boolean) {
        println("MASUK TOCH CHANGED: $isTorch")
    }
}