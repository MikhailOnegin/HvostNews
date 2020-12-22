package ru.hvost.news.presentation.fragments.map

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.runtime.ui_view.ViewProvider
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentMapPartnersPageBinding
import ru.hvost.news.databinding.TextviewPhoneForMapsBinding
import ru.hvost.news.models.Shop
import ru.hvost.news.presentation.fragments.BaseFragment

class PartnersPageFragment : BaseFragment() {

    private lateinit var binding: FragmentMapPartnersPageBinding
    private lateinit var mapVM: MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapPartnersPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapVM = ViewModelProvider(requireActivity())[MapViewModel::class.java]
        checkDataAndInflate()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
    }

    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.favourite.setOnClickListener { onFavouriteClicked() }
    }

    private fun onFavouriteClicked() {
        val shopId = arguments?.getLong(SHOP_ID, -1)
        val shop = mapVM.shops.value?.firstOrNull { it.id == shopId }
        shop?.run {
            //sergeev: Добавить вызов метода API для добавления в избранное.
            if (shop.isFavourite) animateFavourite(false)
            else animateFavourite(true)
            shop.isFavourite = !shop.isFavourite
        }
    }

    private fun animateFavourite(isInFavourite: Boolean) {
        val animator = ValueAnimator.ofFloat(
            1f,
            if (isInFavourite) 1.5f else 0.5f,
            1f
        )
        animator.duration = 300L
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener {
            binding.favourite.scaleX = it.animatedValue as Float
            binding.favourite.scaleY = it.animatedValue as Float
        }
        binding.favourite.setImageResource(
            if (isInFavourite) R.drawable.ic_heart_filled else R.drawable.ic_heart
        )
        animator.start()
    }

    private fun checkDataAndInflate() {
        val shopId = arguments?.getLong(SHOP_ID, -1)
        val shop = mapVM.shops.value?.firstOrNull { it.id == shopId }
        if (shop == null) {
            Toast.makeText(
                App.getInstance(),
                getString(R.string.shopNotFound),
                Toast.LENGTH_LONG
            ).show()
            findNavController().popBackStack()
        } else {
            inflateData(shop)
        }
    }

    private fun inflateData(shop: Shop) {
        binding.apply {
            name.text = shop.name
            description.text = shop.shortDescription
            address.text = shop.address
            regime.text = shop.regime
            website.text = shop.website
            if (shop.isFavourite) favourite.setImageResource(R.drawable.ic_heart_filled)
            else favourite.setImageResource(R.drawable.ic_heart)
        }
        setPhones(shop.phones)
        setMap(shop)
    }

    private fun setMap(shop: Shop) {
        moveCameraToPosition(Point(shop.latitude, shop.longitude))
        setShopOnMap(shop)
        binding.mapView.map.isScrollGesturesEnabled = false
        binding.mapView.map.isRotateGesturesEnabled = false
        binding.mapView.map.isZoomGesturesEnabled = false
    }

    private fun moveCameraToPosition(point: Point) {
        binding.mapView.map.move(
            CameraPosition(
                point,
                16.0f,
                0.0f,
                0.0f
            ),
            Animation(Animation.Type.LINEAR, 0f),
            null
        )
    }

    @SuppressLint("InflateParams")
    private fun setShopOnMap(shop: Shop) {
        val mapObjects = binding.mapView.map.mapObjects.addCollection()
        val view = LayoutInflater.from(requireActivity())
            .inflate(R.layout.view_landmark, null)
        mapObjects.addPlacemark(
            Point(shop.latitude, shop.longitude),
            ViewProvider(view)
        )
    }

    private fun setPhones(phones: List<String>) {
        for (phone in phones) {
            val phoneBinding = TextviewPhoneForMapsBinding.inflate(
                LayoutInflater.from(requireActivity()),
                binding.phones,
                false
            )
            phoneBinding.root.text = phone
            phoneBinding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            }
            binding.phones.addView(phoneBinding.root)
        }
    }

    companion object {

        const val SHOP_ID = "shop_id"

    }

}