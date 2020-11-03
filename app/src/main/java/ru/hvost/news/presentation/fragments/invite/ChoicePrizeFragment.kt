package ru.hvost.news.presentation.fragments.invite

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentChoicePrizeBinding
import ru.hvost.news.presentation.adapters.PrizeProductsAdapter
import ru.hvost.news.utils.enums.State

class ChoicePrizeFragment : Fragment() {

    private lateinit var binding: FragmentChoicePrizeBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChoicePrizeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        binding.toCart.setOnClickListener {
            mainVM.addPrizeToCart(mainVM.prizes.value?.filter { it.prizeId == arguments?.getString("PRIZE_ID") }
                ?.get(0)?.prizeId)
        }
    }

    private fun setObservers() {
        mainVM.prizesState.observe(viewLifecycleOwner, { onPrizesChanged(it) })
        mainVM.prizeToCartState.observe(viewLifecycleOwner, { onCartChanged(it) })
    }

    private fun onCartChanged(state: State?) {
        when (state) {
            State.SUCCESS -> {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.sendedSuccessfull),
                    Toast.LENGTH_SHORT
                ).show()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun onPrizesChanged(state: State?) {
        when (state) {
            State.SUCCESS -> {
                binding.title.text =
                    mainVM.prizes.value?.filter { it.prizeId == arguments?.getString("PRIZE_ID") }
                        ?.get(0)?.category
                setRecyclerView()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun setRecyclerView() {
        val onActionClicked = { product: String ->
            val bundle = Bundle()
            bundle.putString("PRODUCT_ID", product)
            findNavController().navigate(R.id.action_domainFragment_to_subDomainFragment, bundle)
        }
        val adapter = PrizeProductsAdapter(onActionClicked)
        binding.priceList.adapter = adapter
        adapter.submitList(mainVM.prizes.value?.filter { it.prizeId == arguments?.getString("PRIZE_ID") }
            ?.get(0)?.products)
        setDecoration()
    }

    private fun setDecoration() {
        binding.priceList.addItemDecoration(object : RecyclerView.ItemDecoration() {
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