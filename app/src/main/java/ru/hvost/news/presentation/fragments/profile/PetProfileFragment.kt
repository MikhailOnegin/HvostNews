package ru.hvost.news.presentation.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentPetProfileBinding
import ru.hvost.news.models.Breeds
import ru.hvost.news.models.Species
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
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
        binding.delete.setOnClickListener {
            val petData =
                mainVM.userPetsResponse.value?.filter { it.petId == arguments?.getString("PET_ID") }
            if (!petData.isNullOrEmpty()) {
                mainVM.deletePet(petData[0].petId)
                mainVM.loadPetsData()
                findNavController().popBackStack()
            }
        }
        setSpinnerListener()
    }

    private fun setSpinnerListener() {
        binding.type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                sendSpecies(p3)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun sendSpecies(position: Long) {
        val pet = mainVM.petsSpeciesResponse.value?.filter { it.id == position }
        if (pet.isNullOrEmpty()) return
        mainVM.loadBreeds(pet[0].speciesId)
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
        mainVM.petsBreedsResponse.observe(viewLifecycleOwner) { setBreeds(it) }
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
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.speciesSpinnerHint),
                this,
                Species::speciesName
            )
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

    private fun setBreeds(breeds: List<Breeds>?) {
        breeds?.run {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.breedsSpinnerHint),
                this,
                Breeds::breedName
            )
            binding.breed.adapter = adapter
            adapter.notifyDataSetChanged()
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