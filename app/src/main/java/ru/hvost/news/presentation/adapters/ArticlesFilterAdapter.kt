package ru.hvost.news.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.MainViewModel
import ru.hvost.news.databinding.RvFilterFooterBinding
import ru.hvost.news.databinding.RvFilterItemBinding
import ru.hvost.news.databinding.RvFilterItemInterestBinding
import ru.hvost.news.models.*
import ru.hvost.news.utils.events.OneTimeEvent
import java.lang.IllegalArgumentException

class ArticlesFilterAdapter(val mainVM: MainViewModel) :
    ListAdapter<CategoryItem, RecyclerView.ViewHolder>(
        ArticleFilterDiffUtilCallback()
    ) {
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is InterestsCategory -> TYPE_CATEGORY
            is FilterFooter -> TYPE_FOOTER
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CATEGORY -> CategoryItemViewHolder.getCategoryVH(parent)
            TYPE_FOOTER -> FooterViewHolder.getFooterVH(parent, mainVM)
            else -> throw IllegalArgumentException("Wrong voucher view holder type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItem(position)) {
            is InterestsCategory -> (holder as CategoryItemViewHolder).bind(getItem(position) as InterestsCategory)
            is FilterFooter -> (holder as FooterViewHolder).bind()
        }
    }

    class CategoryItemViewHolder(
        private val binding: RvFilterItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InterestsCategory) {
            binding.expand.setOnClickListener { switchVisibility() }
            binding.mainCheckbox.id = item.categoryId.toInt()
            binding.mainCheckbox.text = item.categoryName
            when (item.state) {
                CheckboxStates.SELECTED -> binding.mainCheckbox.isChecked = true
                CheckboxStates.UNSELECTED -> binding.mainCheckbox.isChecked = false
                CheckboxStates.INDETERMINATE -> binding.mainCheckbox.isChecked = true
            }
            binding.mainCheckbox.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> {
                        for (interest in item.interests) {
                            item.state = CheckboxStates.SELECTED
                            interest.state = CheckboxStates.SELECTED
                            item.sendParent = true
                        }
                        setChildCheckboxes(item)
                    }
                    false -> {
                        for (interest in item.interests) {
                            item.state = CheckboxStates.UNSELECTED
                            interest.state = CheckboxStates.UNSELECTED
                            item.sendParent = false
                        }
                        setChildCheckboxes(item)
                    }
                }
            }
            setChildCheckboxes(item)
        }

        private fun setChildCheckboxes(item: InterestsCategory) {
            binding.interests.removeAllViews()
            for (interest in item.interests) {
                val view = RvFilterItemInterestBinding.inflate(
                    LayoutInflater.from(binding.root.context),
                    null,
                    false
                )
                view.checkbox.id = interest.interestId.toInt()
                view.checkbox.text = interest.interestName
                if (item.sendParent || interest.state == CheckboxStates.SELECTED)
                    view.checkbox.isChecked = true
                view.checkbox.setOnCheckedChangeListener { _, isChecked ->
                    when (isChecked) {
                        true -> {
                            interest.state = CheckboxStates.SELECTED
                            if (item.interests.size == item.interests.filter { it.state == CheckboxStates.SELECTED }.size) {
                                binding.mainCheckbox.isChecked = true
                                item.sendParent = true
                            }
                        }
                        false -> {
                            interest.state = CheckboxStates.UNSELECTED
                            item.sendParent = false
                            checkParentIsChecked(item)
                            //todo: indeterminate status for parent Checkbox
                        }
                    }
                }
                binding.interests.addView(view.root)
            }
        }

        private fun checkParentIsChecked(item: InterestsCategory) {
            if (item.interests.filter { it.state == CheckboxStates.SELECTED }.isEmpty()) {
                binding.mainCheckbox.isChecked = false
                item.state = CheckboxStates.UNSELECTED
            }
        }

        private fun switchVisibility() {
            when (binding.interests.visibility) {
                View.GONE -> binding.interests.visibility = View.VISIBLE
                View.VISIBLE -> binding.interests.visibility = View.GONE
            }
        }

        companion object {
            fun getCategoryVH(
                parent: ViewGroup
            ): CategoryItemViewHolder {
                val binding = RvFilterItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CategoryItemViewHolder(binding)
            }
        }
    }

    class FooterViewHolder(
        private val binding: RvFilterFooterBinding,
        private val mainVM: MainViewModel
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.cancel.setOnClickListener {
                mainVM.closeArticlesFilterCustomDialog.value = OneTimeEvent()
            }
            binding.show.setOnClickListener {
                mainVM.updateArticlesWithNewInterests.value = OneTimeEvent()
            }
        }

        companion object {
            fun getFooterVH(
                parent: ViewGroup,
                mainVM: MainViewModel
            ): FooterViewHolder {
                val binding = RvFilterFooterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return FooterViewHolder(binding, mainVM)
            }
        }
    }

    class ArticleFilterDiffUtilCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return if (oldItem is InterestsCategory && newItem is InterestsCategory) {
                oldItem.categoryId == newItem.categoryId
            } else oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return false
        }
    }

    companion object {
        const val TYPE_CATEGORY = 0
        const val TYPE_FOOTER = 1
    }

}