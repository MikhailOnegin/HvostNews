package ru.hvost.news.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.LayoutDomainItemBinding
import ru.hvost.news.databinding.LayoutPopupDomainItemBinding
import ru.hvost.news.models.Domain

class PopupWindowDomainAdapter(private val onClick: (Long) -> Unit) :
    ListAdapter<Domain, PopupWindowDomainAdapter.DomainViewHolder>(DomainDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DomainViewHolder {
        return DomainViewHolder.getDomainVH(parent, onClick)
    }

    override fun onBindViewHolder(holder: DomainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DomainViewHolder(
        private val binding: LayoutPopupDomainItemBinding,
        private val onClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(domainItem: Domain) {
            Glide
                .with(binding.root)
                .load(APIService.baseUrl + domainItem.img)
                .fitCenter()
                .into(binding.img)
            binding.title.text = domainItem.title

            binding.root.setOnClickListener { onClick.invoke(domainItem.id) }
        }

        companion object {
            fun getDomainVH(parent: ViewGroup, onClick: (Long) -> Unit): DomainViewHolder {
                val binding = LayoutPopupDomainItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return DomainViewHolder(binding, onClick)
            }
        }

    }

    class DomainDiffUtilCallback : DiffUtil.ItemCallback<Domain>() {
        override fun areItemsTheSame(oldItem: Domain, newItem: Domain): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Domain, newItem: Domain): Boolean {
            return oldItem == newItem
        }
    }

}