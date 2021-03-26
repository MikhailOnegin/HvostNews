package ru.hvost.news.presentation.fragments.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.PopupWindow
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.runtime.ui_view.ViewProvider
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentMapBinding
import ru.hvost.news.databinding.PopupMapSettingsBinding
import ru.hvost.news.models.Shop
import ru.hvost.news.presentation.adapters.autocomplete.AutoCompleteShopsAdapter
import ru.hvost.news.presentation.dialogs.PartnerDetailDialog
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.events.OneTimeEvent.Observer
import java.lang.Exception
import kotlin.math.pow
import kotlin.math.sqrt

class MapFragment : BaseFragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapPin: View
    private lateinit var mapVM: MapViewModel
    private lateinit var cameraPosition: CameraPosition
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val defaultLatitude = 55.755814
    private val defaultLongitude = 37.617635
    private lateinit var networkEventObserver: DefaultNetworkEventObserver
    private var collapseDistance = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            onRequestPermissionResult
        )
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        mapPin = LayoutInflater.from(requireActivity()).inflate(R.layout.view_landmark, null)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapVM = ViewModelProvider(requireActivity())[MapViewModel::class.java]
        initializeObservers()
        setObservers()
        if(mapVM.shops.value.isNullOrEmpty()) mapVM.loadShops(App.getInstance().userToken)
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        MapKitFactory.getInstance().onStart()
        if (::cameraPosition.isInitialized) {
            binding.mapView.map.move(cameraPosition)
        } else {
            initializeCameraPosition()
        }
        setListeners()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
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

    private fun initializeObservers() {
        networkEventObserver = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnLoading = { setHintsVisibility(true) },
            doOnSuccess = { setHintsVisibility(false) },
            doOnFailure = {
                setHintsVisibility(true)
                mapVM.loadShops(App.getInstance().userToken)
            },
            doOnError = {
                setHintsVisibility(true)
                mapVM.loadShops(App.getInstance().userToken)
            }
        )
    }

    private fun setHintsVisibility(showHints: Boolean) {
        binding.progress.visibility = if (showHints) View.VISIBLE else View.GONE
        binding.hint.visibility = if (showHints) View.VISIBLE else View.GONE
    }

    @Suppress("DEPRECATION")
    private val onOptionsClicked = {
        val popupBinding = PopupMapSettingsBinding.inflate(
            LayoutInflater.from(requireActivity()), null, false
        )
        val popup = PopupWindow(popupBinding.root)
        setPopupControls(popupBinding, popup)
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

    private fun setPopupControls(binding: PopupMapSettingsBinding, popup: PopupWindow) {
        mapVM.showGroomsTemp.value = mapVM.showGrooms
        mapVM.showVetsTemp.value = mapVM.showVets
        mapVM.showZoosTemp.value = mapVM.showZoos
        mapVM.showPromosTemp.value = mapVM.showPromos
        setPopupListeners(binding, popup)
        setPopupObservers(binding)
    }

    private fun setPopupObservers(binding: PopupMapSettingsBinding) {
        mapVM.showGroomsTemp.observe(viewLifecycleOwner) {
            binding.grooms.isSelected = it
        }
        mapVM.showVetsTemp.observe(viewLifecycleOwner) {
            binding.vets.isSelected = it
        }
        mapVM.showZoosTemp.observe(viewLifecycleOwner) {
            binding.zoos.isSelected = it
        }
        mapVM.showPromosTemp.observe(viewLifecycleOwner) {
            binding.switchActions.isChecked = it
        }
    }

    private fun setPopupListeners(binding: PopupMapSettingsBinding, popup: PopupWindow) {
        binding.run {
            grooms.setOnClickListener { mapVM.showGroomsTemp.value = mapVM.showGroomsTemp.value?.not() }
            zoos.setOnClickListener { mapVM.showZoosTemp.value = mapVM.showZoosTemp.value?.not() }
            vets.setOnClickListener { mapVM.showVetsTemp.value = mapVM.showVetsTemp.value?.not() }
            switchActions.setOnCheckedChangeListener { _, isChecked ->
                mapVM.showPromosTemp.value = isChecked
            }
            apply.setOnClickListener {
                mapVM.showGrooms = mapVM.showGroomsTemp.value ?: true
                mapVM.showVets = mapVM.showVetsTemp.value ?: true
                mapVM.showZoos = mapVM.showZoosTemp.value ?: true
                mapVM.showPromos = mapVM.showPromosTemp.value ?: false
                val newList = truncShops(getFilteredShopsList())
                drawShopsOnMap(newList)
                setAutoCompleteTextView(newList)
                popup.dismiss()
            }
            reset.setOnClickListener {
                mapVM.showGrooms = true
                mapVM.showVets = true
                mapVM.showZoos = true
                mapVM.showPromos = false
                val newList = truncShops(getFilteredShopsList())
                drawShopsOnMap(newList)
                setAutoCompleteTextView(newList)
                popup.dismiss()
            }
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

    private val mapObjectTapListener = MapObjectTapListener { mapObject, _ ->
        PartnerDetailDialog(mapObject.userData as Long).show(
            childFragmentManager,
            "success_registration_dialog"
        )
        true
    }

    private fun setObservers() {
        mapVM.shops.observe(viewLifecycleOwner) {
            drawShopsOnMap(truncShops(getFilteredShopsList()))
            setAutoCompleteTextView(it.toMutableList())
        }
        mapVM.optionsClickedEvent.observe(viewLifecycleOwner, Observer(onOptionsClicked))
        mapVM.shopsLoadingEvent.observe(viewLifecycleOwner, networkEventObserver)
    }

    private fun setListeners() {
        binding.settings.setOnClickListener { mapVM.sendOptionsClickedEvent() }
        binding.mapView.map.addCameraListener(cameraListener)
    }

    @Suppress("ObjectLiteralToLambda")
    private val cameraListener = object: CameraListener {

        override fun onCameraPositionChanged(
            p0: Map,
            p1: CameraPosition,
            p2: CameraUpdateReason,
            isFinished: Boolean
        ) {
            if (isFinished) {
                collapseDistance = if (p1.zoom < 4) 6.0
                else 8192/(p1.zoom.toDouble().pow(6.0))
                drawShopsOnMap(truncShops(getFilteredShopsList()))
            }
        }
    }

    private fun getFilteredShopsList(): List<Shop> {
        val start = System.currentTimeMillis()
        var result = mapVM.originShopsList
        if (!mapVM.showGrooms) result = result.filter { it.typeShopId != MapViewModel.GROOMS_ID }
        if (!mapVM.showVets) result = result.filter { it.typeShopId != MapViewModel.VETS_ID }
        if (!mapVM.showZoos) result = result.filter { it.typeShopId != MapViewModel.ZOOS_ID }
        if (mapVM.showPromos) result = result.filter { it.promotions.isNotEmpty() }
        if (App.LOG_ENABLED) Log.d(App.DEBUG_TAG, "getFilteredShopsList() finished in: ${System.currentTimeMillis() - start}")
        return result
    }

    private fun truncShops(shops: List<Shop>): List<Shop> {
        val start = System.currentTimeMillis()
        val list = shops.filter {
            isShopInVisibleArea(it)
        }
        if (App.LOG_ENABLED) Log.d(App.DEBUG_TAG, "truncShops() finished in: ${System.currentTimeMillis() - start}")
        return list
    }

    private fun isShopInVisibleArea(shop: Shop): Boolean {
        val region = binding.mapView.map.visibleRegion
        val fitsY = shop.latitude in region.bottomRight.latitude..region.topLeft.latitude
        val fitsX = shop.longitude in region.topLeft.longitude..region.bottomRight.longitude
        return fitsX && fitsY
    }

    private lateinit var mapObjects: MapObjectCollection
    private val drawnShops = mutableMapOf<String, PlacemarkMapObject>()

    @SuppressLint("InflateParams")
    private fun drawShopsOnMap(shops: List<Shop>?) {
        shops?.let {
            val start = System.currentTimeMillis()
            if (!::mapObjects.isInitialized) {
                mapObjects = binding.mapView.map.mapObjects.addCollection()
                mapObjects.addTapListener(mapObjectTapListener)
            }

            val totalShopsToDraw = mutableMapOf<String, Shop>()
            for (shop in shops) {
                if (hasConflicts(totalShopsToDraw.values.toList(), shop)) continue
                else totalShopsToDraw["${shop.latitude}${shop.longitude}"] = shop
            }

            val shopsToRemove = drawnShops.minus(totalShopsToDraw.keys)
            for (shop in shopsToRemove) {
                try {
                    mapObjects.remove(shop.value)
                } catch (exc: Exception) { }
                drawnShops.remove(shop.key)
            }

            val additionalShopsToDraw = totalShopsToDraw.minus(drawnShops.keys)
            for (shop in additionalShopsToDraw) {
                val mapObject = mapObjects.addPlacemark(
                    Point(shop.value.latitude, shop.value.longitude),
                    ViewProvider(mapPin)
                )
                drawnShops["${shop.value.latitude}${shop.value.longitude}"] = mapObject
                mapObject.userData = shop.value.id
            }

            if (App.LOG_ENABLED) Log.d(App.DEBUG_TAG, "drawShopsOnMap() finished in: ${System.currentTimeMillis() - start}")
            if (App.LOG_ENABLED) Log.d(App.DEBUG_TAG, "objectsToDraw: ${drawnShops.size}")
            if (App.LOG_ENABLED) Log.d(App.DEBUG_TAG, "zoom: ${binding.mapView.map.cameraPosition.zoom}")
        }
    }

    private fun hasConflicts(shopsToDraw: List<Shop>, shop: Shop): Boolean {
        val zoom = binding.mapView.map.cameraPosition.zoom.toDouble()
        if (zoom >= 14.0) return false
        for (shopToDraw in shopsToDraw) {
            if (distanceBetweenTwoShops(shopToDraw, shop) < collapseDistance) return true
        }
        return false
    }

    companion object {

        fun distanceBetweenTwoShops(firstShop: Shop, secondShop: Shop): Double {
            return sqrt(
                (firstShop.latitude - secondShop.latitude).pow(2.0) +
                        (firstShop.longitude - secondShop.longitude).pow(2.0)
            )
        }

    }

}