package ru.hvost.news.presentation.adapters.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.RvOrderBinding
import ru.hvost.news.models.Order
import ru.hvost.news.presentation.adapters.recycler.OrdersAdapter.OrderVH
import ru.hvost.news.utils.*

class OrdersAdapter : ListAdapter<Order, OrderVH>(OrderDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderVH {
        return OrderVH.getOrderViewHolder(parent)
    }

    override fun onBindViewHolder(holder: OrderVH, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderVH(val binding: RvOrderBinding) : RecyclerView.ViewHolder(binding.root) {

        private val stub = App.getInstance().getString(R.string.stub)
        private val textColorWhite = ContextCompat.getColor(App.getInstance(), android.R.color.white)
        private val textColorPrimary = ContextCompat.getColor(App.getInstance(), R.color.TextColorPrimary)

        @SuppressLint("SetTextI18n")
        fun bind(order: Order) {
            binding.apply {
                number.text = "№${order.orderId}"
                date.text = tryFormatDate(
                    serverDateFormat,
                    simpleDateFormat,
                    order.orderDate,
                    App.getInstance().getString(R.string.stub)
                )
                sum.text = "${moneyFormat.format(order.totalCost)} \u20bd"
                setStatus(order)
                root.setOnClickListener {  }
            }
        }

        private fun setStatus(order: Order) {
            binding.status.run {
                text = ordersStatuses[order.orderStatus] ?: stub
                when(order.orderStatus){
                    "N" -> {
                        setBackgroundResource(R.drawable.background_order_status_progress)
                        setTextColor(textColorWhite)
                    }
                    "F" -> {
                        setBackgroundResource(R.drawable.background_order_status_finished)
                        setTextColor(textColorWhite)
                    }
                    else -> {
                        setBackgroundResource(R.drawable.background_order_status)
                        setTextColor(textColorPrimary)
                    }
                }
            }
        }

        companion object {

            fun getOrderViewHolder(parent: ViewGroup): OrderVH {
                val binding = RvOrderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OrderVH(binding)
            }

        }

    }

    class OrderDiffUtilCallback : DiffUtil.ItemCallback<Order>() {

        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }

}