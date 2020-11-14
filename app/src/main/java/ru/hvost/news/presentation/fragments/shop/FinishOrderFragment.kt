package ru.hvost.news.presentation.fragments.shop

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentFinishOrderBinding
import ru.hvost.news.utils.showNotReadyToast
import java.lang.StringBuilder

class FinishOrderFragment : Fragment() {

    private lateinit var binding: FragmentFinishOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFinishOrderBinding.inflate(inflater, container, false)
        setTitle()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setListeners() {
        binding.toOrders.setOnClickListener { onToOrdersClicked() }
        binding.toFeed.setOnClickListener { onToFeedClicked() }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun setTitle() {
        val orderNumber = arguments?.getInt(ORDER_NUMBER) ?: 0
        val builder = StringBuilder()
        builder.append(getString(R.string.finishOrderTitle1))
        val startIndex = builder.length + 1
        builder.append(" №$orderNumber ")
        val endIndex = builder.length - 1
        builder.append(getString(R.string.finishOrderTitle2))
        val span = ForegroundColorSpan(
            ContextCompat.getColor(requireActivity(), R.color.finishOrderTextSpanColor))
        val spanString = SpannableStringBuilder(builder.toString())
        spanString.setSpan(span, startIndex, endIndex, 0)
        binding.title.text = spanString
    }

    private fun onToOrdersClicked() {
        showNotReadyToast()
        //sergeev: Переход к заказам.
    }

    private fun onToFeedClicked() {
        showNotReadyToast()
        //sergeev: Переход в ленту.
    }

    companion object {

        const val ORDER_NUMBER = "order_number"

    }

}