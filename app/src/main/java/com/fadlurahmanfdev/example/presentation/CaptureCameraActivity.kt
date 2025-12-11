package com.fadlurahmanfdev.example.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fadlurahmanfdev.lumi.core.enums.LumiCameraFlash
import com.fadlurahmanfdev.lumi.core.enums.LumiCameraPurpose
import com.fadlurahmanfdev.lumi.core.exception.LumiCameraException
import com.fadlurahmanfdev.lumi.LumiCameraHelper
import com.fadlurahmanfdev.lumi.BaseLumiCameraActivity
import com.fadlurahmanfdev.lumi.domain.listener.LumiCameraCaptureListener
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.other.AppCameraSharedModel

class CaptureCameraActivity : BaseLumiCameraActivity(), BaseLumiCameraActivity.CameraListener {
    lateinit var cameraPreview: PreviewView
    lateinit var ivFlash: ImageView
    lateinit var ivCamera: ImageView
    lateinit var ivSwitch: ImageView
    override var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    override var cameraPurpose: LumiCameraPurpose = LumiCameraPurpose.IMAGE_CAPTURE
    private var currentFlashMode: LumiCameraFlash = LumiCameraFlash.OFF
    private var currentCameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var cameraHelper = LumiCameraHelper()

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
            if (currentFlashMode == LumiCameraFlash.OFF) {
                setFlashModeCapture(LumiCameraFlash.ON)
            } else {
                setFlashModeCapture(LumiCameraFlash.OFF)
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
            takePicture(object : LumiCameraCaptureListener {
                override fun onCaptureSuccess(
                    imageProxy: ImageProxy,
                    cameraSelector: CameraSelector
                ) {
                    AppCameraSharedModel.bitmapImage = cameraHelper.getBitmapFromImageProxy(imageProxy)
                    if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                        AppCameraSharedModel.bitmapImage =
                            cameraHelper.mirrorHorizontalBitmap(AppCameraSharedModel.bitmapImage)
                    }
                    val intent =
                        Intent(this@CaptureCameraActivity, PreviewImageActivity::class.java)
                    startActivity(intent)
                }

                override fun onCaptureError(exception: LumiCameraException) {
                    Toast.makeText(this@CaptureCameraActivity, "Capture Error: ${exception.enumError}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        addCameraListener(this)
    }

    override fun setSurfaceProviderBaseCamera(preview: Preview) {
        preview.surfaceProvider = cameraPreview.surfaceProvider
    }

    override fun onCameraStarted() {

    }

    override fun onFlashModeChanged(flashMode: LumiCameraFlash) {
        super.onFlashModeChanged(flashMode)
        currentFlashMode = flashMode
        when (currentFlashMode) {
            LumiCameraFlash.ON, LumiCameraFlash.AUTO -> {
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