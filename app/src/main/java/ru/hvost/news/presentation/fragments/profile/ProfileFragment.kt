package ru.hvost.news.presentation.fragments.profile

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.layout_add_pet.view.*
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentProfileBinding
import ru.hvost.news.models.Species
import ru.hvost.news.presentation.adapters.PetAdapter
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.dialogs.AddPetCustomDialog
import ru.hvost.news.presentation.fragments.login.RegistrationVM
import ru.hvost.news.presentation.viewmodels.CouponViewModel
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.NetworkEvent
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var couponsMV: CouponViewModel
    private lateinit var navC: NavController

    override fun onStart() {
        setSystemUiVisibility()
        super.onStart()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InlinedApi")
    private fun setSystemUiVisibility() {
        requireActivity().window.run {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }

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
        mainVM.updateOrders(App.getInstance().userToken)
        couponsMV = ViewModelProvider(requireActivity())[CouponViewModel::class.java]
        couponsMV.getCoupons(App.getInstance().userToken!!)
        setObservers()
        setListeners()
        navC = findNavController()
    }

    private fun setObservers() {
        mainVM.changeUserDataState.observe(viewLifecycleOwner, Observer { updateData(it) })
        mainVM.userDataState.observe(viewLifecycleOwner, Observer { setDataToBind(it) })
        mainVM.userPetsState.observe(viewLifecycleOwner, Observer { setPetsToBind(it) })
        mainVM.bonusBalanceState.observe(viewLifecycleOwner, Observer { onBalanceChanged(it) })
        couponsMV.couponsState.observe(viewLifecycleOwner, Observer { onCouponsChanged(it) })
        mainVM.ordersInWork.observe(
            viewLifecycleOwner,
            { binding.inWorkStatus.text = it.toString() })
        mainVM.ordersConstructed.observe(
            viewLifecycleOwner,
            { binding.constructedStatus.text = it.toString() })
        mainVM.ordersFinished.observe(
            viewLifecycleOwner,
            { binding.finishedStatus.text = it.toString() })
    }

    private fun onCouponsChanged(state: State) {
        when (state) {
            State.SUCCESS -> {
                binding.couponsCount.text = couponsMV.couponsCount.toString()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
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

    private fun onBalanceChanged(state: State) {
        when (state) {
            State.SUCCESS -> {
                binding.balance.text = mainVM.bonusBalanceResponse.value?.bonusBalance.toString()
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
        binding.name.text = userData?.name
    }

    private fun setListeners() {
        binding.edit.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment) }
        binding.addPet.setOnClickListener { AddPetCustomDialog().show(childFragmentManager, "info_dialog") }
        binding.buttonCoupons.setOnClickListener {
            navC.navigate(R.id.action_profileFragment_to_myCouponsFragment)
        }
        binding.invite.setOnClickListener {
            navC.navigate(R.id.action_profileFragment_to_inviteFragment)
        }
        binding.choicePrize.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_prizesFragment)
        }
        binding.allOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
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
                    view.context?.resources?.getDimension(R.dimen.xSmallMargin)?.toInt() ?: 0
                parent.adapter.run {
                    if (position == 0) {
                        outRect.top = 0

                    } else {
                        outRect.top = elementMargin
                    }
                }
            }
        })
    }

}