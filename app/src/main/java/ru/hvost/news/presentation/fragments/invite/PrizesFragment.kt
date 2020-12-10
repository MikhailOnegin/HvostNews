package ru.hvost.news.presentation.fragments.invite

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentPrizesBinding
import ru.hvost.news.presentation.adapters.PrizeCategoryAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class PrizesFragment : BaseFragment() {

    private lateinit var binding: FragmentPrizesBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onBonusBalanceLoadingEvent: DefaultNetworkEventObserver


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrizesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        if (mainVM.bonusBalanceLoadingEvent.value?.peekContent() == State.SUCCESS)
            binding.balance.text = mainVM.bonusBalance.value?.bonusBalance.toString()
        initializeObservers()
        setListeners()
        setObservers()
    }

    private fun initializeObservers() {
        onBonusBalanceLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = {
                binding.balance.text = mainVM.bonusBalance.value?.bonusBalance.toString()
            }
        )
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    private fun setObservers() {
        mainVM.bonusBalanceLoadingEvent.observe(viewLifecycleOwner, onBonusBalanceLoadingEvent)
        mainVM.prizeCategoriesState.observe(viewLifecycleOwner, { onPrizeCategoriesChanged(it) })
    }

    private fun onPrizeCategoriesChanged(state: State?) {
        when (state) {
            State.SUCCESS -> {
                setRecyclerView()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun setRecyclerView() {
        val onActionClicked = { categoryId: String ->
            val bundle = Bundle()
            bundle.putString("PRIZE_ID", categoryId)
            mainVM.loadPrizes(categoryId)
            findNavController().navigate(R.id.action_prizesFragment_to_choicePrizeFragment, bundle)
        }
        val adapter = PrizeCategoryAdapter(onActionClicked)
        binding.prizeList.adapter = adapter
        adapter.submitList(mainVM.prizeCategoriesResponse.value)
        setDecoration()
    }

    private fun setDecoration() {
        binding.prizeList.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
                }
            }
        })
    }
}