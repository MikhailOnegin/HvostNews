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
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSplashScreenBinding
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class SplashScreenFragment : Fragment() {

    private val showSplashScreen = false

    private lateinit var binding: FragmentSplashScreenBinding
    private lateinit var splashAnimation: AnimationDrawable
    private lateinit var splashVM: SplashViewModel
    private lateinit var mainVM: MainViewModel
    private lateinit var finishObserver: DefaultNetworkEventObserver
    private lateinit var loadingArticlesEventObserver: DefaultNetworkEventObserver
    private var isAnimationOver = false
    private var isDataLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        binding.imageView.apply {
            splashAnimation = drawable as AnimationDrawable
        }
        initializeObservers()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        splashVM = ViewModelProvider(this)[SplashViewModel::class.java]
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        if(showSplashScreen) runSplashScreenAnimation()
        else splashVM.dismissSplashScreen()
    }

    private fun runSplashScreenAnimation() {
        splashAnimation.start()
        splashVM.startSplashScreenTimer(SPLASH_SCREEN_ANIMATION_TIME)
    }

    fun setObservers() {
        splashVM.splashFinishEvent.observe(viewLifecycleOwner) { onTimerEnds() }
        mainVM.articlesLoadingEvent.observe(viewLifecycleOwner, loadingArticlesEventObserver)
    }

    private fun onTimerEnds() {
        splashAnimation.stop()
        isAnimationOver = true
        tryMoveFurther()
    }

    private fun tryMoveFurther() {
        if(isAnimationOver && isDataLoaded) {
            if (App.getInstance().userToken != null) {
                findNavController().navigate(R.id.action_splashScreen_to_feedFragment)
            } else {
                findNavController().navigate(R.id.action_splashScreen_to_loginFragment)
            }
        }
    }

    private fun initializeObservers() {
        finishObserver = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                if(App.getInstance().userToken != null) {
                    findNavController().navigate(R.id.action_splashScreen_to_feedFragment)
                } else {
                    findNavController().navigate(R.id.action_splashScreen_to_loginFragment)
                }
            }
        )
        loadingArticlesEventObserver = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                isDataLoaded = true
                tryMoveFurther()
            },
            doOnError = { mainVM.initializeData() },
            doOnFailure = { mainVM.initializeData() }
        )
    }

    companion object {

        const val SPLASH_SCREEN_ANIMATION_TIME = 4000L

    }

}