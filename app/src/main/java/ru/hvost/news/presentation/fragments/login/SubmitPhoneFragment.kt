package ru.hvost.news.presentation.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.databinding.FragmentSubmitPhoneBinding
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.PhoneInputFilter
import ru.hvost.news.utils.events.EventObserver
import java.util.regex.Pattern

class SubmitPhoneFragment : BaseFragment() {

    private lateinit var binding: FragmentSubmitPhoneBinding
    private lateinit var authorizationVM: AuthorizationVM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubmitPhoneBinding.inflate(inflater, container, false)
        binding.phone.filters = arrayOf(PhoneInputFilter())
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        authorizationVM = ViewModelProvider(requireActivity())[AuthorizationVM::class.java]
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        authorizationVM.loginResponse?.userPhone?.run {
            setPhoneField(this)
        }
    }

    private fun setObservers() {
        authorizationVM.readyToSubmitPhone.observe(viewLifecycleOwner, EventObserver {
            binding.buttonCheck.isEnabled = it
        })
    }

    private fun setListeners() {
        binding.phone.doOnTextChanged { text, _, _, _ ->
            authorizationVM.setReadyToSubmitPhone(text?.length == 16)
        }
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