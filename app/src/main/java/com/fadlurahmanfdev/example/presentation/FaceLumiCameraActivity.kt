package com.fadlurahmanfdev.example.presentation

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
import com.fadlurahmanfdev.lumi.core.enums.LumiCameraPurpose
import com.fadlurahmanfdev.lumi.core.exception.FeatureCameraException
import com.fadlurahmanfdev.lumi.LumiCameraHelper
import com.fadlurahmanfdev.lumi.LumiLumiCameraActivity
import com.fadlurahmanfdev.lumi.domain.listener.LumiCameraCaptureListener
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.other.AppCameraSharedModel
import com.fadlurahmanfdev.lumi.presentation.CircleProgressOverlayView

class FaceLumiCameraActivity : LumiLumiCameraActivity(),
    LumiLumiCameraActivity.CameraListener {
    lateinit var cameraPreview: PreviewView
    lateinit var ivFlash: ImageView
    lateinit var ivCamera: ImageView
    lateinit var ivSwitch: ImageView
    lateinit var selfieOverlayView: CircleProgressOverlayView
    override var cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    override var cameraPurpose: LumiCameraPurpose = LumiCameraPurpose.IMAGE_CAPTURE
    private val cameraHelper: LumiCameraHelper = LumiCameraHelper()

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
        ivSwitch = findViewById<ImageView>(R.id.iv_switch_camera)
        selfieOverlayView = findViewById(R.id.selfieOverlay)

        ivFlash.setOnClickListener {
            enableTorch()
        }

        ivSwitch.setOnClickListener {}

        ivCamera.setOnClickListener {
            takePicture(object : LumiCameraCaptureListener {
                override fun onCaptureSuccess(
                    imageProxy: ImageProxy,
                    cameraSelector: CameraSelector
                ) {
                    AppCameraSharedModel.bitmapImage =
                        cameraHelper.getBitmapFromImageProxy(imageProxy)
                    if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                        AppCameraSharedModel.bitmapImage =
                            cameraHelper.mirrorHorizontalBitmap(AppCameraSharedModel.bitmapImage)
                    }
                    val intent =
                        Intent(this@FaceLumiCameraActivity, PreviewFaceImageActivity::class.java)
                    startActivity(intent)
                }

                override fun onCaptureError(exception: FeatureCameraException) {
                    Toast.makeText(this@FaceLumiCameraActivity, "Capture Error: ${exception.enumError}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        addCameraListener(this)
    }

    override fun setSurfaceProviderBaseCamera(preview: Preview) {
        preview.setSurfaceProvider(cameraPreview.surfaceProvider)
    }

    override fun onCameraStarted() {

    }
}