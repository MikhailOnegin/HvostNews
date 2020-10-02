package ru.hvost.news.presentation.fragments.coupons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hvost.news.databinding.FragmentMyCouponsBinding
import ru.hvost.news.presentation.viewmodels.CouponViewModel

class MyCouponsFragment: Fragment() {

    private lateinit var binding: FragmentMyCouponsBinding
    private lateinit var couponVM: CouponViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCouponsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        couponVM = ViewModelProvider(requireActivity())[CouponViewModel::class.java]
    }
}