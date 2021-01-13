package ru.hvost.news.presentation.fragments.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.databinding.VpPromotionBinding

class PromotionFragment : Fragment() {

    private lateinit var binding: VpPromotionBinding
    private lateinit var mapVM: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapVM = ViewModelProvider(requireActivity())[MapViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = VpPromotionBinding.inflate(inflater, container, false)
        val position = arguments?.getInt(PROMOTION_POSITION) ?: 0
        if (mapVM.promotions.isNotEmpty()) {
            mapVM.promotions[position].let {
                Glide
                    .with(this)
                    .load(it.imageUri)
                    .placeholder(R.drawable.empty_image)
                    .into(binding.image)
            }
        }
        return binding.root
    }

    companion object {

        const val PROMOTION_POSITION = "promotion_position"

    }

}