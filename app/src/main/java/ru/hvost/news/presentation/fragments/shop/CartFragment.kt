package ru.hvost.news.presentation.fragments.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentCartBinding
import ru.hvost.news.models.CartItem
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
        binding.emptyView.text.text = getString(R.string.cartEmptyViewText)
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
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun setObservers() {
        cartVM.currentCartType.observe(viewLifecycleOwner) { onCurrentCartTypeChanged(it) }
        cartVM.productsCart.observe(viewLifecycleOwner) { onProductsCartChanged(it) }
        cartVM.prizesCart.observe(viewLifecycleOwner) { onPrizesCartChanged(it) }
    }

    private fun onPrizesCartChanged(prizes: List<CartItem>?) {
        if(cartVM.currentCartType.value == CartType.Prizes){
            (binding.recyclerView.adapter as CartProductsAdapter).submitList(prizes)
            setEmptyViewVisibility(prizes)
        }
        binding.prizesCounter.text = (prizes?.size ?: 0).toString()
        if(prizes?.isNullOrEmpty() == true) binding.prizesCounter.visibility = View.GONE
        else binding.prizesCounter.visibility = View.VISIBLE
    }

    private fun onProductsCartChanged(products: List<CartItem>?) {
        if(cartVM.currentCartType.value == CartType.Products){
            (binding.recyclerView.adapter as CartProductsAdapter).submitList(products)
            setEmptyViewVisibility(products)
        }
        binding.productsCounter.text = (products?.size ?: 0).toString()
        if(products?.isNullOrEmpty() == true) binding.productsCounter.visibility = View.GONE
        else binding.productsCounter.visibility = View.VISIBLE
    }

    private fun onCurrentCartTypeChanged(cartType: CartType?) {
        cartType?.run {
            setTabs(this)
            val adapter = (binding.recyclerView.adapter as CartProductsAdapter)
            when(this){
                CartType.Products -> {
                    adapter.submitList(cartVM.productsCart.value)
                    setEmptyViewVisibility(cartVM.productsCart.value)
                }
                CartType.Prizes -> {
                    adapter.submitList(cartVM.prizesCart.value)
                    setEmptyViewVisibility(cartVM.prizesCart.value)
                }
            }
        }
    }

    private fun setEmptyViewVisibility(list: List<Any>?) {
        when(list?.isEmpty()) {
            true -> {
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.root.visibility = View.VISIBLE
            }
            false -> {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyView.root.visibility = View.GONE
            }
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