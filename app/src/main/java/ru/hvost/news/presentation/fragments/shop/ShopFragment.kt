package ru.hvost.news.presentation.fragments.shop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.databinding.FragmentShopBinding
import ru.hvost.news.models.CartFooter
import ru.hvost.news.models.CartItem
import ru.hvost.news.models.ShopItem
import ru.hvost.news.presentation.adapters.recycler.ShopAdapter
import ru.hvost.news.utils.moneyFormat

class ShopFragment : Fragment() {

    private lateinit var binding: FragmentShopBinding
    private lateinit var cartVM: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cartVM = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        cartVM.loadProducts(
            App.getInstance().userToken,
            arguments?.getString(VOUCHER_CODE, "")
        )
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        setSystemUiVisibility()
        setListeners()
    }

    override fun onStop() {
        super.onStop()
        cartVM.resetShop()
    }

    private fun setObservers() {
        cartVM.shopItems.observe(viewLifecycleOwner) { onShopItemsChanged(it) }
        cartVM.productsCart.observe(viewLifecycleOwner) { onCartChanged(it) }
        cartVM.prizesCart.observe(viewLifecycleOwner) { onCartChanged(it) }
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    @SuppressLint("SetTextI18n")
    private fun onCartChanged(cartItems: List<CartItem>?) {
        cartItems?.run {
            binding.cartCount.text = "${this.size}"
            try {
                val total = (cartItems.last() as CartFooter).totalCost
                binding.cartSum.text = "${moneyFormat.format(total)} \u20bd"
            } catch (exc: Exception) {
                binding.cartSum.text = "0 \u20bd"
            }
        }
    }

    private fun onShopItemsChanged(shopItems: List<ShopItem>?) {
        shopItems?.run {
            val adapter = ShopAdapter(this)
            adapter.submitList(this)
            binding.recyclerView.adapter = adapter
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InlinedApi")
    private fun setSystemUiVisibility() {
        requireActivity().window.run {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }

    companion object {

        const val VOUCHER_CODE = "voucher_code"

    }

}