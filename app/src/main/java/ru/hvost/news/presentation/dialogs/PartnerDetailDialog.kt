package ru.hvost.news.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.hvost.news.R
import ru.hvost.news.databinding.DialogMapDetailBinding
import ru.hvost.news.presentation.fragments.map.MapViewModel
import ru.hvost.news.presentation.fragments.map.PartnersPageFragment
import java.lang.StringBuilder

class PartnerDetailDialog( private val shopId: Long): BottomSheetDialogFragment() {

    private lateinit var binding: DialogMapDetailBinding
    private lateinit var mapVM:MapViewModel
    private lateinit var navCMain: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMapDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapVM = ViewModelProvider(requireActivity())[MapViewModel::class.java]
        navCMain = requireActivity().findNavController(R.id.nav_host_fragment)
        setObservers(this)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    private fun setObservers(owner: LifecycleOwner) {
        mapVM.shops.observe(owner, { shops ->
            val shop = shops.firstOrNull { it.id == shopId }
            shop?.let {
                if (it.name.isNotEmpty()) binding.textViewTitle.text = it.name.parseAsHtml()
                if (it.typeShopName.isNotEmpty()) binding.textViewCategory.text = it.typeShopName
                if (it.address.isNotEmpty()) binding.textViewAddress.text = it.address
                if (it.regime.isNotEmpty()) binding.textViewTimeWeekdays.text = it.regime.parseAsHtml()
                val strBuilder = StringBuilder()
                for (i in it.phones.indices){
                    val phone = it.phones[i]
                    strBuilder.append(phone)
                    if(it.phones.size > 1 && i != it.phones.lastIndex ){
                        strBuilder.append("\n")
                    }
                }
                if(strBuilder.toString().isNotEmpty())binding.textViewPhone.text = strBuilder.toString()
                if (it.website.isNotEmpty()) binding.textViewSite.text = shop.website
            }

        })

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonGoPartnerPage.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(PartnersPageFragment.SHOP_ID, shopId)
           navCMain.navigate(R.id.action_mapFragment_to_partnersPageFragment, bundle)
            this.dismiss()
        }
}
}