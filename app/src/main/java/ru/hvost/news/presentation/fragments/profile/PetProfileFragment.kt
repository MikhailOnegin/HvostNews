package ru.hvost.news.presentation.fragments.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.layout_add_other.view.*
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
    private lateinit var onPetSportsLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onPetFeedLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onPetBadHabitsLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onPetDeliciesLoadingEvent: DefaultNetworkEventObserver
    private var petSex: Int? = null
    private var petData: Pets? = null
    private val birthday = MutableLiveData<String>()
    private var sendBreed = true
    private var deliciousBefore: Int = 0
    private var feedBefore: Int = 0
    private var toyBefore: Int = 0
    private var badHabitBefore: Int = 0
    private var sportsBefore: Int = 0
    private var educationBefore: Int = 0

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
        initPetProfileLists()
        initPassportData()
        checkIsDataLoaded()
        initializeEventObservers()
        setObservers()
        setListeners()
    }

    private fun initPetProfileLists() {
        mainVM.loadPetEducation(petData?.petSpecies)
        mainVM.loadPetToys(petData?.petSpecies)
        mainVM.getPetBadHabits(petData?.petSpecies)
        mainVM.getPetDelicies(petData?.petSpecies)
        mainVM.getPetSports(petData?.petSpecies)
    }

    private fun initPassportData() {
        petData?.petId?.let { mainVM.getPetPassport(it) }
        mainVM.getVaccines()
        mainVM.getDeworming()
        mainVM.getExoparazites()
        mainVM.getPetFood()
        mainVM.getPetDiseases()
        mainVM.getNotificationPeriod()
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
        if (mainVM.petSportsLoadingEvent.value?.peekContent() == State.SUCCESS)
            onPetSportChanged()
        if (mainVM.petFeedLoadingEvent.value?.peekContent() == State.SUCCESS)
            onPetFeedChanged()
        if (mainVM.petBadHabitsLoadingEvent.value?.peekContent() == State.SUCCESS)
            onPetBadHabitsChanged()
        if (mainVM.petDeliciesLoadingEvent.value?.peekContent() == State.SUCCESS)
            onPetDeliciesChanged()
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
        onPetSportsLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { onPetSportChanged() }
        )
        onPetFeedLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { onPetFeedChanged() }
        )
        onPetBadHabitsLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { onPetBadHabitsChanged() }
        )
        onPetDeliciesLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { onPetDeliciesChanged() }
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
        val petToy = checkPetToy()
        val petEducation = checkPetEducation()
        val petSports = checkPetSports()
        val petFeed = checkPetFeed()
        val petBadHabbits = checkPetBadHabits()
        val petDelicies = checkPetDelicies()
        petData?.petId?.let {
            if (sendBreed || binding.breed.visibility == View.VISIBLE) {
                mainVM.updatePet(
                    petId = it,
                    petName = binding.name.text.toString(),
                    petSpecies = (binding.type.selectedItem as Species).speciesId.toString(),
                    petSex = petSex.toString(),
                    petBreed = (binding.breed.selectedItem as Breeds).breedId,
                    petBirthday = birthday.value.toString(),
                    petDelicies = petDelicies ?: "",
                    petToy = petToy ?: "",
                    petFeed = petFeed ?: "",
                    petBadHabbit = petBadHabbits ?: "",
                    petChip = binding.chip.text.toString(),
                    isPetForShows = binding.switchShows.isChecked,
                    hasTitles = binding.switchTitules.isChecked,
                    petSports = petSports ?: "",
                    visitsSaloons = binding.switchSaloons.isChecked,
                    petEducation = petEducation ?: ""
                )
            } else {
                mainVM.updatePet(
                    petId = it,
                    petName = binding.name.text.toString(),
                    petSpecies = (binding.type.selectedItem as Species).speciesId.toString(),
                    petSex = petSex.toString(),
                    petBirthday = birthday.value.toString(),
                    petDelicies = petDelicies ?: "",
                    petToy = petToy ?: "",
                    petFeed = petFeed ?: "",
                    petBadHabbit = petBadHabbits ?: "",
                    petChip = binding.chip.text.toString(),
                    isPetForShows = binding.switchShows.isChecked,
                    hasTitles = binding.switchTitules.isChecked,
                    petSports = petSports ?: "",
                    visitsSaloons = binding.switchSaloons.isChecked,
                    petEducation = petEducation ?: ""
                )
            }
        }
    }

    private fun checkPetToy(): String? {
        return if ((binding.favToy.selectedItem as? PetToys)?.id == MainViewModel.OTHER_NEW_ID ||
            (binding.favToy.selectedItem as? PetToys)?.id == MainViewModel.OTHER_ID
        ) {
            (binding.favToy.selectedItem as? PetToys)?.name
        } else {
            (binding.favToy.selectedItem as? PetToys)?.toyId
        }
    }

    private fun checkPetSports(): String? {
        return if ((binding.petSports.selectedItem as? PetSports)?.index == MainViewModel.OTHER_NEW_ID ||
            (binding.petSports.selectedItem as? PetSports)?.index == MainViewModel.OTHER_ID
        ) {
            (binding.petSports.selectedItem as? PetSports)?.name
        } else {
            (binding.petSports.selectedItem as? PetSports)?.sportId
        }
    }

    private fun checkPetBadHabits(): String? {
        return if ((binding.badHabit.selectedItem as? PetBadHabbits)?.index == MainViewModel.OTHER_NEW_ID ||
            (binding.badHabit.selectedItem as? PetBadHabbits)?.index == MainViewModel.OTHER_ID
        ) {
            (binding.badHabit.selectedItem as? PetBadHabbits)?.name
        } else {
            (binding.badHabit.selectedItem as? PetBadHabbits)?.habbitId
        }
    }

    private fun checkPetDelicies(): String? {
        return if ((binding.delicious.selectedItem as? PetDelicies)?.index == MainViewModel.OTHER_NEW_ID ||
            (binding.delicious.selectedItem as? PetDelicies)?.index == MainViewModel.OTHER_ID
        ) {
            (binding.delicious.selectedItem as? PetDelicies)?.name
        } else {
            (binding.delicious.selectedItem as? PetDelicies)?.delliciousId
        }
    }

    private fun checkPetFeed(): String? {
        return if ((binding.feed.selectedItem as? PetFeed)?.index == MainViewModel.OTHER_NEW_ID ||
            (binding.feed.selectedItem as? PetFeed)?.index == MainViewModel.OTHER_ID
        ) {
            (binding.feed.selectedItem as? PetFeed)?.name
        } else {
            (binding.feed.selectedItem as? PetFeed)?.feedId
        }
    }

    private fun checkPetEducation(): String? {
        return if ((binding.education.selectedItem as? PetEducation)?.id == MainViewModel.OTHER_NEW_ID ||
            (binding.education.selectedItem as? PetEducation)?.id == MainViewModel.OTHER_ID
        ) {
            (binding.education.selectedItem as? PetEducation)?.name
        } else {
            (binding.education.selectedItem as? PetEducation)?.educationId
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
        binding.apply {
            delicious.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if ((binding.delicious.selectedItem as PetDelicies).index == MainViewModel.OTHER_ID) {
                        showAddOtherDialog(binding.delicious)
                    } else {
                        deliciousBefore = binding.delicious.selectedItemPosition
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            favToy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if ((binding.favToy.selectedItem as PetToys).id == MainViewModel.OTHER_ID) {
                        showAddOtherDialog(binding.favToy)
                    } else {
                        toyBefore = binding.favToy.selectedItemPosition
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            education.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if ((binding.education.selectedItem as PetEducation).id == MainViewModel.OTHER_ID) {
                        showAddOtherDialog(binding.education)
                    } else {
                        educationBefore = binding.education.selectedItemPosition
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            badHabit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if ((binding.badHabit.selectedItem as PetBadHabbits).index == MainViewModel.OTHER_ID) {
                        showAddOtherDialog(binding.badHabit)
                    } else {
                        badHabitBefore = binding.badHabit.selectedItemPosition
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            petSports.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if ((binding.petSports.selectedItem as PetSports).index == MainViewModel.OTHER_ID) {
                        showAddOtherDialog(binding.petSports)
                    } else {
                        sportsBefore = binding.petSports.selectedItemPosition
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            feed.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if ((binding.feed.selectedItem as PetFeed).index == MainViewModel.OTHER_ID) {
                        showAddOtherDialog(binding.feed)
                    } else {
                        feedBefore = binding.feed.selectedItemPosition
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    mainVM.loadBreeds((binding.type.selectedItem as Species).speciesId)
                    mainVM.loadPetEducation(petData?.petSpecies)
                    mainVM.loadPetToys(petData?.petSpecies)
                    mainVM.getPetBadHabits(petData?.petSpecies)
                    mainVM.getPetDelicies(petData?.petSpecies)
                    mainVM.getPetSports(petData?.petSpecies)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    private fun showAddOtherDialog(view: Spinner) {
        var byAdd = false
        val before = checkBefore(view)
        val addOtherDialog =
            BottomSheetDialog(requireContext(), R.style.popupBottomSheetDialogTheme)
        val bottomSheetBinding =
            layoutInflater.inflate(R.layout.layout_add_other, binding.root, false)
        bottomSheetBinding.addOther.isEnabled = false
        bottomSheetBinding.otherTitle.doOnTextChanged { text, _, _, _ ->
            bottomSheetBinding.addOther.isEnabled = !text.isNullOrEmpty() && text.length > 2
        }
        bottomSheetBinding.addOther.setOnClickListener {
            when (view) {
                binding.delicious -> {
                    (view.adapter as SpinnerAdapter<PetDelicies>).apply {
                        insert(
                            PetDelicies(
                                index = MainViewModel.OTHER_NEW_ID,
                                delliciousId = "",
                                name = bottomSheetBinding.otherTitle.text.toString()
                            ),
                            view.selectedItemPosition
                        )
                        notifyDataSetChanged()
                        if ((getItem(view.selectedItemPosition - 1) as PetDelicies).index == MainViewModel.OTHER_NEW_ID) {
                            remove(getItem(view.selectedItemPosition - 1))
                            view.setSelection(view.selectedItemPosition - 1)
                        }
                    }
                }

                binding.favToy -> {
                    (view.adapter as SpinnerAdapter<PetToys>).apply {
                        insert(
                            PetToys(
                                id = MainViewModel.OTHER_NEW_ID,
                                toyId = "",
                                name = bottomSheetBinding.otherTitle.text.toString()
                            ),
                            view.selectedItemPosition
                        )
                        notifyDataSetChanged()
                        if ((getItem(view.selectedItemPosition - 1) as PetToys).id == MainViewModel.OTHER_NEW_ID) {
                            remove(getItem(view.selectedItemPosition - 1))
                            view.setSelection(view.selectedItemPosition - 1)
                        }
                    }
                }

                binding.education -> {
                    (view.adapter as SpinnerAdapter<PetEducation>).apply {
                        insert(
                            PetEducation(
                                id = MainViewModel.OTHER_NEW_ID,
                                educationId = "",
                                name = bottomSheetBinding.otherTitle.text.toString()
                            ),
                            view.selectedItemPosition
                        )
                        notifyDataSetChanged()
                        if ((getItem(view.selectedItemPosition - 1) as PetEducation).id == MainViewModel.OTHER_NEW_ID) {
                            remove(getItem(view.selectedItemPosition - 1))
                            view.setSelection(view.selectedItemPosition - 1)
                        }
                    }
                }

                binding.badHabit -> {
                    (view.adapter as SpinnerAdapter<PetBadHabbits>).apply {
                        insert(
                            PetBadHabbits(
                                index = MainViewModel.OTHER_NEW_ID,
                                habbitId = "",
                                name = bottomSheetBinding.otherTitle.text.toString()
                            ),
                            view.selectedItemPosition
                        )
                        notifyDataSetChanged()
                        if ((getItem(view.selectedItemPosition - 1) as PetBadHabbits).index == MainViewModel.OTHER_NEW_ID) {
                            remove(getItem(view.selectedItemPosition - 1))
                            view.setSelection(view.selectedItemPosition - 1)
                        }
                    }
                }

                binding.petSports -> {
                    (view.adapter as SpinnerAdapter<PetSports>).apply {
                        insert(
                            PetSports(
                                index = MainViewModel.OTHER_NEW_ID,
                                sportId = "",
                                name = bottomSheetBinding.otherTitle.text.toString()
                            ),
                            view.selectedItemPosition
                        )
                        notifyDataSetChanged()
                        if ((getItem(view.selectedItemPosition - 1) as PetSports).index == MainViewModel.OTHER_NEW_ID) {
                            remove(getItem(view.selectedItemPosition - 1))
                            view.setSelection(view.selectedItemPosition - 1)
                        }
                    }
                }

                binding.feed -> {
                    (view.adapter as SpinnerAdapter<PetFeed>).apply {
                        insert(
                            PetFeed(
                                index = MainViewModel.OTHER_NEW_ID,
                                feedId = "",
                                name = bottomSheetBinding.otherTitle.text.toString()
                            ),
                            view.selectedItemPosition
                        )
                        notifyDataSetChanged()
                        if ((getItem(view.selectedItemPosition - 1) as PetFeed).index == MainViewModel.OTHER_NEW_ID) {
                            remove(getItem(view.selectedItemPosition - 1))
                            view.setSelection(view.selectedItemPosition - 1)
                        }
                    }
                }
            }
            byAdd = true
            addOtherDialog.dismiss()
        }
        addOtherDialog.setOnDismissListener {
            if (!byAdd) {
                view.setSelection(before)
            } else {
                when (view) {
                    binding.delicious -> deliciousBefore = view.selectedItemPosition
                    binding.feed -> feedBefore = view.selectedItemPosition
                    binding.favToy -> toyBefore = view.selectedItemPosition
                    binding.badHabit -> badHabitBefore = view.selectedItemPosition
                    binding.petSports -> sportsBefore = view.selectedItemPosition
                    binding.education -> educationBefore = view.selectedItemPosition
                }
            }
        }
        addOtherDialog.setContentView(bottomSheetBinding)
        addOtherDialog.setOnShowListener {
            addOtherDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            addOtherDialog.behavior.skipCollapsed = true
        }
        addOtherDialog.show()
    }

    private fun checkBefore(view: Spinner): Int {
        return when (view) {
            binding.delicious -> deliciousBefore
            binding.feed -> feedBefore
            binding.favToy -> toyBefore
            binding.badHabit -> badHabitBefore
            binding.petSports -> sportsBefore
            binding.education -> educationBefore
            else -> 0
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
        mainVM.petDeliciesLoadingEvent.observe(viewLifecycleOwner, onPetDeliciesLoadingEvent)
        mainVM.petBadHabitsLoadingEvent.observe(viewLifecycleOwner, onPetBadHabitsLoadingEvent)
        mainVM.petFeedLoadingEvent.observe(viewLifecycleOwner, onPetFeedLoadingEvent)
        mainVM.petSportsLoadingEvent.observe(viewLifecycleOwner, onPetSportsLoadingEvent)
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

    private fun onPetDeliciesChanged() {
        mainVM.petDelicies.value?.run {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.deliciesSpinnerHint),
                this,
                PetDelicies::name
            )
            binding.delicious.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        if (!petData?.petDelicies.isNullOrEmpty()) setSelectedDelicies()
    }

    private fun onPetBadHabitsChanged() {
        mainVM.petBadHabits.value?.run {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.badHabitSpinnerHint),
                this,
                PetBadHabbits::name
            )
            binding.badHabit.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        if (!petData?.petBadHabbit.isNullOrEmpty()) setSelectedBadHabit()
    }

    private fun onPetFeedChanged() {
        mainVM.petFeed.value?.run {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.feedSpinnerHint),
                this,
                PetFeed::name
            )
            binding.feed.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        if (!petData?.petFeed.isNullOrEmpty()) setSelectedFeed()
    }

    private fun onPetSportChanged() {
        mainVM.petSports.value?.run {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.sport),
                this,
                PetSports::name
            )
            binding.petSports.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        if (!petData?.petSports.isNullOrEmpty()) setSelectedSport()
    }

    private fun setSelectedSpecie() {
        val selectedSpecie =
            mainVM.petsSpecies.value?.firstOrNull { it.speciesId == petData?.petSpecies?.toInt() }
                ?.id ?: 0
        binding.type.setSelection(selectedSpecie.dec().toInt())
    }

    private fun setSelectedDelicies() {
        val delicious = petData?.petDelicies?.first()
        if (delicious != null && delicious.delliciousId == "" && delicious.name.isNotEmpty()) {
            (binding.delicious.adapter as SpinnerAdapter<PetDelicies>).insert(
                PetDelicies(
                    index = MainViewModel.OTHER_NEW_ID,
                    delliciousId = "",
                    name = delicious.name
                ),
                (binding.delicious.adapter as SpinnerAdapter<PetDelicies>).count - 1
            )
            binding.delicious.setSelection((binding.delicious.adapter as SpinnerAdapter<PetDelicies>).count - 2)
        } else {
            val selectedDelicious =
                mainVM.petDelicies.value?.firstOrNull { it.delliciousId == delicious?.delliciousId }?.index
                    ?: 0
            binding.delicious.setSelection(selectedDelicious.toInt())
        }
    }

    private fun setSelectedBadHabit() {
        val badHabbit = petData?.petBadHabbit?.first()
        if (badHabbit != null && badHabbit.habbitId == "" && badHabbit.name.isNotEmpty()) {
            (binding.badHabit.adapter as SpinnerAdapter<PetBadHabbits>).insert(
                PetBadHabbits(
                    index = MainViewModel.OTHER_NEW_ID,
                    habbitId = "",
                    name = badHabbit.name
                ),
                (binding.badHabit.adapter as SpinnerAdapter<PetBadHabbits>).count - 1
            )
            binding.badHabit.setSelection((binding.badHabit.adapter as SpinnerAdapter<PetBadHabbits>).count - 2)
        } else {
            val selectedBadHabit =
                mainVM.petBadHabits.value?.firstOrNull { it.habbitId == badHabbit?.habbitId }?.index
                    ?: 0
            binding.badHabit.setSelection(selectedBadHabit.toInt())
        }
    }

    private fun setSelectedFeed() {
        val feed = petData?.petFeed?.first()
        if (feed != null && feed.feedId == "" && feed.name.isNotEmpty()) {
            (binding.feed.adapter as SpinnerAdapter<PetFeed>).insert(
                PetFeed(
                    index = MainViewModel.OTHER_NEW_ID,
                    feedId = "",
                    name = feed.name
                ),
                (binding.feed.adapter as SpinnerAdapter<PetFeed>).count - 1
            )
            binding.feed.setSelection((binding.feed.adapter as SpinnerAdapter<PetFeed>).count - 2)
        } else {
            val selectedFeed =
                mainVM.petFeed.value?.firstOrNull { it.feedId == feed?.feedId }?.index ?: 0
            binding.feed.setSelection(selectedFeed.toInt())
        }
    }

    private fun setSelectedSport() {
        val sport = petData?.petSports?.first()
        if (sport != null && sport.sportId == "" && sport.name.isNotEmpty()) {
            (binding.petSports.adapter as SpinnerAdapter<PetSports>).insert(
                PetSports(
                    index = MainViewModel.OTHER_NEW_ID,
                    sportId = "",
                    name = sport.name
                ),
                (binding.petSports.adapter as SpinnerAdapter<PetSports>).count - 1
            )
            binding.petSports.setSelection((binding.petSports.adapter as SpinnerAdapter<PetSports>).count - 2)
        } else {
            val selectedSport =
                mainVM.petFeed.value?.firstOrNull { it.feedId == sport?.sportId }?.index ?: 0
            binding.petSports.setSelection(selectedSport.toInt())
        }
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

    private fun onPetToysChanged(toys: List<PetToys>?) {
        toys?.run {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.toy),
                this,
                PetToys::name
            )
            binding.favToy.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        if (!petData?.petToy.isNullOrEmpty()) setSelectedToy()
    }

    private fun setSelectedToy() {
        val toy = petData?.petToy?.first()
        if (toy != null && toy.toyId == "" && toy.name.isNotEmpty()) {
            (binding.favToy.adapter as SpinnerAdapter<PetToys>).insert(
                PetToys(
                    id = MainViewModel.OTHER_NEW_ID,
                    toyId = "",
                    name = toy.name
                ),
                (binding.favToy.adapter as SpinnerAdapter<PetToys>).count - 1
            )
            binding.favToy.setSelection((binding.favToy.adapter as SpinnerAdapter<PetToys>).count - 2)
        } else {
            val selectedSport =
                mainVM.petToys.value?.firstOrNull { it.toyId == toy?.toyId }?.id ?: 0
            binding.favToy.setSelection(selectedSport.toInt())
        }
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
        binding.chip.setText(petData?.petChip)
        binding.switchSaloons.isChecked = petData?.visitsSaloons == true
        binding.switchShows.isChecked = petData?.isPetForShows == true
        binding.switchTitules.isChecked = petData?.hasTitles == true
        onPetSexChanged(petData?.petSex)
    }
}