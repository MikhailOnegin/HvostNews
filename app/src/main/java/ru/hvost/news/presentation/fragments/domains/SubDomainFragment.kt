package ru.hvost.news.presentation.fragments.domains

import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import android.widget.PopupWindow
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.layout_popup_domains.view.*
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSubdomainBinding
import ru.hvost.news.presentation.adapters.ArticleAdapter
import ru.hvost.news.presentation.adapters.PopupWindowDomainAdapter
import ru.hvost.news.utils.enums.State

class SubDomainFragment : Fragment() {

    private lateinit var binding: FragmentSubdomainBinding
    private lateinit var mainVM: MainViewModel
    private var domainId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        domainId = arguments?.getLong("DOMAIN_ID") ?: 0
        binding = FragmentSubdomainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setObservers()
        setRecyclerView(domainId)
        setDecoration()
        setListeners()
    }

    private fun setListeners() {
        binding.showPopup.setOnClickListener { callPopup() }
        binding.toolbar.setOnClickListener { findNavController().popBackStack() }
    }

    private fun callPopup() {
        val view = layoutInflater.inflate(R.layout.layout_popup_domains, binding.root, false)
        val popupWindow = PopupWindow(requireActivity())

        val onActionClicked = { domain: Long ->
            setTabs(domain)
            setRecyclerView(domain)
            popupWindow.dismiss()
        }
        val adapter = PopupWindowDomainAdapter(onActionClicked)
        view.domainList.adapter = adapter
        adapter.submitList(mainVM.domains)
        setPopupElementsDecoration(view)

        popupWindow.contentView = view
        popupWindow.width = binding.categoryTabs.measuredWidth
        popupWindow.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popupWindow.setBackgroundDrawable(null)
        popupWindow.elevation = resources.getDimension(R.dimen.listItemElevation)
        popupWindow.isOutsideTouchable = true
        popupWindow.showAsDropDown(binding.title)
    }

    private fun setPopupElementsDecoration(view: View) {
        view.domainList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val elementMargin =
                    view.context?.resources?.getDimension(R.dimen.smallMargin)?.toInt() ?: 0
                parent.adapter.run {
                    outRect.top = elementMargin
                    outRect.bottom = elementMargin
                    outRect.left = elementMargin
                    outRect.right = elementMargin
                }
            }
        })
    }

    private var selectedPosition: Int? = null

    override fun onStop() {
        super.onStop()
        selectedPosition = binding.categoryTabs.selectedTabPosition
    }

    private fun setTabs(domainId: Long?) {
        val categories =
            mainVM.categories?.filter { it.domain == domainId } ?: return
        for ((index, category) in categories.withIndex()) {
            val tab = binding.categoryTabs.newTab()
            tab.tag = category.id
            tab.text = category.title
            binding.categoryTabs.addTab(tab)
        }
        setTabsListener()
        selectedPosition?.run {
            binding.categoryTabs.getTabAt(this)?.select()
        } ?: binding.categoryTabs.getTabAt(0)?.select()
    }

    private fun setObservers() {
        mainVM.allArticlesState.observe(viewLifecycleOwner, Observer { onAllArticlesChange(it) })
    }

    private fun onAllArticlesChange(state: State?) {
        when (state) {
            State.SUCCESS -> {
                setTabs(domainId)
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun setTabsListener() {
        binding.categoryTabs.addOnTabSelectedListener(OnTabSelected(binding, mainVM))
    }

    private class OnTabSelected(
        private val binding: FragmentSubdomainBinding,
        private val mainVM: MainViewModel
    ) : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            updateList(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabReselected(tab: TabLayout.Tab?) {
            updateList(tab)
        }

        private fun updateList(tab: TabLayout.Tab?) {
            val adapter = binding.list.adapter as ArticleAdapter
            val originList = mainVM.allArticles.value ?: listOf()
            adapter.submitList(
                originList.filter { it.categoryId == tab?.tag.toString() }
            )
        }
    }

    private fun setRecyclerView(domainId: Long?) {
        val filteredList = mainVM.allArticles.value?.filter { it.domainId == domainId.toString() }
        binding.title.text = filteredList?.get(0)?.domainTitle
        val onActionClicked = { id: Long ->
            val bundle = Bundle()
            bundle.putLong("ITEM_ID", id)
            bundle.putString("TYPE", "ALL")
            findNavController().navigate(
                R.id.action_subDomainFragment_to_articleDetailFragment,
                bundle
            )
        }
        val adapter = ArticleAdapter(onActionClicked)
        binding.list.adapter = adapter
        adapter.submitList(filteredList)
    }

    private fun setDecoration() {
        binding.list.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                val elementMargin =
                    view.context?.resources?.getDimension(R.dimen.smallMargin)?.toInt() ?: 0
                parent.adapter.run {
                    if (position == 0) {
                        outRect.top = elementMargin
                        outRect.bottom = elementMargin
                        outRect.left = elementMargin
                        outRect.right = elementMargin

                    } else {
                        outRect.top = 0
                        outRect.bottom = elementMargin
                        outRect.left = elementMargin
                        outRect.right = elementMargin
                    }
                }
            }
        })
    }

}
