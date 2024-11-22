package com.fadlurahmanfdev.kotlin_feature_camera.presentation

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.fadlurahmanfdev.kotlin_feature_camera.R


// Reference: [
//  https://medium.com/@rey5137/let-s-drill-a-hole-in-your-view-e7f53fa23376
//  https://stackoverflow.com/questions/18387814/drawing-on-canvas-porterduff-mode-clear-draws-black-why,
//  https://www.google.com/url?sa=i&url=https%3A%2F%2Fblog.budiharso.info%2F2016%2F01%2F09%2FCreate-hole-in-android-view%2F&psig=AOvVaw1H6CAwEK6lTQtNTQQf4gRg&ust=1674365656463000&source=images&cd=vfe&ved=0CBEQjhxqFwoTCJjzlKP41_wCFQAAAAAdAAAAABAE
// ]
class RectangleOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var attributes: TypedArray =
        context.obtainStyledAttributes(attrs, R.styleable.RectangleOverlayView)

    // Position
    private var yTop: Float

    // UI Related
    private var widthLengthRatio: Float
    private var strokeWidth: Float
    private var opacity: Float
    private var backgroundColor: Int

    init {

        // Init Position
        yTop =
            attributes.getDimension(R.styleable.RectangleOverlayView_yTop, -1.0f)
//        centerY =
//            attributes.getDimension(R.styleable.CircleProgressOverlayView_centerY, -1.0f)
//
//        // UI Related
//        strokeWidth =
//            attributes.getFloat(R.styleable.CircleProgressOverlayView_strokeWidth, 5.0f)
//        progressColor =
//            attributes.getColor(R.styleable.CircleProgressOverlayView_progressColor, Color.GREEN)
        backgroundColor =
            attributes.getColor(R.styleable.RectangleOverlayView_backgroundColor, Color.BLACK)
        opacity = attributes.getFloat(R.styleable.RectangleOverlayView_opacity, 0.3f)
        setOpacityValue(opacity)

        strokeWidth = attributes.getDimension(R.styleable.RectangleOverlayView_strokeWidth, 5f)

        widthLengthRatio =
            attributes.getFloat(R.styleable.RectangleOverlayView_widthLengthRatio, 0.9f)
        setWidthLengthRatio(widthLengthRatio)
    }

    private fun setOpacityValue(value: Float) {
        if (value >= 1.0f) {
            opacity = 1.0f
        } else if (value < 0.0f) {
            opacity = 0.0f
        } else {
            opacity = value
        }
        invalidate()
    }

    private fun setWidthLengthRatio(value: Float) {
        if (value >= 1.0f) {
            widthLengthRatio = 1.0f
        } else if (value < 0.0f) {
            widthLengthRatio = 0.0f
        } else {
            widthLengthRatio = value
        }
        invalidate()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        val viewportCornerRadius = 0

        // Set Frame Position
        if (yTop == -1.0f) {
            yTop = (0.2 * height).toFloat()
        }
        val xTopLeft = 1.0f - widthLengthRatio
        val xTopRight = widthLengthRatio
        val rectangleWidth = width * widthLengthRatio
        val rectangleHeight = (54.0f / 86.0f) * rectangleWidth
        val yBottom = yTop + rectangleHeight
        val frame = RectF(
            width * xTopLeft,
            yTop,
            width * xTopRight,
            yBottom
        )

        val overlayPath = Path().apply {
            addRect(frame, Path.Direction.CW)
            addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
            fillType = Path.FillType.EVEN_ODD
        }

        val paint = Paint().apply {
            style = Paint.Style.FILL
            color = backgroundColor
            alpha = (opacity * 255).toInt() // Set Opacity in Percent
        }
        canvas.drawPath(overlayPath, paint)

        val strokePaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            strokeWidth = this@RectangleOverlayView.strokeWidth
            style = Paint.Style.STROKE
        }
        val strokePath = Path().apply {
            addRoundRect(
                frame,
                viewportCornerRadius.toFloat(),
                viewportCornerRadius.toFloat(),
                Path.Direction.CW
            )
        }
        canvas.drawPath(strokePath, strokePaint)


        val eraser = Paint()
        eraser.isAntiAlias = true
        eraser.style = Paint.Style.FILL
        eraser.color = Color.BLACK
        eraser.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        canvas.drawPath(strokePath, eraser)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}