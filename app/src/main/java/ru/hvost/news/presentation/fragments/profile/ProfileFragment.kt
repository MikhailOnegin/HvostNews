package ru.hvost.news.presentation.fragments.profile

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
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentArticlesBinding
import ru.hvost.news.databinding.FragmentProfileBinding
import ru.hvost.news.presentation.adapters.PetAdapter

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var navC:NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setRecyclerView()
        setListeners()
        navC = findNavController()
    }

    private fun setListeners() {
        binding.edit.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment) }
        binding.buttonCoupons.setOnClickListener {
            navC.navigate(R.id.action_profileFragment_to_myCouponsFragment)
        }
    }

    private fun setRecyclerView() {
        val onActionClicked = { id: Long ->
            val bundle = Bundle()
            bundle.putLong("PET_ID", id)
            findNavController().navigate(R.id.action_profileFragment_to_petProfileFragment, bundle)
        }
        val adapter = PetAdapter(onActionClicked)
        binding.petList.adapter = adapter
        adapter.submitList(mainVM.testPets)
        setDecoration()
    }

    private fun setDecoration() {
        binding.petList.addItemDecoration(object : RecyclerView.ItemDecoration() {
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