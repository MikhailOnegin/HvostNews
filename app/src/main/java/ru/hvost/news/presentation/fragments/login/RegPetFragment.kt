package ru.hvost.news.presentation.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        registrationVM.loadSpecies()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        registrationVM.setStage(RegistrationVM.RegStep.PET)
    }

    private fun setObservers() {
        registrationVM.species.observe(viewLifecycleOwner) { onSpeciesChanged(it) }
        registrationVM.petSex.observe(viewLifecycleOwner) { onPetSexChanged(it) }
        registrationVM.petBirthday.observe(viewLifecycleOwner) { onPetBirthdayChanged(it) }
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
        //sergeev Настроить OnItemSelectedListener.
    }



    private fun onPetBirthdayClicked() {
        DatePickerDialog(onPetBirthdaySelected).show(childFragmentManager, "date_picker")
        //sergeev Ограничивать максимальную дату рождения сегодняшней.
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
                scrollToTheTop(binding.root)
                return false
            }
            if(hasTooLongField(*fields)) {
                scrollToTheTop(binding.root)
                return false
            }
            return true
        }
    }

}