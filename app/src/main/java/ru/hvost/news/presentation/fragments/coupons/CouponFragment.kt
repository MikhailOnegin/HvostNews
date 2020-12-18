package ru.hvost.news.presentation.fragments.coupons

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentCouponBinding
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.CouponViewModel
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent

class CouponFragment : BaseFragment() {

    private lateinit var binding: FragmentCouponBinding
    private lateinit var couponVM: CouponViewModel
    private lateinit var navC: NavController
    private var couponId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCouponBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        couponVM = ViewModelProvider(requireActivity())[CouponViewModel::class.java]
        navC = findNavController()
        couponId = arguments?.get("couponId") as String?
        setListeners()
        setObservers(this)
    }

    @SuppressLint("SetTextI18n")
    private fun setObservers(owner: LifecycleOwner) {
        couponVM.couponsEvent.observe(owner, {
            couponsEvent(it)
        })
//
    }

    private val couponsEvent = { event: NetworkEvent<State> ->
        when (event.getContentIfNotHandled()) {
            State.SUCCESS -> {
                couponVM.coupons.value?.run {
                    for (i in this.coupons.indices) {
                        val coupon = this.coupons[i]
                        if (coupon.couponId == couponId) {
                            binding.textViewCode1.text = coupon.qrCode
                            binding.textViewCode2.text = coupon.qrCode
                            Glide.with(requireContext()).load(coupon.qrCodeUrl)
                                .placeholder(R.drawable.not_found)
                                .into(binding.imageViewCoupon)
                            binding.textViewCouponTitle.text = coupon.title
                            if (coupon.expirationDate.isBlank()) binding.textViewCouponActivity.text =
                                resources.getString(R.string.not_found)
                            else {
                                val d = "${resources.getString(R.string.before)} ${coupon.expirationDate}"
                                binding.textViewCouponActivity.text = d
                            }
                            if (coupon.isUsed) {
                                binding.textViewCouponStatus.text = "Использован"
                                binding.textViewCouponStatus.background = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_coupon_staus_false
                                )
                            } else {
                                binding.textViewCouponStatus.text = "Активный"
                                binding.textViewCouponStatus.background = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_coupon_status_true
                                )
                            }
                            binding.textViewCouponDescription.text = coupon.description.parseAsHtml()
                        }
                    }
                }
            }
            State.ERROR -> {
                createSnackbar(
                    binding.root,
                    event.error,
                    getString(R.string.buttonOk)
                ).show()
            }
            State.FAILURE -> {
                createSnackbar(
                    binding.root,
                    getString(R.string.networkFailureMessage),
                    getString(R.string.buttonOk)
                ).show()
            }
            State.LOADING -> {
            }
            else -> {
            }
        }
    }


    private fun setListeners() {
        binding.toolbarCouponPage.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}