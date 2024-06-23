package co.id.fadlurahmanfdev.kotlin_feature_camera.example.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import co.id.fadlurahmanfdev.kotlin_feature_camera.example.R
import co.id.fadlurahmanfdev.kotlin_feature_camera.example.data.FeatureModel

class MainActivity : AppCompatActivity(), ListExampleAdapter.Callback {
    lateinit var viewModel: MainViewModel

    private val features: List<FeatureModel> = listOf<FeatureModel>(
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Capture Camera",
            desc = "Capture Camera",
            enum = "CAPTURE_CAMERA"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Analyze Camera",
            desc = "Analyze Camera",
            enum = "ANALYZE_CAMERA"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "EKTP Camera",
            desc = "EKTP Camera",
            enum = "EKTP_CAMERA"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Face Camera",
            desc = "Face Camera",
            enum = "FACE_CAMERA"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Face Analysis Camera",
            desc = "Face Analysis Camera",
            enum = "FACE_ANALYSIS_CAMERA"
        ),
    )

    private lateinit var rv: RecyclerView

    private lateinit var adapter: ListExampleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        rv = findViewById<RecyclerView>(R.id.rv)

        rv.setItemViewCacheSize(features.size)
        rv.setHasFixedSize(true)

        adapter = ListExampleAdapter()
        adapter.setCallback(this)
        adapter.setList(features)
        adapter.setHasStableIds(true)
        rv.adapter = adapter
    }

    override fun onClicked(item: FeatureModel) {
        when (item.enum) {
            "CAPTURE_CAMERA" -> {
                val intent = Intent(this, SingleCameraActivity::class.java)
                startActivity(intent)
            }

            "ANALYZE_CAMERA" -> {
                val intent = Intent(this, CameraAnalysisActivity::class.java)
                startActivity(intent)
            }

            "EKTP_CAMERA" -> {
                val intent = Intent(this, EktpCameraActivity::class.java)
                startActivity(intent)
            }

            "FACE_CAMERA" -> {
                val intent = Intent(this, FaceCameraActivity::class.java)
                startActivity(intent)
            }

            "FACE_ANALYSIS_CAMERA" -> {
                val intent = Intent(this, FaceCameraAnalysisActivity::class.java)
                startActivity(intent)
            }
        }
    }
}