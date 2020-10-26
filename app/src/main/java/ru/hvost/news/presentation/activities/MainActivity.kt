package ru.hvost.news.presentation.activities

import android.animation.Animator
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isBnvShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setupStatusBar()
        binding.bnv.setupWithNavController(findNavController(R.id.nav_host_fragment))
    }

    private fun setupStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun showBnv(){
        if(isBnvShown) return
        val animator = ObjectAnimator.ofFloat(
            binding.bnv,
            "alpha",
            0f,
            1f
        )
        animator.duration = 1000L
        animator.addListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {
                binding.bnv.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                isBnvShown = true
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}
        })
        animator.start()
    }

    fun hideBnv(){
        if(!isBnvShown) return
        val animator = ObjectAnimator.ofFloat(
            binding.bnv,
            "alpha",
            1f,
            0f
        )
        animator.duration = 1000L
        animator.addListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                binding.bnv.visibility = View.GONE
                isBnvShown = false
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}
        })
        animator.start()
    }

}