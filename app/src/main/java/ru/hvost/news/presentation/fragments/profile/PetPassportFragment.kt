package ru.hvost.news.presentation.fragments.profile

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_pet_passport.*
import kotlinx.android.synthetic.main.fragment_pet_passport.view.*
import kotlinx.android.synthetic.main.layout_add_disease.view.*
import kotlinx.android.synthetic.main.layout_prize_products.view.*
import kotlinx.android.synthetic.main.layout_prize_products.view.toCart
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.data.api.response.*
import ru.hvost.news.databinding.FragmentPetPassportBinding
import ru.hvost.news.models.PetFood
import ru.hvost.news.models.Preps
import ru.hvost.news.models.Prize
import ru.hvost.news.presentation.adapters.PetDiseasesAdapter
import ru.hvost.news.presentation.adapters.PrizeProductsAdapter
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
    private lateinit var mainVM: MainViewModel
    private lateinit var onPetPassportLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onPetFoodLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onVaccineLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onDewormingLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onExoparazitesLoadingEvent: DefaultNetworkEventObserver
    private val vaccineDate = MutableLiveData<String>()
    private val drugDate = MutableLiveData<String>()
    private val exoparazitesDate = MutableLiveData<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetPassportBinding.inflate(inflater, container, false)
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
        if (mainVM.dewormingLoadingEvent.value?.peekContent() == State.SUCCESS) bindDewormingList() else mainVM.getDeworming()
        if (mainVM.exoparazitesLoadingEvent.value?.peekContent() == State.SUCCESS) bindExoparazitesList() else mainVM.getExoparazites()
        if (mainVM.petFoodLoadingEvent.value?.peekContent() == State.SUCCESS) bindPetFoodList() else mainVM.getPetFood()
    }

    private fun initializeObservers() {
        onPetPassportLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = { bindData() }
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
        }
    }

    private fun bindPetFoodList() {
        mainVM.petFood.value?.let {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.vaccine),
                it,
                PetFood::foodName
            )
            binding.petFood.adapter = adapter
        }
    }

    private fun bindData() {
        val passportData = mainVM.petPassportResponse.value
        binding.name.text = passportData?.petName
        binding.switchClean.isChecked = passportData?.isSterilised == true
        vaccineDate.value = passportData?.vacinationDate.toString()
        drugDate.value = passportData?.dewormingDate.toString()
        exoparazitesDate.value = passportData?.exoparasitesDate.toString()
        binding.clinicName.setText(passportData?.favouriteVetName)
        binding.address.setText(passportData?.favouriteVetAdress)
        setRecyclerView(passportData)
        setSpinners()
    }

    private fun setRecyclerView(data: PetPassportResponse?) {
        val adapter = PetDiseasesAdapter()
        binding.list.adapter = adapter
        adapter.submitList(data?.diseases)
        setDecoration()
    }

    private fun setSpinners() {
        setVaccine()
        setDeworming()
        setExoparazites()
        setPetFood()
    }

    private fun setVaccine() {
        val selectedVaccine = mainVM.petPassportResponse.value?.vacineId
        if (mainVM.vaccinesLoadingEvent.value?.peekContent() == State.SUCCESS
            && !selectedVaccine.isNullOrEmpty()
        ) {
            selectedVaccine?.toInt()?.let { binding.vaccine.setSelection(it) }
        }
    }

    private fun setDeworming() {
        val selectedDeworming = mainVM.petPassportResponse.value?.dewormingId
        if (mainVM.vaccinesLoadingEvent.value?.peekContent() == State.SUCCESS
            && !selectedDeworming.isNullOrEmpty()
        ) {
            selectedDeworming?.toInt()?.let { binding.drug.setSelection(it) }
        }
    }

    private fun setExoparazites() {
        val selectedExoparazites = mainVM.petPassportResponse.value?.exoparasiteId
        if (mainVM.vaccinesLoadingEvent.value?.peekContent() == State.SUCCESS
            && !selectedExoparazites.isNullOrEmpty()
        ) {
            selectedExoparazites?.toInt()?.let { binding.parazites.setSelection(it) }
        }
    }

    private fun setPetFood() {
        val selectedPetFood = mainVM.petPassportResponse.value?.feedingTypeId
        if (mainVM.vaccinesLoadingEvent.value?.peekContent() == State.SUCCESS
            && !selectedPetFood.isNullOrEmpty()
        ) {
            selectedPetFood?.toInt()?.let { binding.petFood.setSelection(it) }
        }
    }

    private fun setObservers() {
        mainVM.petPassportLoadingEvent.observe(viewLifecycleOwner, onPetPassportLoadingEvent)
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
        val addDiseaseDialog =
            BottomSheetDialog(requireContext(), R.style.popupBottomSheetDialogTheme)
        val bottomSheetBinding =
            layoutInflater.inflate(R.layout.layout_add_disease, binding.root, false)
        bottomSheetBinding.addDisease.isEnabled = false
        bottomSheetBinding.diseaseTitle.doOnTextChanged { _, _, _, count ->
            bottomSheetBinding.addDisease.isEnabled = when (count) {
                0, 1, 2 -> false
                else -> true
            }
        }
        bottomSheetBinding.addDisease.setOnClickListener {
            val current = (binding.list.adapter as PetDiseasesAdapter).currentList
            var newList = mutableListOf<Diseases>()
            if (!current.isNullOrEmpty()) {
                newList = current.toMutableList()
            }
            val newDisease = bottomSheetBinding.diseaseTitle.text.toString()
            val isNotOriginal =
                current.any { it.diseaseName.equals(newDisease, ignoreCase = true) }
            if (!isNotOriginal) {
                newList.add(
                    Diseases(
                        id = newList.size.inc().toString(),
                        diseaseName = newDisease
                    )
                )
                (binding.list.adapter as PetDiseasesAdapter).submitList(newList)
                addDiseaseDialog.dismiss()
                createSnackbar(
                    binding.root,
                    getString(R.string.diseaseAddedSuccessfull)
                ).show()
            } else {
                createSnackbar(
                    bottomSheetBinding.mainContainer,
                    getString(R.string.diseaseAlreadyContains)
                ).show()
            }
        }
        addDiseaseDialog.setContentView(bottomSheetBinding)
        addDiseaseDialog.setOnShowListener {
            addDiseaseDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            addDiseaseDialog.behavior.skipCollapsed = true
        }
        addDiseaseDialog.show()
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
        var diseases = mutableListOf<Diseases>()
        binding.list.adapter?.let {
            it as PetDiseasesAdapter
            diseases = it.currentList.toMutableList()
        }
        val data = mainVM.petPassportResponse.value
        if (data?.petId != null) {
            mainVM.updatePetPassport(
                data.petId,
                binding.switchClean.isChecked,
                (binding.vaccine.selectedItem as Preps).id,
                vaccineDate.value.toString(),
                (binding.drug.selectedItem as Preps).id,
                drugDate.value.toString(),
                (binding.parazites.selectedItem as Preps).id,
                exoparazitesDate.value.toString(),
                (binding.petFood.selectedItem as PetFood).id,
                diseases.joinToString(separator = ","),
                binding.clinicName.text.toString(),
                binding.address.text.toString()
            )
        }
    }
}