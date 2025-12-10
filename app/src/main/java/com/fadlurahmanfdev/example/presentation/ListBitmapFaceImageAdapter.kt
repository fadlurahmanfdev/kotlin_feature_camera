package com.fadlurahmanfdev.example.presentation

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fadlurahmanfdev.example.R

class ListBitmapFaceImageAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var bitmapList:ArrayList<Bitmap> = arrayListOf()

    fun setList(bitmapList:ArrayList<Bitmap>){
        this.bitmapList.clear()
        this.bitmapList.addAll(bitmapList)
        notifyItemRangeInserted(0, bitmapList.size)
    }

    inner class ViewHolder(view:View): RecyclerView.ViewHolder(view){
        val iv:ImageView = view.findViewById(R.id.iv)

        fun setImage(bitmap: Bitmap){
            iv.setImageBitmap(bitmap)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bitmap_face, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bitmapList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).setImage(bitmapList[position])
    }
}