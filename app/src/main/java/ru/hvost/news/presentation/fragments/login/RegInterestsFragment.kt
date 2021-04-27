package ru.hvost.news.presentation.fragments.login

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentRegInterestsBinding
import ru.hvost.news.models.RegInterest
import ru.hvost.news.presentation.adapters.recycler.RegInterestsAdapter
import ru.hvost.news.utils.GridRvItemDecorations
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent

class RegInterestsFragment : Fragment() {

    private lateinit var binding: FragmentRegInterestsBinding
    private lateinit var registrationVM: RegistrationVM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegInterestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registrationVM = ViewModelProvider(requireActivity())[RegistrationVM::class.java]
        setRecyclerView()
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        registrationVM.setStage(RegistrationVM.RegStep.INTERESTS)
    }

    private fun setListeners() {
        binding.buttonFinish.setOnClickListener(onButtonFinishClicked)
        binding.back.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setObservers() {
        registrationVM.interests.observe(viewLifecycleOwner) { onInterestsChanged(it) }
        registrationVM.thirdStageFinished.observe(viewLifecycleOwner) { onThirdStageFinished(it) }
        registrationVM.registrationState.observe(viewLifecycleOwner) { onRegistrationStateChanged(it) }
        registrationVM.progress.observe(viewLifecycleOwner) { onStageChanged.invoke(it) }
        registrationVM.step.observe(viewLifecycleOwner) { onStepChanged(it) }
    }

    private val onRegistrationStateChanged = { event: NetworkEvent<State> ->
        when(event.getContentIfNotHandled()) {
            State.SUCCESS -> {
                val bundle = Bundle()
                bundle.putString(LoginFragment.EXTRA_LOGIN, registrationVM.userEmail)
                bundle.putString(LoginFragment.EXTRA_PASSWORD, registrationVM.password)
                requireActivity().findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_regParentFragment_to_loginFragment, bundle)
            }
            State.ERROR -> {
                createSnackbar(
                    binding.root,
                    event.error,
                    getString(R.string.buttonOk)
                ).show()
                binding.buttonFinish.isEnabled = true
            }
            State.FAILURE -> {
                createSnackbar(
                    binding.root,
                    getString(R.string.networkFailureMessage),
                    getString(R.string.buttonOk)
                ).show()
                binding.buttonFinish.isEnabled = true
            }
            State.LOADING -> {
                binding.buttonFinish.isEnabled = false
            }
            null -> {}
        }
    }

    private fun onThirdStageFinished(thirdStageFinished: Boolean?) {
        binding.buttonFinish.isEnabled = thirdStageFinished == true
    }

    private fun onInterestsChanged(interests: List<RegInterest>?) {
        interests?.run {
            (binding.recyclerView.adapter as RegInterestsAdapter).submitList(this)
        }
    }

    private val onButtonFinishClicked: (View)->Unit = { _: View ->
        registrationVM.registerUser()
    }

    private fun setRecyclerView() {
        val buttonHeight = resources.getDimension(R.dimen.buttonHeight)
        val margin = resources.getDimension(R.dimen.normalMargin)
        val padding = buttonHeight + margin
        binding.recyclerView.apply {
            updatePadding(top=margin.toInt(), bottom = padding.toInt())
            addItemDecoration(GridRvItemDecorations(
                R.dimen.largeMargin,
                R.dimen.normalMargin
            ))
            adapter = RegInterestsAdapter(registrationVM)
        }
    }

    private fun onStepChanged(step: RegistrationVM.RegStep) {
        when(step) {
            RegistrationVM.RegStep.USER -> {
                binding.subtitle.text = getString(R.string.regStepUser)
                binding.step.text = getString(R.string.regStep1)
            }
            RegistrationVM.RegStep.PET -> {
                binding.subtitle.text = getString(R.string.regStepPet)
                binding.step.text = getString(R.string.regStep2)
            }
            RegistrationVM.RegStep.INTERESTS -> {
                binding.subtitle.text = getString(R.string.regStepInterests)
                binding.step.text = getString(R.string.regStep3)
            }
        }
    }

    private var animator: Animator? = null
    private val onStageChanged: (Pair<Int,Int>)->Unit = { progress ->
        animator?.cancel()
        animator = ObjectAnimator.ofInt(
            binding.progress,
            "progress",
            progress.first,
            progress.second)
        animator?.duration = resources.getInteger(R.integer.regProgressAnimationTime).toLong()
        animator?.interpolator = AccelerateDecelerateInterpolator()
        animator?.start()
    }

}