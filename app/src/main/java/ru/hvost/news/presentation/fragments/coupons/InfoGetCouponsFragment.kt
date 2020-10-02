package ru.hvost.news.presentation.fragments.coupons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ru.hvost.news.databinding.FragmentInfoGetCouponsBinding
import ru.hvost.news.presentation.viewmodels.CouponViewModel

class InfoGetCouponsFragment: Fragment() {

    private lateinit var binding: FragmentInfoGetCouponsBinding
    private lateinit var couponVM: CouponViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoGetCouponsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        couponVM = ViewModelProvider(requireActivity())[CouponViewModel::class.java]
        setObservers()
    }

    private fun setObservers(){
        couponVM.couponsInfo.observe(viewLifecycleOwner, Observer {
            it?.description?.run{
                binding.textViewGetCuponsInfo.text = this

            }
            it?.imageUrl?.run {
                // need add .placeholder
                Glide.with(requireContext()).load(this).centerCrop()
                    .into(binding.imageViewInfoGetCoupons)
            }
        })
    }
}