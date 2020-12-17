package ru.hvost.news.presentation.fragments.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.App
import ru.hvost.news.databinding.FragmentMapPartnersPageBinding
import ru.hvost.news.presentation.fragments.BaseFragment

class PartnersPageFragment : BaseFragment() {

    private lateinit var binding: FragmentMapPartnersPageBinding
    private lateinit var mapVM: MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapPartnersPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapVM = ViewModelProvider(requireActivity())[MapViewModel::class.java]
        Toast.makeText(
            App.getInstance(),
            "${arguments?.getLong(SHOP_ID)}",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {

        const val SHOP_ID = "shop_id"

    }

}