package ru.hvost.news.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.R
import ru.hvost.news.databinding.LayoutPetItemBinding
import ru.hvost.news.models.Pet

class PetAdapter(private val onClick: (Long) -> Unit) :
    ListAdapter<Pet, PetAdapter.PetViewHolder>(FaqDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        return PetViewHolder.getPetVH(parent, onClick)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PetViewHolder(
        private val binding: LayoutPetItemBinding,
        private val onClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(pet: Pet) {
            binding.name.text = pet.name + ", " + pet.breed
            binding.age.text = pet.age.toString()

            binding.root.setOnClickListener { onClick.invoke(pet.id) }
        }

        companion object {
            fun getPetVH(parent: ViewGroup, onClick: (Long) -> Unit): PetViewHolder {
                val binding = LayoutPetItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PetViewHolder(binding, onClick)
            }
        }

    }

    class FaqDiffUtilCallback : DiffUtil.ItemCallback<Pet>() {
        override fun areItemsTheSame(oldItem: Pet, newItem: Pet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Pet, newItem: Pet): Boolean {
            return oldItem == newItem
        }
    }

}