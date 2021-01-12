package ru.hvost.news.presentation.fragments.profile

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentProfileBinding
import ru.hvost.news.models.Order
import ru.hvost.news.presentation.activities.MainActivity
import ru.hvost.news.presentation.adapters.PetAdapter
import ru.hvost.news.presentation.dialogs.AddPetCustomDialog
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.CouponViewModel
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var couponsMV: CouponViewModel
    private lateinit var navC: NavController
    private lateinit var onUserPetsLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onUserDataLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onBonusBalanceLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onCouponsLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onChangeUserDataLoadingEvent: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        couponsMV = ViewModelProvider(requireActivity())[CouponViewModel::class.java]
        mainVM.updateOrders(App.getInstance().userToken)
        checkIsDataLoaded()
        initializeEventObservers()
        setObservers()
        setListeners()
        navC = findNavController()
    }

    private fun checkIsDataLoaded() {
        if (mainVM.userDataLoadingEvent.value?.peekContent() == State.SUCCESS) {
            bindData()
        } else if (mainVM.changeUserDataLoadingEvent.value?.peekContent() == State.SUCCESS) {
            mainVM.loadUserData()
        } else {
            mainVM.loadUserData()
        }
        if (couponsMV.couponsEvent.value?.peekContent() == State.SUCCESS) {
            binding.couponsCount.text = couponsMV.couponsCount.toString()
        } else {
            couponsMV.getCoupons(App.getInstance().userToken!!)
        }
        if (mainVM.userPetsLoadingEvent.value?.peekContent() == State.SUCCESS) {
            setRecyclerView()
        } else {
            mainVM.loadPetsData()
        }
        if (mainVM.petsSpeciesLoadingEvent.value?.peekContent() != State.SUCCESS) {
            mainVM.loadSpecies()
        }
        if (mainVM.petToysLoadingEvent.value?.peekContent() != State.SUCCESS) {
            mainVM.loadPetToys()
        }
        if (mainVM.petEducationLoadingEvent.value?.peekContent() != State.SUCCESS) {
            mainVM.loadPetEducation()
        }
        if (mainVM.petEducationLoadingEvent.value?.peekContent() != State.SUCCESS) {
            mainVM.loadPetEducation()
        }
        if (mainVM.bonusBalanceLoadingEvent.value?.peekContent() == State.SUCCESS) {
            val balance = mainVM.bonusBalance.value?.bonusBalance ?: 0
            binding.balance.text = balance.toString()
        } else {
            mainVM.getBonusBalance()
        }
    }

    private fun initializeEventObservers() {
        onUserPetsLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { setRecyclerView() }
        )
        onUserDataLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { bindData() }
        )
        onBonusBalanceLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                val balance = mainVM.bonusBalance.value?.bonusBalance ?: 0
                binding.balance.text = balance.toString()
            }
        )
        onCouponsLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { binding.couponsCount.text = couponsMV.couponsCount.toString() }
        )
        onChangeUserDataLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { mainVM.loadUserData() }
        )
    }

    private fun setObservers() {
        mainVM.changeUserDataLoadingEvent.observe(viewLifecycleOwner, onChangeUserDataLoadingEvent)
        mainVM.userDataLoadingEvent.observe(viewLifecycleOwner, onUserDataLoadingEvent)
        mainVM.userPetsLoadingEvent.observe(viewLifecycleOwner, onUserPetsLoadingEvent)
        mainVM.bonusBalanceLoadingEvent.observe(viewLifecycleOwner, onBonusBalanceLoadingEvent)
        couponsMV.couponsEvent.observe(viewLifecycleOwner, onCouponsLoadingEvent)
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

    @SuppressLint("SetTextI18n")
    private fun bindData() {
        val userData = mainVM.userData.value
        binding.second.text = userData?.surname
        binding.name.text = userData?.name
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment) }
        binding.addPet.setOnClickListener {
            AddPetCustomDialog().show(
                childFragmentManager,
                "info_dialog"
            )
        }
        binding.buttonCoupons.setOnClickListener {
            if (couponsMV.coupons.value?.coupons.isNullOrEmpty()) {
                createSnackbar(
                    binding.root,
                    getString(R.string.couponsIsNull),
                    getString(R.string.buttonOk)
                ).show()
            } else {
                findNavController().navigate(R.id.action_profileFragment_to_myCouponsFragment)
            }
        }
        binding.invite.setOnClickListener {
            navC.navigate(R.id.action_profileFragment_to_inviteFragment)
        }
        binding.choicePrize.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_prizesFragment)
        }
        binding.allOrders.setOnClickListener {
            if (mainVM.orders.value.isNullOrEmpty()) {
                createSnackbar(
                    binding.root,
                    getString(R.string.ordersIsNull),
                    getString(R.string.buttonOk)
                ).show()
            } else {
                findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
            }
        }
        binding.vouchers.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_vouchersFragment)
        }
        binding.logout.setOnClickListener { (requireActivity() as MainActivity).userLogOut() }
    }

    private fun setRecyclerView() {
        val onActionClicked = { id: String ->
            val bundle = Bundle()
            bundle.putString("PET_ID", id)
            findNavController().navigate(R.id.action_profileFragment_to_petProfileFragment, bundle)
        }
        val adapter = PetAdapter(onActionClicked)
        binding.petList.adapter = adapter
        adapter.submitList(mainVM.userPets.value)
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