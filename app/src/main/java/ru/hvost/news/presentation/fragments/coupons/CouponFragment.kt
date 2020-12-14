package ru.hvost.news.presentation.fragments.coupons

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentCouponBinding
import ru.hvost.news.presentation.viewmodels.CouponViewModel
import ru.hvost.news.utils.events.DefaultNetworkEventObserver

class CouponFragment : Fragment() {

    private lateinit var binding: FragmentCouponBinding
    private lateinit var couponVM: CouponViewModel
    private lateinit var navC: NavController
    private lateinit var onCouponsLoadingEvent: DefaultNetworkEventObserver
    private var couponId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCouponBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        couponVM = ViewModelProvider(requireActivity())[CouponViewModel::class.java]
        navC = findNavController()
        couponId = arguments?.get("couponId") as String?
        initializeObservers()
        setListeners()
        setObservers(this)
        setSystemUiVisibility()
    }

    private fun initializeObservers() {
        onCouponsLoadingEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = { onCouponsChanged() }
        )
    }

    @SuppressLint("SetTextI18n")
    private fun setObservers(owner: LifecycleOwner) {
        couponVM.couponsState.observe(viewLifecycleOwner, onCouponsLoadingEvent)
//        couponVM.coupons.observe(owner, Observer {
//            for (i in it.coupons.indices){
//                val coupon = it.coupons[i]
//                if(coupon.couponId == couponId ){
//                    binding.textViewCode1.text = coupon.qrCode
//                    binding.textViewCode2.text = coupon.qrCode
//                    Glide.with(requireContext()).load(coupon.qrCodeUrl)
//                        .placeholder(R.drawable.not_found)
//                        .into(binding.imageViewCoupon)
//                    binding.textViewCouponTitle.text = coupon.title
//                    if (coupon.expirationDate.isBlank()) binding.textViewCouponActivity.text =
//                        resources.getString(R.string.not_found)
//                    else binding.textViewCouponActivity.text = "${resources.getString(R.string.before)} ${coupon.expirationDate}"
//                    if (coupon.isUsed) {
//                        binding.textViewCouponStatus.text = "Использован"
//                        binding.textViewCouponStatus.background = resources.getDrawable(R.drawable.background_coupon_staus_false)
//                    } else {
//                        binding.textViewCouponStatus.text = "Активный"
//                        binding.textViewCouponStatus.background = resources.getDrawable(R.drawable.background_coupon_status_true)
//                    }
//                    binding.textViewCouponDescription.text = coupon.description.parseAsHtml()
//                }
//            }
//        })
    }

    private fun onCouponsChanged() {
        couponVM.coupons.value?.let {
            for (i in it.coupons.indices) {
                val coupon = it.coupons[i]
                if (coupon.couponId == couponId) {
                    binding.textViewCode1.text = coupon.qrCode
                    binding.textViewCode2.text = coupon.qrCode
                    Glide.with(requireContext()).load(coupon.qrCodeUrl)
                        .placeholder(R.drawable.not_found)
                        .into(binding.imageViewCoupon)
                    binding.textViewCouponTitle.text = coupon.title
                    if (coupon.expirationDate.isBlank()) binding.textViewCouponActivity.text =
                        resources.getString(R.string.not_found)
                    else binding.textViewCouponActivity.text =
                        "${resources.getString(R.string.before)} ${coupon.expirationDate}"
                    if (coupon.isUsed) {
                        binding.textViewCouponStatus.text = "Использован"
                        binding.textViewCouponStatus.background =
                            resources.getDrawable(R.drawable.background_coupon_staus_false)
                    } else {
                        binding.textViewCouponStatus.text = "Активный"
                        binding.textViewCouponStatus.background =
                            resources.getDrawable(R.drawable.background_coupon_status_true)
                    }
                    binding.textViewCouponDescription.text = coupon.description.parseAsHtml()
                }
            }
        }
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("InlinedApi")
    @Suppress("DEPRECATION")
    private fun setSystemUiVisibility() {
        requireActivity().window.run {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        }
    }
}