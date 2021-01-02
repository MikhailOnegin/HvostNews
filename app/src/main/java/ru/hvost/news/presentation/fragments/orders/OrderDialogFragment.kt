package ru.hvost.news.presentation.fragments.orders

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentOrderBinding
import ru.hvost.news.models.Order
import ru.hvost.news.models.toOrderItems
import ru.hvost.news.presentation.adapters.recycler.OrderProductsAdapter
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class OrderDialogFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
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

    private lateinit var binding: FragmentOrderBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var deleteOrderObserver: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        initializeObservers()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        inflateData(mainVM.orders.value?.firstOrNull { it.id == arguments?.getLong(ORDER_ID) })
        mainVM.deleteOrderEvent.observe(viewLifecycleOwner, deleteOrderObserver)
    }

    private fun initializeObservers() {
        deleteOrderObserver = DefaultNetworkEventObserver(
            binding.coordinator,
            doOnSuccess = {
                mainVM.updateOrders(App.getInstance().userToken)
                findNavController().popBackStack()
            }
        )
    }

    @SuppressLint("SetTextI18n")
    private fun inflateData(order: Order?) {
        order?.run {
            val adapter = OrderProductsAdapter(deleteOrder)
            binding.recyclerViewOrders.adapter = adapter
            adapter.submitList(this.toOrderItems())
            binding.orderNumber.text = "${getString(R.string.orderNumber)}${order.orderId}"
        }
    }

    private val deleteOrder: (Long) -> Unit = {
        mainVM.deleteOrder(it)
    }

    companion object {

        const val ORDER_ID = "order_id"

    }

}