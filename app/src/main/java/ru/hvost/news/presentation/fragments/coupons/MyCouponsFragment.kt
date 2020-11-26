package ru.hvost.news.presentation.fragments.coupons

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_coupons_my.view.*
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentCouponsMyBinding
import ru.hvost.news.models.Coupons
import ru.hvost.news.presentation.adapters.recycler.MyCouponsAdapter
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.viewmodels.CouponViewModel
import ru.hvost.news.utils.getValue

class MyCouponsFragment : Fragment() {

    private lateinit var binding: FragmentCouponsMyBinding
    private lateinit var couponVM: CouponViewModel
    private val adapter = MyCouponsAdapter()
    private lateinit var navC: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCouponsMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navC = findNavController()
        couponVM = ViewModelProvider(requireActivity())[CouponViewModel::class.java]
        adapter.clickCoupon = object : MyCouponsAdapter.ClickCoupon {
            override fun click(item: Coupons.Coupon) {
                val bundle = Bundle()
                bundle.putString("couponId", item.couponId)
                navC.navigate(R.id.action_myCouponsFragment_to_couponFragment, bundle)
            }
        }
        binding.recyclerViewCoupons.adapter = adapter
        val itemsSpinner = arrayListOf("Все", "Активные", "Использованные")
        val spinnerAdapter = SpinnerAdapter(requireContext(), "", itemsSpinner, String::getValue)
        binding.spinnerCoupons.adapter = spinnerAdapter
        setListeners()
        setObservers(this)
            couponVM.getCoupons("eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ==")
        setSystemUiVisibility()
    }

    private fun setObservers(owner: LifecycleOwner) {
        couponVM.coupons.observe(owner, Observer {
            adapter.setCoupons(it.coupons)
        })
    }

    @Suppress("UNCHECKED_CAST")
    private fun setListeners() {
        binding.spinnerCoupons.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val isUsed =
                        (binding.spinnerCoupons.adapter as SpinnerAdapter<String>).getItem(p2)
                    isUsed?.run {
                        adapter.filter(this)
                    }
                }
            }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.setOnClickListener {
            navC.navigate(R.id.action_myCouponsFragment_to_infoGetCouponsFragment)
        }
        binding.imageInfo.setOnClickListener {
            Toast.makeText(requireContext(), "Click", Toast.LENGTH_SHORT).show()
            navC.navigate(R.id.action_myCouponsFragment_to_infoGetCouponsFragment)
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