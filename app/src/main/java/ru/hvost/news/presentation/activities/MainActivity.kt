package ru.hvost.news.presentation.activities

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.yandex.mapkit.MapKitFactory
import ru.hvost.news.App
import ru.hvost.news.BuildConfig
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.ActivityMainBinding
import ru.hvost.news.services.FcmService
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
        binding.bnv.setOnNavigationItemReselectedListener { }
        initializeMaps()
        createNotificationsChannels()
        subscribeToNewArticlesTopic()
        printDeviceInfo()
    }

    private fun printDeviceInfo() {
        if (App.LOG_ENABLED && BuildConfig.DEBUG)
            Log.d(App.DEBUG_TAG, resources.configuration.toString())
    }

    private fun initializeMaps() {
        try {
            MapKitFactory.setApiKey(APIService.YANDEX_MAPKIT_KEY)
            MapKitFactory.initialize(this)
        } catch (exc: AssertionError) {
            if (App.LOG_ENABLED) {
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
        App.getInstance().logOut()
        hideBnv()
        viewModelStore.clear()
        mainVM = ViewModelProvider(this)[MainViewModel::class.java]
        val navController = findNavController(R.id.nav_host_fragment)
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val navGraph = inflater.inflate(R.navigation.navigation_graph)
        navController.graph = navGraph
    }

    fun showBnv() {
        if (isBnvShown) return
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

    private fun createNotificationsChannels() {
        createNewArticlesNotificationChannel()
        createOrderStatusNotificationChannel()
        createOfflineEventsNotificationChannel()
    }

    private fun createNewArticlesNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val newArticlesChannelName = getString(R.string.newArticlesChannelName)
            val newArticlesChannelDescription = getString(R.string.newArticlesChannelDescription)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                NEW_ARTICLES_CHANNEL_ID,
                newArticlesChannelName,
                importance
            )
            channel.description = newArticlesChannelDescription
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createOrderStatusNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val orderStatusChannelName = getString(R.string.orderStatusChannelName)
            val orderStatusChannelDescription = getString(R.string.orderStatusChannelDescription)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                ORDER_STATUS_CHANNEL_ID,
                orderStatusChannelName,
                importance
            )
            channel.description = orderStatusChannelDescription
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createOfflineEventsNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val orderStatusChannelName = getString(R.string.offlineEventsChannelName)
            val orderStatusChannelDescription = getString(R.string.offlineEventsChannelDescription)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                OFFLINE_EVENTS_CHANNEL_ID,
                orderStatusChannelName,
                importance
            )
            channel.description = orderStatusChannelDescription
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun subscribeToNewArticlesTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(FcmService.TOPIC_NEW_ARTICLES)
    }

    fun getMainView(): View {
        return binding.root
    }

    companion object {

        const val NEW_ARTICLES_CHANNEL_ID = "new_articles_channel_id"
        const val ORDER_STATUS_CHANNEL_ID = "order_status_channel_id"
        const val OFFLINE_EVENTS_CHANNEL_ID = "offline_events_channel_id"

    }

}