package ru.hvost.news.presentation.activities

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import ru.hvost.news.R
import ru.hvost.news.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isBnvShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSystemUiVisibility()
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.bnv.setupWithNavController(findNavController(R.id.nav_host_fragment))
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InlinedApi")
    private fun setSystemUiVisibility() {
        window.run {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
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