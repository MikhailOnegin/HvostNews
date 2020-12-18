package ru.hvost.news.presentation.fragments.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSubmitPhoneBinding
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.PhoneInputFilter
import ru.hvost.news.utils.WordEnding
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.EventObserver
import ru.hvost.news.utils.getClearPhoneString
import ru.hvost.news.utils.getWordEndingType
import java.util.regex.Pattern

class SubmitPhoneFragment : BaseFragment() {

    private lateinit var binding: FragmentSubmitPhoneBinding
    private lateinit var authorizationVM: AuthorizationVM
    private lateinit var mainVM: MainViewModel
    private lateinit var requestCodeObserver: DefaultNetworkEventObserver
    private lateinit var sendSecretCodeObserver: DefaultNetworkEventObserver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubmitPhoneBinding.inflate(inflater, container, false)
        initializeObservers()
        setListeners()
        authorizationVM = ViewModelProvider(requireActivity())[AuthorizationVM::class.java]
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setObservers()
        binding.phone.filters = arrayOf(PhoneInputFilter())
        authorizationVM.loginResponse?.userPhone?.run {
            setPhoneField(this)
        }
        return binding.root
    }

    private fun initializeObservers() {
        requestCodeObserver = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { onSecretCodeSent() }
        )
        sendSecretCodeObserver = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { onPhoneRegistered() }
        )
    }

    private fun onPhoneRegistered() {
        authorizationVM.loginResponse?.userToken?.run {
            App.getInstance().logIn(this)
            mainVM.initializeData()
            findNavController().navigate(R.id.action_submitPhoneFragment_to_feedFragment)
        }
    }

    private fun onSecretCodeSent() {
        binding.buttonSendAgain.isEnabled = false
        binding.phoneInputLayout.visibility = View.GONE
        binding.codeInputLayout.visibility = View.VISIBLE
        binding.buttonSendAgain.visibility = View.VISIBLE
        binding.timer.visibility = View.VISIBLE
        binding.buttonCheck.isEnabled = false
        binding.code.doOnTextChanged { text, _, _, _ ->
            binding.buttonCheck.isEnabled = text?.isNotBlank() ?: false
        }
        binding.buttonCheck.setOnClickListener {
            authorizationVM.sendSecretCode(
                userToken = authorizationVM.loginResponse?.userToken,
                phone = getClearPhoneString(binding.phone.text.toString()),
                secretCode = binding.code.text.toString()
            )
        }
    }

    private fun setObservers() {
        authorizationVM.readyToSubmitPhone.observe(viewLifecycleOwner, EventObserver {
            binding.buttonCheck.isEnabled = it
        })
        authorizationVM.requestSmsEvent.observe(viewLifecycleOwner, requestCodeObserver)
        authorizationVM.sendSecretCodeEvent.observe(viewLifecycleOwner, sendSecretCodeObserver)
        authorizationVM.timerTickEvent.observe(viewLifecycleOwner, EventObserver( onTimerTick ))
    }

    @SuppressLint("SetTextI18n")
    private val onTimerTick = { seconds: Int ->
        if (seconds == 0) {
            binding.timer.visibility = View.GONE
            binding.buttonSendAgain.isEnabled = true
        } else {
            val text1 = getString(R.string.timerText1)
            val text2 = when (getWordEndingType(seconds)) {
                WordEnding.TYPE_1 -> getString(R.string.timerSeconds1)
                WordEnding.TYPE_2 -> getString(R.string.timerSeconds2)
                WordEnding.TYPE_3 -> getString(R.string.timerSeconds3)
            }
            binding.timer.text = "$text1 $seconds $text2"
        }
    }

    private fun setListeners() {
        binding.phone.doOnTextChanged { text, _, _, _ ->
            authorizationVM.setReadyToSubmitPhone(text?.length == 16)
        }
        binding.buttonCheck.setOnClickListener { requestSms() }
        binding.buttonSendAgain.setOnClickListener { requestSms() }
    }

    private fun requestSms() {
        authorizationVM.requestSms(
            userToken = authorizationVM.loginResponse?.userToken,
            phone = getClearPhoneString(binding.phone.text.toString())
        )
    }

    private fun setPhoneField(phone: String?) {
        if (phone.isNullOrBlank()) setPhoneStartTemplate()
        else {
            val pattern = Pattern.compile("7[0-9]{10}")
            val matcher = pattern.matcher(phone)
            if (matcher.matches()) {
                val builder = StringBuilder(phone)
                builder.insert(0, "+")
                builder.insert(2, "-")
                builder.insert(6, "-")
                builder.insert(10, "-")
                builder.insert(13, "-")
                binding.phone.setText(builder.toString())
            } else {
                setPhoneStartTemplate()
            }
        }
    }

    private fun setPhoneStartTemplate() {
        binding.phone.setText("+7-")
    }

}