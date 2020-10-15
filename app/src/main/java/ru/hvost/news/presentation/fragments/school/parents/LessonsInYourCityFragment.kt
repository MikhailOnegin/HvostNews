package ru.hvost.news.presentation.fragments.school.parents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.databinding.FragmentLessonsInYourCityBinding
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class LessonsInYourCityFragment:Fragment() {
    private lateinit var binding: FragmentLessonsInYourCityBinding
    private lateinit var schoolVM: SchoolViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLessonsInYourCityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        val items = arrayOf("Любой город","Moscow", "Kursk")
        val spinnerAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, items)
        binding.spinnerCities.adapter  =  spinnerAdapter

        //val index = items.indexOf("Все")
        //binding.spinnerCities.setSelection(index)
       // binding.spinnerCities.setItem(arrayOf("Любой город","Moscow", "KURSK"), "Moscow")
    }

    private fun Spinner.setItem(list: Array<CharSequence>, value: String) {
        val index = list.indexOf(value)
        this.post { this.setSelection(index) }
    }
}