package ru.hvost.news.presentation.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentRegPetBinding
import ru.hvost.news.models.Species
import ru.hvost.news.presentation.adapters.spinners.SpeciesSpinnerAdapter

class RegPetFragment : Fragment() {

    private lateinit var binding: FragmentRegPetBinding
    private lateinit var registrationVM: RegistrationVM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegPetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registrationVM = ViewModelProvider(requireActivity())[RegistrationVM::class.java]
        setObservers()
        registrationVM.loadSpecies()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        registrationVM.setStage(RegistrationVM.RegStep.PET)
    }

    private fun setObservers() {
        registrationVM.species.observe(viewLifecycleOwner) { onSpeciesChanged(it) }
    }

    private fun onSpeciesChanged(species: List<Species>?) {
        species?.run {
            val adapter = SpeciesSpinnerAdapter(requireActivity(), R.layout.spinner_dropdown_view)
            adapter.addAll(this)
            binding.spinner.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun setListeners() {
        binding.buttonNext.setOnClickListener(onButtonNextClicked)
    }

    private val onButtonNextClicked = { _: View ->
        findNavController().navigate(R.id.action_regPetFragment_to_regInterestsFragment)
    }

}