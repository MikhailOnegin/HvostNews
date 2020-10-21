package ru.hvost.news.presentation.fragments.coupons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

class CouponFragment: Fragment() {
    
    private lateinit var binding: FragmentCouponBinding
    private lateinit var couponVM: CouponViewModel
    private lateinit var navC:NavController
    private val dialogGrCode = QrCodeDialog()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        binding = FragmentCouponBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        couponVM = ViewModelProvider(this)[CouponViewModel::class.java]
        navC = findNavController()
        binding.imageBack.setOnClickListener {
            navC.popBackStack()
        }
        binding.buttonShowQrCode.setOnClickListener {
            dialogGrCode.show(requireActivity().supportFragmentManager, "customDialog")
        }
        val coupon: Coupons.Coupon? = arguments?.get("coupon") as Coupons.Coupon?
        coupon?.run {
            Glide.with(requireContext()).load(baseUrl + this.imageUrl).placeholder(R.drawable.not_found)
                .into(binding.imageViewCoupon)
            binding.textViewCouponCode.text = this.title
            if(this.expirationDate.isBlank()) binding.textViewCouponActivity.text = resources.getString(R.string.not_found)
            else binding.textViewCouponActivity.text = "До" + this.expirationDate
            if(this.isUsed){
                binding.textViewCouponStatus.text = "Использованный"
            }
            else{
                binding.textViewCouponStatus.text = "Активный"
            }
            binding.textViewCouponDescription.text = this.description
        }

    }
}