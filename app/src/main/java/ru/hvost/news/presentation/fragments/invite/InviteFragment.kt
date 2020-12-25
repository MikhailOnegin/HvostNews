package ru.hvost.news.presentation.fragments.invite

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentInviteBinding
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.OneTimeEvent

class InviteFragment : Fragment() {

    private lateinit var binding: FragmentInviteBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onBonusBalanceLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onSendInviteLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onInviteLinkLoadingEvent: DefaultNetworkEventObserver

    override fun onStart() {
        setSystemUiVisibility()
        super.onStart()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InlinedApi")
    private fun setSystemUiVisibility() {
        requireActivity().window.run {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }

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
        checkIsDataLoaded()
        initializeEventObservers()
        setObservers()
        setListeners()
    }

    private fun checkIsDataLoaded() {
        if (mainVM.bonusBalanceLoadingEvent.value?.peekContent() == State.SUCCESS) setBalance()
        if (mainVM.inviteLinkLoadingEvent.value?.peekContent() == State.SUCCESS) setLink()
    }

    private fun initializeEventObservers() {
        onBonusBalanceLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { setBalance() }
        )
        onSendInviteLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.sendedSuccessfull),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        onInviteLinkLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { setLink() }
        )
    }

    private fun setLink() {
        binding.link.setText(mainVM.inviteLink.value?.inviteLink)
    }

    private fun setBalance() {
        val balance = mainVM.bonusBalance.value?.bonusBalance ?: 0
        binding.balanceBefore.text = balance.dec().toString()
        binding.balance.text = balance.toString()
        binding.balanceAfter.text = balance.inc().toString()
    }

    private fun setListeners() {
        binding.linkContainer.setEndIconOnClickListener { shareRefLink() }
        binding.copy.setOnClickListener { copyRefLink() }
        binding.sendToEmail.setOnClickListener { sendToEmail() }
        binding.toPrizes.setOnClickListener {
            findNavController().navigate(R.id.action_inviteFragment_to_prizesFragment)
        }
        binding.instructions.setOnClickListener { showInviteInstructions() }
        binding.inviteInstructions.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(INSTRUCTIONS_LINK)
            )
            startActivity(intent)
        }
    }

    private fun showInviteInstructions() {
        requireActivity().findNavController(R.id.nav_host_fragment_invite_instructions).apply {
            setGraph(R.navigation.navigation_popup_invite_info)
        }
        binding.instructionsContainer.visibility = View.VISIBLE
    }

    private fun sendToEmail() {
        mainVM.SendInviteToMail(binding.sendToEmail.text.toString())
    }

    private fun copyRefLink() {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData =
            ClipData.newPlainText("simple text", mainVM.inviteLink.value?.inviteLink)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireActivity(), getString(R.string.refLinkCopied), Toast.LENGTH_SHORT)
            .show()
    }

    private fun shareRefLink() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, mainVM.inviteLink.value?.inviteLink)
        startActivity(Intent.createChooser(intent, "Link to share: "))
    }

    private fun setObservers() {
        mainVM.bonusBalanceLoadingEvent.observe(viewLifecycleOwner, onBonusBalanceLoadingEvent)
        mainVM.inviteLinkLoadingEvent.observe(viewLifecycleOwner, onInviteLinkLoadingEvent)
        mainVM.sendInviteLoadingEvent.observe(viewLifecycleOwner, onSendInviteLoadingEvent)
        mainVM.closeInstructionsEvent.observe(
            viewLifecycleOwner,
            OneTimeEvent.Observer { onCloseInstructionsEvent() }
        )
    }

    private fun onCloseInstructionsEvent() {
        binding.instructionsContainer.visibility = View.GONE
    }

    companion object {
        private const val INSTRUCTIONS_LINK =
            "https://hvost.news/upload/iblock/8f6/Konkurs-Priglasi-druga-KHvost.nyus-novyy-2020.pdf"
    }
}