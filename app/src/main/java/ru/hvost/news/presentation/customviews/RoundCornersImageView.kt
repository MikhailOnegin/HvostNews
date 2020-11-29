package ru.hvost.news.presentation.customviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.content.ContextCompat
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
    private val colorBlack = ContextCompat.getColor(context, android.R.color.black)
    private var shadowColor = colorBlack
    private val shadowOffset = 15f
    private var dropShadow = false
    private var insetForShadow = resources.getDimension(R.dimen.imageShadowInset)
    private var widthScale = 1f
    private var heightScale = 1f

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
                shadowColor = getColor(R.styleable.RoundCornersImageView_dropShadowColor, colorBlack)
                drawOutline = getBoolean(R.styleable.RoundCornersImageView_drawOutline, false)
                outlineWidth = getDimension(R.styleable.RoundCornersImageView_outlineWidth, 0f)
                dropShadow = getBoolean(R.styleable.RoundCornersImageView_dropShadow, false)
            } finally {
                recycle()
            }
        }
        path = Path()
        outlinePaint.apply {
            color = outlineColor
            strokeWidth = outlineWidth
            if(dropShadow) setShadowLayer(20f,shadowOffset, shadowOffset, shadowColor)
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
        widthScale = (measuredWidth.toFloat() - 2f * insetForShadow)/measuredWidth
        heightScale = (measuredHeight.toFloat() - 2f * insetForShadow)/measuredHeight
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        if(dropShadow)
            canvas.scale(widthScale, heightScale, width/2f, height/2f)
        canvas.clipPath(if(drawOutline) outlineClipPath else path)
        super.onDraw(canvas)
        canvas.restore()
        if(dropShadow)
            canvas.scale(widthScale, heightScale, width/2f, height/2f)
        if(drawOutline) {
            canvas.drawPath(outlinePath, outlinePaint)
        }
    }

}