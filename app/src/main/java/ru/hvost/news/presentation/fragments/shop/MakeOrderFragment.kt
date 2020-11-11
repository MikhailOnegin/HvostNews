package ru.hvost.news.presentation.fragments.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.hvost.news.databinding.FragmentMakeOrderBinding

class MakeOrderFragment : Fragment() {

    private lateinit var binding: FragmentMakeOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMakeOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

}