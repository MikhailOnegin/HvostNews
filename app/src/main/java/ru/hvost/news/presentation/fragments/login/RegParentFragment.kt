package ru.hvost.news.presentation.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.hvost.news.databinding.FragmentRegParentBinding

class RegParentFragment : Fragment() {

    private lateinit var binding: FragmentRegParentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegParentBinding.inflate(inflater, container, false)
        return binding.root
    }

}