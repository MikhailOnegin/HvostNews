package ru.hvost.news.presentation.customviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import ru.hvost.news.R

class RoundCornersImageView(
    context: Context,
    attributeSet: AttributeSet?
) : androidx.appcompat.widget.AppCompatImageView(context, attributeSet) {

    private var radius:Float = 0f
    private var path: Path
    private var outlineColor = 0
    private val outlinePaint = Paint().apply {
        style = Paint.Style.STROKE
    }
    private var drawOutline = false
    private var outlineWidth = 0f
    private var outlinePath = Path()
    private var outlineClipPath = Path()

    init {
        setWillNotDraw(false)
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.RoundCornersImageView,
            0, 0
        ).apply {
            try {
                radius = getDimension(R.styleable.RoundCornersImageView_cornersRadius, 0f)
                outlineColor = getColor(R.styleable.RoundCornersImageView_outlineColor, outlineColor)
                drawOutline = getBoolean(R.styleable.RoundCornersImageView_drawOutline, false)
                outlineWidth = getDimension(R.styleable.RoundCornersImageView_outlineWidth, 0f)
            } finally {
                recycle()
            }
        }
        path = Path()
        outlinePaint.apply {
            color = outlineColor
            strokeWidth = outlineWidth
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        path.reset()
        path.addRoundRect(
            0f,
            0f,
            measuredWidth.toFloat(),
            measuredHeight.toFloat(),
            radius,
            radius,
            Path.Direction.CCW
        )
        val shift = outlineWidth/2f
        outlinePath.addRoundRect(
            0f + shift,
            0f + shift,
            measuredWidth.toFloat() - shift,
            measuredHeight.toFloat() - shift,
            radius,
            radius,
            Path.Direction.CCW
        )
        outlineClipPath.addRoundRect(
            0f,
            0f,
            measuredWidth.toFloat(),
            measuredHeight.toFloat(),
            radius + shift,
            radius + shift,
            Path.Direction.CCW
        )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipPath(if(drawOutline) outlineClipPath else path)
        super.onDraw(canvas)
        if(drawOutline) {
            canvas.drawPath(outlinePath, outlinePaint)
        }
    }
}