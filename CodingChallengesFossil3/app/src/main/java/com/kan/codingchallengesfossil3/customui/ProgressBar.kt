package com.kan.codingchallengesfossil3.customui

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import com.kan.codingchallengesfossil3.R
import java.lang.Integer.min

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */


class CircularProgressBar(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    companion object {
        private const val DEFAULT_MAX_VALUE = 100f
        private const val DEFAULT_START_ANGLE = 270f
        private const val DEFAULT_ANIMATION_DURATION = 1000L
    }

    // Properties
    private var progressAnimator: ValueAnimator? = null

    // View
    private var rectF = RectF()
    private var backgroundPaint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
    }
    private var foregroundPaint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    //region Attributes
    var progress: Float = 0f
        set(value) {
            field = if (progress <= progressMax) value else progressMax
            invalidate()
        }
    var progressMax: Float = DEFAULT_MAX_VALUE
        set(value) {
            field = if (field >= 0) value else DEFAULT_MAX_VALUE
            invalidate()
        }
    var progressBarWidth: Float = resources.getDimension(R.dimen.default_stroke_width)
        set(value) {
            field = value.dpToPx()
            foregroundPaint.strokeWidth = field
            requestLayout()
            invalidate()
        }
    var backgroundProgressBarWidth: Float =
        resources.getDimension(R.dimen.default_background_stroke_width)
        set(value) {
            field = value.dpToPx()
            backgroundPaint.strokeWidth = field
            requestLayout()
            invalidate()
        }
    var progressBarColor: Int = Color.BLACK
        set(value) {
            field = value
            manageColor()
            invalidate()
        }
    var progressBarColorStart: Int? = null
        set(value) {
            field = value
            manageColor()
            invalidate()
        }
    var progressBarColorEnd: Int? = null
        set(value) {
            field = value
            manageColor()
            invalidate()
        }

    var backgroundProgressBarColor: Int = Color.GRAY
        set(value) {
            field = value
            manageBackgroundProgressBarColor()
            invalidate()
        }
    var backgroundProgressBarColorStart: Int? = null
        set(value) {
            field = value
            manageBackgroundProgressBarColor()
            invalidate()
        }
    var backgroundProgressBarColorEnd: Int? = null
        set(value) {
            field = value
            manageBackgroundProgressBarColor()
            invalidate()
        }

    var startAngle: Float = DEFAULT_START_ANGLE
        set(value) {
            var angle = value + DEFAULT_START_ANGLE
            while (angle > 360) {
                angle -= 360
            }
            field = if (angle < 0) 0f else if (angle > 360) 360f else angle
            invalidate()
        }
    var progressDirection: ProgressDirection = ProgressDirection.TO_RIGHT
        set(value) {
            field = value
            invalidate()
        }

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        // Load the styled attributes and set their properties
        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, 0, 0)

        // Value
        progress = attributes.getFloat(R.styleable.CircularProgressBar_kan_progress, progress)
        progressMax =
            attributes.getFloat(R.styleable.CircularProgressBar_kan_progress_max, progressMax)

        // StrokeWidth
        progressBarWidth = attributes.getDimension(
            R.styleable.CircularProgressBar_kan_progressbar_width,
            progressBarWidth
        ).pxToDp()
        backgroundProgressBarWidth = attributes.getDimension(
            R.styleable.CircularProgressBar_kan_background_progressbar_width,
            backgroundProgressBarWidth
        ).pxToDp()

        // Color
        progressBarColor = attributes.getInt(
            R.styleable.CircularProgressBar_kan_progressbar_color,
            progressBarColor
        )
        attributes.getColor(R.styleable.CircularProgressBar_kan_progressbar_color_start, 0)
            .also { if (it != 0) progressBarColorStart = it }
        attributes.getColor(R.styleable.CircularProgressBar_kan_progressbar_color_end, 0)
            .also { if (it != 0) progressBarColorEnd = it }

        backgroundProgressBarColor = attributes.getInt(
            R.styleable.CircularProgressBar_kan_background_progressbar_color,
            backgroundProgressBarColor
        )
        attributes.getColor(
            R.styleable.CircularProgressBar_kan_background_progressbar_color_start,
            0
        )
            .also { if (it != 0) backgroundProgressBarColorStart = it }
        attributes.getColor(R.styleable.CircularProgressBar_kan_background_progressbar_color_end, 0)
            .also { if (it != 0) backgroundProgressBarColorEnd = it }

        // Progress Direction
        progressDirection = attributes.getInteger(
            R.styleable.CircularProgressBar_kan_progress_direction,
            progressDirection.value
        ).toProgressDirection()

        // Angle
        startAngle = attributes.getFloat(R.styleable.CircularProgressBar_kan_start_angle, 0f)

        attributes.recycle()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        progressAnimator?.cancel()
    }

    //region Draw Method
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        manageColor()
        manageBackgroundProgressBarColor()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawOval(rectF, backgroundPaint)

        val isToRightFromNormalMode = progressDirection.isToRight()
        val angle =
            (if (isToRightFromNormalMode) 360 else -360) * progress / 100

        canvas.drawArc(
            rectF,
            startAngle,
            angle,
            false,
            foregroundPaint
        )
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        backgroundProgressBarColor = backgroundColor
    }

    private fun manageColor() {
        foregroundPaint.shader = createLinearGradient(
            progressBarColorStart ?: progressBarColor,
            progressBarColorEnd ?: progressBarColor
        )
    }

    private fun manageBackgroundProgressBarColor() {
        backgroundPaint.shader = createLinearGradient(
            backgroundProgressBarColorStart ?: backgroundProgressBarColor,
            backgroundProgressBarColorEnd ?: backgroundProgressBarColor
        )
    }

    private fun createLinearGradient(
        startColor: Int,
        endColor: Int
    ): LinearGradient {
        val x0 = 0f
        val y0 = 0f
        val x1 = 0f
        val y1 = 0f

        return LinearGradient(x0, y0, x1, y1, startColor, endColor, Shader.TileMode.CLAMP)
    }
    //endregion


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val min = min(width, height)
        setMeasuredDimension(min, min)
        val highStroke =
            if (progressBarWidth > backgroundProgressBarWidth) progressBarWidth else backgroundProgressBarWidth
        rectF.set(
            0 + highStroke / 2,
            0 + highStroke / 2,
            min - highStroke / 2,
            min - highStroke / 2
        )
    }

    @JvmOverloads
    fun setProgressWithAnimation(
        progress: Float,
        duration: Long? = null,
        interpolator: TimeInterpolator? = null,
        startDelay: Long? = null
    ) {
        progressAnimator?.cancel()
        progressAnimator = ValueAnimator.ofFloat(
            this.progress,
            progress
        )
        duration?.also { progressAnimator?.duration = it }
        interpolator?.also { progressAnimator?.interpolator = it }
        startDelay?.also { progressAnimator?.startDelay = it }
        progressAnimator?.addUpdateListener { animation ->
            (animation.animatedValue as? Float)?.also { value ->
                this.progress = value
            }
        }
        progressAnimator?.start()
    }

    //region Extensions Utils
    private fun Float.dpToPx(): Float =
        this * Resources.getSystem().displayMetrics.density

    private fun Float.pxToDp(): Float =
        this / Resources.getSystem().displayMetrics.density

    private fun Int.toProgressDirection(): ProgressDirection =
        when (this) {
            1 -> ProgressDirection.TO_RIGHT
            2 -> ProgressDirection.TO_LEFT
            else -> throw IllegalArgumentException("This value is not supported for ProgressDirection: $this")
        }

    private fun ProgressDirection.reverse(): ProgressDirection =
        if (this.isToRight()) ProgressDirection.TO_LEFT else ProgressDirection.TO_RIGHT

    private fun ProgressDirection.isToRight(): Boolean = this == ProgressDirection.TO_RIGHT

    /**
     * ProgressDirection enum class to set the direction of the progress in progressBar
     */
    enum class ProgressDirection(val value: Int) {
        TO_RIGHT(1),
        TO_LEFT(2)
    }

}