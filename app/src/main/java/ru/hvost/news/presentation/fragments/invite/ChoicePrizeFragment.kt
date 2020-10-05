package ru.hvost.news.presentation.fragments.invite

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentChoicePrizeBinding
import ru.hvost.news.databinding.FragmentPrizesBinding
import ru.hvost.news.presentation.adapters.PrizeAdapter
import ru.hvost.news.presentation.adapters.PrizePriceAdapter

class ChoicePrizeFragment : Fragment() {

    private lateinit var binding: FragmentChoicePrizeBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChoicePrizeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setRecyclerView()
    }

    private fun setRecyclerView() {
        val onActionClicked = { prize: Long ->
            val bundle = Bundle()
            bundle.putLong("ID", prize)
            findNavController().navigate(R.id.action_domainFragment_to_subDomainFragment, bundle)
        }
        val adapter = PrizePriceAdapter(onActionClicked)
        binding.priceList.adapter = adapter
        adapter.submitList(mainVM.testPrice)
        setDecoration()
    }

    private fun setDecoration() {
        binding.priceList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                val elementMargin =
                    view.context?.resources?.getDimension(R.dimen.smallMargin)?.toInt() ?: 0
                parent.adapter.run {
                    if (position % 2 == 0) {
                        outRect.top = elementMargin
                        outRect.bottom = elementMargin
                        outRect.left = elementMargin
                        outRect.right = elementMargin

                    } else {
                        outRect.top = elementMargin
                        outRect.bottom = elementMargin
                        outRect.left = 0
                        outRect.right = elementMargin
                    }
                }
            }
        })
    }
}