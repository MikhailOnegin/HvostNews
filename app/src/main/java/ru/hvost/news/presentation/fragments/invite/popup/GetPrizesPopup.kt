package ru.hvost.news.presentation.fragments.invite.popup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.MainViewModel
import ru.hvost.news.databinding.FragmentGetPrizesPopupBinding
import ru.hvost.news.utils.events.OneTimeEvent

class GetPrizesPopup : Fragment() {

    private lateinit var binding: FragmentGetPrizesPopupBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGetPrizesPopupBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener { }
        binding.done.setOnClickListener {
            //Отправляем событие о необходимости закрытия инструкций.
            mainVM.closeInstructionsEvent.value = OneTimeEvent()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

}