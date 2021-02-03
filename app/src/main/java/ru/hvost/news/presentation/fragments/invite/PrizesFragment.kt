package ru.hvost.news.presentation.fragments.invite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentPrizesBinding
import ru.hvost.news.presentation.adapters.recycler.PrizeCategoryAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.fragments.shop.CartViewModel
import ru.hvost.news.utils.LinearRvItemDecorations
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class PrizesFragment : BaseFragment() {

    private lateinit var binding: FragmentPrizesBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var cartVM: CartViewModel
    private lateinit var onBonusBalanceLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onPrizeCategoriesLoadingEvent: DefaultNetworkEventObserver


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrizesBinding.inflate(inflater, container, false)
        binding.prizeList.addItemDecoration(LinearRvItemDecorations(
            marginBetweenElementsDimension = R.dimen.smallMargin
        ))
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        if (mainVM.bonusBalanceLoadingEvent.value?.peekContent() == State.SUCCESS) {
            setBalance()
        } else mainVM.getBonusBalance()
        if (mainVM.prizeCategoriesState.value?.peekContent() == State.SUCCESS) {
            setRecyclerView()
        } else mainVM.getPrizeCategories()
        cartVM = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        cartVM.updateCartAsync(App.getInstance().userToken)
        initializeObservers()
        setListeners()
        setObservers()
    }

    private fun setBalance() {
        binding.balance.text = (mainVM.bonusBalance.value?.bonusBalance ?: 0).toString()
    }

    private fun initializeObservers() {
        onBonusBalanceLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = {
                setBalance()
            }
        )
        onPrizeCategoriesLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = {
                setRecyclerView()
            }
        )
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.cartCount.setOnClickListener { findNavController().navigate(R.id.action_prizesFragment_to_cartFragment) }
    }

    private fun onCartCounterChanged(cartItems: Int?) {
        cartItems?.run {
            if (cartItems == 0)
                binding.cartCount.text = ""
            else
                binding.cartCount.text = "$cartItems"
        }
    }

    private fun setObservers() {
        mainVM.bonusBalanceLoadingEvent.observe(viewLifecycleOwner, onBonusBalanceLoadingEvent)
        mainVM.prizeCategoriesState.observe(viewLifecycleOwner, onPrizeCategoriesLoadingEvent)
        cartVM.cartCounter.observe(viewLifecycleOwner) { onCartCounterChanged(it) }
    }

    private fun setRecyclerView() {
        val onActionClicked = { categoryId: String ->
            val bundle = Bundle()
            bundle.putString("PRIZE_ID", categoryId)
            mainVM.prizeDomainId = categoryId
            mainVM.loadPrizes(categoryId)
            findNavController().navigate(R.id.action_prizesFragment_to_choicePrizeFragment, bundle)
        }
        val adapter = PrizeCategoryAdapter(onActionClicked)
        binding.prizeList.adapter = adapter
        adapter.submitList(mainVM.prizeCategoriesResponse.value)
    }

}