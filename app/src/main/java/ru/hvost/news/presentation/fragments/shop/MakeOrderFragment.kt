package ru.hvost.news.presentation.fragments.shop

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentMakeOrderBinding
import ru.hvost.news.models.CartFooter
import ru.hvost.news.models.CartItem
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.*
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.EventObserver

class MakeOrderFragment : BaseFragment() {

    private lateinit var binding: FragmentMakeOrderBinding
    private lateinit var cartVM: CartViewModel
    private lateinit var mainVM: MainViewModel
    private lateinit var fields: Array<TextInputEditText>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMakeOrderBinding.inflate(inflater, container, false)
        fields = arrayOf(
            binding.name, binding.phone, binding.email,
            binding.city, binding.street, binding.house, binding.flat
        )
        binding.phone.filters = arrayOf(PhoneInputFilter())
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cartVM = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setUserData()
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setUserData() {
        binding.run {
            name.setText(mainVM.userData.value?.name)
            phone.setText(formatPhoneString(mainVM.userData.value?.phone))
            email.setText(mainVM.userData.value?.email)
            mainVM.userData.value?.deliveryAddresses?.lastOrNull()?.let {
                city.setText(it.city)
                street.setText(it.street)
                house.setText(it.house)
                flat.setText(it.flat)
            }
        }
        setReadyToMakeOrder()
    }

    private fun setObservers() {
        cartVM.readyToMakeOrder.observe(viewLifecycleOwner) { onReadyToMakeOrderChanged(it) }
        cartVM.makeOrderEvent.observe(viewLifecycleOwner, DefaultNetworkEventObserver(binding.root))
        cartVM.productsCart.observe(viewLifecycleOwner) { onCartChanged(it) }
        cartVM.prizesCart.observe(viewLifecycleOwner) { onCartChanged(it) }
        cartVM.finishOrderEvent.observe(viewLifecycleOwner, EventObserver{ onFinishEvent(it) })
    }

    private fun setListeners() {
        binding.apply {
            name.doOnTextChanged { _, _, _, _ -> setReadyToMakeOrder() }
            phone.doOnTextChanged { _, _, _, _ -> setReadyToMakeOrder() }
            email.doOnTextChanged { _, _, _, _ -> setReadyToMakeOrder() }
            city.doOnTextChanged { _, _, _, _ -> setReadyToMakeOrder() }
            street.doOnTextChanged { _, _, _, _ -> setReadyToMakeOrder() }
            house.doOnTextChanged { _, _, _, _ -> setReadyToMakeOrder() }
            flat.doOnTextChanged { _, _, _, _ -> setReadyToMakeOrder() }
            makeOrder.setOnClickListener { onMakeOrderClicked() }
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun onFinishEvent(orderNumber: Int) {
        cartVM.updateCartAsync(App.getInstance().userToken)
        val bundle = Bundle()
        bundle.putInt(FinishOrderFragment.ORDER_NUMBER, orderNumber)
        findNavController().navigate(R.id.action_makeOrderFragment_to_finishOrderFragment, bundle)
    }

    @SuppressLint("SetTextI18n")
    private fun onCartChanged(cartItems: List<CartItem>?) {
        val cart = cartItems?.firstOrNull { it is CartFooter } as CartFooter?
        cart?.run {
            binding.count.text = ((cartItems?.size ?: 1) - 1).toString()
            binding.discount.text = "${moneyFormat.format(discount)} %"
            binding.discountSum.text = "${moneyFormat.format(discountSum)} \u20bd"
            binding.delivery.text = "${moneyFormat.format(deliveryCost)} \u20bd"
            if (isForPrizes) {
                setProductViewsVisibility(false)
                val bonuses = when(getWordEndingType(bonusesCost.toInt())){
                    WordEnding.TYPE_1 -> App.getInstance().getString(R.string.cartForBonusesType1)
                    WordEnding.TYPE_2 -> App.getInstance().getString(R.string.cartForBonusesType2)
                    WordEnding.TYPE_3 -> App.getInstance().getString(R.string.cartForBonusesType3)
                }
                binding.total.text = "${moneyFormat.format(bonusesCost)} $bonuses"
            } else {
                setProductViewsVisibility(true)
                binding.total.text = "${moneyFormat.format(totalCost)} \u20bd"
            }
        }
    }

    private fun setProductViewsVisibility(visible: Boolean) {
        binding.run {
            if (visible) {
                discountTitle.visibility = View.VISIBLE
                discountFiller.visibility = View.VISIBLE
                discount.visibility = View.VISIBLE
                discountSumTitle.visibility = View.VISIBLE
                discountSumFiller.visibility = View.VISIBLE
                discountSum.visibility = View.VISIBLE
            } else {
                discountTitle.visibility = View.GONE
                discountFiller.visibility = View.GONE
                discount.visibility = View.GONE
                discountSumTitle.visibility = View.GONE
                discountSumFiller.visibility = View.GONE
                discountSum.visibility = View.GONE
            }
        }
    }

    private fun onMakeOrderClicked() {
        if(isEverythingOk()) {
            cartVM.makeOrder(
                name = binding.name.text.toString(),
                phone = binding.phone.text.toString(),
                email = binding.email.text.toString(),
                city = binding.city.text.toString(),
                street = binding.street.text.toString(),
                house = binding.house.text.toString(),
                flat = binding.flat.text.toString(),
                saveDataForFuture = binding.saveDataForFuture.isChecked
            )
        }
    }

    private fun onReadyToMakeOrderChanged(ready: Boolean?) {
        binding.makeOrder.isEnabled = ready == true
    }

    private fun isEverythingOk(): Boolean {
        if(hasBlankField(*fields)) return false
        if(hasTooLongField(*fields)) return false
        if(isPhoneFieldIncorrect(binding.phone)) return false
        if(isEmailFieldIncorrect(binding.email)) return false
        return true
    }

    private fun setReadyToMakeOrder() {
        cartVM.readyToMakeOrder.value = !(
                binding.name.text.isNullOrBlank()
                || binding.phone.text.isNullOrBlank()
                || binding.email.text.isNullOrBlank()
                || binding.city.text.isNullOrBlank()
                || binding.street.text.isNullOrBlank()
                || binding.house.text.isNullOrBlank()
                || binding.flat.text.isNullOrBlank()
        )
    }

}