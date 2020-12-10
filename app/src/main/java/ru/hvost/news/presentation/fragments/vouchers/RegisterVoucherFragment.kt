package ru.hvost.news.presentation.fragments.vouchers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentRegisterVoucherBinding
import ru.hvost.news.models.Pets
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.dialogs.AddPetCustomDialog
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class RegisterVoucherFragment : BaseFragment() {

    private lateinit var binding: FragmentRegisterVoucherBinding
    private lateinit var vouchersVM: VouchersViewModel
    private lateinit var mainVM: MainViewModel
    private lateinit var checkVouchersEventObserver: DefaultNetworkEventObserver
    private lateinit var registerVouchersEventObserver: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterVoucherBinding.inflate(inflater, container, false)
        initializeObservers()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vouchersVM = ViewModelProvider(requireActivity())[VouchersViewModel::class.java]
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
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

    private fun onActivateButtonClicked() {
        vouchersVM.registerVoucher(
            voucherCode = binding.voucherCode.text.toString(),
            petId = (binding.spinner.selectedItem as Pets).petId
        )
    }

    private fun setObservers() {
        vouchersVM.run {
            checkVoucherEvent.observe(viewLifecycleOwner, checkVouchersEventObserver)
            registerVoucherEvent.observe(viewLifecycleOwner, registerVouchersEventObserver)
            isReadyToCheckVoucher.observe(viewLifecycleOwner) { onIsReadyToCheckChanged(it) }
            isVoucherCorrect.observe(viewLifecycleOwner) { onIsVoucherCorrectChanged(it) }
        }
        mainVM.userPets.observe(viewLifecycleOwner) { onPetsChanged(it) }
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
    }

    private fun onIsVoucherCorrectChanged(isCorrect: Boolean?) {
        isCorrect?.run {
            if(isCorrect) {
                binding.voucherCode.isEnabled = false
                binding.voucherProgram.visibility = View.VISIBLE
                binding.buttonActivate.visibility = View.VISIBLE
                binding.controlsLayout.visibility = View.VISIBLE
                binding.buttonCheckCode.visibility = View.GONE
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
        registerVouchersEventObserver = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = { findNavController().popBackStack() }
        )
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

}