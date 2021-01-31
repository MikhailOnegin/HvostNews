package ru.hvost.news.presentation.fragments.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.layout_confirm_pet_deleting.view.*
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentPetProfileBinding
import ru.hvost.news.models.*
import ru.hvost.news.presentation.activities.MainActivity
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.fragments.login.RegistrationVM
import ru.hvost.news.utils.createSnackbar
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
    private var petData: Pets? = null
    private val birthday = MutableLiveData<String>()
    private var sendBreed = true

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
        petData =
            mainVM.userPets.value?.firstOrNull { it.petId == arguments?.getString(ProfileFragment.PET_ID) }
        initPassportData()
        checkIsDataLoaded()
        initializeEventObservers()
        setObservers()
        setListeners()
    }

    private fun initPassportData() {
        petData?.petId?.let { mainVM.getPetPassport(it) }
        mainVM.getVaccines()
        mainVM.getDeworming()
        mainVM.getExoparazites()
        mainVM.getPetFood()
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
            doOnSuccess = { setBreeds() },
            doOnError = {
                sendBreed = false
                binding.breed.visibility = View.GONE
            },
            invokeErrorSnackbar = false
        )
        onPetSpeciesLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { onSpeciesChanged() }
        )
        onUpdatePetLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                mainVM.loadPetsData()
                createSnackbar(
                    (requireActivity() as MainActivity).getMainView(),
                    getString(R.string.petDataChangedSuccessfully),
                ).show()
                findNavController().popBackStack()
            }
        )
    }

    private fun setListeners() {
        binding.sexMale.setOnClickListener(onSexClicked)
        binding.sexFemale.setOnClickListener(onSexClicked)
        binding.sexUnknown.setOnClickListener(onSexClicked)
        binding.birthday.setOnClickListener(openDatePickerDialog)
        binding.passport.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(ProfileFragment.PET_ID, petData?.petId)
            findNavController()
                .navigate(R.id.action_petProfileFragment_to_petPassportFragment, bundle)
        }
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.cancel.setOnClickListener { findNavController().popBackStack() }
        binding.delete.setOnClickListener { deletePetConfirmDialog() }
        binding.save.setOnClickListener { sendPetDataToServer() }
        setSpinnerListener()
    }

    private fun deletePetConfirmDialog() {
        val confirmDialog =
            BottomSheetDialog(requireContext(), R.style.popupBottomSheetDialogTheme)
        confirmDialog.dismissWithAnimation = true
        val confirmDialogBinding =
            layoutInflater.inflate(R.layout.layout_confirm_pet_deleting, binding.root, false)
        confirmDialogBinding.buttonCancel.setOnClickListener { confirmDialog.dismiss() }
        confirmDialogBinding.buttonConfirm.setOnClickListener {
            val petData =
                mainVM.userPets.value?.firstOrNull { it.petId == arguments?.getString("PET_ID") }
            if (petData != null) {
                mainVM.deletePet(petData.petId)
                mainVM.loadPetsData()
                findNavController().popBackStack()
            }
            confirmDialog.dismiss()
        }
        confirmDialog.setContentView(confirmDialogBinding)
        confirmDialog.setOnShowListener {
            confirmDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            confirmDialog.behavior.skipCollapsed = true
        }
        confirmDialog.show()
    }

    private fun sendPetDataToServer() {
        petData?.petId?.let {
            if (sendBreed || binding.breed.visibility == View.VISIBLE) {
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
            } else {
                mainVM.updatePet(
                    petId = it,
                    petName = binding.name.text.toString(),
                    petSpecies = (binding.type.selectedItem as Species).speciesId.toString(),
                    petSex = petSex.toString(),
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
        if (!petData?.petSpecies.isNullOrEmpty()) setSelectedSpecie()
    }

    private fun setSelectedSpecie() {
        val selectedSpecie =
            mainVM.petsSpecies.value?.firstOrNull { it.speciesId == petData?.petSpecies?.toInt() }
                ?.id ?: 0
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
        if (!petData?.petEducation.isNullOrEmpty()) setSelectedEducation()
    }

    private fun setSelectedEducation() {
        val selectedEducation = mainVM.petEducation.value?.firstOrNull {
            it.educationId == petData?.petEducation?.firstOrNull()?.educationId
        }
        val educationId = selectedEducation?.id?.toInt() ?: 0
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
        if (!petData?.petToy.isNullOrEmpty()) setSelectedToy()
    }

    private fun setSelectedToy() {
        val selectedToy =
            mainVM.petToys.value?.firstOrNull { it.toyId == petData?.petToy?.firstOrNull()?.toyId }
        val toyId = selectedToy?.id?.toInt() ?: 0
        binding.favToy.setSelection(toyId)
    }

    private fun setBreeds() {
        mainVM.petsBreeds.value?.run {
            binding.breed.visibility = View.VISIBLE
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.breedsSpinnerHint),
                this,
                Breeds::breedName
            )
            binding.breed.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        if (!petData?.petBreed.isNullOrEmpty()) setSelectedBreed()
    }

    private fun setSelectedBreed() {
        val selectedBreed =
            mainVM.petsBreeds.value?.firstOrNull { it.breedId == petData?.petBreed }
                ?.id?.toInt() ?: 0
        binding.breed.setSelection(selectedBreed)
    }

    private fun bindData() {
        birthday.value = petData?.petBirthday
        binding.name.setText(petData?.petName)
        binding.delicious.setText(petData?.petDelicies)
        binding.badHabit.setText(petData?.petBadHabbit)
        binding.chip.setText(petData?.petChip)
        binding.switchSaloons.isChecked = petData?.visitsSaloons == true
        binding.switchShows.isChecked = petData?.isPetForShows == true
        binding.switchSport.isChecked = petData?.isSportsPet == true
        binding.switchTitules.isChecked = petData?.hasTitles == true
        onPetSexChanged(petData?.petSex)
    }
}