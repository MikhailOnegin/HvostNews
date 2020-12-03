package ru.hvost.news.presentation.fragments

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    override fun onStart() {
        super.onStart()
        setSystemUiVisibility()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InlinedApi")
    private fun setSystemUiVisibility() {
        requireActivity().window.run {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.white)
        }
    }

}