package com.fadlurahmanfdev.example.presentation

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.other.AppCameraSharedModel


class PreviewListBitmapFaceImageActivity : AppCompatActivity() {
    lateinit var recycleView: RecyclerView
    lateinit var tv: TextView
    lateinit var adapter: ListBitmapFaceImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preview_list_bitmap_face_image)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tv = findViewById(R.id.tv_total_bitmap_face)
        recycleView = findViewById(R.id.rv)

        adapter = ListBitmapFaceImageAdapter()
        adapter.setList(AppCameraSharedModel.bitmapImageList)

        recycleView.layoutManager = GridLayoutManager(this, 3)
        recycleView.adapter = adapter

        tv.text = "Total Bitmap Face Image: ${AppCameraSharedModel.bitmapImageList.size}"
    }
}