package ru.hvost.news.presentation.fragments.login

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AdapterView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentRegPetBinding
import ru.hvost.news.models.Species
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.dialogs.DatePickerDialog
import ru.hvost.news.utils.hasBlankField
import ru.hvost.news.utils.hasTooLongField
import ru.hvost.news.utils.petBirthdayDateFormat
import ru.hvost.news.utils.scrollToTheTop
import java.util.*

class RegPetFragment : Fragment() {

    private lateinit var binding: FragmentRegPetBinding
    private lateinit var registrationVM: RegistrationVM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegPetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registrationVM = ViewModelProvider(requireActivity())[RegistrationVM::class.java]
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        registrationVM.setStage(RegistrationVM.RegStep.PET)
        registrationVM.petName?.run{ binding.petName.setText(this) }
        registrationVM.voucher?.run { binding.promocode.setText(this) }
        checkIfSecondStageFinished()
    }

    override fun onStop() {
        super.onStop()
        registrationVM.petName = binding.petName.text.toString()
        registrationVM.voucher = binding.promocode.text.toString()
    }

    private fun checkIfSecondStageFinished() {
        registrationVM.secondStageFinished.value = !binding.petName.text.isNullOrBlank()
    }

    @Suppress("UNCHECKED_CAST")
    private fun setSelectedSpecies() {
        registrationVM.species.value?.firstOrNull {
            it.id == registrationVM.petSpeciesId.toLong()
        }?.run {
            binding.spinner.setSelection(
                (binding.spinner.adapter as SpinnerAdapter<Species>).getPosition(this)
            )
        }
    }

    private fun setObservers() {
        registrationVM.species.observe(viewLifecycleOwner) { onSpeciesChanged(it) }
        registrationVM.petSex.observe(viewLifecycleOwner) { onPetSexChanged(it) }
        registrationVM.petBirthday.observe(viewLifecycleOwner) { onPetBirthdayChanged(it) }
        registrationVM.stage.observe(viewLifecycleOwner) { onStageChanged.invoke(it) }
        registrationVM.step.observe(viewLifecycleOwner) { onStepChanged(it) }
        registrationVM.secondStageFinished.observe(viewLifecycleOwner) { onSecondStageFinished(it) }
    }

    private fun onSecondStageFinished(isFinished: Boolean?) {
        isFinished?.run {
            binding.buttonNext.isEnabled = this
        }
    }

    private fun onPetBirthdayChanged(petBirthday: Date?) {
        petBirthday?.run {
            binding.petBirthday.setSelection(
                petBirthdayDateFormat.format(this)
            )
        }
    }

    private fun onPetSexChanged(petSex: Int?) {
        binding.sexMale.isSelected = false
        binding.sexFemale.isSelected = false
        binding.sexUnknown.isSelected = false
        when(petSex) {
            RegistrationVM.SEX_MALE -> binding.sexMale.isSelected = true
            RegistrationVM.SEX_FEMALE -> binding.sexFemale.isSelected = true
            RegistrationVM.SEX_UNKNOWN -> binding.sexUnknown.isSelected = true
        }
    }

    private fun onSpeciesChanged(species: List<Species>?) {
        species?.run {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.speciesSpinnerHint),
                this,
                Species::speciesName
            )
            binding.spinner.adapter = adapter
            binding.spinner.visibility = View.VISIBLE
            setSelectedSpecies()
        } ?: run {
            binding.spinner.visibility = View.GONE
        }
    }

    private fun setListeners() {
        binding.buttonNext.setOnClickListener(onButtonNextClicked)
        binding.sexMale.setOnClickListener(onSexClicked)
        binding.sexFemale.setOnClickListener(onSexClicked)
        binding.sexUnknown.setOnClickListener(onSexClicked)
        binding.petBirthday.setOnClickListener { onPetBirthdayClicked() }
        binding.spinner.onItemSelectedListener = OnSpeciesSelectedListener(registrationVM)
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.petName.doOnTextChanged { _, _, _, _ -> checkIfSecondStageFinished() }
    }

    private class OnSpeciesSelectedListener(
        private val registrationVM: RegistrationVM
    ): AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selection = parent?.getItemAtPosition(position) as Species
            registrationVM.petSpeciesId = selection.speciesId
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun onPetBirthdayClicked() {
        DatePickerDialog(
            onDateSelected = onPetBirthdaySelected,
            initialDate = registrationVM.petBirthday.value,
            maxDate = Date()
        ).show(childFragmentManager, "date_picker")
    }

    private val onPetBirthdaySelected: (Date) -> Unit = {
        registrationVM.petBirthday.value = it
    }

    private val onSexClicked = { view: View ->
        when(view.id) {
            R.id.sexMale -> registrationVM.petSex.value = RegistrationVM.SEX_MALE
            R.id.sexFemale -> registrationVM.petSex.value = RegistrationVM.SEX_FEMALE
            R.id.sexUnknown -> registrationVM.petSex.value = RegistrationVM.SEX_UNKNOWN
        }
    }

    private val onButtonNextClicked = { _: View ->
        if(isEverythingOk()) {
            findNavController().navigate(R.id.action_regPetFragment_to_regInterestsFragment)
        }
    }

    private fun isEverythingOk(): Boolean {
        binding.run {
            val fields = arrayOf(petName, promocode)
            if(hasBlankField(*fields)) {
                scrollToTheTop(binding.scrollView)
                return false
            }
            if(hasTooLongField(*fields)) {
                scrollToTheTop(binding.scrollView)
                return false
            }
            setViewModelFields()
            return true
        }
    }

    private fun setViewModelFields() {
        registrationVM.petName = binding.petName.text.toString()
        registrationVM.voucher = binding.promocode.text.toString()
    }

    private fun onStepChanged(step: RegistrationVM.RegStep) {
        when(step) {
            RegistrationVM.RegStep.USER -> {
                binding.subtitle.text = getString(R.string.regStepUser)
                binding.step.text = getString(R.string.regStep1)
            }
            RegistrationVM.RegStep.PET -> {
                binding.subtitle.text = getString(R.string.regStepPet)
                binding.step.text = getString(R.string.regStep2)
            }
            RegistrationVM.RegStep.INTERESTS -> {
                binding.subtitle.text = getString(R.string.regStepInterests)
                binding.step.text = getString(R.string.regStep3)
            }
        }
    }

    private var animator: Animator? = null
    private val onStageChanged: (Pair<Int,Int>)->Unit = { progress ->
        animator?.cancel()
        animator = ObjectAnimator.ofInt(
            binding.progress,
            "progress",
            progress.first,
            progress.second)
        animator?.duration = resources.getInteger(R.integer.regProgressAnimationTime).toLong()
        animator?.interpolator = AccelerateDecelerateInterpolator()
        animator?.start()
    }

}