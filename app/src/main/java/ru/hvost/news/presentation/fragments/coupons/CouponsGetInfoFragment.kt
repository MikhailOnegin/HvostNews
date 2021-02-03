package ru.hvost.news.presentation.fragments.coupons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentCouponsGetInfoBinding
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.CouponViewModel

class CouponsGetInfoFragment : BaseFragment() {

    private lateinit var binding: FragmentCouponsGetInfoBinding
    private lateinit var couponVM: CouponViewModel
    private lateinit var navC: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCouponsGetInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        couponVM = ViewModelProvider(requireActivity())[CouponViewModel::class.java]
        couponVM.getCouponsInfo()
        navC = findNavController()
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        binding.toolbarCouponsHowGet.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun setObservers() {
        couponVM.couponsInfo.observe(viewLifecycleOwner, {
            binding.textViewGetCouponsInfo.text = it.description.parseAsHtml()
            Glide.with(requireContext()).load(it.imageUrl)
                .placeholder(R.drawable.empty_image).centerCrop()
                .into(binding.imageViewInfoGetCoupons)
        })
    }
}