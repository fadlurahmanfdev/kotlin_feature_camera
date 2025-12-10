package com.fadlurahmanfdev.lumi.presentation

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import com.fadlurahmanfdev.lumi.R

// Reference: [
// https://medium.com/@rey5137/let-s-drill-a-hole-in-your-view-e7f53fa23376
// https://stackoverflow.com/questions/18387814/drawing-on-canvas-porterduff-mode-clear-draws-black-why,
// https://www.google.com/url?sa=i&url=https%3A%2F%2Fblog.budiharso.info%2F2016%2F01%2F09%2FCreate-hole-in-android-view%2F&psig=AOvVaw1H6CAwEK6lTQtNTQQf4gRg&ust=1674365656463000&source=images&cd=vfe&ved=0CBEQjhxqFwoTCJjzlKP41_wCFQAAAAAdAAAAABAE
// ]
/**
 * Circle Progress Overlay, Usually use for selfie purpose
 * */
class CircleProgressOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var attributes: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressOverlayView)

    private var progress: Float

    // Position Related
    private var circleRadiusRatio: Float
    private var centerY: Float

    // UI Related
    private var strokeWidth: Float
    private var progressColor: Int
    private var backgroundColor: Int
    private var opacity: Float

    init {

        // Init Position
        circleRadiusRatio =
            attributes.getFloat(R.styleable.CircleProgressOverlayView_circleRadiusRatio, -1.0f)
        centerY =
            attributes.getDimension(R.styleable.CircleProgressOverlayView_centerY, -1.0f)

        // UI Related
        strokeWidth =
            attributes.getDimension(R.styleable.CircleProgressOverlayView_strokeWidth, 5.0f)
        progressColor =
            attributes.getColor(R.styleable.CircleProgressOverlayView_progressColor, Color.GREEN)
        backgroundColor =
            attributes.getColor(R.styleable.CircleProgressOverlayView_backgroundColor, Color.BLACK)
        opacity = attributes.getFloat(R.styleable.CircleProgressOverlayView_opacity, 0.3f)
        setOpacityValue(opacity)

        progress = attributes.getFloat(R.styleable.CircleProgressOverlayView_progress, 0.0f)
        setProgressValue(progress)
    }

    private val overlayPaint = Paint()

    private val strokePaint = Paint()
    private val strokePaintProgress = Paint()

    private fun setProgressValue(value: Float) {
        if (value >= 1.0f) {
            progress = 4.0f
        } else if (value < 0.0f) {
            progress = 0.0f
        } else {
            progress = value * 4.0f
        }
        invalidate()
    }

    private fun setCircleRadiusRatioValue(value: Float) {
        circleRadiusRatio = value
        invalidate()
    }

    private fun setCenterYValue(value: Float) {
        centerY = value
        invalidate()
    }

    private fun setStrokeWidthValue(value: Float) {
        strokeWidth = value
        invalidate()
    }

    private fun setProgressColor(@ColorRes value: Int) {
        progressColor = value
        invalidate()
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
        val circleRadius = getCircleRadiusBasedOnCircleRadiusRatio(
            width = width,
            circleRadiusRatio = circleRadiusRatio
        )
        if (centerY == -1.0f) {
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

        overlayPaint.apply {
            style = Paint.Style.FILL
            color = backgroundColor
            alpha = (opacity * 255).toInt() // Set Opacity in Percent
        }
        canvas.drawPath(overlayPath, overlayPaint)

        // Step 2: Draw the progress arc

        // Set Border Color of Circle Overlay
        strokePaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = this@CircleProgressOverlayView.strokeWidth // Stroke thickness
            color = Color.WHITE
            isAntiAlias = true
        }

        // Set Color of Progress
        strokePaintProgress.apply {
            style = Paint.Style.STROKE
            strokeWidth = this@CircleProgressOverlayView.strokeWidth // Stroke thickness
            color = progressColor
            isAntiAlias = true
        }

        val arcRect = RectF(
            centerX - circleRadius,
            centerY - circleRadius,
            centerX + circleRadius,
            centerY + circleRadius
        )

        canvas.drawArc(
            arcRect,
            -90f, // Start at the top (-90 degrees)
            4.0f * 90f, // Full Circle for Circle Overlay
            false,
            strokePaint
        )

        canvas.drawArc(
            arcRect,
            -90f, // Start at the top (-90 degrees)
            progress * 90f, // Sweep angle (quarter circle)
            false,
            strokePaintProgress
        )
    }
}