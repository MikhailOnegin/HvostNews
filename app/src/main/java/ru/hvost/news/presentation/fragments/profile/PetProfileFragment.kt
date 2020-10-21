package ru.hvost.news.presentation.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentPetProfileBinding
import ru.hvost.news.models.Species
import ru.hvost.news.presentation.adapters.spinners.SpeciesSpinnerAdapter
import ru.hvost.news.presentation.fragments.login.RegistrationVM
import ru.hvost.news.utils.enums.State

class PetProfileFragment : Fragment() {

    private lateinit var binding: FragmentPetProfileBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setObservers()
        setListeners()
    }

    private fun setListeners() {
        binding.sexMale.setOnClickListener(onSexClicked)
        binding.sexFemale.setOnClickListener(onSexClicked)
        binding.sexUnknown.setOnClickListener(onSexClicked)
    }

    private val onSexClicked = { view: View ->
        when (view.id) {
            R.id.sexMale -> {
                binding.sexMale.isSelected = true
                binding.sexFemale.isSelected = false
                binding.sexUnknown.isSelected = false
            }
            R.id.sexFemale -> {
                binding.sexMale.isSelected = false
                binding.sexFemale.isSelected = true
                binding.sexUnknown.isSelected = false
            }
            R.id.sexUnknown -> {
                binding.sexMale.isSelected = false
                binding.sexFemale.isSelected = false
                binding.sexUnknown.isSelected = true
            }
        }
    }

    private fun setObservers() {
        mainVM.userPetsState.observe(viewLifecycleOwner, Observer { setDataToBind(it) })
        mainVM.petsSpeciesResponse.observe(viewLifecycleOwner) { onSpeciesChanged(it) }
    }

    private fun onPetSexChanged(petSex: Int?) {
        binding.sexMale.isSelected = false
        binding.sexFemale.isSelected = false
        binding.sexUnknown.isSelected = false
        when (petSex) {
            RegistrationVM.SEX_MALE -> binding.sexMale.isSelected = true
            RegistrationVM.SEX_FEMALE -> binding.sexFemale.isSelected = true
            null -> binding.sexUnknown.isSelected = true
        }
    }

    private fun onSpeciesChanged(species: List<Species>?) {
        species?.run {
            val adapter = SpeciesSpinnerAdapter(
                requireActivity(),
                R.layout.spinner_dropdown_view,
                getString(R.string.speciesSpinnerHint)
            )
            adapter.addAll(this)
            binding.type.adapter = adapter
            adapter.notifyDataSetChanged()
        }

    }

    private fun setDataToBind(state: State?) {
        when (state) {
            State.SUCCESS -> {
                bindData()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun bindData() {
        val petData =
            mainVM.userPetsResponse.value?.filter { it.petId == arguments?.getString("PET_ID") }
        if (petData.isNullOrEmpty()) return
        binding.name.setText(petData[0].petName)
        binding.birthday.setText(petData[0].petBirthday)
        binding.delicious.setText(petData[0].petDelicies)
        binding.badHabit.setText(petData[0].petBadHabbit)
        binding.chip.setText(petData[0].petChip)
        onPetSexChanged(petData[0].petSex.toInt())
    }
}