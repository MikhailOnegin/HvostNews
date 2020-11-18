package ru.hvost.news.presentation.fragments.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentOrdersBinding
import ru.hvost.news.models.Order
import ru.hvost.news.models.orderStatus
import ru.hvost.news.presentation.adapters.recycler.OrdersAdapter
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.utils.LinearRvItemDecorations
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class OrdersFragment : Fragment(){

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var loadingObserver: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
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
            adapter = OrdersAdapter()
            addItemDecoration(LinearRvItemDecorations(
                sideMarginsDimension = R.dimen.largeMargin,
                marginBetweenElementsDimension = R.dimen.normalMargin
            ))
        }
    }

}