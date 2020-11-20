package ru.hvost.news.presentation.fragments.vouchers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.databinding.FragmentVouchersBinding
import ru.hvost.news.models.VoucherItem
import ru.hvost.news.presentation.adapters.recycler.VouchersAdapter
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.OneTimeEvent

class VouchersFragment : Fragment() {

    private lateinit var binding: FragmentVouchersBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var loadingVouchersEventObserver: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVouchersBinding.inflate(inflater, container, false)
        initializeObservers()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setObservers()
        mainVM.updateVouchers(App.getInstance().userToken)
        binding.recyclerView.adapter = VouchersAdapter(mainVM)
    }

    private fun setObservers() {
        mainVM.loadingVouchersEvent.observe(viewLifecycleOwner, loadingVouchersEventObserver)
        mainVM.vouchers.observe(viewLifecycleOwner) { onVouchersUpdated(it) }
        mainVM.vouchersFooterClickEvent.observe(viewLifecycleOwner, OneTimeEvent.Observer {
            findNavController().popBackStack()
        })
    }

    private fun onVouchersUpdated(vouchers: List<VoucherItem>?) {
        (binding.recyclerView.adapter as VouchersAdapter).submitList(vouchers)
    }

    private fun initializeObservers() {
        loadingVouchersEventObserver = DefaultNetworkEventObserver(
            binding.root,
            doOnError = { findNavController().popBackStack() },
            doOnFailure = { findNavController().popBackStack() }
        )
    }

}