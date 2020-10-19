package ru.hvost.news.presentation.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentRegUserBinding
import ru.hvost.news.utils.*

class RegUserFragment : Fragment() {

    private lateinit var binding: FragmentRegUserBinding
    private lateinit var registrationVM: RegistrationVM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registrationVM = ViewModelProvider(requireActivity())[RegistrationVM::class.java]
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        registrationVM.setStage(RegistrationVM.RegStep.USER)
    }

    private fun setListeners() {
        binding.buttonNext.setOnClickListener(onButtonNextClicked)
    }

    private val onButtonNextClicked = { _: View ->
        if(isEverythingOk()) {
            findNavController().navigate(R.id.action_regUserFragment_to_regPetFragment)
        }
    }

    private fun isEverythingOk(): Boolean {
        binding.run {
            val fields = arrayOf(surname, name, patronymic, phone, email, city)
            if(hasBlankField(*fields)) {
                scrollToTheTop()
                return false
            }
            if(hasTooLongField(*fields)) {
                scrollToTheTop()
                return false
            }
            if(isPhoneFieldIncorrect(phone)) {
                scrollToTheTop()
                return false
            }
            if(isEmailFieldIncorrect(email)) {
                scrollToTheTop()
                return false
            }
            if(!agreement.isChecked) {
                createSnackbar(
                    root,
                    getString(R.string.agreementRequired),
                    getString(R.string.buttonOk)
                ).show()
                return false
            }
        }
        return true
    }

    private fun scrollToTheTop() {
        binding.root.smoothScrollTo(0, 0)
    }

}