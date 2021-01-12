package ru.hvost.news.presentation.fragments.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentPetProfileBinding
import ru.hvost.news.models.*
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.fragments.login.RegistrationVM
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.tryStringToDate
import java.text.SimpleDateFormat
import java.util.*

class PetProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentPetProfileBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onPetToysLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onUserPetsLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onPetSpeciesLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onPetBreedsLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onPetEducationLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onUpdatePetLoadingEvent: DefaultNetworkEventObserver
    private var petSex: Int? = null
    private var petData: List<Pets>? = listOf()
    private val birthday = MutableLiveData<String>()
    private val myFormat = "dd.MM.yyyy"
    private val sdf = SimpleDateFormat(myFormat)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPetProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        petData = mainVM.userPets.value?.filter { it.petId == arguments?.getString("PET_ID") }
        petData?.get(0)?.petId?.let { mainVM.getPetPassport(it) }
        checkIsDataLoaded()
        initializeEventObservers()
        setObservers()
        setListeners()
    }

    private fun checkIsDataLoaded() {
        if (mainVM.petToysLoadingEvent.value?.peekContent() == State.SUCCESS)
            onPetToysChanged(mainVM.petToys.value)
        if (mainVM.petEducationLoadingEvent.value?.peekContent() == State.SUCCESS)
            onPetEducationChanged(mainVM.petEducation.value)
        if (mainVM.userPetsLoadingEvent.value?.peekContent() == State.SUCCESS)
            bindData()
        if (mainVM.petsSpeciesLoadingEvent.value?.peekContent() == State.SUCCESS)
            onSpeciesChanged()
    }

    private fun initializeEventObservers() {
        onPetToysLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { onPetToysChanged(mainVM.petToys.value) }
        )
        onPetEducationLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { onPetEducationChanged(mainVM.petEducation.value) }
        )
        onUserPetsLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { bindData() }
        )
        onPetBreedsLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { setBreeds() }
        )
        onPetSpeciesLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { onSpeciesChanged() }
        )
        onUpdatePetLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.sendedSuccessfull),
                    Toast.LENGTH_SHORT
                ).show()
                mainVM.loadPetsData()
                findNavController().popBackStack()
            }
        )
    }

    private fun setListeners() {
        binding.sexMale.setOnClickListener(onSexClicked)
        binding.sexFemale.setOnClickListener(onSexClicked)
        binding.sexUnknown.setOnClickListener(onSexClicked)
        binding.birthday.setOnClickListener(openDatePickerDialog)
        binding.passport.setOnClickListener { findNavController().navigate(R.id.action_petProfileFragment_to_petPassportFragment) }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.cancel.setOnClickListener { findNavController().popBackStack() }
        binding.delete.setOnClickListener {
            val petData =
                mainVM.userPets.value?.filter { it.petId == arguments?.getString("PET_ID") }
            if (!petData.isNullOrEmpty()) {
                mainVM.deletePet(petData[0].petId)
                mainVM.loadPetsData()
                findNavController().popBackStack()
            }
        }
        binding.save.setOnClickListener { sendPetDataToServer() }
        setSpinnerListener()
    }

    private fun sendPetDataToServer() {
        petData?.get(0)?.petId?.let {
            mainVM.updatePet(
                petId = it,
                petName = binding.name.text.toString(),
                petSpecies = (binding.type.selectedItem as Species).speciesId.toString(),
                petSex = petSex.toString(),
                petBreed = (binding.breed.selectedItem as Breeds).breedId,
                petBirthday = birthday.value.toString(),
                petDelicies = binding.delicious.text.toString(),
                petToy = (binding.favToy.selectedItem as Toys).toyId,
                petBadHabbit = binding.badHabit.text.toString(),
                petChip = binding.chip.text.toString(),
                isPetForShows = binding.switchShows.isChecked,
                hasTitles = binding.switchTitules.isChecked,
                isSportsPet = binding.switchSport.isChecked,
                visitsSaloons = binding.switchSaloons.isChecked,
                petEducation = (binding.education.selectedItem as PetEducation).educationId
            )
        }
    }

    private val openDatePickerDialog = View.OnClickListener { showDatePicker() }

    @SuppressLint("SimpleDateFormat")
    private fun showDatePicker() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat)
        ru.hvost.news.presentation.dialogs.DatePickerDialog(
            initialDate = tryStringToDate(birthday.value.toString()),
            onDateSelected = {
                birthday.value = sdf.format(it.time)
                birthday.value = sdf.format(it.time)
            },
            maxDate = Date()
        ).show(childFragmentManager, "date_picker")
    }

    private fun setSpinnerListener() {
        binding.type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                mainVM.loadBreeds((binding.type.selectedItem as Species).speciesId)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private val onSexClicked = { view: View ->
        when (view.id) {
            R.id.sexMale -> {
                binding.sexMale.isSelected = true
                binding.sexFemale.isSelected = false
                binding.sexUnknown.isSelected = false
                petSex = RegistrationVM.SEX_MALE
            }
            R.id.sexFemale -> {
                binding.sexMale.isSelected = false
                binding.sexFemale.isSelected = true
                binding.sexUnknown.isSelected = false
                petSex = RegistrationVM.SEX_FEMALE
            }
            R.id.sexUnknown -> {
                binding.sexMale.isSelected = false
                binding.sexFemale.isSelected = false
                binding.sexUnknown.isSelected = true
                petSex = null
            }
        }
    }

    private fun setObservers() {
        mainVM.userPetsLoadingEvent.observe(viewLifecycleOwner, onUserPetsLoadingEvent)
        mainVM.petsSpeciesLoadingEvent.observe(viewLifecycleOwner, onPetSpeciesLoadingEvent)
        mainVM.petsBreedsLoadingEvent.observe(viewLifecycleOwner, onPetBreedsLoadingEvent)
        mainVM.petToysLoadingEvent.observe(viewLifecycleOwner, onPetToysLoadingEvent)
        mainVM.petEducationLoadingEvent.observe(viewLifecycleOwner, onPetEducationLoadingEvent)
        mainVM.updatePetLoadingEvent.observe(viewLifecycleOwner, onUpdatePetLoadingEvent)
        birthday.observe(viewLifecycleOwner, { onDateChanged() })
    }

    private fun onDateChanged() {
        binding.birthday.setSelection(birthday.value.toString())
    }

    private fun onPetSexChanged(sex: String?) {
        binding.sexMale.isSelected = false
        binding.sexFemale.isSelected = false
        binding.sexUnknown.isSelected = false
        when (sex) {
            RegistrationVM.SEX_MALE.toString() -> {
                binding.sexMale.isSelected = true
                petSex = RegistrationVM.SEX_MALE
            }
            RegistrationVM.SEX_FEMALE.toString() -> {
                binding.sexFemale.isSelected = true
                petSex = RegistrationVM.SEX_FEMALE
            }
            else -> binding.sexUnknown.isSelected = true
        }
    }

    private fun onSpeciesChanged() {
        mainVM.petsSpecies.value?.run {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.speciesSpinnerHint),
                this,
                Species::speciesName
            )
            binding.type.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        if (!petData?.get(0)?.petSpecies.isNullOrEmpty()) setSelectedSpecie()
    }

    private fun setSelectedSpecie() {
        val selectedSpecie =
            mainVM.petsSpecies.value?.filter { it.speciesId == petData?.get(0)?.petSpecies?.toInt() }
                ?.get(0)?.id ?: 0
        binding.type.setSelection(selectedSpecie.dec().toInt())
    }

    private fun onPetEducationChanged(education: List<PetEducation>?) {
        education?.run {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.education),
                this,
                PetEducation::name
            )
            binding.education.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        if (!petData?.get(0)?.petEducation.isNullOrEmpty()) setSelectedEducation()
    }

    private fun setSelectedEducation() {
        val selectedEducation = mainVM.petEducation.value?.filter {
            it.educationId == petData?.get(0)?.petEducation?.get(0)?.educationId
        }
        val educationId = selectedEducation?.get(0)?.id?.toInt() ?: 0
        binding.education.setSelection(educationId)
    }

    private fun onPetToysChanged(toys: List<Toys>?) {
        toys?.run {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.toy),
                this,
                Toys::name
            )
            binding.favToy.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        if (!petData?.get(0)?.petToy.isNullOrEmpty()) setSelectedToy()
    }

    private fun setSelectedToy() {
        val selectedToy =
            mainVM.petToys.value?.filter { it.toyId == petData?.get(0)?.petToy?.get(0)?.toyId }
        val toyId = selectedToy?.get(0)?.id?.toInt() ?: 0
        binding.favToy.setSelection(toyId)
    }

    private fun setBreeds() {
        mainVM.petsBreeds.value?.run {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.breedsSpinnerHint),
                this,
                Breeds::breedName
            )
            binding.breed.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        if (!petData?.get(0)?.petBreed.isNullOrEmpty()) setSelectedBreed()
    }

    private fun setSelectedBreed() {
        val selectedBreed =
            mainVM.petsBreeds.value?.filter { it.breedId == petData?.get(0)?.petBreed }
                ?.get(0)?.id?.toInt() ?: 0
        binding.breed.setSelection(selectedBreed)
    }

    private fun bindData() {
        birthday.value = petData?.get(0)?.petBirthday
        binding.name.setText(petData?.get(0)?.petName)
        binding.delicious.setText(petData?.get(0)?.petDelicies)
        binding.badHabit.setText(petData?.get(0)?.petBadHabbit)
        binding.chip.setText(petData?.get(0)?.petChip)
        binding.switchSaloons.isChecked = petData?.get(0)?.visitsSaloons == true
        binding.switchShows.isChecked = petData?.get(0)?.isPetForShows == true
        binding.switchSport.isChecked = petData?.get(0)?.isSportsPet == true
        binding.switchTitules.isChecked = petData?.get(0)?.hasTitles == true
        onPetSexChanged(petData?.get(0)?.petSex)
    }
}