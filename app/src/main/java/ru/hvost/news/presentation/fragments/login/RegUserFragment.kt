package ru.hvost.news.presentation.fragments.login

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
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
    ): View {
        binding = FragmentRegUserBinding.inflate(inflater, container, false)
        binding.phone.filters = arrayOf(PhoneInputFilter())
        binding.password.filters = arrayOf(PasswordInputFilter())
        binding.passwordConfirm.filters = arrayOf(PasswordInputFilter())
        binding.phone.setText(getString(R.string.phonePrefix))
        binding.phone.setSelection(binding.phone.length())
        //setTestData() //sergeev: Выпилить из релиза
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setTestData() {
        binding.apply {
            surname.setText("Сергеев")
            name.setText("Денис")
            patronymic.setText("Витальевич")
            phone.setText("+7-963-095-67-22")
            email.setText("test@sergeev.ru")
            password.setText("passw0rd")
            passwordConfirm.setText("passw0rd")
            city.setText("Магнитогорск")
            agreement.isChecked = true
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registrationVM = ViewModelProvider(requireActivity())[RegistrationVM::class.java]
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        registrationVM.setStage(RegistrationVM.RegStep.USER)
        checkIfFirstStageFinished()
    }

    private fun setListeners() {
        binding.buttonNext.setOnClickListener(onButtonNextClicked)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment).popBackStack()
        }
        binding.agreement.setOnCheckedChangeListener { _, _ -> checkIfFirstStageFinished() }
        binding.name.doOnTextChanged { _, _, _, _ -> checkIfFirstStageFinished() }
        binding.surname.doOnTextChanged { _, _, _, _ -> checkIfFirstStageFinished() }
        binding.patronymic.doOnTextChanged { _, _, _, _ -> checkIfFirstStageFinished() }
        binding.phone.doOnTextChanged { _, _, _, _ -> checkIfFirstStageFinished() }
        binding.email.doOnTextChanged { _, _, _, _ -> checkIfFirstStageFinished() }
        binding.city.doOnTextChanged { _, _, _, _ -> checkIfFirstStageFinished() }
        binding.password.doOnTextChanged { _, _, _, _ -> checkIfFirstStageFinished() }
        binding.passwordConfirm.doOnTextChanged { _, _, _, _ -> checkIfFirstStageFinished() }
    }

    private val onButtonNextClicked = { _: View ->
        if(isEverythingOk()) {
            findNavController().navigate(R.id.action_regUserFragment_to_regPetFragment)
        }
    }

    private fun isEverythingOk(): Boolean {
        binding.run {
            val fields =
                arrayOf(surname, name, patronymic, phone, email, city, password, passwordConfirm)
            if(hasBlankField(*fields)) {
                scrollToTheTop(binding.scrollView)
                return false
            }
            if(hasTooLongField(*fields)) {
                scrollToTheTop(binding.scrollView)
                return false
            }
            if(isPhoneFieldIncorrect(phone)) {
                scrollToTheTop(binding.scrollView)
                return false
            }
            if(isEmailFieldIncorrect(email)) {
                scrollToTheTop(binding.scrollView)
                return false
            }
            if(password.text.toString() != passwordConfirm.text.toString()) {
                password.error = getString(R.string.errorPasswordsAreDifferent)
                return false
            }
            if(password.text.toString().length < resources.getInteger(R.integer.minPassLength)) {
                password.error = getString(R.string.errorPasswordsTooSmall)
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
        setViewModelFields()
        return true
    }

    private fun setViewModelFields() {
        registrationVM.userName = binding.name.text.toString()
        registrationVM.userSurname = binding.surname.text.toString()
        registrationVM.userPatronymic = binding.patronymic.text.toString()
        registrationVM.userPhone = binding.phone.text.toString()
        registrationVM.userEmail = binding.email.text.toString()
        registrationVM.userCity = binding.city.text.toString()
        registrationVM.password = binding.password.text.toString()
    }

    private fun setObservers() {
        registrationVM.stage.observe(viewLifecycleOwner) { onStageChanged.invoke(it) }
        registrationVM.step.observe(viewLifecycleOwner) { onStepChanged(it) }
        registrationVM.firstStageFinished.observe(viewLifecycleOwner) { onFirstStageFinished(it) }
    }

    private fun onFirstStageFinished(isFinished: Boolean?) {
        isFinished?.run {
            binding.buttonNext.isEnabled = this
        }
    }

    private fun checkIfFirstStageFinished() {
        registrationVM.firstStageFinished.value = !(
                binding.name.text.isNullOrBlank() ||
                binding.surname.text.isNullOrBlank() ||
                binding.patronymic.text.isNullOrBlank() ||
                binding.phone.text.isNullOrBlank() ||
                binding.email.text.isNullOrBlank() ||
                binding.city.text.isNullOrBlank() ||
                binding.password.text.isNullOrBlank() ||
                binding.passwordConfirm.text.isNullOrBlank() ||
                !binding.agreement.isChecked)
    }

    private fun onStepChanged(step: RegistrationVM.RegStep) {
        when(step) {
            RegistrationVM.RegStep.USER -> {
                binding.subtitle.text = getString(R.string.regStepUser)
                binding.step.text = getString(R.string.regStep1)
            }
            RegistrationVM.RegStep.PET -> {
                binding.subtitle.text = getString(R.string.regStepPet)
                binding.step.text = getString(R.string.regStep2)
            }
            RegistrationVM.RegStep.INTERESTS -> {
                binding.subtitle.text = getString(R.string.regStepInterests)
                binding.step.text = getString(R.string.regStep3)
            }
        }
    }

    private var animator: Animator? = null
    private val onStageChanged: (Pair<Int,Int>)->Unit = { progress ->
        animator?.cancel()
        animator = ObjectAnimator.ofInt(
            binding.progress,
            "progress",
            progress.first,
            progress.second)
        animator?.duration = resources.getInteger(R.integer.regProgressAnimationTime).toLong()
        animator?.interpolator = AccelerateDecelerateInterpolator()
        animator?.start()
    }

}