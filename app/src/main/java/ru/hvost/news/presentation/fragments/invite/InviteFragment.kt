package ru.hvost.news.presentation.fragments.invite

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentInviteBinding
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.OneTimeEvent

class InviteFragment : Fragment() {

    private lateinit var binding: FragmentInviteBinding
    private lateinit var mainVM: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInviteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setObservers()
        setListeners()
    }

    private fun showInfoPopup() {
        val view = layoutInflater.inflate(R.layout.popup_invite_info, binding.root, false)
        val popupWindow = PopupWindow(requireActivity())

        popupWindow.contentView = view
        popupWindow.width = LinearLayout.LayoutParams.WRAP_CONTENT
        popupWindow.height = LinearLayout.LayoutParams.WRAP_CONTENT
        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
    }

    private fun setListeners() {
        binding.share.setOnClickListener { shareRefLink() }
        binding.copy.setOnClickListener { copyRefLink() }
        binding.sendToEmail.setOnClickListener { sendToEmail() }
        binding.toPrizes.setOnClickListener {
            findNavController().navigate(R.id.action_inviteFragment_to_prizesFragment)
        }
        binding.instructions.setOnClickListener { showInviteInstructions() }
    }

    private fun showInviteInstructions() {
        requireActivity().findNavController(R.id.nav_host_fragment_invite_instructions).apply {
            //Обновляем граф в NavController, чтобы всегда начинать с первого фрагмента.
            setGraph(R.navigation.navigation_popup_invite_info)
        }
        //Показываем контейнер с инструкциями.
        binding.instructionsContainer.visibility = View.VISIBLE
    }

    private fun sendToEmail() {
        mainVM.SendInviteToMail(binding.sendToEmail.text.toString())
    }

    private fun copyRefLink() {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData =
            ClipData.newPlainText("simple text", mainVM.inviteLinkResponse.value?.inviteLink)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireActivity(), getString(R.string.refLinkCopied), Toast.LENGTH_SHORT)
            .show()
    }

    private fun shareRefLink() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, mainVM.inviteLinkResponse.value?.inviteLink)
        startActivity(Intent.createChooser(intent, "Link to share: "))
    }

    private fun setObservers() {
        mainVM.bonusBalanceState.observe(viewLifecycleOwner, { onBalanceChanged(it) })
        mainVM.inviteLinkState.observe(viewLifecycleOwner, { onLinkChanged(it) })
        mainVM.sendInviteState.observe(viewLifecycleOwner, { onInviteSended(it) })
        mainVM.closeInstructionsEvent.observe(
            viewLifecycleOwner,
            OneTimeEvent.Observer { onCloseInstructionsEvent() }
            /* OneTimeEvent.Observer - специальный Observer для объектов типа OneTimeEvent.
            * Реализация гарантирует, что обработчик сработает только один раз по одному событию. */
        )
    }

    private fun onCloseInstructionsEvent() {
        //Прячем контейнер с инструкциями.
        binding.instructionsContainer.visibility = View.GONE
    }

    private fun onInviteSended(state: State?) {
        when (state) {
            State.SUCCESS -> {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.sendedSuccessfull),
                    Toast.LENGTH_SHORT
                ).show()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun onLinkChanged(state: State?) {
        when (state) {
            State.SUCCESS -> {
                binding.link.setText(mainVM.inviteLinkResponse.value?.inviteLink)
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }

    private fun onBalanceChanged(state: State?) {
        when (state) {
            State.SUCCESS -> {
                binding.balance.text = mainVM.bonusBalanceResponse.value?.bonusBalance.toString()
            }
            State.FAILURE, State.ERROR -> {
            }
        }
    }
}