package ru.hvost.news.presentation.fragments.orders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentOrdersBinding
import ru.hvost.news.models.*
import ru.hvost.news.presentation.adapters.recycler.OrderProductsAdapter
import ru.hvost.news.presentation.adapters.recycler.OrdersAdapter
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.utils.LinearRvItemDecorations
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.EventObserver

class OrdersFragment : Fragment(){

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var loadingObserver: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        binding.recyclerViewOrders.adapter = OrderProductsAdapter()
        initializeObservers()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setObservers()
        setRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setObservers() {
        mainVM.apply {
            orders.observe(viewLifecycleOwner) { onOrdersChanged(it) }
            ordersFilter.observe(viewLifecycleOwner) { onOrdersFilterChanged(it) }
            loadingOrdersEvent.observe(viewLifecycleOwner, loadingObserver)
            orderSelectedEvent.observe(viewLifecycleOwner, EventObserver(onOrderSelected))
        }
    }

    private fun initializeObservers() {
        loadingObserver = DefaultNetworkEventObserver(
            binding.root,
            doOnError = { findNavController().popBackStack() },
            doOnFailure = { findNavController().popBackStack() }
        )
    }

    private fun setListeners() {
        binding.spinner.onItemSelectedListener = onFilterSelectedListener
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    @SuppressLint("SetTextI18n")
    private val onOrderSelected = { order: Order ->
        binding.root.isEnabled = false
        val adapter = (binding.recyclerViewOrders.adapter as OrderProductsAdapter)
        adapter.submitList(order.toOrderItems())
        binding.orderNumber.text = "${getString(R.string.orderNumber)}${order.orderId}"
        val params = (binding.orderContainer.layoutParams as CoordinatorLayout.LayoutParams)
        val behavior = params.behavior as BottomSheetBehavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private val onFilterSelectedListener = object: AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            mainVM.ordersFilter.value = parent?.adapter?.getItem(position) as String
        }

        override fun onNothingSelected(parent: AdapterView<*>?) { }

    }

    private fun onOrdersFilterChanged(filter: String?) {
        val adapter = (binding.recyclerView.adapter as OrdersAdapter)
        adapter.submitList(when(filter){
            "all" -> mainVM.orders.value
            else -> mainVM.orders.value?.filter { it.orderStatus == filter }
        })
    }

    private fun onOrdersChanged(orders: List<Order>?) {
        (binding.recyclerView.adapter as OrdersAdapter).submitList(orders)
        val list = orders?.distinctBy { it.orderStatus }
            ?.map { it.orderStatus }?.toMutableList() ?: mutableListOf()
        list.add(0, "all")
        binding.spinner.adapter = SpinnerAdapter(
            requireActivity(),
            "",
            list,
            String::orderStatus
        )
    }

    private fun setRecyclerView() {
        binding.recyclerView.apply {
            adapter = OrdersAdapter(mainVM)
            addItemDecoration(LinearRvItemDecorations(
                sideMarginsDimension = R.dimen.largeMargin,
                marginBetweenElementsDimension = R.dimen.normalMargin
            ))
        }
    }

}