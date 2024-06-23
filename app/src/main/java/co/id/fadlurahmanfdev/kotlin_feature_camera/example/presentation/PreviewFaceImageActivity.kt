package co.id.fadlurahmanfdev.kotlin_feature_camera.example.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.id.fadlurahmanfdev.kotlin_feature_camera.example.R
import co.id.fadlurahmanfdev.kotlin_feature_camera.other.utility.FeatureCameraUtility


class PreviewFaceImageActivity : AppCompatActivity() {
    //    lateinit var imageView: CircleImageView
    lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preview_face_image)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView = findViewById(R.id.previewImage)

        val bitmapImage = FeatureCameraUtility.bitmapImage
//        val newBitmapImage = Bitmap.createBitmap(
//            bitmapImage,
//            (bitmapImage.width * 0.25).toInt(),
//            0,
//            (bitmapImage.width) - ((bitmapImage.width * 0.25).toInt()),
//            (bitmapImage.height * 1).toInt(),
//        )
        imageView.setImageBitmap(bitmapImage)
//        imageView.rotation = FeatureCameraUtility.rotationDegree
    }
}