package ru.hvost.news.presentation.fragments.profile

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.installations.Utils
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentEditProfileBinding
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.services.FcmService
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.simpleDateFormat
import ru.hvost.news.utils.tryStringToDate
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class EditProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onUserDataLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onChangeUserDataLoadingEvent: DefaultNetworkEventObserver
    private val birthday = MutableLiveData<String>()
    private val myFormat = "dd.MM.yyyy"
    private val sdf = SimpleDateFormat(myFormat)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        if (mainVM.userDataLoadingEvent.value?.peekContent() == State.SUCCESS) bindData()
        initializeObservers()
        setObservers()
        setListeners()
    }

    private fun initializeObservers() {
        onUserDataLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = { bindData() },
        )
        onChangeUserDataLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = {
                createSnackbar(
                    binding.root,
                    getString(R.string.userDataChangedSuccessfully),
                    getString(R.string.buttonOk)
                ) { findNavController().popBackStack() }.show()
            },
        )
    }

    private fun setObservers() {
        mainVM.userDataLoadingEvent.observe(viewLifecycleOwner, onUserDataLoadingEvent)
        mainVM.changeUserDataLoadingEvent.observe(viewLifecycleOwner, onChangeUserDataLoadingEvent)
        birthday.observe(viewLifecycleOwner, { onDateChanged() })
    }

    private fun onDateChanged() {
        binding.birthday.setSelection(birthday.value.toString())
    }

    private fun bindData() {
        val userData = mainVM.userData.value
        if (userData?.birthday.isNullOrEmpty()) {
            birthday.value = BIRTHDAY_EMPTY
        } else {
            birthday.value = userData?.birthday.toString()
        }
        binding.surname.setText(userData?.surname)
        binding.name.setText(userData?.name)
        binding.patronymic.setText(userData?.patronymic)
        binding.phone.setSelection(userData?.phone)
        binding.email.setSelection(userData?.email)
        binding.city.setText(userData?.city)
        binding.switchEmail.isChecked = userData?.mailNotifications == true
        setPushSwitch()
    }

    private fun setPushSwitch() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        binding.switchPush.isChecked = prefs.getBoolean(FcmService.PREF_SHOW_NOTIFICATIONS, false)
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.birthday.setOnClickListener(openDatePickerDialog)
        binding.cancel.setOnClickListener { findNavController().popBackStack() }
        binding.save.setOnClickListener { tryToSend() }
        binding.switchPush.setOnCheckedChangeListener { _, isChecked ->
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            prefs.edit().putBoolean(FcmService.PREF_SHOW_NOTIFICATIONS, isChecked).apply()
        }
    }

    private fun tryToSend() {
        tryToUpdateData()
    }

    private fun tryToUpdateData() {
        if (birthday.value == BIRTHDAY_EMPTY) {
            mainVM.changeUserData(
                surname = binding.surname.text.toString(),
                name = binding.name.text.toString(),
                patronymic = binding.patronymic.text.toString(),
                city = binding.city.text.toString(),
                mailNotifications = binding.switchEmail.isChecked
            )
        } else {
            mainVM.changeUserData(
                surname = binding.surname.text.toString(),
                name = binding.name.text.toString(),
                patronymic = binding.patronymic.text.toString(),
                birthday = birthday.value.toString(),
                city = binding.city.text.toString(),
                mailNotifications = binding.switchEmail.isChecked
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

    companion object {
        private const val BIRTHDAY_EMPTY = "Не указано"
    }
}