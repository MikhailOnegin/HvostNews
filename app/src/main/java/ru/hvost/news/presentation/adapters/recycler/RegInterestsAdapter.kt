package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.databinding.RvInterestBinding
import ru.hvost.news.models.RegInterest
import ru.hvost.news.presentation.adapters.recycler.RegInterestsAdapter.*

class RegInterestsAdapter : ListAdapter<RegInterest, RegInterestVH>(RegInterestDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegInterestVH {
        return RegInterestVH.getViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RegInterestVH, position: Int) {
        holder.bind(getItem(position))
    }

    class RegInterestVH(
        private val binding: RvInterestBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(interest: RegInterest) {
            binding.name.text = interest.interestName
            Glide.with(binding.root)
                .load(interest.imageUri)
                .into(binding.image)
        }

        companion object{

            fun getViewHolder(parent: ViewGroup): RegInterestVH{
                val binding = RvInterestBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return RegInterestVH(binding)
            }

        }

    }

    class RegInterestDiffUtilCallback : DiffUtil.ItemCallback<RegInterest>(){

        override fun areItemsTheSame(oldItem: RegInterest, newItem: RegInterest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RegInterest, newItem: RegInterest): Boolean {
            return oldItem == newItem
        }
    }

}