package ru.hvost.news.presentation.fragments.invite

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentInviteBinding
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.OneTimeEvent
import ru.hvost.news.utils.isEmailFieldIncorrect
import java.util.*

class InviteFragment : Fragment() {

    private lateinit var binding: FragmentInviteBinding
    private lateinit var mainVM: MainViewModel
    private lateinit var onBonusBalanceLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onSendInviteLoadingEvent: DefaultNetworkEventObserver
    private lateinit var onInviteLinkLoadingEvent: DefaultNetworkEventObserver
    private lateinit var callback: OnBackPressedCallback

    override fun onStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) setSystemUiVisibility()
        super.onStart()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InlinedApi")
    private fun setSystemUiVisibility() {
        requireActivity().window.run {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.white)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callback =
            requireActivity().onBackPressedDispatcher.addCallback {
                val isVisible = binding.instructionsContainer.visibility == View.VISIBLE
                if (isVisible) {
                    animateFadeDialog(false)
                } else {
                    findNavController().popBackStack()
                }
            }
        callback.isEnabled = true
    }

    override fun onStop() {
        super.onStop()
        callback.isEnabled = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInviteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        if(mainVM.inviteInstructionFirstTime) showInviteInstructions()
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
                createSnackbar(
                    binding.root,
                    getString(R.string.sendedSuccessfull),
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
        binding.balance.text = balance.toString()
        if (balance > 0) {
            binding.balanceBefore.text = balance.dec().toString()
            binding.balanceAfter.text = balance.inc().toString()
        } else {
            binding.balanceBefore.visibility = View.GONE
            binding.balanceAfter.visibility = View.GONE
        }
        binding.balanceBefore.text =
            if (balance == 0) balance.toString() else balance.dec().toString()
        binding.balance.text = balance.toString()
        binding.balanceAfter.text =
            if (balance == 0) balance.toString() else balance.inc().toString()
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
        setSocialsShare()
    }

    private fun setSocialsShare() {
        binding.vk.setOnClickListener(onSelectTabButton)
        binding.facebook.setOnClickListener(onSelectTabButton)
        binding.ok.setOnClickListener(onSelectTabButton)
    }

    private val onSelectTabButton = { view: View ->
        when (view.id) {
            R.id.vk -> {
                shareIntent("vk")
            }
            R.id.facebook -> {
                shareIntent("facebook")
            }
            R.id.ok -> {
                shareIntent("ok")
            }
            else -> {
            }
        }
    }

    private fun shareIntent(to: String) {
        val intent = Intent()
        intent.apply {
            action = Intent.ACTION_SEND
            intent.type = "text/plain"
        }
        val intentList = requireActivity().packageManager.queryIntentActivities(intent, 0)
        if (intentList.isNullOrEmpty()) return

        for (item in intentList) {
            if (item.activityInfo.packageName.toLowerCase(Locale.ROOT).contains(to)
                || item.activityInfo.name.toLowerCase(Locale.ROOT).contains(to)
            ) {
                if (to == "ok" && (item.activityInfo.packageName.toLowerCase(Locale.ROOT)
                        .contains("facebook") || item.activityInfo.name.toLowerCase(Locale.ROOT)
                        .contains("facebook"))
                ) continue
                intent.component = ComponentName(
                    item.activityInfo.packageName,
                    item.activityInfo.name
                )
                intent.putExtra(Intent.EXTRA_TEXT, SOCIALS_SHARE_LINK)
                break
            }
        }
        startActivity(Intent.createChooser(intent, "Select"))
    }

    private fun showInviteInstructions() {
        if(mainVM.inviteInstructionFirstTime) mainVM.inviteInstructionFirstTime = false
        requireActivity().findNavController(R.id.nav_host_fragment_invite_instructions).apply {
            setGraph(R.navigation.navigation_popup_invite_info)
        }
        binding.instructionsContainer.visibility = View.VISIBLE
        animateFadeDialog(true)
    }

    private fun animateFadeDialog(toFull: Boolean) {
        val alpha = binding.instructionsContainer.alpha
        val startValue = if (toFull) alpha else 1f
        val endValue = if (toFull) 1f else 0f
        val animator = ValueAnimator.ofFloat(startValue, endValue)
        animator.duration = resources.getInteger(R.integer.filtersContainerAnimationTime).toLong()
        animator.addUpdateListener {
            binding.instructionsContainer.alpha = it.animatedValue as Float
        }
        if (!toFull) animator.addListener(OnDialogDismissAnimationListener())
        animator.start()
    }

    private fun sendToEmail() {
        if (isEmailFieldIncorrect(binding.mail))
            binding.mail.error = getString(R.string.incorrectEmail)
        else
            mainVM.sendInviteToMail(binding.mail.text.toString())
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
        animateFadeDialog(false)
    }

    inner class OnDialogDismissAnimationListener : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {
            binding.instructionsContainer.visibility = View.GONE
        }

        override fun onAnimationCancel(animation: Animator?) {}

        override fun onAnimationRepeat(animation: Animator?) {}

    }

    companion object {
        private const val SOCIALS_SHARE_LINK = "https://hvost.news/invite/"
        private const val INSTRUCTIONS_LINK =
            "https://hvost.news/upload/iblock/8f6/Konkurs-Priglasi-druga-KHvost.nyus-novyy-2020.pdf"
    }
}