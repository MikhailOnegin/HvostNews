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
import ru.hvost.news.utils.EventObserver
import ru.hvost.news.utils.enums.State

class LoginFragment : Fragment() {

    private lateinit var authorizationVM: AuthorizationVM
    private lateinit var binding: FragmentLoginBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        //TODO: Выпилить из релиза.
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
    }

    private fun setObservers() {
        authorizationVM.loginEvent.observe(viewLifecycleOwner, EventObserver(onLoginEvent))
    }

    private val onLoginEvent = { state: State ->
        when(state){
            State.SUCCESS -> {
                findNavController().navigate(R.id.action_loginFragment_to_newsFragment)
            }
        }
    }

    private val onLoginButtonClicked = { _: View ->
        authorizationVM.logIn(
            binding.login.text.toString(),
            binding.password.text.toString()
        )
    }

}