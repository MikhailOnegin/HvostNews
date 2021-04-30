package ru.hvost.news.presentation.fragments.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentDomainsGridBinding
import ru.hvost.news.presentation.adapters.recycler.DomainAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.GridRvItemDecorations
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class DomainsGridFragment : BaseFragment() {

    private lateinit var binding: FragmentDomainsGridBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onArticlesLoadingEvent: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDomainsGridBinding.inflate(inflater, container, false)
        binding.root.addItemDecoration(GridRvItemDecorations(
            R.dimen.largeMargin,
            R.dimen.normalMargin,
            App.getInstance().resources.getDimension(R.dimen.extraLargeMargin).toInt(),
            true
        ))
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        if (mainVM.allArticlesLoadingEvent.value?.peekContent() == State.SUCCESS) setRecyclerView()
        initializeObservers()
        setObservers()
    }

    private fun setObservers() {
        mainVM.allArticlesLoadingEvent.observe(viewLifecycleOwner, onArticlesLoadingEvent)
    }

    private fun initializeObservers() {
        onArticlesLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { setRecyclerView() }
        )
    }

    private fun setRecyclerView() {
        val onActionClicked = { domain: Long ->
            val bundle = Bundle()
            bundle.putLong("DOMAIN_ID", domain)
            requireActivity().findNavController(R.id.nav_host_fragment)
                .navigate(R.id.action_feedFragment_to_subDomainFragment, bundle)
        }
        val adapter = DomainAdapter(onActionClicked)
        binding.list.adapter = adapter
        adapter.submitList(mainVM.domains)
    }

}