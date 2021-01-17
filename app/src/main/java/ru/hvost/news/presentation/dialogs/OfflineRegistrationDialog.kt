package ru.hvost.news.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.DialogSeminarRegistrationBinding
import ru.hvost.news.models.OfflineSeminars
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.startIntentActionView

class OfflineRegistrationDialog(private val seminarId:Int) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogSeminarRegistrationBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var setParticipateEvent: DefaultNetworkEventObserver
    private var seminar: OfflineSeminars.OfflineSeminar? = null
    override fun onStart() {
        super.onStart()
        val decorView = dialog?.window?.decorView
        decorView?.animate()?.translationY(-100f)?.setStartDelay(300)?.setDuration(300)?.start()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSeminarRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        initializedEvents()
        setObservers(this)
        setListeners()
        val text = resources.getString(R.string.accept_terms_of_agreement)
        val spannable = SpannableString(text)
        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(p0: View) {
                startIntentActionView(requireContext(), "https://hvost.news/upload/iblock/78f/Politika-konfidentsialnosti-hvost.news.pdf")
            }
        }
        spannable.setSpan(clickableSpan1, 19, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(colorSpan, 19, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.checkBox2.text = spannable
        binding.checkBox2.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setObservers(owner:LifecycleOwner) {
        schoolVM.offlineSeminars.observe(owner, {
            val f = 2
            for(i in it.seminars.indices){
                val seminar = it.seminars[i]
                if(seminar.id == seminarId){
                    this.seminar = seminar
                    val head = "${getString(R.string.registrated_offline_semianar)} ${seminar.title}"
                    binding.textViewHead.text = head
                }
            }
        })
        schoolVM.enabledSeminarRegister.observe(owner, {
            binding.buttonCompleteRegistration.isEnabled = it
        })
        schoolVM.setParticipateEvent.observe(owner, setParticipateEvent)
    }

    private fun initializedEvents(){
        setParticipateEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnLoading = {
                binding.buttonCompleteRegistration.isEnabled = false
            },
            doOnSuccess = {
                seminar?.let {
                    SuccessRegistrationSeminarDialog(it.title).show(
                        childFragmentManager,
                        "success_registration_dialog"
                    )
                }
                this.dismiss()
            }
            ,
            doOnFailure = {
                binding.buttonCompleteRegistration.isEnabled = true
                this.dismiss()
            },
            doOnError = {
                binding.buttonCompleteRegistration.isEnabled = true
                this.dismiss()
            },)
    }

    private fun setListeners() {
        binding.checkBox2.setOnClickListener {
            schoolVM.enabledSeminarRegister.value = binding.checkBox2.isChecked
        }
        binding.buttonCompleteRegistration.setOnClickListener {
            App.getInstance().userToken?.let {
                schoolVM.setParticipate(it, seminarId.toString(), null)
            }

        }
    }
}