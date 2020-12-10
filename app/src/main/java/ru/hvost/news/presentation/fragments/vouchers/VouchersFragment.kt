package ru.hvost.news.presentation.fragments.vouchers

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentVouchersBinding
import ru.hvost.news.models.*
import ru.hvost.news.presentation.adapters.recycler.VouchersAdapter
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.fragments.shop.CartViewModel
import ru.hvost.news.presentation.fragments.shop.ShopFragment
import ru.hvost.news.utils.LinearRvItemDecorations
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.OneTimeEvent
import ru.hvost.news.utils.getValue

class VouchersFragment : BaseFragment() {

    private lateinit var binding: FragmentVouchersBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var cartVM: CartViewModel
    private lateinit var loadingVouchersEventObserver: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVouchersBinding.inflate(inflater, container, false)
        initializeObservers()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        cartVM = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        setObservers()
        setRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        App.getInstance().userToken.run {
            mainVM.updateVouchers(this)
            cartVM.updateCartAsync(this)
        }
    }

    private fun setListeners() {
        binding.spinner.onItemSelectedListener = SpinnerListener(mainVM)
        binding.cartContainer.setOnClickListener {
            findNavController().navigate(R.id.action_vouchersFragment_to_cartFragment)
        }
    }

    class SpinnerListener(
        private val mainVM: MainViewModel
    ) : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selection = parent?.adapter?.getItem(position) as String?
            mainVM.vouchersFilter.value = selection
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}

    }

    private fun setObservers() {
        mainVM.loadingVouchersEvent.observe(viewLifecycleOwner, loadingVouchersEventObserver)
        mainVM.vouchers.observe(viewLifecycleOwner) { onVouchersUpdated(it) }
        mainVM.vouchersFilter.observe(viewLifecycleOwner) { onVouchersFilterChanged(it) }
        mainVM.vouchersFooterClickEvent.observe(viewLifecycleOwner, footerClickObserver)
        cartVM.productsCart.observe(viewLifecycleOwner) { onCartChanged(it) }
    }

    private val footerClickObserver = OneTimeEvent.Observer {
        findNavController().navigate(R.id.action_vouchersFragment_to_registerVoucherFragment)
    }

    @Suppress("USELESS_CAST")
    private fun onVouchersFilterChanged(filter: String?) {
        val filterAll = getString(R.string.voucherProgramAll)
        val newList = if(filter == filterAll) {
            mainVM.vouchers.value?.toMutableList() ?: mutableListOf()
        } else {
            val result = mainVM.vouchers.value?.filterIsInstance<Voucher>()
                ?.filter { it.voucherProgram == filter }
                ?.map { it as VoucherItem }
                ?.toMutableList() ?: mutableListOf()
            result.add(VoucherFooter())
            result
        }
        (binding.recyclerView.adapter as VouchersAdapter).submitList(newList)
    }

    private fun setRecyclerView() {
        binding.recyclerView.addItemDecoration(LinearRvItemDecorations(
            marginBetweenElementsDimension = R.dimen.largeMargin
        ))
        binding.recyclerView.adapter = VouchersAdapter(mainVM, onGoToShopClicked)
    }

    private fun onVouchersUpdated(vouchers: List<VoucherItem>?) {
        if(vouchers.isNullOrEmpty()) {
            findNavController().navigate(R.id.action_vouchersFragment_to_registerVoucherFragment)
        }
        (binding.recyclerView.adapter as VouchersAdapter).submitList(vouchers)
        setSpinner(vouchers)
    }

    private fun setSpinner(vouchers: List<VoucherItem>?) {
        val spinnerSet = vouchers?.filterIsInstance<Voucher>()
            ?.distinctBy { it.voucherProgram }
            ?.map { it.voucherProgram }
            ?.toMutableList() ?: mutableListOf()
        spinnerSet.add(0, getString(R.string.voucherProgramAll))
        binding.spinner.adapter = SpinnerAdapter(
            requireActivity(),
            "",
            spinnerSet,
            String::getValue
        )
    }

    private fun initializeObservers() {
        loadingVouchersEventObserver = DefaultNetworkEventObserver(
            binding.root,
            doOnError = { findNavController().popBackStack() },
            doOnFailure = { findNavController().popBackStack() }
        )
    }

    @SuppressLint("SetTextI18n")
    private fun onCartChanged(cartItems: List<CartItem>?) {
        cartItems?.run {
            binding.cartCount.text = "${if(this.isEmpty()) this.size else this.size - 1}"
        }
    }

    private val onGoToShopClicked = { voucherCode: String ->
        cartVM.resetShop()
        cartVM.loadProducts(
            App.getInstance().userToken,
            voucherCode
        )
        val bundle = Bundle()
        bundle.putString(
            ShopFragment.VOUCHER_CODE,
            voucherCode
        )
        findNavController().navigate(R.id.action_vouchersFragment_to_shopFragment, bundle)
    }

}