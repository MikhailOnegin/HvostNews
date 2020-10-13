package ru.hvost.news.presentation.fragments.domains

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
import ru.hvost.news.databinding.FragmentDomainBinding
import ru.hvost.news.presentation.adapters.DomainAdapter

class DomainFragment : Fragment() {

    private lateinit var binding: FragmentDomainBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDomainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setRecyclerView()
    }

    private fun setRecyclerView() {
        val onActionClicked = { domain: String ->
            val bundle = Bundle()
            bundle.putString("DOMAIN_NAME", domain)
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