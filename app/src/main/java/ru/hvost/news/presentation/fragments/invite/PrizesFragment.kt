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
import ru.hvost.news.databinding.FragmentInviteBinding
import ru.hvost.news.databinding.FragmentPrizesBinding
import ru.hvost.news.presentation.adapters.DomainAdapter
import ru.hvost.news.presentation.adapters.PrizeAdapter
import ru.hvost.news.utils.enums.State

class PrizesFragment : Fragment() {

    private lateinit var binding: FragmentPrizesBinding
    private lateinit var mainVM: MainViewModel

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
        setObservers()
    }

    private fun setObservers() {
        mainVM.bonusBalanceState.observe(viewLifecycleOwner, { onBalanceChanged(it) })
        mainVM.prizesState.observe(viewLifecycleOwner, { onPrizesChanged(it) })
    }

    private fun onBalanceChanged(state: State?) {
        when (state) {
            State.SUCCESS -> {
                binding.balance.text = mainVM.bonusBalanceResponse.value?.balance.toString()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun onPrizesChanged(state: State?) {
        when (state) {
            State.SUCCESS -> {
                setRecyclerView()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun setRecyclerView() {
        val onActionClicked = { prize: String ->
            val bundle = Bundle()
            bundle.putString("PRIZE_ID", prize)
            findNavController().navigate(R.id.action_prizesFragment_to_choicePrizeFragment, bundle)
        }
        val adapter = PrizeAdapter(onActionClicked)
        binding.prizeList.adapter = adapter
        adapter.submitList(mainVM.prizes.value)
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
                val position = parent.getChildAdapterPosition(view)
                val elementMargin =
                    view.context?.resources?.getDimension(R.dimen.smallMargin)?.toInt() ?: 0
                parent.adapter.run {
                    if (position % 2 == 0) {
                        outRect.top = elementMargin
                        outRect.bottom = elementMargin
                        outRect.left = elementMargin
                        outRect.right = elementMargin

                    } else {
                        outRect.top = elementMargin
                        outRect.bottom = elementMargin
                        outRect.left = 0
                        outRect.right = elementMargin
                    }
                }
            }
        })
    }
}