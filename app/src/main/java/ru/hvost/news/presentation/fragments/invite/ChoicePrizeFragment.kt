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
import ru.hvost.news.App
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
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.moneyFormat

class ChoicePrizeFragment : BaseFragment() {

    private lateinit var binding: FragmentChoicePrizeBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var cartVM: CartViewModel
    private lateinit var onPrizesLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onCartChangeLoadingEvent: DefaultNetworkEventObserver
    private lateinit var detailDialog: BottomSheetDialog
    private lateinit var bottomSheetBinding: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChoicePrizeBinding.inflate(inflater, container, false)
        detailDialog =
            BottomSheetDialog(requireActivity(), R.style.popupBottomSheetDialogTheme)
        detailDialog.dismissWithAnimation = true
        bottomSheetBinding =
            layoutInflater.inflate(R.layout.layout_prize_products, binding.root, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        cartVM = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        initializeObservers()
        setListeners()
        setObservers()
    }

    private fun initializeObservers() {
        onPrizesLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = {
                binding.title.text = mainVM.prizeCategoriesResponse.value?.filter {
                    it.prizeCategoryId == arguments?.getString("PRIZE_ID")
                }?.get(0)?.prizeCategoryName?.replace("Приз для", "Приз для владельцев")
                setRecyclerView()
            }
        )
        onCartChangeLoadingEvent = DefaultNetworkEventObserver(
            bottomSheetBinding.container,
            doOnSuccess = {
                detailDialog.dismiss()
                cartVM.updateCartAsync(App.getInstance().userToken)
                createSnackbar(
                    binding.root,
                    getString(R.string.addedToCartSuccessfull)
                ).show()
            }
        )
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.cartCount.setOnClickListener { findNavController().navigate(R.id.action_choicePrizeFragment_to_cartFragment) }
    }

    private fun setObservers() {
        mainVM.prizesLoadingEvent.observe(viewLifecycleOwner, onPrizesLoadingEvent)
        mainVM.changeCartLoadingEvent.observe(viewLifecycleOwner, onCartChangeLoadingEvent)
        cartVM.cartCounter.observe(viewLifecycleOwner) { onCartCounterChanged(it) }
    }

    private fun onCartCounterChanged(cartItems: Int?) {
        cartItems?.run {
            if (cartItems == 0)
                binding.cartCount.text = ""
            else
                binding.cartCount.text = "$cartItems"
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
        val productsAdapter = PrizeProductsAdapter()
        bottomSheetBinding.title.text = "Приз за " + prize.prizeCost + " баллов"
        bottomSheetBinding.products.adapter = productsAdapter
        bottomSheetBinding.toCart.setOnClickListener {
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