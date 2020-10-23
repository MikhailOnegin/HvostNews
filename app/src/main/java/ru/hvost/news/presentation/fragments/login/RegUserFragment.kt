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
        binding.phone.filters = arrayOf(PhoneInputFilter())
        binding.phone.setText("+7")
        //sergeev: Выпилить из релиза.
        setDummies()
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

    private fun setDummies() {
        binding.surname.setText("Сергеев")
        binding.name.setText("Денис")
        binding.patronymic.setText("Витальевич")
        binding.phone.setText("+7-963-095-67-22")
        binding.email.setText("sergeev@studiofact.ru")
        binding.city.setText("Магнитогорск")
        binding.agreement.isChecked = true
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
                scrollToTheTop(binding.root)
                return false
            }
            if(hasTooLongField(*fields)) {
                scrollToTheTop(binding.root)
                return false
            }
            if(isPhoneFieldIncorrect(phone)) {
                scrollToTheTop(binding.root)
                return false
            }
            if(isEmailFieldIncorrect(email)) {
                scrollToTheTop(binding.root)
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

}