package ru.hvost.news.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.LayoutPetItemBinding
import ru.hvost.news.models.Pets
import ru.hvost.news.utils.WordEnding
import ru.hvost.news.utils.getWordEndingType
import java.text.SimpleDateFormat
import java.util.*

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
        @SuppressLint("SimpleDateFormat")
        private val format = SimpleDateFormat("dd.MM.yyyy")
        private val today = System.currentTimeMillis()
        private val date = Date()

        @SuppressLint("SetTextI18n")
        fun bind(pet: Pets) {
            val birthday = pet.petBirthday
            val birthDate = format.parse(birthday) ?: date
            var age = (today - birthDate.time) / (365L * 24L * 60L * 60L * 1000L)
            if (age < 1L && birthDate != date) {
                age = (today - birthDate.time) / (30L * 24L * 60L * 60L * 1000L)
                val month = when (getWordEndingType(age.toInt())) {
                    WordEnding.TYPE_1 -> App.getInstance().getString(R.string.ageForMonthType1)
                    WordEnding.TYPE_2 -> App.getInstance().getString(R.string.ageForMonthType2)
                    WordEnding.TYPE_3 -> App.getInstance().getString(R.string.ageForMonthType3)
                }
                binding.age.text = "$age $month"
            } else {
                val years = when (getWordEndingType(age.toInt())) {
                    WordEnding.TYPE_1 -> App.getInstance().getString(R.string.ageForYearsType1)
                    WordEnding.TYPE_2 -> App.getInstance().getString(R.string.ageForYearsType2)
                    WordEnding.TYPE_3 -> App.getInstance().getString(R.string.ageForYearsType3)
                }
                binding.age.text = "$age $years"
            }
            binding.name.text = pet.petName + ", " + pet.breedName
            binding.root.setOnClickListener { onClick.invoke(pet.petId) }
        }

        companion object {
            fun getPetVH(
                parent: ViewGroup,
                onClick: (String) -> Unit
            ): PetViewHolder {
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