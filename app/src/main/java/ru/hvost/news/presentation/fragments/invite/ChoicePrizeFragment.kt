package ru.hvost.news.presentation.fragments.invite

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_prizes.view.*
import kotlinx.android.synthetic.main.layout_popup_domains.view.*
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentChoicePrizeBinding
import ru.hvost.news.presentation.adapters.PopupWindowDomainAdapter
import ru.hvost.news.presentation.adapters.PrizeProductsAdapter
import ru.hvost.news.utils.enums.State

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
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        binding.back.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setObservers() {
        mainVM.prizesState.observe(viewLifecycleOwner, { onPrizesChanged(it) })
        mainVM.prizeToCartState.observe(viewLifecycleOwner, { onCartChanged(it) })
    }

    private fun onCartChanged(state: State?) {
        when (state) {
            State.SUCCESS -> {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.sendedSuccessfull),
                    Toast.LENGTH_SHORT
                ).show()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun onPrizesChanged(state: State?) {
        when (state) {
            State.SUCCESS -> {
                binding.title.text =
                    mainVM.prizes.value?.filter { it.prizeId == arguments?.getString("PRIZE_ID") }
                        ?.get(0)?.category
                setRecyclerView()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun setRecyclerView() {
        val onActionClicked = { product: String -> showPrizePopup(product) }
        val adapter = PrizeProductsAdapter(onActionClicked)
        binding.priceList.adapter = adapter
        adapter.submitList(mainVM.prizes.value?.filter { it.prizeId == arguments?.getString("PRIZE_ID") }
            ?.get(0)?.products)
        setDecoration()
    }

    private fun showPrizePopup(product: String) {
//        val view = layoutInflater.inflate(R.layout.layout_popup_prize, binding.root, false)
//        val popupWindow = PopupWindow(requireActivity())
//
//        val onActionClicked = { domain: String -> }
//        val adapter = PrizeProductsAdapter(onActionClicked)
//        view.prizeList.adapter = adapter
//        adapter.submitList(mainVM.prizes.value?.filter { it.prizeId == product } )
//        setPopupElementsDecoration(view)
//        popupWindow.contentView = view
//        popupWindow.width = binding.categoryTabs.measuredWidth
//        popupWindow.height = LinearLayout.LayoutParams.WRAP_CONTENT
//        popupWindow.setBackgroundDrawable(null)
//        popupWindow.elevation = resources.getDimension(R.dimen.listItemElevation)
//        popupWindow.isOutsideTouchable = true
//        popupWindow.showAsDropDown(binding.title)
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
                    outRect.top = elementMargin
                    outRect.bottom = elementMargin
                    outRect.left = elementMargin
                    outRect.right = elementMargin
                }
            }
        })
    }
}