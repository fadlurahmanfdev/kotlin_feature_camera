package com.fadlurahmanfdev.kotlin_feature_camera.presentation

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.fadlurahmanfdev.kotlin_feature_camera.R

class CircleProgressOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var attributes: TypedArray

    private var progress: Float

    var circleRadiusRatio: Float
    var centerY: Float

    init {
        attributes = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressOverlay)

        circleRadiusRatio =
            attributes.getFloat(R.styleable.CircleProgressOverlay_circleRadiusRatio, -1.0f)

        centerY =
            attributes.getDimension(R.styleable.CircleProgressOverlay_centerY, -1.0f)
//        if (centerY == -1.0f) {
//            centerY = height * 0.1f + circleRadius
//        }
//        this.centerY = centerY
//
//        setCenterYFromParameter(centerY)


        progress = attributes.getFloat(R.styleable.CircleProgressOverlay_progress, 4.0f)
        setProgress(progress)
    }

    private val overlayPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        alpha = (0.3 * 255).toInt() // 30% opacity
    }

    private val strokePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f // Stroke thickness
        color = Color.WHITE // White color for the progress arc
        isAntiAlias = true
    }

    private fun setProgress(progress: Float) {
        if (progress >= 1.0f) {
            this.progress = 4.0f
        } else if (progress < 0.0f) {
            this.progress = 0.0f
        } else {
            this.progress = progress * 4.0f
        }
        invalidate()
    }

    private fun setCenterYFromParameter(centerY: Float) {
        this.centerY = centerY
    }

    private fun getCircleRadiusBasedOnCircleRadiusRatio(
        width: Int,
        circleRadiusRatio: Float
    ): Float {
        var circleRadius = width / 2.5f
        if (circleRadiusRatio >= 1.0f) {
            circleRadius = width / 2f
        } else if (circleRadiusRatio in 0.0f..1.0f) {
            circleRadius = (width / 2f) * circleRadiusRatio
        }
        return circleRadius
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        // Step 1: Draw the semi-transparent black overlay with a transparent circle
        val centerX = width / 2f
        val circleRadius = getCircleRadiusBasedOnCircleRadiusRatio(width = width, circleRadiusRatio = circleRadiusRatio)
        if (centerY == -1.0f){
            centerY = height * 0.15f + circleRadius
        }

        val overlayPath = Path().apply {
            addOval(
                RectF(
                    centerX - circleRadius,
                    centerY - circleRadius,
                    centerX + circleRadius,
                    centerY + circleRadius
                ),
                Path.Direction.CW
            )
            addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
            fillType = Path.FillType.EVEN_ODD
        }
        canvas.drawPath(overlayPath, overlayPaint)

        // Step 2: Draw the progress arc
        val arcRect = RectF(
            centerX - circleRadius,
            centerY - circleRadius,
            centerX + circleRadius,
            centerY + circleRadius
        )

        canvas.drawArc(
            arcRect,
            -90f, // Start at the top (-90 degrees)
            progress * 90f, // Sweep angle (quarter circle)
            false,
            strokePaint
        )
    }
}