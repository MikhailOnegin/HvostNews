package ru.hvost.news.presentation.fragments.coupons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.databinding.FragmentCouponsGetInfoBinding
import ru.hvost.news.presentation.viewmodels.CouponViewModel

class CouponsGetInfoFragment: Fragment() {

    private lateinit var binding: FragmentCouponsGetInfoBinding
    private lateinit var couponVM: CouponViewModel
    private lateinit var navC:NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCouponsGetInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        couponVM = ViewModelProvider(this)[CouponViewModel::class.java]
        couponVM.getCouponsInfo("eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ==")
        navC = findNavController()
        setObservers()
    }



    private fun setObservers(){
        couponVM.couponsInfo.observe(viewLifecycleOwner, Observer {
            binding.textViewGetCouponsInfo.text = it.description
                Glide.with(requireContext()).load(baseUrl + it.imageUrl).placeholder(R.drawable.not_found).centerCrop()
                    .into(binding.imageViewInfoGetCoupons)

        })
    }
}