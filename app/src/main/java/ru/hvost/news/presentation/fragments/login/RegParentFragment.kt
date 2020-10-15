package ru.hvost.news.presentation.fragments.login

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.databinding.FragmentRegParentBinding
import ru.hvost.news.utils.events.EventObserver

class RegParentFragment : Fragment() {

    private lateinit var binding: FragmentRegParentBinding
    private lateinit var registrationVM: RegistrationVM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegParentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registrationVM = ViewModelProvider(requireActivity())[RegistrationVM::class.java]
        setObservers()
    }

    private fun setObservers() {
        registrationVM.stage.observe(viewLifecycleOwner, EventObserver(onStageChanged))
    }

    private var animator: Animator? = null
    private val onStageChanged: (Int)->Unit = { progress: Int ->
        animator?.cancel()
        animator = ObjectAnimator.ofInt(
            binding.progress,
            "progress",
            binding.progress.progress,
            progress)
        animator?.duration = 600L
        animator?.interpolator = AccelerateDecelerateInterpolator()
        animator?.start()
    }

}