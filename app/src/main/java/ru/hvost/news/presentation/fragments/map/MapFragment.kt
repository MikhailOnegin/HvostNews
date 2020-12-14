package ru.hvost.news.presentation.fragments.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import ru.hvost.news.databinding.FragmentMapBinding
import ru.hvost.news.presentation.fragments.BaseFragment

class MapFragment : BaseFragment() {

    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
        moveToStartPosition()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
        binding.mapView.onStop()
    }

    private fun moveToStartPosition() {
        binding.mapView.map.move(
            CameraPosition(
                Point(53.398767,58.984585),
                18.0f,
                0.0f,
                0.0f
            ),
            Animation(Animation.Type.SMOOTH, 2f),
            null
        )
    }

}