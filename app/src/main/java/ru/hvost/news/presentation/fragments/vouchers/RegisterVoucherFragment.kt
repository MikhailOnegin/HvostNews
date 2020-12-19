package ru.hvost.news.presentation.fragments.vouchers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentRegisterVoucherBinding
import ru.hvost.news.models.Pets
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.dialogs.AddPetCustomDialog
import ru.hvost.news.presentation.dialogs.SubmitActionDialog
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.NetworkEvent

class RegisterVoucherFragment : BaseFragment() {

    private lateinit var binding: FragmentRegisterVoucherBinding
    private lateinit var vouchersVM: VouchersViewModel
    private lateinit var mainVM: MainViewModel
    private lateinit var checkVouchersEventObserver: DefaultNetworkEventObserver
    private lateinit var registerVouchersEventObserver: RegisterVoucherNetworkEventObserver
    private lateinit var loadingVouchersEventObserver: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vouchersVM = ViewModelProvider(requireActivity())[VouchersViewModel::class.java]
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        initializeObservers()
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        mainVM.loadPetsData()
    }

    override fun onDestroy() {
        super.onDestroy()
        vouchersVM.reset()
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.buttonCheckCode.setOnClickListener { onButtonCheckCodeClicked() }
        binding.voucherCode.doOnTextChanged { text, _, _, _ ->
            vouchersVM.setIsReadyToCheckVoucher(!text.isNullOrEmpty())
        }
        binding.buttonCancel.setOnClickListener { onClearVoucher() }
        binding.buttonActivate.setOnClickListener { onActivateButtonClicked() }
        binding.addPet.setOnClickListener { onAddPetClicked() }
    }

    private fun onAddPetClicked() {
        AddPetCustomDialog().show(
            childFragmentManager,
            "addPetDialog"
        )
    }

    private fun onActivateButtonClicked(forceRegister: Boolean = false) {
        vouchersVM.registerVoucher(
            voucherCode = binding.voucherCode.text.toString(),
            petId = (binding.spinner.selectedItem as Pets).petId,
            forceRegister = forceRegister
        )
    }

    private fun setObservers() {
        vouchersVM.run {
            checkVoucherEvent.observe(viewLifecycleOwner, checkVouchersEventObserver)
            registerVoucherEvent.observe(viewLifecycleOwner, registerVouchersEventObserver)
            isReadyToCheckVoucher.observe(viewLifecycleOwner) { onIsReadyToCheckChanged(it) }
            isVoucherCorrect.observe(viewLifecycleOwner) { onIsVoucherCorrectChanged(it) }
        }
        mainVM.run {
            userPets.observe(viewLifecycleOwner) { onPetsChanged(it) }
            loadingVouchersEvent.observe(viewLifecycleOwner, loadingVouchersEventObserver)
        }
    }

    private fun onPetsChanged(pets: List<Pets>?) {
        pets?.run {
            binding.spinner.adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.vouchersPetsSpinnerHint),
                this,
                Pets::petName
            )
        }
    }

    private fun onClearVoucher() {
        vouchersVM.reset()
        binding.voucherCode.setText("")
        binding.buttonCancel.visibility = View.GONE
    }

    private fun onIsVoucherCorrectChanged(isCorrect: Boolean?) {
        isCorrect?.run {
            if(isCorrect) {
                binding.voucherCode.isEnabled = false
                binding.voucherProgram.visibility = View.VISIBLE
                binding.buttonActivate.visibility = View.VISIBLE
                binding.controlsLayout.visibility = View.VISIBLE
                binding.buttonCheckCode.visibility = View.GONE
                binding.buttonCancel.visibility = View.VISIBLE
            } else {
                binding.voucherCode.isEnabled = true
                binding.voucherProgram.visibility = View.GONE
                binding.buttonActivate.visibility = View.GONE
                binding.controlsLayout.visibility = View.GONE
                binding.buttonCheckCode.visibility = View.VISIBLE
            }
        }
    }

    private fun initializeObservers() {
        checkVouchersEventObserver = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = onVoucherCheckSuccess
        )
        registerVouchersEventObserver = RegisterVoucherNetworkEventObserver(
            binding.root,
            doOnSuccess = {
                mainVM.updateVouchers(App.getInstance().userToken)
            },
            doOnError = onRegisterVoucherError,
            vouchersVM = vouchersVM
        )
        loadingVouchersEventObserver = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = {
                if ((mainVM.vouchers.value?.size ?: 0) > 1) {
                    findNavController().navigate(R.id.action_registerVoucherFragment_to_vouchersFragment)
                }
            }
        )
    }

    private val onRegisterVoucherError = {
        if (vouchersVM.registerVoucherResponse?.result == "attention") {
            SubmitActionDialog(
                title = getString(R.string.promocodeOverrideWarningTitle),
                message = getString(R.string.promocodeOverrideWarning),
                onSubmitButtonClicked = { onActivateButtonClicked(forceRegister = true) }
            ).show(
                childFragmentManager,
                "submit_override_voucher_dialog"
            )
        }
    }

    private val onVoucherCheckSuccess = {
        binding.voucherProgram.setSelection(vouchersVM.voucherProgram)
    }

    private fun onIsReadyToCheckChanged(isReady: Boolean?) {
        isReady?.run {
            binding.buttonCheckCode.isEnabled = isReady
            binding.voucherCodeLayout.isEndIconVisible = isReady
        }
    }

    private fun onButtonCheckCodeClicked() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        vouchersVM.checkVoucher(binding.voucherCode.text.toString())
    }

    class RegisterVoucherNetworkEventObserver(
        private val anchorView: View,
        private val doOnSuccess: (()->Unit)? = null,
        private val doOnError: (()->Unit)? = null,
        private val doOnFailure: (()->Unit)? = null,
        private val vouchersVM: VouchersViewModel
    ) : Observer<NetworkEvent<State>> {

        @Suppress("ControlFlowWithEmptyBody")
        override fun onChanged(event: NetworkEvent<State>?) {
            event?.run {
                event.getContentIfNotHandled()?.run {
                    val context = App.getInstance()
                    when(this) {
                        State.SUCCESS -> doOnSuccess?.invoke()
                        State.ERROR -> {
                            if (vouchersVM.registerVoucherResponse?.result != "attention") {
                                createSnackbar(
                                    anchorView = anchorView,
                                    text = error ?: context.getString(R.string.networkErrorMessage),
                                    context.getString(R.string.buttonOk),
                                    doOnError
                                ).show()
                            } else { doOnError?.invoke() }
                        }
                        State.FAILURE -> {
                            createSnackbar(
                                anchorView = anchorView,
                                text = context.getString(R.string.networkFailureMessage),
                                context.getString(R.string.buttonOk),
                                doOnFailure
                            ).show()
                        }
                        else -> {}
                    }
                }
            }
        }

    }

}