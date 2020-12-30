package ru.hvost.news.presentation.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentEditProfileBinding
import ru.hvost.news.databinding.FragmentPetPassportBinding
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class PetPassportFragment : BaseFragment() {

    private lateinit var binding: FragmentPetPassportBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onPetPassportLoadingEvent: DefaultNetworkEventObserver


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetPassportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        initializeObservers()
        setObservers()
        setListeners()
    }

    private fun initializeObservers() {
        onPetPassportLoadingEvent = DefaultNetworkEventObserver(
            binding.root,
            doOnSuccess = { bindData() }
        )
    }

    private fun bindData() {
        val passportData = mainVM.petPassportResponse.value
        binding.name.text
        binding.switchClean.isChecked = passportData?.isSterilised == true
        binding.vaccine.setText(passportData?.vacineTitle)
        binding.vaccineDate.setText(passportData?.vacinationDate)
        binding.drug.setText(passportData?.dewormingTitle)
        binding.dewormingDate.setText(passportData?.dewormingDate)
        binding.parazites.setText(passportData?.exoparasiteTitle)
        binding.parazitesDate.setText(passportData?.exoparasitesDate)
        binding.clinicName.setText(passportData?.favouriteVetName)
        binding.address.setText(passportData?.favouriteVetAdress)
        setFoodType()
        setDiseases()
    }

    private fun setDiseases() {
        //yunusov: вывести болезни в RecyclerView
    }

    private fun setFoodType() {
        //yunusov: вывести типы питания в Spinner
    }

    private fun setObservers() {
        mainVM.petPassportLoadingEvent.observe(viewLifecycleOwner, onPetPassportLoadingEvent)
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

}