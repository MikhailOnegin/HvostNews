package ru.hvost.news.presentation.fragments.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentCartBinding
import ru.hvost.news.models.CartProduct
import ru.hvost.news.presentation.adapters.recycler.CartProductsAdapter
import ru.hvost.news.presentation.fragments.shop.CartViewModel.*

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartVM: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = CartProductsAdapter()
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
        setSystemUi()
    }

    private fun setSystemUi() {
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.windowBackgroundTopGradientColor)
    }

    private fun setListeners() {
        binding.products.setOnClickListener { cartVM.currentCartType.value = CartType.Products }
        binding.prizes.setOnClickListener { cartVM.currentCartType.value = CartType.Prizes }
    }

    private fun setObservers() {
        cartVM.currentCartType.observe(viewLifecycleOwner) { onCurrentCartTypeChanged(it) }
        cartVM.productsCart.observe(viewLifecycleOwner) { onProductsCartChanged(it) }
    }

    private fun onProductsCartChanged(products: List<CartProduct>?) {
        (binding.recyclerView.adapter as CartProductsAdapter).submitList(products)
    }

    private fun onCurrentCartTypeChanged(cartType: CartType?) {
        cartType?.run {
            setTabs(this)
        }
    }

    private fun setTabs(cartType: CartType) {
        val textColorPrimary = ContextCompat.getColor(requireContext(), R.color.TextColorPrimary)
        val textColorWhite = ContextCompat.getColor(requireContext(), android.R.color.white)
        when(cartType) {
            CartType.Products -> {
                binding.run {
                    products.isSelected = true
                    productsCounter.isSelected = true
                    prizes.isSelected = false
                    prizesCounter.isSelected = false
                    productsTitle.setTextColor(textColorWhite)
                    productsCounter.setTextColor(textColorWhite)
                    prizesTitle.setTextColor(textColorPrimary)
                    prizesCounter.setTextColor(textColorPrimary)
                }
            }
            CartType.Prizes -> {
                binding.run {
                    products.isSelected = false
                    productsCounter.isSelected = false
                    prizes.isSelected = true
                    prizesCounter.isSelected = true
                    productsTitle.setTextColor(textColorPrimary)
                    productsCounter.setTextColor(textColorPrimary)
                    prizesTitle.setTextColor(textColorWhite)
                    prizesCounter.setTextColor(textColorWhite)
                }
            }
        }
    }

}