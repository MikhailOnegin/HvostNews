package ru.hvost.news.presentation.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentPassRestoreBinding
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent

class PassRestoreFragment : Fragment() {

    private lateinit var binding: FragmentPassRestoreBinding
    private lateinit var authorizationVM: AuthorizationVM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPassRestoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        authorizationVM = ViewModelProvider(this)[AuthorizationVM::class.java]
        authorizationVM.passRestoreEvent.observe(viewLifecycleOwner) { onPassRestoreEvent(it) }
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonSend.setOnClickListener(onSendButtonClicked)
    }

    private val onSendButtonClicked = { _: View ->
        if(authorizationVM.passRestoreEvent.value?.peekContent() != State.LOADING) {
            if (!binding.email.text.isNullOrBlank()) {
                authorizationVM.restorePassAsync(binding.email.text.toString())
            }
        }
    }

    private val onPassRestoreEvent = { event: NetworkEvent<State> ->
        when(event.getContentIfNotHandled()){
            State.SUCCESS -> {
                binding.progress.visibility = View.GONE
                createSnackbar(
                    binding.root,
                    getString(R.string.passRestoringInstructionsSent),
                    getString(R.string.buttonOk)
                ) { findNavController().popBackStack() }.show()
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

}