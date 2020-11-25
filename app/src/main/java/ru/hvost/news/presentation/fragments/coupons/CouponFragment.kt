package ru.hvost.news.presentation.fragments.coupons

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_qr_code.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.databinding.DialogQrCodeBinding
import ru.hvost.news.databinding.FragmentCouponBinding
import ru.hvost.news.models.Coupons
import ru.hvost.news.presentation.dialogs.QrCodeDialog
import ru.hvost.news.presentation.viewmodels.CouponViewModel

class CouponFragment : Fragment() {

    private lateinit var binding: FragmentCouponBinding
    private lateinit var couponVM: CouponViewModel
    private lateinit var navC: NavController

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
        couponVM = ViewModelProvider(this)[CouponViewModel::class.java]
        navC = findNavController()
        val coupon: Coupons.Coupon? = arguments?.get("coupon") as Coupons.Coupon?
        coupon?.run {
            Glide.with(requireContext()).load(baseUrl + this.imageUrl)
                .placeholder(R.drawable.not_found)
                .into(binding.imageViewCoupon)
            binding.textViewCouponTitle.text = this.title
            if (this.expirationDate.isBlank()) binding.textViewCouponActivity.text =
                resources.getString(R.string.not_found)
            else binding.textViewCouponActivity.text =
                "${resources.getString(R.string.before)} ${this.expirationDate}"
            if (this.isUsed) {
                binding.textViewCouponStatus.text = "Использованный"
            } else {
                binding.textViewCouponStatus.text = "Активный"
            }
            binding.textViewCouponDescription.text = this.description
        }
        setSystemUiVisibility()
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