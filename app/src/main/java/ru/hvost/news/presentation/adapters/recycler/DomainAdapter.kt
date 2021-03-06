package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.LayoutDomainItemBinding
import ru.hvost.news.models.Domain
import ru.hvost.news.utils.getDefaultShimmer

class DomainAdapter(private val onClick: (Long) -> Unit) :
    ListAdapter<Domain, DomainAdapter.DomainViewHolder>(DomainDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DomainViewHolder {
        return DomainViewHolder.getDomainVH(parent, onClick)
    }

    override fun onBindViewHolder(holder: DomainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DomainViewHolder(
        private val binding: LayoutDomainItemBinding,
        private val onClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(domainItem: Domain) {
            Glide
                .with(binding.root)
                .load(APIService.baseUrl + domainItem.img)
                .placeholder(getDefaultShimmer(itemView.context))
                .error(R.drawable.empty_image)
                .into(binding.img)
            binding.title.text = domainItem.title
            binding.root.setOnClickListener { onClick.invoke(domainItem.id) }
        }

        companion object {
            fun getDomainVH(parent: ViewGroup, onClick: (Long) -> Unit): DomainViewHolder {
                val binding = LayoutDomainItemBinding.inflate(
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