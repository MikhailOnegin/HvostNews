package ru.hvost.news.presentation.fragments.coupons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.R
import ru.hvost.news.data.api.response.CouponsResponse
import ru.hvost.news.databinding.FragmentMyCouponsBinding
import ru.hvost.news.presentation.adapters.recyclers.MyCouponsAdapter
import ru.hvost.news.presentation.viewmodels.CouponViewModel

class MyCouponsFragment: Fragment() {

    private lateinit var binding: FragmentMyCouponsBinding
    private lateinit var couponVM: CouponViewModel
    private val adapter = MyCouponsAdapter()
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var navC: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCouponsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navC = findNavController()
        couponVM = ViewModelProvider(requireActivity())[CouponViewModel::class.java]
        adapter.clickCoupon = object : MyCouponsAdapter.ClickCoupon {
            override fun click(item: CouponsResponse.Coupon) {
                val bundle = Bundle()
                bundle.putSerializable("coupon", item)
                navC.navigate(R.id.action_myCouponsFragment_to_couponFragment)
            }
        }
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerViewCoupons.adapter = adapter
        binding.recyclerViewCoupons.layoutManager = layoutManager
        binding.spinnerCoupons
        couponVM.getCoupons("eyJpdiI6IlZBPT0iLCJ2YWx1ZSI6ImYwYlwvaEV4UE15aWtrcUdVMENWbEYrK2JHMTVUMG5sd3FkeFZuR21oYkFZPSJ9")
        setObservers(this)
    }

    private fun setObservers(owner: LifecycleOwner) {
        couponVM.coupons.observe(owner, Observer {
            it.coupons?.run {
                adapter.setCoupons(it.coupons)
            }
        })
    }

}