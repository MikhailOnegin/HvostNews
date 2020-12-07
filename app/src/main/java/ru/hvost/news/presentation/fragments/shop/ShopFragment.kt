package ru.hvost.news.presentation.fragments.shop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentShopBinding
import ru.hvost.news.models.CartFooter
import ru.hvost.news.models.CartItem
import ru.hvost.news.models.ShopItem
import ru.hvost.news.presentation.adapters.recycler.ShopAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.events.EventObserver
import ru.hvost.news.utils.moneyFormat

class ShopFragment : BaseFragment() {

    private lateinit var binding: FragmentShopBinding
    private lateinit var cartVM: CartViewModel
    private lateinit var adapter: ShopAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopBinding.inflate(inflater, container, false)
        if(!::adapter.isInitialized) {
            adapter = ShopAdapter(onProductClicked)
        }
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cartVM = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setObservers() {
        cartVM.apply {
            shopItems.observe(viewLifecycleOwner) { onShopItemsChanged(it) }
            productsCart.observe(viewLifecycleOwner) { onCartChanged(it) }
            showAddToCartDialogEvent.observe(viewLifecycleOwner, EventObserver(showAddToCartDialog))
        }
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.cartContainer.setOnClickListener {
            findNavController().navigate(R.id.action_shopFragment_to_cartFragment)
        }
    }

    private val showAddToCartDialog = { productId: Long ->
        val bundle = Bundle()
        bundle.putLong(AddToCartFragment.PRODUCT_ID, productId)
        findNavController().navigate(R.id.action_shopFragment_to_addToCartFragment, bundle)
    }

    @SuppressLint("SetTextI18n")
    private fun onCartChanged(cartItems: List<CartItem>?) {
        cartItems?.run {
            binding.cartCount.text = "${if(this.isEmpty()) this.size else this.size - 1}"
            try {
                val total = (cartItems.last() as CartFooter).totalCost
                binding.cartSum.text = "${moneyFormat.format(total)} \u20bd"
            } catch (exc: Exception) {
                binding.cartSum.text = "0 \u20bd"
            }
        }
    }

    private val onProductClicked = { productId: Long ->
        cartVM.showAddToCartDialog(productId)
    }

    private fun onShopItemsChanged(shopItems: List<ShopItem>?) {
        shopItems?.run {
            (binding.recyclerView.adapter as ShopAdapter).let {
                it.setFullList(this)
                it.submitList(this, isAfterChanging = true)
            }
        }
    }

    companion object {

        const val VOUCHER_CODE = "voucher_code"

    }

}