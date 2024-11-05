package co.id.fadlurahmanfdev.kotlin_feature_camera.presentation

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi

// Reference: [
// https://medium.com/@rey5137/let-s-drill-a-hole-in-your-view-e7f53fa23376
// https://stackoverflow.com/questions/18387814/drawing-on-canvas-porterduff-mode-clear-draws-black-why,
// https://www.google.com/url?sa=i&url=https%3A%2F%2Fblog.budiharso.info%2F2016%2F01%2F09%2FCreate-hole-in-android-view%2F&psig=AOvVaw1H6CAwEK6lTQtNTQQf4gRg&ust=1674365656463000&source=images&cd=vfe&ved=0CBEQjhxqFwoTCJjzlKP41_wCFQAAAAAdAAAAABAE
// ]
/**
 * add background for see the result
 * */
class SelfieOverlayView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        val eraser = Paint()
        eraser.isAntiAlias = true
        eraser.style = Paint.Style.FILL
        eraser.color = Color.WHITE
        eraser.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        canvas.drawCircle(
            width.toFloat() / 2,
            (height * 0.4).toFloat(),
            (width / 2.5).toFloat(),
            eraser
        )

        val path = Path()
        val stroke = Paint()
        stroke.isAntiAlias = true
        stroke.strokeWidth = 4f
        stroke.color = Color.WHITE
        stroke.style = Paint.Style.STROKE
        path.addCircle(
            width.toFloat() / 2,
            (height * 0.4).toFloat(),
            (width / 2.5).toFloat(),
            Path.Direction.CW
        )
        canvas.drawPath(path, stroke)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}