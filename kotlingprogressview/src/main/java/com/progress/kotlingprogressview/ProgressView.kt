package com.progress.kotlingprogressview

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator

enum class Direction(val value: Int) {
    fromLeft(0),
    fromRight(1);
    companion object {
        internal fun getByValue(value: Int): Direction {
            return when (value) {
                Direction.fromLeft.value -> Direction.fromLeft
                Direction.fromRight.value -> Direction.fromRight
                else -> throw IllegalArgumentException()
            }
        }
    }
}

internal enum class Shape(val value: Int) {
    line(0),
    arc(1),
    circle(2);
    companion object {
        internal fun getByValue(value: Int): Shape {
            return when (value) {
                Shape.line.value -> Shape.line
                Shape.arc.value -> Shape.arc
                Shape.circle.value -> Shape.circle
                else -> throw IllegalArgumentException()
            }
        }
    }
}

open class ProgressView(contex: Context, attributeSet: AttributeSet) : View(contex, attributeSet) {

    companion object {
        private const val PROPERTY_PROGRESS = "ProgressValue"
    }

    var progress = 0F
        set(value) {
            setProgressWithAnimation(value)
            field = value
        }
    var progressDirection = Direction.fromLeft
        set(value) {
            if (field != value) {
                gradientColors?.reverse()
                updateShader()
            }
            field = value
            invalidate()
        }
    var animationDuration = 1500
    var backgroundWidth = 0F
        set(value) {
            field = value
            backgroundPaint.strokeWidth = value
            invalidate()
        }
    var progressWidth = 0F
        set(value) {
            field = value
            progressPaint.strokeWidth = value
            invalidate()
        }

    @ColorInt
    var progressBackgroundColor = Color.BLACK
        set(value) {
            field = value
            backgroundPaint.color = value
            invalidate()
        }
    @ColorInt
    var progressColor: Int = Color.BLACK
        set(value) {
            field = value
            progressPaint.color = value
            invalidate()
        }

    private val shape: Shape
    private var gradientColors: IntArray? = null
    private var animatedProgress = 0F
    private val rectF = RectF()
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val typedArray = contex.theme.obtainStyledAttributes(attributeSet, R.styleable.ProgressView, 0, 0)
        val directionValue = typedArray.getInt(R.styleable.ProgressView_pvDirection, Direction.fromLeft.value)
        progressDirection = Direction.getByValue(directionValue)
        val shapeValue = typedArray.getInt(R.styleable.ProgressView_pvShape, Shape.arc.value)
        shape = Shape.getByValue(shapeValue)
        progress = typedArray.getFloat(R.styleable.ProgressView_pvProgress, progress)
        backgroundWidth = typedArray.getDimension(R.styleable.ProgressView_pvBackgroundWidth, 2.toPx())
        progressWidth = typedArray.getDimension(R.styleable.ProgressView_pvProgressWidth, 10.toPx())
        progressBackgroundColor = typedArray.getColor(R.styleable.ProgressView_pvBackgroundColor, Color.BLACK)
        progressColor = typedArray.getColor(R.styleable.ProgressView_pvProgressColor, Color.RED)
        animationDuration = typedArray.getInt(R.styleable.ProgressView_pvAnimateDuration, animationDuration)
        typedArray.recycle()

        backgroundPaint.style = Paint.Style.STROKE

        progressPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        when (shape) {
            Shape.line -> drawLine(canvas)
            Shape.arc -> drawArc(canvas)
            Shape.circle -> drawCircle(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val highStroke = Math.max(progressWidth, backgroundWidth)

        val min = Math.min(width, height)
        rectF.set(highStroke / 2, highStroke / 2, min - highStroke / 2, min - highStroke / 2)

        when (shape) {
            Shape.line -> {
                rectF.set(0F, highStroke / 2, min.toFloat(), highStroke / 2)
                setMeasuredDimension(width, Math.min(highStroke, height.toFloat()).toInt())
            }
            Shape.arc -> {
                setMeasuredDimension(min, min / 2)
            }
            Shape.circle -> {
                setMeasuredDimension(min, min)
            }
        }
        updateShader()
    }

    private fun setProgressWithAnimation(progress: Float) {
        val propertyValuesHolder = PropertyValuesHolder.ofFloat(PROPERTY_PROGRESS, animatedProgress, progress)
        val animator = ValueAnimator()
        animator.setValues(propertyValuesHolder)
        animator.interpolator = DecelerateInterpolator()
        animator.duration = animationDuration.toLong()
        animator.addUpdateListener {
            animatedProgress = it.getAnimatedValue(PROPERTY_PROGRESS) as Float
            invalidate()
        }
        animator.start()
    }

    fun applyGradient(vararg colors: Int) {
        gradientColors = colors
        if (progressDirection == Direction.fromRight) {
            gradientColors?.reverse()
        }
        updateShader()
    }

    private fun drawArc(canvas: Canvas?) {
        drawArc(canvas, 180F)
    }

    private fun drawCircle(canvas: Canvas?) {
        drawArc(canvas, 360F)
    }

    private fun drawLine(canvas: Canvas?) {
        if (canvas == null) {
            return
        }
        val y = canvas.height / 2F
        val width: Float = canvas.width.toFloat()
        canvas.drawLine(0F, y, width, y, backgroundPaint)
        val progressWidth = animatedProgress * width
        if (progressDirection == Direction.fromRight) {
            canvas.drawLine(width, y, width - progressWidth, y, progressPaint)
        } else {
            canvas.drawLine(0F, y, progressWidth, y, progressPaint)
        }
    }

    private fun drawArc(canvas: Canvas?, sweepAngle: Float) {
        var startAngle = 180F
        canvas?.drawArc(rectF, startAngle, sweepAngle, false, backgroundPaint)
        var progressSweepAngle = animatedProgress * sweepAngle
        if (progressDirection == Direction.fromRight) {
            startAngle = 0F
            progressSweepAngle = -progressSweepAngle
        }
        canvas?.drawArc(rectF, startAngle, progressSweepAngle, false, progressPaint)
    }

    private fun updateShader() {
        if (gradientColors == null) {
            progressPaint.shader = null
            invalidate()
            return
        }

        val shader = when (shape) {
            Shape.line -> LinearGradient(
                0F,
                0F,
                measuredWidth.toFloat(),
                0F,
                gradientColors,
                null,
                android.graphics.Shader.TileMode.CLAMP
            )
            Shape.arc -> LinearGradient(
                0F,
                measuredWidth / 2F,
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                gradientColors,
                null,
                android.graphics.Shader.TileMode.CLAMP
            )
            Shape.circle -> LinearGradient(
                0F,
                0F,
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                gradientColors,
                null,
                android.graphics.Shader.TileMode.CLAMP
            )
        }
        progressPaint.shader = shader
        invalidate()
    }
}

fun Int.toPx(): Float = this * Resources.getSystem().displayMetrics.density
