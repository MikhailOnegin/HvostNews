package ru.hvost.news.presentation.fragments.profile

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.databinding.FragmentEditProfileBinding
import ru.hvost.news.utils.enums.State
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var mainVM: MainViewModel
    private var state: State? = null

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
        setObservers()
        setListeners()
    }

    private fun setObservers() {
        mainVM.userDataState.observe(viewLifecycleOwner, {
            state = it
            setDataToBind(it)
        })
    }

    private fun setDataToBind(state: State) {
        when (state) {
            State.SUCCESS -> {
                bindData()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun bindData() {
        val userData = mainVM.userDataResponse.value
        binding.surname.setText(userData?.surname)
        binding.name.setText(userData?.name)
        binding.patronymic.setText(userData?.patronymic)
        binding.birthday.setText(userData?.birthday)
        binding.phone.setText(userData?.phone)
        binding.email.setText(userData?.email)
        binding.city.setText(userData?.city)
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.birthday.setOnClickListener(openDatePickerDialog)
        binding.cancel.setOnClickListener {
            if (state == State.SUCCESS) {
                bindData()
            } else {
                binding.surname.setText("")
                binding.name.setText("")
                binding.patronymic.setText("")
                binding.birthday.setText("")
                binding.phone.setText("")
                binding.email.setText("")
                binding.city.setText("")
            }
        }
        binding.save.setOnClickListener { tryToSend() }
    }

    private fun tryToSend() {
        mainVM.changeUserData(
            surname = binding.surname.text.toString(),
            name = binding.name.text.toString(),
            patronymic = binding.patronymic.text.toString(),
            birthday = binding.birthday.text.toString(),
            phone = binding.phone.text.toString(),
            email = binding.email.text.toString(),
            city = binding.city.text.toString()
        )
        Toast.makeText(requireActivity(), "Success", Toast.LENGTH_SHORT).show()
    }

    private val openDatePickerDialog = View.OnClickListener { showDatePicker() }

    @SuppressLint("SimpleDateFormat")
    private fun showDatePicker() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat)
        ru.hvost.news.presentation.dialogs.DatePickerDialog(
            onDateSelected = {
                binding.birthday.setText(sdf.format(it.time))
            },
            maxDate = Date()
        ).show(childFragmentManager, "date_picker")
    }

}