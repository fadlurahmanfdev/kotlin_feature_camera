package com.fadlurahmanfdev.example.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fadlurahmanfdev.lumi.core.enums.LumiCameraPurpose
import com.fadlurahmanfdev.lumi.LumiCameraHelper
import com.fadlurahmanfdev.lumi.LumiLumiCameraActivity
import com.fadlurahmanfdev.lumi.domain.listener.LumiCameraAnalysisListener
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.other.AppCameraSharedModel

class FaceLumiLumiCameraAnalysisActivity : LumiLumiCameraActivity(), LumiCameraAnalysisListener {
    lateinit var cameraPreview: PreviewView
    lateinit var ivFlash: ImageView
    lateinit var ivCamera: ImageView
    lateinit var ivStopCamera: ImageView
    lateinit var ivSwitch: ImageView
    override var cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    override var cameraPurpose: LumiCameraPurpose = LumiCameraPurpose.IMAGE_ANALYSIS
    private val cameraHelper = LumiCameraHelper()

    var counter = 0

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
                if (counter > 10) {
                    stopAnalyze()
                    AppCameraSharedModel.bitmapImage =
                        cameraHelper.getBitmapFromImageProxy(imageProxy)
                    if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                        AppCameraSharedModel.bitmapImage =
                            cameraHelper.mirrorHorizontalBitmap(AppCameraSharedModel.bitmapImage)
                    }
                    imageProxy.close()
                    val intent =
                        Intent(
                            this@FaceLumiLumiCameraAnalysisActivity,
                            PreviewFaceImageActivity::class.java
                        )
                    startActivity(intent)
                } else {
                    counter++
                    imageProxy.close()
                }
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

    override fun onCameraStarted() {

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