package io.github.noahzu.videoedit.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageView
import io.github.noahzu.videoedit.R
import io.github.noahzu.videoedit.base.DisplayUtils

class ColorRoundImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val strokePaint = Paint()
    private var color: Int = Color.WHITE
    private lateinit var contentRect: RectF
    private lateinit var strokeRect: RectF
    private var strokeColor: Int = Color.BLACK
    private val strokeWidth = DisplayUtils.dp2px(context,1f).toFloat()

    init {
        attrs?.let {
            context.obtainStyledAttributes(attrs, R.styleable.ColorRoundImageView)?.run {
                color = this.getColor(R.styleable.ColorRoundImageView_fill_color, Color.WHITE)
                strokeColor = this.getColor(R.styleable.ColorRoundImageView_stroke_color, color)
                recycle()
            }
        }
        initPaints()
    }

    private fun initPaints() {
        paint.color = color
        paint.isAntiAlias = true

        strokePaint.color = strokeColor
        strokePaint.isAntiAlias = true
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        strokeRect = RectF(0f, 0f, w.toFloat(), h.toFloat())
        contentRect = RectF(strokeWidth, strokeWidth, w.toFloat() - strokeWidth, h.toFloat() - strokeWidth)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawArc(strokeRect, 0f, 360f, true, strokePaint)
        canvas?.drawArc(contentRect, 0f, 360f, true, paint)
        super.onDraw(canvas)
    }

    fun getFillColor(): Int = color
}