package ru.hvost.news.presentation.fragments.domains

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentDomainBinding
import ru.hvost.news.presentation.adapters.DomainAdapter
import ru.hvost.news.presentation.fragments.BaseFragment

class DomainFragment : BaseFragment() {

    private lateinit var binding: FragmentDomainBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var navC: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDomainBinding.inflate(inflater, container, false)
        binding.tabLayout.getTabAt(1)?.select()
        return binding.root
    }

    private class OnTabSelected(
        private val nav: NavController
    ) : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab?.text) {
                "Лента" -> {
                    val bundle = Bundle()
                    bundle.putString("CATEGORY", "ARTICLES")
                    nav.navigate(
                        R.id.action_domainFragment_to_newsFragment,
                        bundle
                    )
                }
                "Новости" -> {
                    val bundle = Bundle()
                    bundle.putString("CATEGORY", "NEWS")
                    nav.navigate(
                        R.id.action_domainFragment_to_newsFragment,
                        bundle
                    )
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabReselected(tab: TabLayout.Tab?) {
            when (tab?.text) {
                "Лента" -> nav.popBackStack()
                "Новости" -> nav.popBackStack()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        navC = findNavController()
        binding.tabLayout.addOnTabSelectedListener(OnTabSelected(navC))
        setRecyclerView()
    }

    private fun setRecyclerView() {
        val onActionClicked = { domain: Long ->
            val bundle = Bundle()
            bundle.putLong("DOMAIN_ID", domain)
            findNavController().navigate(R.id.action_domainFragment_to_subDomainFragment, bundle)
        }
        val adapter = DomainAdapter(onActionClicked)
        binding.list.adapter = adapter
        adapter.submitList(mainVM.domains)
        setDecoration()
    }

    private fun setDecoration() {
        binding.list.addItemDecoration(object : RecyclerView.ItemDecoration() {
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

}