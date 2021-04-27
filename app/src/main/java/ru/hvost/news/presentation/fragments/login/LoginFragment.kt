package ru.hvost.news.presentation.fragments.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentLoginBinding
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent
import ru.hvost.news.utils.hasTooLongField

class LoginFragment : BaseFragment() {

    private lateinit var authorizationVM: AuthorizationVM
    private lateinit var mainVM: MainViewModel
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
        setHintSpannable()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authorizationVM = ViewModelProvider(requireActivity())[AuthorizationVM::class.java]
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setObservers()
        trySetExtraData()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        setLoginButton()
    }

    private fun trySetExtraData() {
        arguments?.run {
            val login = getString(EXTRA_LOGIN)
            val password = getString(EXTRA_PASSWORD)
            if (!login.isNullOrBlank() && !password.isNullOrBlank()) {
                binding.login.setText(login)
                binding.password.setText(password)
                onLoginButtonClicked.invoke(binding.buttonLogin)
            }
        }
    }

    private fun setListeners() {
        binding.buttonLogin.setOnClickListener(onLoginButtonClicked)
        binding.buttonRegister.setOnClickListener(onRegisterButtonClicked)
        binding.restorePassword.setOnClickListener(onRestorePasswordButtonClicked)
        binding.login.doOnTextChanged { _, _, _, _ -> setLoginButton() }
        binding.password.doOnTextChanged { _, _, _, _ -> setLoginButton() }
    }

    private fun setHintSpannable() {
        val spannable = SpannableString(getString(R.string.loginScreenHint))
        spannable.setSpan(
            ScreenHintClickableSpan(requireActivity()),
            38, 50,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.loginScreenHint.text = spannable
        binding.loginScreenHint.movementMethod = LinkMovementMethod()
    }

    class ScreenHintClickableSpan(
        private val context: Context
    ) : ClickableSpan() {

        private val linkColor = ContextCompat.getColor(context, R.color.colorPrimary)

        override fun onClick(widget: View) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://hvost.news")
            context.startActivity(intent)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = linkColor
        }

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
                if (authorizationVM.loginResponse?.isPhoneRegistered == true) {
                    navigateToMainScreen()
                } else {
                    navigateToSubmitPhoneScreen()
                }
                binding.buttonLogin.isEnabled = true
                mainVM.initializeData()
            }
            State.ERROR -> {
                binding.progress.visibility = View.GONE
                createSnackbar(
                    binding.root,
                    event.error,
                    getString(R.string.buttonOk)
                ).show()
                binding.buttonLogin.isEnabled = true
            }
            State.FAILURE -> {
                binding.progress.visibility = View.GONE
                createSnackbar(
                    binding.root,
                    getString(R.string.networkFailureMessage),
                    getString(R.string.buttonOk)
                ).show()
                binding.buttonLogin.isEnabled = true
            }
            State.LOADING -> {
                binding.buttonLogin.isEnabled = false
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
        findNavController().navigate(R.id.action_loginFragment_to_feedFragment)
    }

    private fun navigateToSubmitPhoneScreen() {
        findNavController().navigate(R.id.action_loginFragment_to_submitPhoneFragment)
    }

    companion object {

        const val EXTRA_LOGIN = "extra_login"
        const val EXTRA_PASSWORD = "extra_password"

    }

}