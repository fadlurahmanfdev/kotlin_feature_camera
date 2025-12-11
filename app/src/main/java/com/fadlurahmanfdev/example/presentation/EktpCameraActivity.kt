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
import com.fadlurahmanfdev.lumi.core.exception.LumiCameraException
import com.fadlurahmanfdev.lumi.LumiCameraHelper
import com.fadlurahmanfdev.lumi.BaseLumiCameraActivity
import com.fadlurahmanfdev.lumi.domain.listener.LumiCameraCaptureListener
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.other.AppCameraSharedModel

class EktpCameraActivity : BaseLumiCameraActivity(),
    BaseLumiCameraActivity.CameraListener {
    lateinit var cameraPreview: PreviewView
    lateinit var ivFlash: ImageView
    lateinit var ivCamera: ImageView
    lateinit var ivSwitch: ImageView
    override var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    override var cameraPurpose: LumiCameraPurpose = LumiCameraPurpose.IMAGE_CAPTURE
    private val cameraHelper = LumiCameraHelper()

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
            takePicture(object : LumiCameraCaptureListener {
                override fun onCaptureSuccess(
                    imageProxy: ImageProxy,
                    cameraSelector: CameraSelector
                ) {


                    val bitmap = cameraHelper.getBitmapFromImageProxy(imageProxy)
                    AppCameraSharedModel.bitmapImage = bitmap
                    val intent =
                        Intent(this@EktpCameraActivity, PreviewEKTPImageActivity::class.java)
                    startActivity(intent)


                }

                override fun onCaptureError(exception: LumiCameraException) {
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

    override fun onCameraStarted() {

    }
}