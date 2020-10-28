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

    init {
        setWillNotDraw(false)
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.RoundCornersImageView,
            0, 0
        ).apply {
            try {
                radius = getDimension(R.styleable.RoundCornersImageView_cornersRadius, 0f)
            } finally {
                recycle()
            }
        }
        path = Path()
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
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipPath(path)
        super.onDraw(canvas)
    }
}