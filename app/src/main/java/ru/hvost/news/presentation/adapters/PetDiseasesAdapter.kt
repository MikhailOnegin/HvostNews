package ru.hvost.news.presentation.adapters

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.databinding.RvDiseasesBinding

class PetDiseasesAdapter() :
    ListAdapter<String, PetDiseasesAdapter.DiseasesViewHolder>(DiseasesDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseasesViewHolder {
        return DiseasesViewHolder.getPetVH(parent, this)
    }

    override fun onBindViewHolder(holder: DiseasesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiseasesViewHolder(
        private val binding: RvDiseasesBinding,
        private val adapter: PetDiseasesAdapter
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(disease: String) {
            binding.diseasTitle.text = disease
            binding.delete.setOnClickListener {
                animateFadeDialog(disease)
            }
        }

        private fun animateFadeDialog(disease: String) {
            val animator = ValueAnimator.ofFloat(binding.root.alpha, 0f)
            animator.duration = 200L
            animator.addUpdateListener {
                binding.root.alpha = it.animatedValue as Float
            }
            animator.addListener(OnDialogDismissAnimationListener(disease))
            animator.start()
        }

        inner class OnDialogDismissAnimationListener(
            private val disease: String
        ) : Animator.AnimatorListener {

            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                val list = adapter.currentList.toMutableList()
                list.remove(disease)
                adapter.submitList(list)
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}

        }

        companion object {
            fun getPetVH(parent: ViewGroup, adapter: PetDiseasesAdapter): DiseasesViewHolder {
                val binding = RvDiseasesBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return DiseasesViewHolder(binding, adapter)
            }
        }
    }

    class DiseasesDiffUtilCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.equals(newItem, ignoreCase = true)
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

}