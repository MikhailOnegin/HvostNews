package ru.hvost.news.presentation.adapters

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.R
import ru.hvost.news.data.api.response.Diseases
import ru.hvost.news.databinding.RvDiseasesBinding

class PetDiseasesAdapter() :
    ListAdapter<Diseases, PetDiseasesAdapter.DiseasesViewHolder>(DiseasesDiffUtilCallback()) {

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
        fun bind(disease: Diseases) {
            binding.diseasTitle.text = disease.diseaseName
            binding.delete.setOnClickListener {
                animateFadeDialog(disease)
            }
        }

        private fun animateFadeDialog(disease: Diseases) {
            val animator = ValueAnimator.ofFloat(binding.root.alpha, 0f)
            animator.duration = 200L
            animator.addUpdateListener {
                binding.root.alpha = it.animatedValue as Float
            }
            animator.addListener(OnDialogDismissAnimationListener(disease))
            animator.start()
        }

        inner class OnDialogDismissAnimationListener(
            private val disease: Diseases
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

    class DiseasesDiffUtilCallback : DiffUtil.ItemCallback<Diseases>() {
        override fun areItemsTheSame(oldItem: Diseases, newItem: Diseases): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Diseases, newItem: Diseases): Boolean {
            return oldItem == newItem
        }
    }

}