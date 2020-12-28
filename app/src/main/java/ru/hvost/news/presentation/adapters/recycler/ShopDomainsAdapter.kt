package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.databinding.RvShopDomainBinding
import ru.hvost.news.models.ShopDomain

class ShopDomainsAdapter(
    private val voucherProgram: String?,
    private val onDomainClicked: (String) -> Unit
) : ListAdapter<ShopDomain, ShopDomainsAdapter.ShopDomainVH>(ShopDomainDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopDomainVH {
        return ShopDomainVH.getViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ShopDomainVH, position: Int) {
        holder.bind(getItem(position), position, voucherProgram, onDomainClicked)
    }

    class ShopDomainVH(
        private val binding: RvShopDomainBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            domain: ShopDomain,
            position: Int,
            voucherProgram: String?,
            onDomainClicked: (String) -> Unit
        ) {
            binding.title.text = voucherProgram
            binding.category.text = domain.domainTitle
            if (position == 0) binding.title.visibility = View.VISIBLE
            else binding.title.visibility = View.GONE
            Glide.with(binding.root).load(domain.imageUri).into(binding.image)
            binding.constraintLayout.setOnClickListener {
                onDomainClicked.invoke(domain.domainId)
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): ShopDomainVH {
                val binding = RvShopDomainBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ShopDomainVH(binding)
            }

        }

    }

    class ShopDomainDiffUtilCallback : DiffUtil.ItemCallback<ShopDomain>() {

        override fun areItemsTheSame(oldItem: ShopDomain, newItem: ShopDomain): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopDomain, newItem: ShopDomain): Boolean {
            return oldItem == newItem
        }

    }

}