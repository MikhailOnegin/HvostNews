package ru.hvost.news.presentation.fragments.shop

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.parseAsHtml
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentProductBinding
import ru.hvost.news.models.CartFooter
import ru.hvost.news.models.CartItem
import ru.hvost.news.models.ShopProduct
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.moneyFormat

class ProductFragment : BaseFragment(){

    private lateinit var binding: FragmentProductBinding
    private lateinit var cartVM: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cartVM = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        val product = cartVM.shopItems.value?.firstOrNull{ it.id == arguments?.getLong(
            AddToCartFragment.PRODUCT_ID
        ) }
        if(product == null) {
            Toast.makeText(
                requireActivity(),
                getString(R.string.productNotFoundToast),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().popBackStack()
        } else {
            try {
                inflateData(product as ShopProduct)
            } catch (exc: java.lang.Exception) {}
        }
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setObservers() {
        cartVM.apply {
            productsCart.observe(viewLifecycleOwner) { onCartChanged(it) }
            cartCounter.observe(viewLifecycleOwner) { onCartCounterChanged(it) }
        }
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.cartContainer.setOnClickListener {
            findNavController().navigate(R.id.action_productFragment_to_cartFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun inflateData(product: ShopProduct) {
        binding.apply {
            Glide.with(binding.root).load(product.imageUri).into(image)
            title.text = product.title.parseAsHtml()
            article.text = "${getString(R.string.productArticleTitle)} ${product.article}"
            brand.text = product.brand
            manufacturer.text = product.manufacturer
            commonCategory.text = product.`class`
            price.text = "${moneyFormat.format(product.price)} \u20bd"
            oldPrice.text = "${moneyFormat.format(product.oldPrice)} \u20bd"
            oldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            weight.text = product.weight
            barcode.text = product.barcode
            regime.text = product.regime
            buttonAddToCart.setOnClickListener { addProductToCart(product) }
            tabs.addOnTabSelectedListener(OnTabSelectedListener(product, binding))
            tabs.getTabAt(0)?.select()
        }
    }

    class OnTabSelectedListener(
        private val product: ShopProduct,
        private val binding: FragmentProductBinding
    ) : TabLayout.OnTabSelectedListener {

        private val stub = App.getInstance().getString(R.string.stub)

        override fun onTabSelected(tab: TabLayout.Tab?) {
             val text = when(tab?.position) {
                0 -> product.description.parseAsHtml()
                1 -> product.composition.parseAsHtml()
                2 -> product.ingredients.parseAsHtml()
                3 -> product.contraindications.parseAsHtml()
                else -> stub.parseAsHtml()
            }
            binding.text.text = text
            if(binding.text.text.isNullOrBlank()) binding.text.text = stub
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) { }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            onTabSelected(tab)
        }

    }

    private fun addProductToCart(product: ShopProduct) {
        try {
            cartVM.addToCart(product.productId, 1)
        } catch (exc: java.lang.Exception) {}
    }

    @SuppressLint("SetTextI18n")
    private fun onCartChanged(cartItems: List<CartItem>?) {
        cartItems?.run {
            try {
                val total = (cartItems.last() as CartFooter).totalCost
                binding.cartSum.text = "${moneyFormat.format(total)} \u20bd"
            } catch (exc: Exception) {
                binding.cartSum.text = "0 \u20bd"
            }
        }
    }

    private fun onCartCounterChanged(cartItems: Int?) {
        cartItems?.run {
            binding.cartCount.text = "$cartItems"
        }
    }

}