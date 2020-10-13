package ru.hvost.news.presentation.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.databinding.FragmentPassRestoreBinding
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

    private val onPassRestoreEvent = { event: NetworkEvent<State> ->
        //TODO работать здесь.
    }

}