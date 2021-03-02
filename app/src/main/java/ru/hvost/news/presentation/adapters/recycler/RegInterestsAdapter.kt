package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.databinding.RvInterestBinding
import ru.hvost.news.models.RegInterest
import ru.hvost.news.presentation.adapters.recycler.RegInterestsAdapter.*
import ru.hvost.news.presentation.fragments.login.RegistrationVM

class RegInterestsAdapter(
    private val registrationVM: RegistrationVM
) : ListAdapter<RegInterest, RegInterestVH>(RegInterestDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegInterestVH {
        return RegInterestVH.getViewHolder(parent, registrationVM)
    }

    override fun onBindViewHolder(holder: RegInterestVH, position: Int) {
        holder.bind(getItem(position))
    }

    class RegInterestVH(
        private val binding: RvInterestBinding,
        private val registrationVM: RegistrationVM
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(interest: RegInterest) {
            binding.name.text = interest.interestName
            Glide.with(binding.root)
                .load(interest.imageUri)
                .placeholder(R.drawable.loader_anim)
                .error(R.drawable.ic_load_error)
                .into(binding.image)
            binding.selection.isSelected = interest.isSelected
            binding.root.setOnClickListener {
                interest.isSelected = !interest.isSelected
                binding.selection.isSelected = interest.isSelected
                registrationVM.setThirdStageFinished()
            }
        }

        companion object{

            fun getViewHolder(
                parent: ViewGroup,
                registrationVM: RegistrationVM
            ): RegInterestVH{
                val binding = RvInterestBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return RegInterestVH(binding, registrationVM)
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