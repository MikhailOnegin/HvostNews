package ru.hvost.news.presentation.fragments.shop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentShopEntranceBinding
import ru.hvost.news.models.CartFooter
import ru.hvost.news.models.CartItem
import ru.hvost.news.models.Voucher
import ru.hvost.news.models.VoucherItem
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.moneyFormat

class ShopEntranceFragment : BaseFragment() {

    private lateinit var binding: FragmentShopEntranceBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var cartVM: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopEntranceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        cartVM = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        setObservers()
        cartVM.updateCartAsync(App.getInstance().userToken)
    }

    override fun onStart() {
        super.onStart()
        mainVM.updateVouchers(App.getInstance().userToken)
        setListeners()
    }

    private fun setObservers() {
        mainVM.vouchers.observe(viewLifecycleOwner) { onVouchersUpdated(it) }
        cartVM.productsCart.observe(viewLifecycleOwner) { onCartChanged(it) }
        cartVM.prizesCart.observe(viewLifecycleOwner) { onCartChanged(it) }
    }

    private fun setListeners() {
        binding.button.setOnClickListener { onGoToShopClicked() }
        binding.cartContainer.setOnClickListener {
            findNavController().navigate(R.id.action_shopEntranceFragment_to_cartFragment)
        }
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

    private fun onGoToShopClicked() {
        cartVM.resetShop()
        cartVM.loadProducts(
            App.getInstance().userToken,
            (binding.spinner.selectedItem as Voucher).voucherCode
        )
        val bundle = Bundle()
        bundle.putString(
            ShopFragment.VOUCHER_CODE,
            (binding.spinner.selectedItem as Voucher).voucherCode
        )
        findNavController().navigate(R.id.action_shopEntranceFragment_to_shopFragment, bundle)
    }

    private fun onVouchersUpdated(vouchers: List<VoucherItem>) {
        val list = vouchers.filterIsInstance(Voucher::class.java)
        binding.spinner.adapter = SpinnerAdapter(
            requireActivity(),
            "",
            list,
            Voucher::textForSpinner
        )
        //sergeev: Настроить передачу и установку в спиннер переданного промокода.
    }

}