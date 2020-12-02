package ru.hvost.news.presentation.fragments.shop

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.parseAsHtml
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentAddProductBinding
import ru.hvost.news.models.ShopProduct
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.moneyFormat
import java.lang.Exception

class AddToCartFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddProductBinding
    private lateinit var cartVM: CartViewModel
    private lateinit var cartChangingObserver: DefaultNetworkEventObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)
        initializeObservers()
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cartVM = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        val product = cartVM.shopItems.value?.firstOrNull{ it.id == arguments?.getLong(PRODUCT_ID) }
        if(product == null) {
            Toast.makeText(
                requireActivity(),
                getString(R.string.productNotFoundToast),
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
        } else {
            try {
                inflateData(product as ShopProduct)
            } catch (exc: Exception) {}
        }
        setObservers()
    }

    private fun initializeObservers() {
        cartChangingObserver = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { dismiss() }
        )
    }

    private fun setObservers() {
        cartVM.cartChangingEvent.observe(viewLifecycleOwner, cartChangingObserver)
    }

    @SuppressLint("SetTextI18n")
    private fun inflateData(product: ShopProduct) {
        binding.apply {
            Glide.with(binding.root).load(product.imageUri).into(image)
            title.text = product.description.parseAsHtml()
            article.text = product.article
            brand.text = product.brand
            manufacturer.text = product.manufacturer
            commonCategory.text = product.`class`
            buttonToDetailsScreen.setOnClickListener { navigateToDetailsScreen(product.id) }
            buttonAddToCart.setOnClickListener { addProductToCart(product) }
            price.text = "${moneyFormat.format(product.price)} \u20bd"
            minus.setOnClickListener { onMinusButtonClicked(product) }
            plus.setOnClickListener { onPlusButtonClicked(product) }
        }
        setControlsContainer(product)
    }

    private fun onMinusButtonClicked(product: ShopProduct) {
        if(productsCount > 1){
            productsCount--
            setControlsContainer(product)
        }
    }

    private fun onPlusButtonClicked(product: ShopProduct) {
        productsCount++
        setControlsContainer(product)
    }

    private var productsCount = 1

    @SuppressLint("SetTextI18n")
    private fun setControlsContainer(product: ShopProduct) {
        binding.apply {
            count.text = "$productsCount"
            cost.text = "${moneyFormat.format(productsCount * product.price)} \u20bd"
        }
    }

    private fun addProductToCart(product: ShopProduct) {
        try {
            cartVM.addToCart(product.productId.toLong(), productsCount)
        } catch (exc: Exception) {}
    }

    private fun navigateToDetailsScreen(productId: Long) {
        val bundle = Bundle()
        bundle.putLong(PRODUCT_ID, productId)
        findNavController().navigate(R.id.action_addToCartFragment_to_productFragment)
    }

    companion object {

        const val PRODUCT_ID = "product_id"

    }

}