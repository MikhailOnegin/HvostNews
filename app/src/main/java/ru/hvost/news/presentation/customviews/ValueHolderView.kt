package ru.hvost.news.presentation.customviews

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import ru.hvost.news.R
import ru.hvost.news.databinding.ValueHolderViewBinding

class ValueHolderView(
    context: Context,
    attributeSet: AttributeSet? = null
) : ConstraintLayout(context, attributeSet) {

    private val binding: ValueHolderViewBinding = ValueHolderViewBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    init {
        layoutTransition = LayoutTransition()
        setWillNotDraw(false)
        setBackgroundResource(R.drawable.ripple_spinner_view)
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.ValueHolderView,
            0, 0
        ).apply {
            try {
                val iconResource = getResourceId(
                    R.styleable.ValueHolderView_iconResource,
                    R.drawable.ic_chevron_down
                )
                binding.chevron.setImageResource(iconResource)
                val hint = getString(
                    R.styleable.ValueHolderView_holderHint
                )
                binding.hint.text = hint ?: context.getString(R.string.stub)
                val showIcon = getBoolean(R.styleable.ValueHolderView_showIcon, true)
                if(showIcon) binding.chevron.visibility = View.VISIBLE
                else binding.chevron.visibility = View.GONE
            } finally {
                recycle()
            }
        }
    }

    fun setSelection(selection: String?) {
        binding.selection.text = selection
    }

    fun setHint(hint: String) {
        binding.hint.apply {
            text = hint
            visibility = if(hint.isBlank()) View.GONE else View.VISIBLE
        }
    }

    @Suppress("unused")
    fun setIcon(iconResource: Int) {
        binding.chevron.setImageResource(iconResource)
    }

}