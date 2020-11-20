package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.MainViewModel
import ru.hvost.news.databinding.RvVoucherBinding
import ru.hvost.news.databinding.RvVoucherFooterBinding
import ru.hvost.news.models.Voucher
import ru.hvost.news.models.VoucherFooter
import ru.hvost.news.models.VoucherItem
import ru.hvost.news.utils.events.OneTimeEvent
import java.lang.IllegalArgumentException

class VouchersAdapter(
    private val mainVM: MainViewModel
) : ListAdapter<VoucherItem, RecyclerView.ViewHolder>(VoucherItemDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_VOUCHER -> VoucherVH.getViewHolder(parent)
            TYPE_FOOTER -> VoucherFooterVH.getViewHolder(parent, mainVM)
            else -> throw IllegalArgumentException("Wrong voucher view holder type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItem(position)) {
            is Voucher -> (holder as VoucherVH).bind(getItem(position) as Voucher)
            is VoucherFooter -> (holder as VoucherFooterVH).bind(getItem(position) as VoucherFooter)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is Voucher -> TYPE_VOUCHER
            is VoucherFooter -> TYPE_FOOTER
        }
    }

    class VoucherVH(
        private val binding: RvVoucherBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(voucher: Voucher) {

        }

        companion object {

            fun getViewHolder(parent: ViewGroup): VoucherVH {
                val binding = RvVoucherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return VoucherVH(binding)
            }

        }

    }

    class VoucherFooterVH(
        private val binding: RvVoucherFooterBinding,
        private val mainVM: MainViewModel
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(footer: VoucherFooter) {
            binding.button.setOnClickListener {
                mainVM.vouchersFooterClickEvent.value = OneTimeEvent()
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup, mainVM: MainViewModel): VoucherFooterVH {
                val binding = RvVoucherFooterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return VoucherFooterVH(binding, mainVM)
            }

        }

    }

    class VoucherItemDiffUtilCallback : DiffUtil.ItemCallback<VoucherItem>() {

        override fun areItemsTheSame(oldItem: VoucherItem, newItem: VoucherItem): Boolean {
            return if(oldItem is Voucher && newItem is Voucher){
                oldItem.id == newItem.id
            } else false
        }

        override fun areContentsTheSame(oldItem: VoucherItem, newItem: VoucherItem): Boolean {
            return if(oldItem is Voucher && newItem is Voucher){
                oldItem == newItem
            } else false
        }

    }

    companion object {

        const val TYPE_VOUCHER = 1
        const val TYPE_FOOTER = 2

    }

}