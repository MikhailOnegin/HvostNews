package ru.hvost.news.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.databinding.LayoutPetItemBinding
import ru.hvost.news.models.Pets

class PetAdapter(private val onClick: (String) -> Unit) :
    ListAdapter<Pets, PetAdapter.PetViewHolder>(PetDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        return PetViewHolder.getPetVH(parent, onClick)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PetViewHolder(
        private val binding: LayoutPetItemBinding,
        private val onClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(pet: Pets) {
            binding.name.text = pet.petName + ", " + pet.petBreed
            binding.age.text = pet.petBirthday

            binding.root.setOnClickListener { onClick.invoke(pet.petId) }
        }

        companion object {
            fun getPetVH(parent: ViewGroup, onClick: (String) -> Unit): PetViewHolder {
                val binding = LayoutPetItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PetViewHolder(binding, onClick)
            }
        }

    }

    class PetDiffUtilCallback : DiffUtil.ItemCallback<Pets>() {
        override fun areItemsTheSame(oldItem: Pets, newItem: Pets): Boolean {
            return oldItem.petId == newItem.petId
        }

        override fun areContentsTheSame(oldItem: Pets, newItem: Pets): Boolean {
            return oldItem == newItem
        }
    }

}