package ru.hvost.news.presentation.fragments.shop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentShopDomainsBinding
import ru.hvost.news.models.CartFooter
import ru.hvost.news.models.CartItem
import ru.hvost.news.models.ShopDomain
import ru.hvost.news.presentation.adapters.recycler.ShopDomainsAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.LinearRvItemDecorations
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent
import ru.hvost.news.utils.moneyFormat

class ShopDomainsFragment : BaseFragment() {

    private lateinit var binding: FragmentShopDomainsBinding
    private lateinit var cartVM: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartVM = ViewModelProvider(requireActivity())[CartViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopDomainsBinding.inflate(inflater, container, false)
        setRecyclerView()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setObservers()
        setListeners()
    }

    private fun setObservers() {
        cartVM.apply {
            productsCart.observe(viewLifecycleOwner) { onCartChanged(it) }
            domains.observe(viewLifecycleOwner) { onDomainsChanged(it) }
            productsLoadingEvent.observe(viewLifecycleOwner) { onLoadingEvent(it) }
        }
    }

    private fun onLoadingEvent(event: NetworkEvent<State>?) {
        event?.getContentIfNotHandled()?.run {
            when (this) {
                State.LOADING -> {
                    binding.recyclerView.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.cartContainer.setOnClickListener {
            findNavController().navigate(R.id.action_shopDomainsFragment_to_cartFragment)
        }
    }

    private fun setRecyclerView() {
        binding.recyclerView.addItemDecoration(LinearRvItemDecorations(
            R.dimen.largeMargin,
            R.dimen.largeMargin
        ))
        binding.recyclerView.adapter = ShopDomainsAdapter(
            arguments?.getString(VOUCHER_PROGRAM),
            onDomainClicked
        )
    }

    private val onDomainClicked = { domainId: String ->
        cartVM.resetShop()
        cartVM.createShopItemsList(domainId)
        val bundle = Bundle()
        bundle.putString(ShopFragment.DOMAIN_ID, domainId)
        findNavController().navigate(R.id.action_shopDomainsFragment_to_shopFragment, bundle)
    }

    private fun onDomainsChanged(domains: List<ShopDomain>?) {
        (binding.recyclerView.adapter as ShopDomainsAdapter).submitList(domains)
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

    companion object {

        const val VOUCHER_PROGRAM = "voucher_program"

    }

}