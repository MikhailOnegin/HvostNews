package ru.hvost.news.presentation.fragments.articles
//
//import android.graphics.Rect
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.lifecycle.ViewModelProvider
//import androidx.navigation.NavController
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.tabs.TabLayout
//import ru.hvost.news.MainViewModel
//import ru.hvost.news.R
//import ru.hvost.news.databinding.FragmentArticlesBinding
//import ru.hvost.news.models.CheckboxStates
//import ru.hvost.news.models.InterestsCategory
//import ru.hvost.news.presentation.activities.MainActivity
//import ru.hvost.news.presentation.adapters.ArticleAdapter
//import ru.hvost.news.presentation.dialogs.ArticlesFilterCustomDialog
import ru.hvost.news.presentation.fragments.BaseFragment
//import ru.hvost.news.utils.enums.State
//import ru.hvost.news.utils.events.DefaultNetworkEventObserver
//import ru.hvost.news.utils.events.OneTimeEvent
//
class ArticlesFragment : BaseFragment() {
//
//    private lateinit var binding: FragmentArticlesBinding
//    private lateinit var mainVM: MainViewModel
//    private lateinit var navC: NavController
//    private lateinit var onChangeUserDataLoadingEvent: DefaultNetworkEventObserver
//    private val filterDialog = ArticlesFilterCustomDialog()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentArticlesBinding.inflate(inflater, container, false)
//        setDecoration()
//        if (arguments?.getString("CATEGORY") == "NEWS") binding.tabLayout.getTabAt(2)?.select()
//        return binding.root
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
//        mainVM.loadUserData()
//        navC = findNavController()
//        initializeObservers()
//        setListeners()
//        setObservers()
//    }
//
//    private fun initializeObservers() {
//        onChangeUserDataLoadingEvent = DefaultNetworkEventObserver(
//            anchorView = binding.root,
//            doOnSuccess = {
//                mainVM.loadArticles()
//                mainVM.loadUserData()
//            }
//        )
//    }
//
//    private fun setListeners() {
//        binding.filter.setOnClickListener {
//            filterDialog.show(childFragmentManager, "info_dialog")
//        }
//        binding.tabLayout.addOnTabSelectedListener(
//            OnTabSelected(
//                navC,
//                binding,
//                mainVM
//            )
//        )
//    }
//
//    private class OnTabSelected(
//        private val nav: NavController,
//        private val binding: FragmentArticlesBinding,
//        private val mainVM: MainViewModel
//    ) : TabLayout.OnTabSelectedListener {
//        override fun onTabSelected(tab: TabLayout.Tab?) {
//            updateList(tab)
//        }
//
//        override fun onTabUnselected(tab: TabLayout.Tab?) {}
//
//        override fun onTabReselected(tab: TabLayout.Tab?) {
//            updateList(tab)
//        }
//
//        private fun updateList(tab: TabLayout.Tab?) {
//            when (tab?.text) {
//                "Лента" -> loadArticles()
////                "Статьи" -> nav.navigate(R.id.action_newsFragment_to_domainFragment)
//                "Новости" -> loadNews()
//            }
//        }
//
//        private fun loadArticles() {
//            binding.articlesFilter.visibility = View.VISIBLE
//            val adapter = binding.list.adapter as ArticleAdapter
//            adapter.submitList(mainVM.articles.value)
//        }
//
//        private fun loadNews() {
//            binding.articlesFilter.visibility = View.GONE
//            val adapter = binding.list.adapter as ArticleAdapter
//            val originList = mainVM.allArticles.value ?: listOf()
//            adapter.submitList(
//                originList.filter { it.categoryTitle == "Новости" }
//            )
//        }
//    }
//
//    private fun setObservers() {
////        mainVM.articlesState.observe(viewLifecycleOwner, { onArticleStateChanged(it) })
////        mainVM.changeUserDataLoadingEvent.observe(viewLifecycleOwner, onChangeUserDataLoadingEvent)
////        mainVM.closeArticlesFilterCustomDialog.observe(
////            viewLifecycleOwner,
////            OneTimeEvent.Observer { closeDialog() })
////        mainVM.updateArticlesWithNewInterests.observe(viewLifecycleOwner,
////            OneTimeEvent.Observer { updateArticles() })
//    }
//
//    private fun updateArticles() {
//        val interests = mainVM.interests.value ?: listOf()
//        val sendList: MutableList<String> = mutableListOf()
//        interests.map { category ->
//            if ((category as InterestsCategory).sendParent) {
//                sendList.add(category.categoryId)
//            } else {
//                category.interests.map { interest ->
//                    if (interest.state == CheckboxStates.SELECTED) {
//                        sendList.add(interest.interestId)
//                    }
//                }
//            }
//        }
//        mainVM.changeUserData(interests = sendList.joinToString())
//        closeDialog()
//    }
//
//    private fun closeDialog() {
//        mainVM.interests.value?.map { category ->
//            (category as InterestsCategory).sendParent = false
//            category.state = CheckboxStates.UNSELECTED
//            category.interests.map { interest ->
//                interest.state = CheckboxStates.UNSELECTED
//            }
//        }
//        filterDialog.dismiss()
//    }
//
//    private fun onArticleStateChanged(state: State) {
//        when (state) {
//            State.SUCCESS -> {
//                setRecyclerView()
//            }
//            State.FAILURE, State.ERROR -> {
//            }
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        (requireActivity() as MainActivity).showBnv()
//    }
//
//    private fun setRecyclerView() {
//        val onActionClicked = { id: String ->
//            val bundle = Bundle()
//            bundle.putString(ARTICLE_ID, id)
//            bundle.putString("TYPE", "INDIVIDUAL")
////            findNavController().navigate(R.id.action_newsFragment_to_articleDetailFragment, bundle)
//        }
//        val adapter = ArticleAdapter(onActionClicked)
//        binding.list.adapter = adapter
//        if (arguments?.getString("CATEGORY") == "NEWS") {
//            binding.articlesFilter.visibility = View.GONE
//            adapter.submitList(mainVM.allArticles.value?.filter { it.categoryTitle == "Новости" })
//        } else {
//            adapter.submitList(mainVM.articles.value)
//        }
//    }
//
//    private fun setDecoration() {
//        binding.list.addItemDecoration(object : RecyclerView.ItemDecoration() {
//            override fun getItemOffsets(
//                outRect: Rect,
//                view: View,
//                parent: RecyclerView,
//                state: RecyclerView.State
//            ) {
//                val position = parent.getChildAdapterPosition(view)
//                val elementMargin =
//                    view.context?.resources?.getDimension(R.dimen.largeMargin)?.toInt() ?: 0
//                parent.adapter.run {
//                    outRect.bottom = elementMargin
//                }
//            }
//        })
//    }
//
    companion object {

        const val ARTICLE_ID = "article_id"

    }
//
}