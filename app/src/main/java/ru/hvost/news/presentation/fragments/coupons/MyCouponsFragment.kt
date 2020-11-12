package ru.hvost.news.presentation.fragments.coupons

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentCouponsMyBinding
import ru.hvost.news.models.Coupons
import ru.hvost.news.presentation.adapters.recycler.MyCouponsAdapter
import ru.hvost.news.presentation.adapters.spinners.SpinnerCustomAdapter
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
        couponVM = ViewModelProvider(this)[CouponViewModel::class.java]
        adapter.clickCoupon = object : MyCouponsAdapter.ClickCoupon {
            override fun click(item: Coupons.Coupon) {
                val bundle = Bundle()
                bundle.putSerializable("coupon", item)
                navC.navigate(R.id.action_myCouponsFragment_to_couponFragment, bundle)
            }
        }
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerViewCoupons.adapter = adapter
        binding.recyclerViewCoupons.layoutManager = layoutManager
        val items = arrayListOf("Все","Активные", "Использованные")
        binding.spinnerCoupons.adapter = SpinnerCustomAdapter(requireContext(), items)
        binding.imageBack.setOnClickListener {
            navC.popBackStack()
        }
        binding.imageInfo.setOnClickListener {
            navC.navigate(R.id.action_myCouponsFragment_to_infoGetCouponsFragment)
        }
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
                adapter.setCoupons(it.coupons)
        })
    }
    //override fun onCreate(savedInstanceState: Bundle?) {
    //    super.onCreate(savedInstanceState)
    //    setHasOptionsMenu(true)
    //}
    //override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    //    inflater.inflate(R.menu.my_coupons_menu,menu)
    //    super.onCreateOptionsMenu(menu, inflater)
    //}
//
//
    //override fun onOptionsItemSelected(item: MenuItem): Boolean {
    //    if(item.itemId == R.id.info_get){
    //        navC.navigate(R.id.action_myCouponsFragment_to_infoGetCouponsFragment)
    //    }
    //    return super.onOptionsItemSelected(item)
    //}

}