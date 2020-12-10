package ru.hvost.news.presentation.fragments.invite

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.layout_prize_products.view.*
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentChoicePrizeBinding
import ru.hvost.news.models.CartFooter
import ru.hvost.news.models.CartItem
import ru.hvost.news.models.Prize
import ru.hvost.news.presentation.adapters.PrizeAdapter
import ru.hvost.news.presentation.adapters.PrizeProductsAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.fragments.shop.CartViewModel
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.moneyFormat

class ChoicePrizeFragment : BaseFragment() {

    private lateinit var binding: FragmentChoicePrizeBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var cartVM: CartViewModel

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
        cartVM = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
//        binding.toolbar.setOnMenuItemClickListener (onMenuItemClicked)
    }

    private fun setObservers() {
        mainVM.prizesState.observe(viewLifecycleOwner, { onPrizesChanged(it) })
        mainVM.prizeToCartState.observe(viewLifecycleOwner, { onCartChanged(it) })
        cartVM.apply {
            productsCart.observe(viewLifecycleOwner) { onCartCountChanged(it) }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onCartCountChanged(cartItems: List<CartItem>?) {
        cartItems?.run {
            binding.cartCount.text = "${if(this.isEmpty()) this.size else this.size - 1}"
        }
    }

    private fun onCartChanged(state: State?) {
        when (state) {
            State.SUCCESS -> {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.addedToCartSuccessfull),
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
                binding.title.text = mainVM.prizeCategoriesResponse.value?.filter {
                    it.prizeCategoryId == arguments?.getString("PRIZE_ID")
                }?.get(0)?.prizeCategoryName?.replace("Приз для", "Приз для владельцев")
                setRecyclerView()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun setRecyclerView() {
        val onActionClicked = { product: Prize -> showPrizeDetailDialog(product) }
        val adapter = PrizeAdapter(onActionClicked)
        binding.priceList.adapter = adapter
        adapter.submitList(mainVM.prizes.value)
        setDecoration()
    }

    @SuppressLint("SetTextI18n")
    private fun showPrizeDetailDialog(prize: Prize) {
        val detailDialog = BottomSheetDialog(requireContext(), R.style.popupBottomSheetDialogTheme)
        val bottomSheetBinding =
            layoutInflater.inflate(R.layout.layout_prize_products, binding.root, false)
        val productsAdapter = PrizeProductsAdapter()
        bottomSheetBinding.title.text = "Приз за " + prize.prizeCost + " баллов"
        bottomSheetBinding.products.adapter = productsAdapter
        bottomSheetBinding.setOnClickListener {
            mainVM.addPrizeToCart(prize.prizeId)
        }
        productsAdapter.submitList(prize.products)
        detailDialog.setContentView(bottomSheetBinding)
        detailDialog.setOnShowListener {
            detailDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            detailDialog.behavior.skipCollapsed = true
        }
        detailDialog.show()
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