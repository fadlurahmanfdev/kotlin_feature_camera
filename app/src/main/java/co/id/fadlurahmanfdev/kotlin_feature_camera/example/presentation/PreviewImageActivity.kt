package co.id.fadlurahmanfdev.kotlin_feature_camera.example.presentation

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import co.id.fadlurahmanfdev.kotlin_feature_camera.example.R
import co.id.fadlurahmanfdev.kotlin_feature_camera.other.utility.FeatureCameraUtility
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation

class PreviewImageActivity : AppCompatActivity() {
    lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preview_image)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageView = findViewById(R.id.previewImage)

//        val base64Image = FeatureCameraUtility.base64Image
//        val imageBytes = Base64.decode(base64Image, 0)
//        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//        imageView.rotation = FeatureCameraUtility.rotationDegree
//        imageView.setImageBitmap(image)

        Glide.with(imageView).load(FeatureCameraUtility.bitmapImage) .into(imageView)

//        val bitmapImage = FeatureCameraUtility.bitmapImage
//        imageView.rotation = FeatureCameraUtility.rotationDegree
//        imageView.setImageBitmap(bitmapImage)
    }
}