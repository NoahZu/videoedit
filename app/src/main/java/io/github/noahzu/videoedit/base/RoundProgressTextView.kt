package io.github.noahzu.videoedit.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView

class RoundProgressTextView : AppCompatTextView {
    private val paint = Paint()
    private var progress = 0f
    private lateinit var areaRect: RectF

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr)

    init {
        gravity = Gravity.CENTER
        paint.color = Color.BLUE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        areaRect = RectF(3f, 3f, w.toFloat() - 3, height.toFloat() - 3)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawArc(areaRect, 0f, 360f * progress, false, paint)
    }

    fun setProgress(newProgress: Float) {
        if (newProgress > 1f) {
            progress = 1f
        } else {
            progress = newProgress
        }
        text = "${(progress * 100).toInt()}%"
        invalidate()
    }
}