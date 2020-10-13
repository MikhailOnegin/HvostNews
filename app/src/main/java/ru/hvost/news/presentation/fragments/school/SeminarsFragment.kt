package ru.hvost.news.presentation.fragments.school

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.databinding.FragmentSeminarsBinding
import ru.hvost.news.presentation.viewmodels.SchoolViewModel

class SeminarsFragment: Fragment() {
    private lateinit var binding: FragmentSeminarsBinding
    private lateinit var schoolVM: SchoolViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSeminarsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        val items = arrayOf("Все семинары", "Ваши семинары")
        binding.spinnerSeminars.adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, items)
    }

}