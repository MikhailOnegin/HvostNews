package ru.hvost.news.presentation.activities

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.yandex.mapkit.MapKitFactory
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.ActivityMainBinding
import java.lang.AssertionError

class MainActivity : AppCompatActivity() {

    private lateinit var mainVM: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private var isBnvShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSystemUiVisibility()
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.bnv.setupWithNavController(findNavController(R.id.nav_host_fragment))
        initializeMaps()
    }

    private fun initializeMaps() {
        try {
            MapKitFactory.setApiKey(APIService.YANDEX_MAPKIT_KEY)
            MapKitFactory.initialize(this)
        } catch (exc: AssertionError) {
            if(App.LOG_ENABLED) {
                Log.e(App.DEBUG_TAG, "Can\'t initialize yandex maps: $exc")
            }
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InlinedApi")
    private fun setSystemUiVisibility() {
        window.run {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun userLogOut() {
        hideBnv()
        viewModelStore.clear()
        mainVM = ViewModelProvider(this)[MainViewModel::class.java]
        val navController = findNavController(R.id.nav_host_fragment)
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val navGraph = inflater.inflate(R.navigation.navigation_graph)
        navController.graph = navGraph
        App.getInstance().logOut()
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
        animator.addListener(object : Animator.AnimatorListener {
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

    private fun hideBnv() {
        binding.bnv.visibility = View.GONE
        isBnvShown = false
    }

    fun setBnvChecked(id: Int) {
        binding.bnv.menu.findItem(id).isChecked = true
    }

}