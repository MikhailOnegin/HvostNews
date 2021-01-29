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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.layout_add_disease.view.*
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentPetPassportBinding
import ru.hvost.news.models.PetFood
import ru.hvost.news.models.Preps
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
    private lateinit var mainVM: MainViewModel
    private lateinit var onPetPassportLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onUpdatePetPassportLoadingEvent: DefaultNetworkEventObserver
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
    ): View {
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
        if (mainVM.petPassportLoadingEvent.value?.peekContent() == State.SUCCESS) bindData() else arguments?.getString(
            ProfileFragment.PET_ID
        )?.let { mainVM.getPetPassport(it) }
    }

    private fun initializeObservers() {
        onPetPassportLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = { bindData() }
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
            if (mainVM.petPassportLoadingEvent.value?.peekContent() == State.SUCCESS) setExoparazites()
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
            if (mainVM.petPassportLoadingEvent.value?.peekContent() == State.SUCCESS) setDeworming()
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
            if (mainVM.petPassportLoadingEvent.value?.peekContent() == State.SUCCESS) setVaccine()
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
            if (mainVM.petPassportLoadingEvent.value?.peekContent() == State.SUCCESS) setPetFood()
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
        setSpinners()
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
        }
    }

    private fun setDeworming() {
        val petDeworming = mainVM.petPassportResponse.value?.dewormingId
        val deworming = mainVM.deworming.value?.firstOrNull { it.id == petDeworming }
        if (mainVM.dewormingLoadingEvent.value?.peekContent() == State.SUCCESS
            && deworming != null
        ) {
            binding.drug.setSelection(deworming.index.toInt())
        }
    }

    private fun setExoparazites() {
        val petExoparazites = mainVM.petPassportResponse.value?.exoparasiteId
        val exoparazites = mainVM.exoparazites.value?.firstOrNull { it.id == petExoparazites }
        if (mainVM.exoparazitesLoadingEvent.value?.peekContent() == State.SUCCESS
            && exoparazites != null
        ) {
            binding.parazites.setSelection(exoparazites.index.toInt())
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
        bottomSheetBinding.diseaseTitle.doOnTextChanged { text, _, _, _ ->
            bottomSheetBinding.addDisease.isEnabled = !text.isNullOrEmpty() && text.length > 2
        }
        bottomSheetBinding.addDisease.setOnClickListener {
            val current = (binding.list.adapter as PetDiseasesAdapter).currentList
            var newList = mutableListOf<String>()
            if (!current.isNullOrEmpty()) {
                newList = current.toMutableList()
            }
            val newDisease = bottomSheetBinding.diseaseTitle.text.toString()
            val isNotOriginal =
                current.any { it.equals(newDisease, ignoreCase = true) }
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
        var diseases = String()
        binding.list.adapter?.let {
            it as PetDiseasesAdapter
            diseases = it.currentList.joinToString(",")
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
                diseases,
                binding.clinicName.text.toString(),
                binding.address.text.toString()
            )
        }
    }
}