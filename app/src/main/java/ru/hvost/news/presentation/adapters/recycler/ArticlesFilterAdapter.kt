package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
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

    fun getFullList(): List<CategoryItem>? {
        return this.fullList
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is InterestsCategory -> TYPE_CATEGORY
            is Interests -> TYPE_INTERESTS
        }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<CategoryItem>,
        currentList: MutableList<CategoryItem>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        mainVM.filterRvChangedEvent.value = OneTimeEvent()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CATEGORY -> CategoryItemViewHolder.getCategoryVH(parent, this)
            TYPE_INTERESTS -> InterestViewHolder.getInterestVH(parent, this)
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
            if (item.isExpanded) binding.expand.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.ic_button_expanded
                )
            )
            else binding.expand.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.ic_button_expand
                )
            )
            when (item.state) {
                CheckboxStates.SELECTED -> binding.mainCheckbox.setImageResource(R.drawable.ic_checkbox_checked)
                CheckboxStates.UNSELECTED -> binding.mainCheckbox.setImageResource(android.R.color.transparent)
                CheckboxStates.INDETERMINATE -> binding.mainCheckbox.setImageResource(R.drawable.ic_checkbox_indeterminate)
            }
            binding.apply {
                mainCheckbox.setOnClickListener { changeState(item) }
                title.setOnClickListener { changeState(item) }
            }
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
                adapter: ArticlesFilterAdapter
            ): CategoryItemViewHolder {
                val binding = RvFilterItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CategoryItemViewHolder(binding, adapter)
            }
        }
    }

    class InterestViewHolder(
        private val binding: RvFilterItemInterestBinding,
        private val adapter: ArticlesFilterAdapter
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Interests, prevItem: CategoryItem?, nextItem: CategoryItem?) {
            if (prevItem is InterestsCategory) {
                binding.root.background =
                    ResourcesCompat.getDrawable(
                        binding.root.context.resources,
                        R.drawable.background_interests_first,
                        null
                    )
            } else if (nextItem is InterestsCategory || nextItem == null) {
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
            binding.apply {
                childCheckbox.setOnClickListener { changeState(item) }
                title.setOnClickListener { changeState(item) }
            }
        }

        private fun changeState(item: Interests) {
            val allInterests =
                adapter.fullList?.filter { it is Interests && it.parentCategoryId == item.parentCategoryId }
            val newList: MutableList<CategoryItem>
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
                adapter: ArticlesFilterAdapter
            ): InterestViewHolder {
                val binding = RvFilterItemInterestBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return InterestViewHolder(binding = binding, adapter = adapter)
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
    }

}