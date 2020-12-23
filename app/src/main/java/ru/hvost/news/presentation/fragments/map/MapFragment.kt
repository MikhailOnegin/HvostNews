package ru.hvost.news.presentation.fragments.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.PopupWindow
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.runtime.ui_view.ViewProvider
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentMapBinding
import ru.hvost.news.databinding.PopupMapSettingsBinding
import ru.hvost.news.models.Shop
import ru.hvost.news.presentation.adapters.autocomplete.AutoCompleteShopsAdapter
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.events.OneTimeEvent.Observer
import ru.hvost.news.utils.showNotReadyToast

class MapFragment : BaseFragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapVM: MapViewModel
    private lateinit var cameraPosition: CameraPosition
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val defaultLatitude = 55.755814
    private val defaultLongitude = 37.617635

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            onRequestPermissionResult
        )
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapVM = ViewModelProvider(requireActivity())[MapViewModel::class.java]
        setObservers()
        if(mapVM.shops.value.isNullOrEmpty()) mapVM.loadShops(App.getInstance().userToken)
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
        if (::cameraPosition.isInitialized) {
            binding.mapView.map.move(cameraPosition)
        } else {
            initializeCameraPosition()
        }
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
        binding.mapView.onStop()
        cameraPosition = binding.mapView.map.cameraPosition
    }

    private val onRequestPermissionResult = { permissionGranted: Boolean ->
        if (permissionGranted) {
            tryMoveToUsersLastLocation()
        } else {
            moveCameraToDefaultPosition()
        }
    }

    private fun initializeCameraPosition() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                tryMoveToUsersLastLocation()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun tryMoveToUsersLastLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    moveCameraToPosition(
                        Point(
                            it.latitude,
                            it.longitude
                        )
                    )
                } else moveCameraToDefaultPosition()
            }
        } catch (exc: SecurityException) {
            moveCameraToDefaultPosition()
        }
    }

    private fun moveCameraToDefaultPosition() {
        moveCameraToPosition(Point(defaultLatitude, defaultLongitude))
    }

    private fun moveCameraToPosition(point: Point) {
        binding.mapView.map.move(
            CameraPosition(
                point,
                14.0f,
                0.0f,
                0.0f
            ),
            Animation(Animation.Type.SMOOTH, 2f),
            null
        )
    }

    private fun setObservers() {
        mapVM.shops.observe(viewLifecycleOwner) { onShopsLoaded(it) }
        mapVM.optionsClickedEvent.observe(viewLifecycleOwner, Observer(onOptionsClicked))
    }

    private fun setListeners() {
        binding.settings.setOnClickListener { showNotReadyToast() }
        binding.settings.setOnClickListener { mapVM.sendOptionsClickedEvent() }
    }

    @Suppress("DEPRECATION")
    private val onOptionsClicked = {
        val popupBinding = PopupMapSettingsBinding.inflate(
            LayoutInflater.from(requireActivity()), null, false
        )
        val popup = PopupWindow(popupBinding.root)
        popup.apply {
            setWindowLayoutMode(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            isOutsideTouchable = true
            animationStyle = R.style.MapsPopupAnimation
            elevation = resources.getDimension(R.dimen.popupElevation)
            setOnDismissListener { popup.dismiss() }
        }
        popup.showAtLocation(binding.settings, Gravity.NO_GRAVITY, 0, 0)
    }

    private fun onShopsLoaded(shops: List<Shop>?) {
        shops?.run {
            setShopsOnMap(this)
            setAutoCompleteTextView(this.toMutableList())
        }
    }

    private fun setAutoCompleteTextView(shops: List<Shop>) {
        binding.search.setAdapter(AutoCompleteShopsAdapter(
            requireActivity(),
            R.layout.suggestions_dropdown_view,
            shops
        ))
        binding.search.onItemClickListener = AdapterView.OnItemClickListener {
                parent, _, position, _ ->
            parent?.run {
                val shop = adapter.getItem(position) as Shop
                moveCameraToPosition(Point(shop.latitude, shop.longitude))
            }
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
            (imm as InputMethodManager).hideSoftInputFromWindow(binding.root.windowToken, 0)
        }
    }

    @SuppressLint("InflateParams")
    private fun setShopsOnMap(shops: List<Shop>) {
        val mapObjects = binding.mapView.map.mapObjects.addCollection()
        for (shop in shops) {
            val view = LayoutInflater.from(requireActivity())
                .inflate(R.layout.view_landmark, null)
            val mapObject = mapObjects.addPlacemark(
                Point(shop.latitude, shop.longitude),
                ViewProvider(view)
            )
            mapObject.userData = shop.id
        }
        mapObjects.addTapListener(mapObjectTapListener)
    }

    private val mapObjectTapListener = MapObjectTapListener { mapObject, _ ->
        val bundle = Bundle()
        bundle.putLong(PartnersPageFragment.SHOP_ID, mapObject.userData as Long)
        findNavController().navigate(R.id.action_mapFragment_to_partnersPageFragment, bundle)
        true
    }

}