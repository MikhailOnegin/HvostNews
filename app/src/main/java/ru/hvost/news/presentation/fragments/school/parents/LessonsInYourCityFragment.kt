package ru.hvost.news.presentation.fragments.school.parents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.databinding.FragmentLessonsInYourCityBinding
import ru.hvost.news.presentation.adapters.recycler.LessonsInYourCityAdapter
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class LessonsInYourCityFragment:Fragment() {

    private val  adapter:LessonsInYourCityAdapter = LessonsInYourCityAdapter()
    private lateinit var layoutManager: RecyclerView.LayoutManager
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
        val items = arrayOf("Любой город","Москва", "Санкт-Петербург", "Новосибирск")
        val spinnerAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, items)
        binding.spinnerCities.adapter  =  spinnerAdapter
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        schoolVM.getOfflineLessons(binding.spinnerCities.selectedItem.toString())
        binding.recyclerViewLessons.adapter = adapter
        binding.recyclerViewLessons.layoutManager = layoutManager
        binding.spinnerCities.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val city = items[p2]
                adapter.clear()
                schoolVM.getOfflineLessons(city)
            }
        }
        binding.buttonYes.setOnClickListener {
            adapter.filter(showFinished = true)
        }
        binding.buttonNo.setOnClickListener {
            adapter.filter(showFinished = false)
        }
        setObservers(this)
    }

    private fun Spinner.setItem(list: Array<CharSequence>, value: String) {
        val index = list.indexOf(value)
        this.post { this.setSelection(index) }
    }

    private fun setObservers(owner:LifecycleOwner){
        schoolVM.offlineLessons.observe(owner, Observer {
            adapter.setLessons(it.lessons)
        })
    }
}