package ru.hvost.news.presentation.fragments.login

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.FragmentRegInterestsBinding

class RegInterestsFragment : Fragment() {

    private lateinit var binding: FragmentRegInterestsBinding
    private lateinit var registrationVM: RegistrationVM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegInterestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registrationVM = ViewModelProvider(requireActivity())[RegistrationVM::class.java]
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        registrationVM.setStage(RegistrationVM.RegStep.INTERESTS)
    }

    private fun setListeners() {
        binding.buttonFinish.setOnClickListener(onButtonFinishClicked)
    }

    private val onButtonFinishClicked: (View)->Unit = { _: View ->
        requireActivity().findNavController(R.id.nav_host_fragment).popBackStack()
    }

}