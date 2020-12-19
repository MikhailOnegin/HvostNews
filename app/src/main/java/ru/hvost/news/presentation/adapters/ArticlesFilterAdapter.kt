package ru.hvost.news.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
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

    private var fullList: List<CategoryItem>? = null

    fun setFullList(fullList: List<CategoryItem>) {
        this.fullList = fullList
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is InterestsCategory -> TYPE_CATEGORY
            is Interests -> TYPE_INTERESTS
            is FilterFooter -> TYPE_FOOTER
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CATEGORY -> CategoryItemViewHolder.getCategoryVH(parent, this, mainVM)
            TYPE_INTERESTS -> InterestViewHolder.getInterestVH(parent, this, mainVM)
            TYPE_FOOTER -> FooterViewHolder.getFooterVH(parent, mainVM)
            else -> throw IllegalArgumentException("Wrong voucher view holder type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItem(position)) {
            is InterestsCategory -> (holder as CategoryItemViewHolder).bind(
                getItem(position) as InterestsCategory,
                position
            )
            is Interests -> (holder as InterestViewHolder).bind(
                getItem(position) as Interests,
                if (position > 0) getItem(position - 1) else null,
                if (position <= itemCount - 2) getItem(position + 1) else null
            )
            is FilterFooter -> (holder as FooterViewHolder).bind()
        }
    }

    fun handleClickOnCategory(position: Int) {
        val category = getItem(position) as InterestsCategory
        category.isExpanded = !category.isExpanded
        val newList = if (category.isExpanded) {
            val list = currentList.toMutableList()
            val interests = fullList?.filter { it.parentId == category.id } ?: listOf()
            if (category.state == CheckboxStates.SELECTED) {
                interests.map { (it as Interests).state = CheckboxStates.SELECTED }
            }
            list.addAll(
                position + 1,
                interests
            )
            list
        } else {
            currentList.filter { it.parentId != category.id }
        }
        submitList(newList)
    }

    class CategoryItemViewHolder(
        private val binding: RvFilterItemBinding,
        mainVM: MainViewModel,
        private val adapter: ArticlesFilterAdapter,
        private var isChildSelected: Boolean = false
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InterestsCategory, position: Int) {
            binding.expand.setOnClickListener {
                adapter.handleClickOnCategory(position)
            }
            isChildSelected =
                (adapter.fullList?.any { it is Interests && it.parentCategoryId == item.categoryId && it.state == CheckboxStates.SELECTED } == true && !item.sendParent)
            if (isChildSelected) item.state = CheckboxStates.INDETERMINATE
            binding.title.text = item.categoryName
            when (item.state) {
                CheckboxStates.SELECTED -> binding.mainCheckbox.setImageResource(R.drawable.ic_checkbox_checked)
                CheckboxStates.UNSELECTED -> binding.mainCheckbox.setImageResource(android.R.color.transparent)
                CheckboxStates.INDETERMINATE -> binding.mainCheckbox.setImageResource(R.drawable.ic_checkbox_indeterminate)
            }
            binding.mainCheckbox.setOnClickListener { changeState(item) }
        }

        private fun changeState(item: InterestsCategory) {
            when (item.state) {
                CheckboxStates.UNSELECTED -> {
                    binding.mainCheckbox.setImageResource(R.drawable.ic_checkbox_checked)
                    item.state = CheckboxStates.SELECTED
                    item.sendParent = true
                    adapter.fullList?.filter { it.parentId == item.categoryId }?.map {
                        if (it is Interests) it.state = CheckboxStates.SELECTED
                    }
                }
                else -> {
                    binding.mainCheckbox.setImageResource(android.R.color.transparent)
                    item.state = CheckboxStates.UNSELECTED
                    item.sendParent = false
                    adapter.fullList?.filter { it.parentId == item.categoryId }?.map {
                        if (it is Interests) it.state = CheckboxStates.UNSELECTED
                    }
                }
            }
            adapter.submitList(adapter.currentList.toMutableList())
        }

        companion object {
            fun getCategoryVH(
                parent: ViewGroup,
                adapter: ArticlesFilterAdapter,
                mainVM: MainViewModel
            ): CategoryItemViewHolder {
                val binding = RvFilterItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CategoryItemViewHolder(binding, mainVM, adapter)
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

    class InterestViewHolder(
        private val binding: RvFilterItemInterestBinding,
        private val adapter: ArticlesFilterAdapter,
        private val mainVM: MainViewModel
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Interests, prevItem: CategoryItem?, nextItem: CategoryItem?) {
            if (prevItem is InterestsCategory) {
                binding.root.background =
                    ResourcesCompat.getDrawable(
                        binding.root.context.resources,
                        R.drawable.background_interests_first,
                        null
                    )
            } else if (nextItem is InterestsCategory || nextItem is FilterFooter) {
                binding.root.background =
                    ResourcesCompat.getDrawable(
                        binding.root.context.resources,
                        R.drawable.background_interests_last,
                        null
                    )
            } else {
                binding.root.background =
                    ResourcesCompat.getDrawable(
                        binding.root.context.resources,
                        R.drawable.background_interests_item,
                        null
                    )
            }
            when (item.state) {
                CheckboxStates.SELECTED -> binding.childCheckbox.setImageResource(R.drawable.ic_checkbox_checked)
                else -> binding.childCheckbox.setImageResource(android.R.color.transparent)
            }
            binding.title.text = item.interestName
            binding.childCheckbox.setOnClickListener { changeState(item) }
        }

        private fun changeState(item: Interests) {
            val allInterests =
                adapter.fullList?.filter { it is Interests && it.parentCategoryId == item.parentCategoryId }
            var newList = mutableListOf<CategoryItem>()
            when (item.state) {
                CheckboxStates.UNSELECTED -> {
                    binding.childCheckbox.setImageResource(R.drawable.ic_checkbox_checked)
                    item.state = CheckboxStates.SELECTED
                    val oldList = adapter.currentList.toMutableList()
                    val selectedInterests =
                        oldList.filter { it is Interests && it.state == CheckboxStates.SELECTED && it.parentCategoryId == item.parentCategoryId }
                    for (element in oldList) {
                        if (element is InterestsCategory && element.id == item.parentCategoryId) {
                            element.apply {
                                if (allInterests?.size == selectedInterests.size) {
                                    state = CheckboxStates.SELECTED
                                    sendParent = true
                                } else {
                                    state = CheckboxStates.INDETERMINATE
                                }
                            }
                        }
                    }
                    newList = oldList
                }
                else -> {
                    binding.childCheckbox.setImageResource(android.R.color.transparent)
                    item.state = CheckboxStates.UNSELECTED
                    val oldList = adapter.currentList.toMutableList()
                    val selectedInterests =
                        oldList.filter { it is Interests && it.state == CheckboxStates.SELECTED && it.parentCategoryId == item.parentCategoryId }
                    for (element in oldList) {
                        if (element is InterestsCategory && element.id == item.parentCategoryId) {
                            element.sendParent = false
                            element.state = if (selectedInterests.isEmpty()) {
                                CheckboxStates.UNSELECTED
                            } else {
                                CheckboxStates.INDETERMINATE
                            }
                        }
                    }
                    newList = oldList
                }
            }
            adapter.submitList(newList)
        }

        companion object {
            fun getInterestVH(
                parent: ViewGroup,
                adapter: ArticlesFilterAdapter,
                mainVM: MainViewModel
            ): InterestViewHolder {
                val binding = RvFilterItemInterestBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return InterestViewHolder(binding = binding, mainVM = mainVM, adapter = adapter)
            }
        }
    }

    class ArticleFilterDiffUtilCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return false
        }
    }

    companion object {
        const val TYPE_CATEGORY = 0
        const val TYPE_INTERESTS = 1
        const val TYPE_FOOTER = 2
    }

}