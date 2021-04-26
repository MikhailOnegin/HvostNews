package ru.hvost.news.presentation.fragments.splash

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSplashScreenBinding

class SplashScreenFragment : Fragment() {

    private val showSplashScreen = true

    private lateinit var binding: FragmentSplashScreenBinding
    private lateinit var splashAnimation: AnimationDrawable
    private lateinit var splashVM: SplashViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        binding.imageView.apply {
            splashAnimation = drawable as AnimationDrawable
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        splashVM = ViewModelProvider(this)[SplashViewModel::class.java]
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        if (showSplashScreen) runSplashScreenAnimation()
        else splashVM.dismissSplashScreen()
    }

    private fun runSplashScreenAnimation() {
        splashAnimation.start()
        splashVM.startSplashScreenTimer(SPLASH_SCREEN_ANIMATION_TIME)
    }

    fun setObservers() {
        splashVM.splashFinishEvent.observe(viewLifecycleOwner) { onTimerEnds() }
    }

    private fun onTimerEnds() {
        splashAnimation.stop()
        moveFurther()
    }

    private fun moveFurther() {
        if (App.getInstance().userToken != null) {
            findNavController().navigate(R.id.action_splashScreen_to_feedFragment)
        } else {
            findNavController().navigate(R.id.action_splashScreen_to_loginFragment)
        }
    }

    companion object {

        const val SPLASH_SCREEN_ANIMATION_TIME = 4000L

    }

}