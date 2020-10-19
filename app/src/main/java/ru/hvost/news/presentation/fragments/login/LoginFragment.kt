package ru.hvost.news.presentation.fragments.login

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentLoginBinding
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent

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
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        //sergeev: Выпилить из релиза.
        binding.login.setText("v.fedotov@studiofact.ru")
        binding.password.setText("123123123")
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
    }

    private fun setListeners() {
        binding.buttonLogin.setOnClickListener(onLoginButtonClicked)
        binding.buttonRegister.setOnClickListener(onRegisterButtonClicked)
        binding.restorePassword.setOnClickListener(onRestorePasswordButtonClicked)
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
            authorizationVM.logIn(
                binding.login.text.toString(),
                binding.password.text.toString()
            )
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