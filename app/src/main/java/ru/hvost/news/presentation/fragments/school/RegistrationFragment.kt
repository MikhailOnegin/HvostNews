package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.data.api.response.PetsResponse
import ru.hvost.news.databinding.FragmentRegistrationBinding
import ru.hvost.news.models.CitiesOffline
import ru.hvost.news.models.Pets
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.dialogs.AddPetRegistrationDialog
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.getValue

class RegistrationFragment: BaseFragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var mainVM: MainViewModel
    private lateinit var loadPetsEvent:DefaultNetworkEventObserver
    private var idSchool:String? = null
    private var pets: List<Pets>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mainVM.loadPetsData()
        setListeners()
        setObservers(this)
        binding.spinnerPets.setSelection(0, false)
        binding.spinnerPets.adapter = SpinnerAdapter(requireContext(), "", arrayListOf(), Pets::petName)
        idSchool = arguments?.getString("schoolId")

    }

    @Suppress("UNCHECKED_CAST")
    private fun initializedEvents() {
        loadPetsEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
            }
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun setObservers(owner:LifecycleOwner) {
        schoolVM.onlineSchools.observe(owner, {
            idSchool?.run {
                for(i in it.onlineSchools.indices){
                    val school = it.onlineSchools[i]
                        if(school.id.toString() == this){
                        val head = "Регистрация на онлайн семинар ${school.title}"
                        binding.textViewHead.text = head
                    }
                }
            }
        })
        mainVM.userPets.observe(owner, {
            pets = it
            val adapter = (binding.spinnerPets.adapter as SpinnerAdapter<Pets>)
            adapter.clear()
            (binding.spinnerPets.adapter as SpinnerAdapter<Pets>).addAll(it)
        })
    }

    @Suppress("UNCHECKED_CAST")
    private fun setListeners() {
        binding.toolbarRegistration.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.imageButtonAddPet.setOnClickListener {
            AddPetRegistrationDialog().show(
                childFragmentManager,
                "info_dialog"
            )
        }
        binding.buttonCompleteRegistration.setOnClickListener {
            if(binding.checkBox2.isChecked && binding.spinnerPets.size > 0 ){
                var petId:String? = null
                    (binding.spinnerPets.adapter as SpinnerAdapter<Pets>).getItem(0)?.let {pets2 ->
                        pets?.let {
                            for(i in it.indices){
                                val pet = it[i]
                                if( pet == pets2){
                                    petId = pet.petId
                                }
                            }
                        }
                }
                App.getInstance().userToken?.let {userToken ->
                    idSchool?.let { idSchool ->

                        petId?.let {idPet ->
                            schoolVM.setParticipate(userToken, idSchool, idPet)
                        }

                    }

                }
                findNavController().popBackStack()
            }
        }
    }
}