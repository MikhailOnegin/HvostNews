package ru.hvost.news.presentation.fragments.profile

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentProfileBinding
import ru.hvost.news.presentation.adapters.PetAdapter
import ru.hvost.news.utils.enums.State

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var navC: NavController

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
        setObservers()
        setListeners()
        navC = findNavController()
    }

    private fun setObservers() {
        mainVM.changeUserDataState.observe(viewLifecycleOwner, Observer { updateData(it) })
        mainVM.userDataState.observe(viewLifecycleOwner, Observer { setDataToBind(it) })
        mainVM.userPetsState.observe(viewLifecycleOwner, Observer { setPetsToBind(it) })
    }

    private fun setDataToBind(state: State) {
        when (state) {
            State.SUCCESS -> {
                bindData()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun updateData(state: State) {
        when (state) {
            State.SUCCESS -> {
                mainVM.loadUserData()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun setPetsToBind(state: State) {
        when (state) {
            State.SUCCESS -> {
                setRecyclerView()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindData() {
        val userData = mainVM.userDataResponse.value
        binding.second.text = userData?.surname
        binding.name.text = userData?.name + " " + userData?.patronymic
    }

    private fun setListeners() {
        binding.edit.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment) }
        binding.buttonCoupons.setOnClickListener {
            navC.navigate(R.id.action_profileFragment_to_myCouponsFragment)
        }
        binding.invite.setOnClickListener {
            navC.navigate(R.id.action_profileFragment_to_inviteFragment)
        }
    }

    private fun setRecyclerView() {
        val onActionClicked = { id: String ->
            val bundle = Bundle()
            bundle.putString("PET_ID", id)
            findNavController().navigate(R.id.action_profileFragment_to_petProfileFragment, bundle)
        }
        val adapter = PetAdapter(onActionClicked)
        binding.petList.adapter = adapter
        adapter.submitList(mainVM.userPetsResponse.value)
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