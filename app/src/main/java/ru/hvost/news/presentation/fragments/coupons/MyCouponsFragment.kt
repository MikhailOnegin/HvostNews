package ru.hvost.news.presentation.fragments.coupons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
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
import ru.hvost.news.databinding.FragmentCouponsMyBinding
import ru.hvost.news.models.Coupons
import ru.hvost.news.models.toDomain
import ru.hvost.news.presentation.adapters.recycler.MyCouponsAdapter
import ru.hvost.news.presentation.viewmodels.CouponViewModel

class MyCouponsFragment: Fragment() {

    private lateinit var binding: FragmentCouponsMyBinding
    private lateinit var couponVM: CouponViewModel
    private val adapter = MyCouponsAdapter()
    private lateinit var layoutManager: RecyclerView.LayoutManager
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
                bundle.putSerializable("coupon", item)
                navC.navigate(R.id.action_myCouponsFragment_to_couponFragment)
            }
        }
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerViewCoupons.adapter = adapter
        binding.recyclerViewCoupons.layoutManager = layoutManager
        val items = arrayOf("Все","Активные", "Использованные")
        val spinnerAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, items)
        binding.spinnerCoupons.adapter = spinnerAdapter
        binding.spinnerCoupons.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val isUsed = items[p2]
                adapter.filter(isUsed)
            }
        }
        setObservers(this)
        couponVM.getCoupons("eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ==")

    }

    private fun setObservers(owner: LifecycleOwner) {
        couponVM.coupons.observe(owner, Observer {
            it.coupons?.run {
                val coupons = this.toDomain()
                adapter.setCoupons(coupons)
            }
        })
    }

}