package ru.hvost.news.presentation.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.MainViewModel
import ru.hvost.news.databinding.FragmentPetProfileBinding
import ru.hvost.news.utils.enums.State

class PetProfileFragment : Fragment() {

    private lateinit var binding: FragmentPetProfileBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setObservers()
    }

    private fun setObservers() {
        mainVM.userPetsState.observe(viewLifecycleOwner, Observer { setDataToBind(it) })
    }

    private fun setDataToBind(state: State?) {
        when (state) {
            State.SUCCESS -> {
                bindData()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun bindData() {
        val petData = mainVM.userPetsResponse.value?.filter { it.petId == arguments?.getString("PET_ID") }

    }
}