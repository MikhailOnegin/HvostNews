package ru.hvost.news.presentation.fragments.domains

import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.layout_popup_domains.view.*
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSubdomainBinding
import ru.hvost.news.presentation.adapters.recycler.ArticleAdapter
import ru.hvost.news.presentation.adapters.recycler.PopupWindowDomainAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.fragments.feed.FeedFragment
import ru.hvost.news.utils.LinearRvItemDecorations
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class SubDomainFragment : BaseFragment() {

    private lateinit var binding: FragmentSubdomainBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var nav: NavController
    private lateinit var onAllArticlesLoadingEvent: DefaultNetworkEventObserver
    private lateinit var popupView: View
    private lateinit var popupWindow: PopupWindow
    private lateinit var callback: OnBackPressedCallback
    private var domain: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        domain = if (domain == null) arguments?.getLong("DOMAIN_ID") else domain
        binding = FragmentSubdomainBinding.inflate(inflater, container, false)
        binding.list.addItemDecoration(
            LinearRvItemDecorations(
                sideMarginsDimension = R.dimen.largeMargin,
                marginBetweenElementsDimension = R.dimen.normalMargin
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        nav = findNavController()
        initializePopup()
        checkIsDataLoaded()
        initializeObservers()
        setObservers()
        setListeners()
    }

    private fun initializePopup() {
        popupView = layoutInflater.inflate(R.layout.layout_popup_domains, binding.root, false)
        popupWindow = PopupWindow(requireActivity())
        setPopupElementsDecoration(popupView)
    }

    private fun initializeObservers() {
        onAllArticlesLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = { setData() }
        )
    }

    private fun setData() {
        setTabs(domain)
        setRecyclerView(domain)
    }

    private fun checkIsDataLoaded() {
        if (mainVM.allArticlesLoadingEvent.value?.peekContent() == State.SUCCESS) setData()
    }

    private fun setListeners() {
        binding.titleContainer.setOnClickListener { callPopup() }
        binding.toolbar.setNavigationOnClickListener { nav.popBackStack() }
    }

    override fun onPause() {
        super.onPause()
        popupWindow.dismiss()
    }

    private fun callPopup() {
        val onActionClicked = { domain: Long ->
            setTabs(domain)
            setRecyclerView(domain)
            popupWindow.dismiss()
        }
        val adapter = PopupWindowDomainAdapter(onActionClicked)
        popupView.domainList.adapter = adapter
        adapter.submitList(mainVM.domains)

        popupWindow.contentView = popupView
        popupWindow.width = binding.categoryTabs.measuredWidth
        popupWindow.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popupWindow.setBackgroundDrawable(null)
        popupWindow.elevation = resources.getDimension(R.dimen.listItemElevation)
        popupWindow.isFocusable = true
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
        binding.categoryTabs.removeAllTabs()
        val categories =
            mainVM.categories?.filter { it.domain == domainId } ?: return
        for ((index, category) in categories.withIndex()) {
            val tab = binding.categoryTabs.newTab()
            when {
                index == 0 -> {
                    tab.view.background =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.background_category_first_tab_item,
                            null
                        )
                }
                (index + 1) == categories.size -> {
                    tab.view.background =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.background_category_last_tab_item,
                            null
                        )
                }
                else -> {
                    tab.view.background =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.background_category_tab_item,
                            null
                        )
                }
            }
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
        mainVM.allArticlesLoadingEvent.observe(viewLifecycleOwner, onAllArticlesLoadingEvent)
    }

    private fun setTabsListener() {
        binding.categoryTabs.addOnTabSelectedListener(OnTabSelected(binding, domain, nav, mainVM))
    }

    private class OnTabSelected(
        private val binding: FragmentSubdomainBinding,
        private val domainId: Long?,
        private val nav: NavController,
        private val mainVM: MainViewModel
    ) : TabLayout.OnTabSelectedListener {

        override fun onTabSelected(tab: TabLayout.Tab?) {
            checkTabPosition(tab)
            updateList(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabReselected(tab: TabLayout.Tab?) {
            checkTabPosition(tab)
            updateList(tab)
        }

        private fun checkTabPosition(tab: TabLayout.Tab?) {
            val categories = mainVM.categories?.filter { it.domain == domainId }?.size ?: return
            when (tab?.position) {
                0 -> binding.categoryTabs.setSelectedTabIndicator(R.drawable.background_category_first_tab_indicator)
                (categories - 1) -> binding.categoryTabs.setSelectedTabIndicator(
                    R.drawable.background_category_last_tab_indicator
                )
                else -> binding.categoryTabs.setSelectedTabIndicator(R.drawable.background_category_tab_indicator)
            }
        }

        private fun updateList(tab: TabLayout.Tab?) {
            val onActionClicked = { id: String ->
                val bundle = Bundle()
                bundle.putString(FeedFragment.ARTICLE_ID, id)
                nav.navigate(
                    R.id.action_subDomainFragment_to_articleDetailFragment,
                    bundle
                )
            }
            val onActionLiked = { id: String, isLiked: Boolean ->
                mainVM.setArticleLiked(id, isLiked)
            }
            val adapter = ArticleAdapter(onActionClicked, onActionLiked)
            binding.list.adapter = adapter
            val originList =
                mainVM.allArticles.value?.filter { it.categoryId == tab?.tag.toString() }
                    ?: listOf()
            adapter.submitList(originList)
        }
    }

    private fun setRecyclerView(domainId: Long?) {
        domain = domainId ?: domain
        val filteredList = mainVM.allArticles.value?.filter { it.domainId == domainId.toString() }
        binding.title.text = filteredList?.get(0)?.domainTitle
    }

}
