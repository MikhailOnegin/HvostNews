package ru.hvost.news.presentation.fragments.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.hvost.news.MainViewModel
import ru.hvost.news.databinding.FragmentEditProfileBinding
import java.text.SimpleDateFormat
import java.util.*

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var mainVM: MainViewModel
    private var date = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        setListeners()
        return binding.root
    }

    private fun setListeners() {
        binding.birthday.setOnClickListener { showDatePicker() }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDatePicker() {
        val onDateSet = DatePickerDialog.OnDateSetListener{ view, year, monthOfYear, dayOfMonth ->
            date.set(Calendar.YEAR, year)
            date.set(Calendar.MONTH, monthOfYear)
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd.MM.yyyy"
            val sdf = SimpleDateFormat(myFormat)
            binding.birthday.setText(sdf.format(date.time))
        }

        DatePickerDialog(
            requireActivity(),
            onDateSet,
            date.get(Calendar.YEAR),
            date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH),
        ).show()
    }

}