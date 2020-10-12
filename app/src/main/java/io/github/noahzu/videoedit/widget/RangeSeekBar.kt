package com.haoqi.teacher.videoedit.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.FloatRange
import androidx.core.content.res.ResourcesCompat
import io.github.noahzu.videoedit.R
import io.github.noahzu.videoedit.utils.SizeUtils

/**
 * 可以选择范围的seekbar
 */
class RangeSeekBar : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var leftThumb: RangeSeekBarThumb
    private lateinit var rightThumb: RangeSeekBarThumb
    private lateinit var bottomLineRect: Rect
    private lateinit var topLineRect: Rect
    private lateinit var contentRect: Rect
    private lateinit var indicatorRect: Rect

    private val lineColor = Color.parseColor("#3A78E5")
    private val indicatorWidth = SizeUtils.dp2px(3f)

    private val thumbDefaultWidth = 45
    private val lineHeight = 13
    private val paint: Paint = Paint()

    private var latestPositionX = 0f
    private var selectedThumb: RangeSeekBarThumb? = null
    private var maxDistance: Float = 0f
    private var isDrawIndicator = false
    var maxProgress: Float = 0f

    var onRangeSeekBarChangeListener: OnRangeSeekBarChangeListener? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        maxDistance = maxProgress * w
        initThumb()
    }

    private fun initThumb() {
        val leftDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_range_left, null)
        val rightDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_range_right, null)

        val leftBounds = Rect(0, paddingTop, thumbDefaultWidth, height - paddingBottom)
        val rightBounds = Rect(width - thumbDefaultWidth, paddingTop, width, height - paddingBottom)
        leftDrawable?.setBounds(leftBounds)
        rightDrawable?.setBounds(rightBounds)

        leftThumb = RangeSeekBarThumb(leftBounds, leftDrawable!!)
        rightThumb = RangeSeekBarThumb(rightBounds, rightDrawable!!)
        bottomLineRect = Rect()
        topLineRect = Rect()
        contentRect = Rect()
        indicatorRect = Rect(leftThumb.position.right, 0, leftThumb.position.right + indicatorWidth, height)

        calculateAllRect()
    }

    private fun calculateAllRect() {
        bottomLineRect.set(leftThumb.position.right, paddingTop, rightThumb.position.left, paddingTop + lineHeight)
        topLineRect.set(
            leftThumb.position.right,
            height - lineHeight - paddingBottom,
            rightThumb.position.left,
            height - paddingBottom
        )
        contentRect.set(
            leftThumb.position.right,
            lineHeight + paddingTop,
            rightThumb.position.left,
            height - lineHeight - paddingBottom
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawThumb(canvas)
        drawLineAndContent(canvas)
        drawIndicator(canvas)
    }

    private fun drawIndicator(canvas: Canvas?) {
        if (!isDrawIndicator) {
            return
        }
        canvas?.run {
            //            paint.setARGB(0xFF, 0x3A, 0x78, 0xE5)
            paint.color = lineColor
            paint.alpha = 0xFF
            canvas.drawRect(indicatorRect, paint)
        }
    }

    private fun drawLineAndContent(canvas: Canvas?) {
        canvas?.run {
            //            paint.setARGB(0xFF, 0x3A, 0x78, 0xE5)
            paint.color = lineColor
            paint.alpha = 0xFF
            canvas.drawRect(topLineRect, paint)
            canvas.drawRect(bottomLineRect, paint)

            paint.setARGB(0x66, 0x00, 0x00, 0x00)
            paint.alpha = 0x66
            canvas.drawRect(contentRect, paint)
        }
    }

    private fun drawThumb(canvas: Canvas?) {
        canvas?.run {
            leftThumb.updateBounds()
            leftThumb.drawable.draw(this)
            rightThumb.updateBounds()
            rightThumb.drawable.draw(this)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.isTouchArea(leftThumb.position)) {
                    latestPositionX = event.x
                    selectedThumb = leftThumb
                    onRangeSeekBarChangeListener?.onStartTrackingTouch(this)
                    isDrawIndicator = false
                    return true
                }

                if (event.isTouchArea(rightThumb.position)) {
                    latestPositionX = event.x
                    selectedThumb = rightThumb
                    onRangeSeekBarChangeListener?.onStartTrackingTouch(this)
                    return true
                }

                return super.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                selectedThumb?.let {
                    moveThumb(it, event.x - latestPositionX)
                    latestPositionX = event.x
                    onRangeSeekBarChangeListener?.onProgressChanged(
                        this@RangeSeekBar,
                        getRightPosition() - getLeftPosition(),
                        if (it == leftThumb) getLeftPosition() else getRightPosition()
                    )
                }
            }
            MotionEvent.ACTION_UP -> {
                if (selectedThumb != null) {
                    onRangeSeekBarChangeListener?.onStopTrackingTouch(this, getLeftPosition(), getRightPosition())
                    isDrawIndicator = true
                }
                latestPositionX = 0f
                selectedThumb = null
            }
        }
        return super.onTouchEvent(event)
    }

    private fun moveThumb(thumb: RangeSeekBarThumb, x: Float) {

        if (thumb.position.left + x < 0) {
            return
        }
        if (thumb.position.right + x > width) {
            return
        }
        if (thumb == leftThumb && thumb.position.right + x + maxDistance > rightThumb.position.left) {
            return
        }
        if (thumb == rightThumb && thumb.position.left + x - maxDistance < leftThumb.position.right) {
            return
        }

        thumb.position.offset(x.toInt(), 0)
        calculateAllRect()
        invalidate()
    }

    fun getLeftPosition(): Float {
        return (leftThumb.position.left).toFloat() / width
    }

    fun getRightPosition(): Float {
        return (rightThumb.position.right).toFloat() / width
    }

    fun reset() {
        val leftBounds = Rect(0, 0, thumbDefaultWidth, height)
        val rightBounds = Rect(width - thumbDefaultWidth, 0, width, height)
        leftThumb.position.set(leftBounds)
        rightThumb.position.set(rightBounds)
        isDrawIndicator = false
        calculateAllRect()
        invalidate()
        isDrawIndicator = true
        onRangeSeekBarChangeListener?.onStopTrackingTouch(this, getLeftPosition(), getRightPosition())
    }

    fun updateIndicator(@FloatRange(from = 0.0, to = 1.0) progress: Float) {
        val w = contentRect.width() * progress

        indicatorRect.left = (contentRect.left + w - indicatorWidth / 2).toInt()
        indicatorRect.right = (contentRect.left + w + indicatorWidth / 2).toInt()
        invalidate()
    }

    interface OnRangeSeekBarChangeListener {
        fun onProgressChanged(seekBar: RangeSeekBar, cuttedProgress: Float, slideProgress: Float)
        fun onStartTrackingTouch(seekBar: RangeSeekBar)
        fun onStopTrackingTouch(seekBar: RangeSeekBar, leftProgress: Float, rightProgress: Float)
    }
}


fun MotionEvent.isTouchArea(area: Rect): Boolean {
    return area.contains(x.toInt(), y.toInt())
}


class RangeSeekBarThumb(val position: Rect, val drawable: Drawable) {
    fun updateBounds() {
        drawable.bounds = position
    }
}