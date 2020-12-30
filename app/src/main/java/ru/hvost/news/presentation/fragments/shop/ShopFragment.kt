package ru.hvost.news.presentation.fragments.shop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentShopBinding
import ru.hvost.news.models.*
import ru.hvost.news.presentation.activities.MainActivity
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
            adapter = ShopAdapter(onProductClicked, onGoToMapClicked)
        }
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cartVM = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        setGui()
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        (requireActivity() as MainActivity).setBnvChecked(R.id.vouchersFragment)
    }

    private val onGoToMapClicked: () -> Unit = {
        requireActivity().findViewById<BottomNavigationView>(R.id.bnv)?.run {
            selectedItemId = R.id.mapFragment
        }
    }

    private fun setGui() {
        cartVM.domains.value?.firstOrNull {
            it.domainId == arguments?.getString(DOMAIN_ID)
        }?.run {
            Glide.with(binding.root).load(imageUri).into(binding.image)
            binding.text.text = domainTitle
        }
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
                //sergeev: Не сворачивать список при возврате из деталки.
                it.setFullList(this)
                it.submitList(
                    collapseListToFirstCategory(this),
                    isAfterChanging = true
                )
            }
        }
    }

    private fun collapseListToFirstCategory(shopItems: List<ShopItem>): List<ShopItem> {
        val result = mutableListOf<ShopItem>()
        val categories = shopItems.filterIsInstance<ShopCategory>().toMutableList()
        var isEnough = false
        for ((index, category) in categories.withIndex()){
            result.add(category)
            if (!isEnough &&
                shopItems.firstOrNull { it is ShopProduct && it.categoryId == category.id } != null
            ) {
                result.addAll(index + 1, shopItems.filter { it.categoryId == category.id })
                category.isExpanded = true
                isEnough = true
            } else {
                category.isExpanded = false
            }
        }
        return result
    }

    companion object {

        const val DOMAIN_ID = "domain_id"

    }

}