package ru.hvost.news.presentation.fragments.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentMapBinding


class MapFragment: Fragment(), OnMapReadyCallback {


    private lateinit var binding: FragmentMapBinding
   // private lateinit var mapVM: MapViewModel
   // private lateinit var googleMap:GoogleMap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("71fcd725-4e14-4eb6-95be-0dadad11466f")
        MapKitFactory.initialize(requireContext())

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.mapView.map.move(
            CameraPosition(
                Point(
                    55.751574,
                    37.573856
                ), 13.0f, 0.0f, 0.0f
            ),
            Animation(Animation.Type.SMOOTH, 0f),
            null
        )
        // mapVM = ViewModelProvider(requireActivity())[MapViewModel::class.java]
        // binding.mapView.onCreate(savedInstanceState)
        //   binding.mapView.onResume()
        //binding.mapView.getMapAsync(this)
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onMapReady(p0: GoogleMap?) {
        // p0?.let {
       //      googleMap = it
       // }
    }
}