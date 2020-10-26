//package ru.hvost.news.presentation.adapters
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import ru.hvost.news.databinding.LayoutPrizePriceItemBinding
//import ru.hvost.news.models.PrizePrice
//
//class PrizePriceAdapter(private val onClick: (Long) -> Unit) :
//    ListAdapter<PrizePrice, PrizePriceAdapter.PetViewHolder>(FaqDiffUtilCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
//        return PetViewHolder.getPriceVH(parent, onClick)
//    }
//
//    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    class PetViewHolder(
//        private val binding: LayoutPrizePriceItemBinding,
//        private val onClick: (Long) -> Unit
//    ) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(prize: PrizePrice) {
//            binding.title.text = prize.cost
//
//            binding.root.setOnClickListener { onClick.invoke(prize.id) }
//        }
//
//        companion object {
//            fun getPriceVH(parent: ViewGroup, onClick: (Long) -> Unit): PetViewHolder {
//                val binding = LayoutPrizePriceItemBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//                return PetViewHolder(binding, onClick)
//            }
//        }
//
//    }
//
//    class FaqDiffUtilCallback : DiffUtil.ItemCallback<PrizePrice>() {
//        override fun areItemsTheSame(oldItem: PrizePrice, newItem: PrizePrice): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: PrizePrice, newItem: PrizePrice): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//}