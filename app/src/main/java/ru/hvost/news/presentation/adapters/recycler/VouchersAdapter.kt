package ru.hvost.news.presentation.adapters.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.RvVoucherBinding
import ru.hvost.news.databinding.RvVoucherFooterBinding
import ru.hvost.news.models.Voucher
import ru.hvost.news.models.VoucherFooter
import ru.hvost.news.models.VoucherItem
import ru.hvost.news.utils.events.OneTimeEvent
import ru.hvost.news.utils.showNotReadyToast
import java.lang.Exception
import java.lang.IllegalArgumentException

class VouchersAdapter(
    private val mainVM: MainViewModel
) : ListAdapter<VoucherItem, RecyclerView.ViewHolder>(VoucherItemDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_VOUCHER -> VoucherVH.getViewHolder(parent, mainVM)
            TYPE_FOOTER -> VoucherFooterVH.getViewHolder(parent, mainVM)
            else -> throw IllegalArgumentException("Wrong voucher view holder type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItem(position)) {
            is Voucher -> (holder as VoucherVH).bind(
                getItem(position) as Voucher,
                try {
                    getItem(position - 1) as Voucher
                }catch (exc: Exception) { null }
            )
            is VoucherFooter -> (holder as VoucherFooterVH).bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is Voucher -> TYPE_VOUCHER
            is VoucherFooter -> TYPE_FOOTER
        }
    }

    class VoucherVH(
        private val binding: RvVoucherBinding,
        private val mainVM: MainViewModel
    ): RecyclerView.ViewHolder(binding.root) {

        private val until = App.getInstance().getString(R.string.dateUntil)
        private val stub = App.getInstance().getString(R.string.stub)

        @SuppressLint("SetTextI18n")
        fun bind(voucher: Voucher, prevVoucher: Voucher?) {
            binding.run {
                title.text = voucher.voucherProgram
                promocode.text = voucher.voucherCode
                name.text = voucher.petName
                setSpecies(voucher)
                expiration.text = "$until ${voucher.expirationDate}"
                if(voucher.voucherProgram == prevVoucher?.voucherProgram) {
                    title.visibility = View.GONE
                    divider.visibility = View.GONE
                } else {
                    title.visibility = View.VISIBLE
                    divider.visibility = View.VISIBLE
                }
                if(prevVoucher == null) divider.visibility = View.GONE
                button.setOnClickListener {
                    //sergeev: Настроить переход в магазин.
                    showNotReadyToast()
                }
            }
        }

        private fun setSpecies(voucher: Voucher) {
            val species = mainVM.petsSpeciesResponse.value?.firstOrNull {
                it.speciesId.toString() == voucher.petSpecies
            }?.speciesName ?: stub
            binding.species.text = species
        }

        companion object {

            fun getViewHolder(
                parent: ViewGroup,
                mainVM: MainViewModel
            ): VoucherVH {
                val binding = RvVoucherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return VoucherVH(binding, mainVM)
            }

        }

    }

    class VoucherFooterVH(
        private val binding: RvVoucherFooterBinding,
        private val mainVM: MainViewModel
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind() {
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
            } else oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: VoucherItem, newItem: VoucherItem): Boolean {
            return false
        }

    }

    companion object {

        const val TYPE_VOUCHER = 1
        const val TYPE_FOOTER = 2

    }

}