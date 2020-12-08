package ru.hvost.news.presentation.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.DialogQrCodeBinding
import ru.hvost.news.databinding.LayoutAddPetBinding
import ru.hvost.news.models.Species
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.fragments.login.RegistrationVM
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import java.text.SimpleDateFormat
import java.util.*

class AddPetCustomDialog() : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutAddPetBinding
    private lateinit var onPetAdded: DefaultNetworkEventObserver
    private lateinit var mainVM: MainViewModel
    private var petSex: Int = RegistrationVM.SEX_UNKNOWN

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutAddPetBinding.inflate(inflater, container, false)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
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
        setObservers()
        setListeners()
        super.onActivityCreated(savedInstanceState)
    }

    private fun setObservers() {
        mainVM.petsSpeciesState.observe(viewLifecycleOwner) {
            onSpeciesChanged(it)
        }
    }

    private fun onSpeciesChanged(state: State?) {
        when (state) {
            State.SUCCESS -> {
                mainVM.petsSpeciesResponse.value?.run {
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
        }
    }

    private fun setListeners() {
        binding.sexMale.setOnClickListener(onSexClicked)
        binding.sexFemale.setOnClickListener(onSexClicked)
        binding.sexUnknown.setOnClickListener(onSexClicked)
        binding.birthday.setOnClickListener { showDatePicker() }
        binding.actionAdd.setOnClickListener {
            mainVM.addPet(
                petSpecies = (binding.type.selectedItem as Species).speciesId.toString(),
                petName = binding.petName.text.toString(),
                petBirthday = binding.birthday.text.toString(),
                petSex = petSex.toString()
            )
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDatePicker() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat)
        DatePickerDialog(
            onDateSelected = {
                binding.birthday.setText(sdf.format(it.time))
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
    }

}