package ru.hvost.news.presentation.fragments.coupons


import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentCouponsMyBinding
import ru.hvost.news.models.Coupons
import ru.hvost.news.presentation.adapters.recycler.MyCouponsAdapter
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.CouponViewModel
import ru.hvost.news.utils.getValue

class MyCouponsFragment : BaseFragment() {

    private lateinit var binding: FragmentCouponsMyBinding
    private lateinit var couponVM: CouponViewModel
    private val adapter = MyCouponsAdapter()
    private lateinit var navC: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCouponsMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
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
        App.getInstance().userToken?.run {
            couponVM.getCoupons(this)
        }
    }

    private fun setObservers(owner: LifecycleOwner) {
        couponVM.coupons.observe(owner, {
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
        binding.toolbarCouponsMy.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.imageButtonCouponsInfo.setOnClickListener {
            navC.navigate(R.id.action_myCouponsFragment_to_infoGetCouponsFragment)
        }
    }
}