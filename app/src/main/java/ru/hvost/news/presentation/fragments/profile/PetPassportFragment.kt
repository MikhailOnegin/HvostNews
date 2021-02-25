package ru.hvost.news.presentation.fragments.profile

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.layout_add_disease.view.*
import kotlinx.android.synthetic.main.layout_add_disease.view.mainContainer
import kotlinx.android.synthetic.main.layout_add_other.view.*
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentPetPassportBinding
import ru.hvost.news.databinding.LayoutAddDiseaseBinding
import ru.hvost.news.models.*
import ru.hvost.news.presentation.activities.MainActivity
import ru.hvost.news.presentation.adapters.recycler.PetDiseasesAdapter
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.tryStringToDate
import java.text.SimpleDateFormat
import java.util.*

class PetPassportFragment : BaseFragment() {

    private lateinit var binding: FragmentPetPassportBinding
    private lateinit var diseaseBinding: View
    private lateinit var addDiseaseDialog: BottomSheetDialog
    private lateinit var mainVM: MainViewModel
    private lateinit var onPetPassportLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onUpdatePetPassportLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onPetFoodLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onVaccineLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onDewormingLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onExoparazitesLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onNotificationPeriodLoadingEvent: DefaultNetworkEventObserver
    private val vaccineDate = MutableLiveData<String>()
    private val drugDate = MutableLiveData<String>()
    private val exoparazitesDate = MutableLiveData<String>()
    private var vaccineBefore: Int = 0
    private var drugBefore: Int = 0
    private var exoparazitesBefore: Int = 0
    private var foodBefore: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPetPassportBinding.inflate(inflater, container, false)
        diseaseBinding = layoutInflater.inflate(R.layout.layout_add_disease, binding.root, false)
        addDiseaseDialog = BottomSheetDialog(requireContext(), R.style.popupBottomSheetDialogTheme)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        checkIsDataLoaded()
        initializeObservers()
        setObservers()
        setListeners()
    }

    private fun checkIsDataLoaded() {
        if (mainVM.vaccinesLoadingEvent.value?.peekContent() == State.SUCCESS) bindVaccineList() else mainVM.getVaccines()
        if (mainVM.notificationsPeriodLoadingEvent.value?.peekContent() == State.SUCCESS) setPeriods() else mainVM.getNotificationPeriod()
        if (mainVM.petDiseasesLoadingEvent.value?.peekContent() != State.SUCCESS) mainVM.getPetDiseases()
        if (mainVM.dewormingLoadingEvent.value?.peekContent() == State.SUCCESS) bindDewormingList() else mainVM.getDeworming()
        if (mainVM.exoparazitesLoadingEvent.value?.peekContent() == State.SUCCESS) bindExoparazitesList() else mainVM.getExoparazites()
        if (mainVM.petFoodLoadingEvent.value?.peekContent() == State.SUCCESS) bindPetFoodList() else mainVM.getPetFood()
        if (mainVM.petPassportLoadingEvent.value?.peekContent() == State.SUCCESS) bindData() else arguments?.getString(
            ProfileFragment.PET_ID
        )?.let { mainVM.getPetPassport(it) }
    }

    private fun setPeriods() {
        mainVM.notificationsPeriod.value?.let {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.notifyPeriod),
                it,
                NotificationPeriod::value
            )
            binding.apply {
                vaccinePeriod.adapter = adapter
                dewormingPeriod.adapter = adapter
                parazitesPeriod.adapter = adapter
            }
            adapter.notifyDataSetChanged()
            if (mainVM.notificationsPeriodLoadingEvent.value?.peekContent() == State.SUCCESS) setPrepsPeriods()
        }
    }

    private fun setPrepsPeriods() {
        val passport = mainVM.petPassportResponse.value
        if (!passport?.vacinationPeriodId.isNullOrEmpty()) {
            val selected =
                mainVM.notificationsPeriod.value?.firstOrNull { it.periodId == passport?.vacinationPeriodId }?.index
                    ?: 0
            binding.vaccinePeriod.setSelection(selected.toInt())
        }
        if (!passport?.dewormingPeriodId.isNullOrEmpty()) {
            val selected =
                mainVM.notificationsPeriod.value?.firstOrNull { it.periodId == passport?.dewormingPeriodId }?.index
                    ?: 0
            binding.dewormingPeriod.setSelection(selected.toInt())
        }
        if (!passport?.exoparasitePeriodId.isNullOrEmpty()) {
            val selected =
                mainVM.notificationsPeriod.value?.firstOrNull { it.periodId == passport?.exoparasitePeriodId }?.index
                    ?: 0
            binding.parazitesPeriod.setSelection(selected.toInt())
        }
    }

    private fun initializeObservers() {
        onPetPassportLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = { bindData() }
        )
        onNotificationPeriodLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = { setPeriods() }
        )
        onUpdatePetPassportLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = {
                createSnackbar(
                    (requireActivity() as MainActivity).getMainView(),
                    getString(R.string.petDataChangedSuccessfully),
                ).show()
                findNavController().popBackStack()
            }
        )
        onPetFoodLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = { bindPetFoodList() }
        )
        onVaccineLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = { bindVaccineList() }
        )
        onDewormingLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = { bindDewormingList() }
        )
        onExoparazitesLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = { bindExoparazitesList() }
        )
    }

    private fun bindExoparazitesList() {
        mainVM.exoparazites.value?.let {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.drug),
                it,
                Preps::name
            )
            binding.parazites.adapter = adapter
            adapter.notifyDataSetChanged()
            if (mainVM.exoparazitesLoadingEvent.value?.peekContent() == State.SUCCESS) setExoparazites()
        }
    }

    private fun bindDewormingList() {
        mainVM.deworming.value?.let {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.drug),
                it,
                Preps::name
            )
            binding.drug.adapter = adapter
            adapter.notifyDataSetChanged()
            if (mainVM.dewormingLoadingEvent.value?.peekContent() == State.SUCCESS) setDeworming()
        }
    }

    private fun bindVaccineList() {
        mainVM.vaccines.value?.let {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.vaccine),
                it,
                Preps::name
            )
            binding.vaccine.adapter = adapter
            adapter.notifyDataSetChanged()
            if (mainVM.vaccinesLoadingEvent.value?.peekContent() == State.SUCCESS) setVaccine()
        }
    }

    private fun bindPetFoodList() {
        mainVM.petFood.value?.let {
            val adapter = SpinnerAdapter(
                requireActivity(),
                "",
                it,
                PetFood::foodName
            )
            binding.petFood.adapter = adapter
            adapter.notifyDataSetChanged()
            if (mainVM.petFoodLoadingEvent.value?.peekContent() == State.SUCCESS) setPetFood()
        }
    }

    private fun bindData() {
        binding.name.text = mainVM.petPassportResponse.value?.petName
        binding.switchClean.isChecked = mainVM.petPassportResponse.value?.isSterilised == true
        vaccineDate.value = mainVM.petPassportResponse.value?.vacinationDate.toString()
        drugDate.value = mainVM.petPassportResponse.value?.dewormingDate.toString()
        exoparazitesDate.value = mainVM.petPassportResponse.value?.exoparasitesDate.toString()
        binding.clinicName.setText(mainVM.petPassportResponse.value?.favouriteVetName)
        binding.address.setText(mainVM.petPassportResponse.value?.favouriteVetAdress)
        setRecyclerView()
        checkPeriodsVisibility()
        setSpinners()
    }

    private fun checkPeriodsVisibility() {
        if (!mainVM.petPassportResponse.value?.vacinationPeriodId.isNullOrEmpty()) {
            binding.vaccinePeriod.visibility = View.VISIBLE
            binding.vaccineDate.setIcon(R.drawable.ic_notification_active)
        }
        if (!mainVM.petPassportResponse.value?.dewormingPeriodId.isNullOrEmpty()) {
            binding.dewormingPeriod.visibility = View.VISIBLE
            binding.dewormingDate.setIcon(R.drawable.ic_notification_active)
        }
        if (!mainVM.petPassportResponse.value?.exoparasitePeriodId.isNullOrEmpty()) {
            binding.parazitesPeriod.visibility = View.VISIBLE
            binding.parazitesDate.setIcon(R.drawable.ic_notification_active)
        }
    }

    private fun setRecyclerView() {
        val adapter = PetDiseasesAdapter()
        binding.list.adapter = adapter
        adapter.submitList(mainVM.petPassportResponse.value?.diseases)
        setDecoration()
    }

    private fun setSpinners() {
        setVaccine()
        setDeworming()
        setExoparazites()
        setPetFood()
    }

    private fun setVaccine() {
        val petVaccine = mainVM.petPassportResponse.value?.vacineId
        val vaccine = mainVM.vaccines.value?.firstOrNull { it.id == petVaccine }
        if (mainVM.vaccinesLoadingEvent.value?.peekContent() == State.SUCCESS
                && vaccine != null
        ) {
            binding.vaccine.setSelection(vaccine.index.toInt())
        } else {
            (binding.vaccine.adapter as SpinnerAdapter<Preps>).insert(
                    Preps(
                            index = MainViewModel.OTHER_NEW_ID,
                            id = "",
                            name = petVaccine ?: "",
                            typeId = ""
                    ),
                    (binding.vaccine.adapter as SpinnerAdapter<Preps>).count - 1
            )
            binding.vaccine.setSelection((binding.vaccine.adapter as SpinnerAdapter<Preps>).count - 2)
        }
    }

    private fun setDeworming() {
        val petDeworming = mainVM.petPassportResponse.value?.dewormingId
        val deworming = mainVM.deworming.value?.firstOrNull { it.id == petDeworming }
        if (mainVM.dewormingLoadingEvent.value?.peekContent() == State.SUCCESS
                && deworming != null
        ) {
            binding.drug.setSelection(deworming.index.toInt())
        } else {
            (binding.drug.adapter as SpinnerAdapter<Preps>).insert(
                    Preps(
                            index = MainViewModel.OTHER_NEW_ID,
                            id = "",
                            name = petDeworming ?: "",
                            typeId = ""
                    ),
                    (binding.drug.adapter as SpinnerAdapter<Preps>).count - 1
            )
            binding.drug.setSelection((binding.drug.adapter as SpinnerAdapter<Preps>).count - 2)
        }
    }

    private fun setExoparazites() {
        val petExoparazites = mainVM.petPassportResponse.value?.exoparasiteId
        val exoparazites = mainVM.exoparazites.value?.firstOrNull { it.id == petExoparazites }
        if (mainVM.exoparazitesLoadingEvent.value?.peekContent() == State.SUCCESS
                && exoparazites != null
        ) {
            binding.parazites.setSelection(exoparazites.index.toInt())
        } else {
            (binding.parazites.adapter as SpinnerAdapter<Preps>).insert(
                    Preps(
                            index = MainViewModel.OTHER_NEW_ID,
                            id = "",
                            name = petExoparazites ?: "",
                            typeId = ""
                    ),
                    (binding.parazites.adapter as SpinnerAdapter<Preps>).count - 1
            )
            binding.parazites.setSelection((binding.parazites.adapter as SpinnerAdapter<Preps>).count - 2)
        }
    }

    private fun setPetFood() {
        val petFood = mainVM.petPassportResponse.value?.feedingTypeId
        val selectedPetFood = mainVM.petFood.value?.firstOrNull { it.id == petFood }
        if (mainVM.petFoodLoadingEvent.value?.peekContent() == State.SUCCESS
            && selectedPetFood != null
        ) {
            binding.petFood.setSelection(selectedPetFood.index.toInt())
        }
    }

    private fun setObservers() {
        mainVM.petPassportLoadingEvent.observe(viewLifecycleOwner, onPetPassportLoadingEvent)
        mainVM.updatePetPassportLoadingEvent.observe(
            viewLifecycleOwner,
            onUpdatePetPassportLoadingEvent
        )
        mainVM.vaccinesLoadingEvent.observe(viewLifecycleOwner, onVaccineLoadingEvent)
        mainVM.dewormingLoadingEvent.observe(viewLifecycleOwner, onDewormingLoadingEvent)
        mainVM.exoparazitesLoadingEvent.observe(viewLifecycleOwner, onExoparazitesLoadingEvent)
        mainVM.petFoodLoadingEvent.observe(viewLifecycleOwner, onPetFoodLoadingEvent)
        vaccineDate.observe(viewLifecycleOwner, { setVaccineDate(it) })
        drugDate.observe(viewLifecycleOwner, { setDrugDate(it) })
        exoparazitesDate.observe(viewLifecycleOwner, { setExoparazitesDate(it) })
    }

    private fun setExoparazitesDate(it: String?) {
        if (it.isNullOrEmpty())
            binding.parazitesDate.setSelection(getString(R.string.not_selected))
        else
            binding.parazitesDate.setSelection(it)
    }

    private fun setDrugDate(it: String?) {
        if (it.isNullOrEmpty())
            binding.dewormingDate.setSelection(getString(R.string.not_selected))
        else
            binding.dewormingDate.setSelection(it)
    }

    private fun setVaccineDate(it: String?) {
        if (it.isNullOrEmpty())
            binding.vaccineDate.setSelection(getString(R.string.not_selected))
        else
            binding.vaccineDate.setSelection(it)
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.cancel.setOnClickListener { findNavController().popBackStack() }
        binding.update.setOnClickListener { updatePetPassport() }
        binding.add.setOnClickListener { showAddDiseaseDialog() }
        binding.vaccineDate.setOnClickListener { showDatePicker(vaccineDate) }
        binding.dewormingDate.setOnClickListener { showDatePicker(drugDate) }
        binding.parazitesDate.setOnClickListener { showDatePicker(exoparazitesDate) }
        binding.showVaccinePeriod.setOnClickListener { changeVaccinePeriodVisibility() }
        binding.showDewormingPeriod.setOnClickListener { changeDewormingPeriodVisibility() }
        binding.showParazitesPeriod.setOnClickListener { changeParazitesPeriodVisibility() }
        setSpinnerListeners()
    }

    private fun changeVaccinePeriodVisibility() {
        when (binding.vaccinePeriod.visibility) {
            View.GONE -> {
                binding.vaccinePeriod.visibility = View.VISIBLE
                binding.vaccineDate.setIcon(R.drawable.ic_notification_active)
            }
            View.VISIBLE -> {
                binding.vaccinePeriod.visibility = View.GONE
                binding.vaccineDate.setIcon(R.drawable.ic_notification)
            }
            View.INVISIBLE -> {
            }
        }
    }

    private fun changeDewormingPeriodVisibility() {
        when (binding.dewormingPeriod.visibility) {
            View.GONE -> {
                binding.dewormingPeriod.visibility = View.VISIBLE
                binding.dewormingDate.setIcon(R.drawable.ic_notification_active)
            }
            View.VISIBLE -> {
                binding.dewormingPeriod.visibility = View.GONE
                binding.dewormingDate.setIcon(R.drawable.ic_notification)
            }
            View.INVISIBLE -> {
            }
        }
    }

    private fun changeParazitesPeriodVisibility() {
        when (binding.parazitesPeriod.visibility) {
            View.GONE -> {
                binding.parazitesPeriod.visibility = View.VISIBLE
                binding.parazitesDate.setIcon(R.drawable.ic_notification_active)
            }
            View.VISIBLE -> {
                binding.parazitesPeriod.visibility = View.GONE
                binding.parazitesDate.setIcon(R.drawable.ic_notification)
            }
            View.INVISIBLE -> {
            }
        }
    }

    private fun setSpinnerListeners() {
        binding.apply {
            vaccine.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if ((binding.vaccine.selectedItem as Preps).index == MainViewModel.OTHER_ID) {
                        showAddOtherDialog(binding.vaccine)
                    } else {
                        vaccineBefore = binding.vaccine.selectedItemPosition
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            drug.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if ((binding.drug.selectedItem as Preps).index == MainViewModel.OTHER_ID) {
                        showAddOtherDialog(binding.drug)
                    } else {
                        drugBefore = binding.drug.selectedItemPosition
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            parazites.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if ((binding.parazites.selectedItem as Preps).index == MainViewModel.OTHER_ID) {
                        showAddOtherDialog(binding.parazites)
                    } else {
                        exoparazitesBefore = binding.parazites.selectedItemPosition
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            petFood.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if ((binding.petFood.selectedItem as PetFood).index == MainViewModel.OTHER_ID) {
                        showAddOtherDialog(binding.petFood)
                    } else {
                        foodBefore = binding.petFood.selectedItemPosition
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDatePicker(data: MutableLiveData<String>) {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat)
        ru.hvost.news.presentation.dialogs.DatePickerDialog(
            initialDate = tryStringToDate(data.value.toString()),
            onDateSelected = {
                data.value = sdf.format(it.time)
                data.value = sdf.format(it.time)
            },
            maxDate = Date()
        ).show(childFragmentManager, "date_picker")
    }

    private fun showAddDiseaseDialog() {
        diseaseBinding.addDisease.isEnabled = false
        diseaseBinding.diseaseTitle.doOnTextChanged { text, _, _, _ ->
            diseaseBinding.addDisease.isEnabled = !text.isNullOrEmpty() && text.length > 2
        }
        setDiseaseSpinner(diseaseBinding)
        diseaseBinding.disease.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selected = diseaseBinding.disease.selectedItem as PetDiseases
                animateViewVisibility(selected.index == MainViewModel.OTHER_ID)
                diseaseBinding.addDisease.isEnabled = (selected.index != MainViewModel.UNSELECTED_ID) &&
                        ((selected.index != MainViewModel.OTHER_ID) ||
                                (!diseaseBinding.diseaseTitle.text.isNullOrEmpty() &&
                                        diseaseBinding.diseaseTitle.text.toString().length > 2))
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        diseaseBinding.addDisease.setOnClickListener {
            val current = (binding.list.adapter as PetDiseasesAdapter).currentList
            var newList = mutableListOf<String>()
            if (!current.isNullOrEmpty()) {
                newList = current.toMutableList()
            }
            val newDisease: String = if (diseaseBinding.diseaseTitle.text.isNullOrEmpty()) {
                (diseaseBinding.disease.selectedItem as PetDiseases).value
            } else {
                diseaseBinding.diseaseTitle.text.toString()
            }
            val isNotOriginal =
                current.any { it.equals(newDisease, ignoreCase = true) && it.length == newDisease.length }
            if (!isNotOriginal) {
                newList.add(newDisease)
                (binding.list.adapter as PetDiseasesAdapter).submitList(newList)
                addDiseaseDialog.dismiss()
                createSnackbar(
                    binding.root,
                    getString(R.string.diseaseAddedSuccessfull)
                ).show()
            } else {
                createSnackbar(
                    diseaseBinding.mainContainer,
                    getString(R.string.diseaseAlreadyContains)
                ).show()
            }
        }
        addDiseaseDialog.setContentView(diseaseBinding)
        addDiseaseDialog.setOnShowListener {
            addDiseaseDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            addDiseaseDialog.behavior.skipCollapsed = true
        }
        addDiseaseDialog.show()
    }

    private fun animateViewVisibility(b: Boolean) {
        diseaseBinding.otherContainer.measure(
                View.MeasureSpec.makeMeasureSpec(diseaseBinding.otherContainer.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(diseaseBinding.otherContainer.height, View.MeasureSpec.UNSPECIFIED)
        )
        val height = diseaseBinding.otherContainer.measuredHeight
        val startValue = if (b) 0 else diseaseBinding.otherContainer.height
        val endValue = if (b) height else 0
        val animator = ValueAnimator.ofInt(startValue, endValue)
        animator.duration = resources.getInteger(R.integer.filtersContainerAnimationTime).toLong()
        animator.addUpdateListener {
            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    it.animatedValue as Int
            )
            diseaseBinding.otherContainer.layoutParams = params
        }
        animator.addListener(AnimationListener(b))
        animator.start()
    }

    inner class AnimationListener(
            private val shouldExpand: Boolean
    ) : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator?) {
            diseaseBinding.otherContainer.visibility = View.INVISIBLE
        }

        override fun onAnimationEnd(animation: Animator?) {
            if (shouldExpand) {
                diseaseBinding.otherContainer.visibility = View.VISIBLE
            } else {
                diseaseBinding.otherContainer.visibility = View.GONE
            }
        }

        override fun onAnimationCancel(animation: Animator?) {}

        override fun onAnimationRepeat(animation: Animator?) {}

    }

    private fun setDiseaseSpinner(bottomSheetBinding: View) {
        if(mainVM.petDiseasesLoadingEvent.value?.peekContent() == State.SUCCESS){
            mainVM.petDiseases.value?.let {
                val adapter = SpinnerAdapter(
                        requireActivity(),
                        getString(R.string.choiceDiseases),
                        it,
                        PetDiseases::value
                )
                bottomSheetBinding.disease.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun showAddOtherDialog(view: Spinner) {
        var byAdd = false
        val before = checkBefore(view)
        val addOtherDialog =
            BottomSheetDialog(requireContext(), R.style.popupBottomSheetDialogTheme)
        val otherBinding =
            layoutInflater.inflate(R.layout.layout_add_other, binding.root, false)
        otherBinding.addOther.isEnabled = false
        otherBinding.otherTitle.doOnTextChanged { text, _, _, _ ->
            otherBinding.addOther.isEnabled = !text.isNullOrEmpty() && text.length > 2
        }
        otherBinding.addOther.setOnClickListener {
            when (view) {
                binding.drug, binding.vaccine, binding.parazites -> {
                    (view.adapter as SpinnerAdapter<Preps>).apply {
                        insert(
                            Preps(
                                index = MainViewModel.OTHER_NEW_ID,
                                id = "",
                                name = otherBinding.otherTitle.text.toString(),
                                typeId = ""
                            ),
                            view.selectedItemPosition
                        )
                        notifyDataSetChanged()
                        if ((getItem(view.selectedItemPosition - 1) as Preps).index == MainViewModel.OTHER_NEW_ID) {
                            remove(getItem(view.selectedItemPosition - 1))
                            view.setSelection(view.selectedItemPosition - 1)
                        }
                    }
                }
                binding.petFood -> {
                    (view.adapter as SpinnerAdapter<PetFood>).apply {
                        insert(
                            PetFood(
                                index = MainViewModel.OTHER_NEW_ID,
                                id = "",
                                foodName = otherBinding.otherTitle.text.toString()
                            ),
                            view.selectedItemPosition
                        )
                        notifyDataSetChanged()
                        if ((getItem(view.selectedItemPosition - 1) as PetFood).index == MainViewModel.OTHER_NEW_ID) {
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
                    binding.drug -> drugBefore = view.selectedItemPosition
                    binding.vaccine -> vaccineBefore = view.selectedItemPosition
                    binding.parazites -> exoparazitesBefore = view.selectedItemPosition
                    binding.petFood -> foodBefore = view.selectedItemPosition
                }
            }
        }
        addOtherDialog.setContentView(otherBinding)
        addOtherDialog.setOnShowListener {
            addOtherDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            addOtherDialog.behavior.skipCollapsed = true
        }
        addOtherDialog.show()
    }

    private fun checkBefore(view: Spinner): Int {
        return when (view) {
            binding.drug -> drugBefore
            binding.vaccine -> vaccineBefore
            binding.parazites -> exoparazitesBefore
            binding.petFood -> foodBefore
            else -> 0
        }
    }

    private fun setDecoration() {
        binding.list.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val elementMargin =
                    view.context?.resources?.getDimension(R.dimen.smallMargin)?.toInt() ?: 0
                parent.adapter.run {
                    outRect.bottom = elementMargin
                }
            }
        })
    }

    private fun updatePetPassport() {
        val vaccine = checkPreps(binding.vaccine)
        val drug = checkPreps(binding.drug)
        val parazites = checkPreps(binding.parazites)
        val petFood = checkPetFood()
        val diseases = joinDiseasesString()
        val data = mainVM.petPassportResponse.value
        if (data?.petId != null) {
            mainVM.updatePetPassport(
                data.petId,
                binding.switchClean.isChecked,
                vaccine,
                vaccineDate.value.toString(),
                (binding.vaccinePeriod.selectedItem as NotificationPeriod).periodId,
                drug,
                drugDate.value.toString(),
                (binding.dewormingPeriod.selectedItem as NotificationPeriod).periodId,
                parazites,
                exoparazitesDate.value.toString(),
                (binding.parazitesPeriod.selectedItem as NotificationPeriod).periodId,
                petFood,
                diseases,
                binding.clinicName.text.toString(),
                binding.address.text.toString()
            )
        }
    }

    private fun joinDiseasesString(): String {
        var diseases = mutableListOf<String>()
        val diseaseList = mainVM.petDiseases.value
        binding.list.adapter?.let { adapter ->
            adapter as PetDiseasesAdapter
            val listToSend = adapter.currentList.toMutableList()
            listToSend.forEach { item ->
                val disease = diseaseList?.firstOrNull { it.value.equals(item, true) }
                if(disease != null){
                    diseases.add(disease.diseaseId)
                }else{
                    diseases.add("[$item]")
                }
            }
        }
        return diseases.joinToString(",")
    }

    private fun checkPetFood(): String {
        return if ((binding.petFood.selectedItem as PetFood).index == MainViewModel.OTHER_NEW_ID ||
            (binding.petFood.selectedItem as PetFood).index == MainViewModel.OTHER_ID
        ) {
            (binding.petFood.selectedItem as PetFood).foodName
        } else {
            (binding.petFood.selectedItem as PetFood).id
        }
    }

    private fun checkPreps(spinner: Spinner): String {
        return if ((spinner.selectedItem as Preps).index == MainViewModel.OTHER_NEW_ID ||
            (spinner.selectedItem as Preps).index == MainViewModel.OTHER_ID
        ) {
            (spinner.selectedItem as Preps).name
        } else {
            (spinner.selectedItem as Preps).id
        }
    }
}