package ru.hvost.news.presentation.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentRegParentBinding
import ru.hvost.news.models.RegInterest
import ru.hvost.news.models.Species
import ru.hvost.news.utils.createSnackbar

class RegParentFragment : Fragment() {

    private lateinit var binding: FragmentRegParentBinding
    private lateinit var registrationVM: RegistrationVM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegParentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registrationVM = ViewModelProvider(requireActivity())[RegistrationVM::class.java]
        registrationVM.species.observe(viewLifecycleOwner) { onSpeciesChanged(it) }
        registrationVM.interests.observe(viewLifecycleOwner) { onInterestsChanged(it) }
        if(registrationVM.species.value?.isEmpty() == true) registrationVM.loadSpecies()
        if(registrationVM.interests.value?.isEmpty() == true) registrationVM.loadInterests()
    }

    override fun onDestroy() {
        super.onDestroy()
        registrationVM.reset()
    }

    private fun onInterestsChanged(interests: List<RegInterest>?) {
        if(interests == null && registrationVM.species.value == null) {
            createSnackbar(
                requireActivity().findViewById(R.id.nav_host_fragment),
                getString(R.string.errorLoadingRegData),
                getString(R.string.buttonOk)
            ).show()
            registrationVM.interests.removeObservers(viewLifecycleOwner)
            registrationVM.species.removeObservers(viewLifecycleOwner)
            registrationVM.interests.value = listOf()
            registrationVM.species.value = listOf()
            findNavController().popBackStack()
        }
    }

    private fun onSpeciesChanged(species: List<Species>?) {
        if(species == null && registrationVM.interests.value == null) {
            createSnackbar(
                requireActivity().findViewById(R.id.nav_host_fragment),
                getString(R.string.errorLoadingRegData),
                getString(R.string.buttonOk)
            ).show()
            registrationVM.interests.removeObservers(viewLifecycleOwner)
            registrationVM.species.removeObservers(viewLifecycleOwner)
            registrationVM.interests.value = listOf()
            registrationVM.species.value = listOf()
            findNavController().popBackStack()
        }
    }

}