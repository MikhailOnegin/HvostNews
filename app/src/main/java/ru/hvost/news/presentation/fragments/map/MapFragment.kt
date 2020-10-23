package ru.hvost.news.presentation.fragments.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentMapBinding
import ru.hvost.news.presentation.dialogs.MapFilterDialog
import ru.hvost.news.presentation.viewmodels.MapViewModel
import ru.hvost.news.presentation.viewmodels.SchoolViewModel


class MapFragment: Fragment(), OnMapReadyCallback {


    private lateinit var binding: FragmentMapBinding
    private lateinit var mapVm: MapViewModel
    private val dialogMapFilter: MapFilterDialog = MapFilterDialog()
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
        mapVm = ViewModelProvider(this)[MapViewModel::class.java]
        mapVm.getShops("eyJpdiI6Ik93PT0iLCJ2YWx1ZSI6ImZJVFpNQ3FJXC95eXBPbUg2QVhydDh2cURPNXI5WmR4VUNBdVBIbkU1MEhRPSIsInBhc3N3b3JkIjoiTkhOUFcyZ3dXbjVpTnpReVptWXdNek5oTlRZeU5UWmlOR1kwT1RabE5HSXdOMlJtTkRnek9BPT0ifQ==")
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
        binding.imageButtonFilter.setOnClickListener {
            dialogMapFilter.show(requireActivity().supportFragmentManager, "customDialog")
        }


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