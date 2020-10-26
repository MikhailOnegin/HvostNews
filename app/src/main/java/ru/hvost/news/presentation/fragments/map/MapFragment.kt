package ru.hvost.news.presentation.fragments.map

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentMapBinding
import ru.hvost.news.presentation.dialogs.MapFilterDialog
import ru.hvost.news.presentation.viewmodels.MapViewModel


class MapFragment: Fragment(), Session.SearchListener, CameraListener {


    private lateinit var binding: FragmentMapBinding
    private lateinit var mapVm: MapViewModel
    private lateinit var searchManager:com.yandex.mapkit.search.SearchManager // Search request for searching a user query near given geometry.
    private val dialogMapFilter: MapFilterDialog = MapFilterDialog()

    private var searchSession:Session? = null


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

        MapKitFactory.initialize(requireContext())
        SearchFactory.initialize(requireContext())

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapVm = ViewModelProvider(this)[MapViewModel::class.java]
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        binding.mapView.map.addCameraListener(this)
        binding.editTextQuery.setOnEditorActionListener { textView:TextView, i:Int, keyEvent:KeyEvent? ->
            if(i == EditorInfo.IME_ACTION_SEARCH){
                submitQuery(binding.editTextQuery.text.toString())
            }
            return@setOnEditorActionListener false
        }

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
    }

    private fun submitQuery(query: String) {
        searchSession = searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(binding.mapView.map.visibleRegion),
            SearchOptions(),
            this
        )
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onSearchError(p0: Error) {
        var errorMessage = "Unknown error"
        when(p0){
            is RemoteError -> errorMessage ="Remote service error"
            is NetworkError -> errorMessage = "Network error"
        }
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onSearchResponse(p0: Response) {
        val mapObjects: MapObjectCollection = binding.mapView.map.mapObjects
        mapObjects.clear()

        for (searchResult in p0.collection.children) {
            val resultLocation =
                searchResult.obj!!.geometry[0].point
            if (resultLocation != null) {
                mapObjects.addPlacemark(
                    resultLocation,
                    ImageProvider.fromResource(requireContext(), R.drawable.search_result)
                )
            }
        }
    }

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateReason,
        p3: Boolean
    ) {
        if(p3){
            submitQuery(binding.editTextQuery.text.toString())
        }
    }
}