package ru.hvost.news.presentation.fragments.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentLoginBinding
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent
import ru.hvost.news.utils.hasTooLongField

class LoginFragment : Fragment() {

    private lateinit var authorizationVM: AuthorizationVM
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(App.getInstance().userToken != null) {
            navigateToMainScreen()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        authorizationVM = ViewModelProvider(this)[AuthorizationVM::class.java]
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        setSystemUiVisibility()
        setLoginButton()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InlinedApi")
    private fun setSystemUiVisibility() {
        requireActivity().window.run {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.white)
        }
    }

    private fun setListeners() {
        binding.buttonLogin.setOnClickListener(onLoginButtonClicked)
        binding.buttonRegister.setOnClickListener(onRegisterButtonClicked)
        binding.restorePassword.setOnClickListener(onRestorePasswordButtonClicked)
        binding.login.doOnTextChanged { _, _, _, _ -> setLoginButton() }
        binding.password.doOnTextChanged { _, _, _, _ -> setLoginButton() }
    }

    private fun setLoginButton() {
        binding.buttonLogin.isEnabled =
            !binding.login.text.isNullOrBlank() && !binding.password.text.isNullOrBlank()
    }

    private fun setObservers() {
        authorizationVM.loginEvent.observe(viewLifecycleOwner) { onLoginEvent(it) }
    }

    private val onLoginEvent = { event: NetworkEvent<State> ->
        when(event.getContentIfNotHandled()){
            State.SUCCESS -> {
                binding.progress.visibility = View.GONE
                navigateToMainScreen()
            }
            State.ERROR -> {
                binding.progress.visibility = View.GONE
                createSnackbar(
                    binding.root,
                    event.error,
                    getString(R.string.buttonOk)
                ).show()
            }
            State.FAILURE -> {
                binding.progress.visibility = View.GONE
                createSnackbar(
                    binding.root,
                    getString(R.string.networkFailureMessage),
                    getString(R.string.buttonOk)
                ).show()
            }
            State.LOADING -> {
                binding.progress.visibility = View.VISIBLE
            }
        }
    }

    private val onLoginButtonClicked = { _: View ->
        if(authorizationVM.loginEvent.value?.peekContent() != State.LOADING) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
            val fields = arrayOf(binding.login, binding.password)
            if(!hasTooLongField(*fields)) {
                authorizationVM.logIn(
                    binding.login.text.toString(),
                    binding.password.text.toString()
                )
            }
        }
    }

    private val onRegisterButtonClicked = { _: View ->
        if(authorizationVM.loginEvent.value?.peekContent() != State.LOADING) {
            findNavController().navigate(R.id.action_loginFragment_to_regParentFragment)
        }
    }

    private val onRestorePasswordButtonClicked = { _: View ->
        if(authorizationVM.loginEvent.value?.peekContent() != State.LOADING) {
            findNavController().navigate(R.id.action_loginFragment_to_passRestoreFragment)
        }
    }

    private fun navigateToMainScreen() {
        findNavController().navigate(R.id.action_loginFragment_to_newsFragment)
    }

}