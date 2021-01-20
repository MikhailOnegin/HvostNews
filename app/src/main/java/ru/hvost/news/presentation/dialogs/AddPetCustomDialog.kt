package ru.hvost.news.presentation.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.LayoutAddPetBinding
import ru.hvost.news.models.Species
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.fragments.login.RegistrationVM
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import java.text.SimpleDateFormat
import java.util.*

class AddPetCustomDialog : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutAddPetBinding
    private lateinit var onPetAdded: DefaultNetworkEventObserver
    private lateinit var onPetSpeciesLoadingEvent: DefaultNetworkEventObserver
    private lateinit var mainVM: MainViewModel
    private var petSex: Int? = null
    private val petBirthday = MutableLiveData<String>()
    private val myFormat = "dd.MM.yyyy"
    private val sdf = SimpleDateFormat(myFormat)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutAddPetBinding.inflate(inflater)
        binding.actionAdd.isEnabled = false
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        if (mainVM.petsSpeciesLoadingEvent.value?.peekContent() == State.SUCCESS) {
            onSpeciesChanged()
        }
        initializeObservers()
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mainVM.addPetEvent.observe(viewLifecycleOwner, onPetAdded)
        binding.sexUnknown.isSelected = true
        petBirthday.value = sdf.format(Date().time)
        setObservers()
        setListeners()
        super.onActivityCreated(savedInstanceState)
    }

    private fun setObservers() {
        mainVM.petsSpeciesLoadingEvent.observe(viewLifecycleOwner, onPetSpeciesLoadingEvent)
        petBirthday.observe(viewLifecycleOwner, { onDateChanged() })
    }

    private fun onDateChanged() {
        binding.birthday.setSelection(petBirthday.value.toString())
    }

    private fun onSpeciesChanged() {
        mainVM.petsSpecies.value?.run {
            val adapter = SpinnerAdapter(
                requireActivity(),
                getString(R.string.addPetTitle),
                this,
                Species::speciesName
            )
            binding.type.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun setListeners() {
        binding.petName.doOnTextChanged { text, _, _, _ ->
            binding.actionAdd.isEnabled = !text.isNullOrEmpty() && text.length > 2
        }
        binding.sexMale.setOnClickListener(onSexClicked)
        binding.sexFemale.setOnClickListener(onSexClicked)
        binding.sexUnknown.setOnClickListener(onSexClicked)
        binding.birthday.setOnClickListener { showDatePicker() }
        binding.actionAdd.setOnClickListener {
            mainVM.addPet(
                petSpecies = (binding.type.selectedItem as Species).speciesId.toString(),
                petName = binding.petName.text.toString(),
                petBirthday = petBirthday.value.toString(),
                petSex = petSex.toString()
            )
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDatePicker() {
        DatePickerDialog(
            onDateSelected = {
                binding.birthday.setSelection(sdf.format(it.time))
                petBirthday.value = sdf.format(it.time)
            },
            maxDate = Date()
        ).show(childFragmentManager, "date_picker")
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    private fun initializeObservers() {
        onPetAdded = DefaultNetworkEventObserver(
            binding.parent,
            doOnSuccess = {
                mainVM.loadPetsData()
                Toast.makeText(
                    requireActivity(),
                    getText(R.string.petAddedSuccessfull),
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            },
        )
        onPetSpeciesLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.parent,
            doOnSuccess = { onSpeciesChanged() }
        )
    }

}