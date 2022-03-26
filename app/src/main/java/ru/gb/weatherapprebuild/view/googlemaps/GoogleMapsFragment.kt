package ru.gb.weatherapprebuild.view.googlemaps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_google_maps_main.*
import ru.gb.weatherapprebuild.R
import ru.gb.weatherapprebuild.databinding.FragmentGoogleMapsMainBinding
import java.io.IOException

class GoogleMapsFragment : Fragment() {

    private var _binding: FragmentGoogleMapsMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var map : GoogleMap
    private val markers : ArrayList<Marker> = arrayListOf()

    companion object {
        fun newInstance() = GoogleMapsFragment()
    }

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        map = googleMap
        val initialPlace = LatLng(52.52000659999999, 13.404953999999975)

        googleMap.addMarker(MarkerOptions().position(initialPlace).title("Start"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(initialPlace))

        googleMap.setOnMapClickListener { latLng ->
            getAddressAsync(latLng)
            addMarkerToArray(latLng)
            drawLine()
        }

        activateMyLocation(googleMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoogleMapsMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        initSearchByAddress()
    }

    private fun initSearchByAddress() {
        binding.buttonSearch.setOnClickListener {
            val geoCoder = Geocoder(it.context)
            val searchText = searchAddress.text.toString()

            Thread {
                try {
                    val addresses = geoCoder.getFromLocationName(searchText, 1)
                    if (addresses.size > 0) {
                        val location = LatLng(
                            addresses[0].latitude,
                            addresses[0].longitude
                        )

                        it.post {
                            goToAddress(location, searchText)
                        }

                        //goToAddress(addresses, it, searchText)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun goToAddress(location : LatLng,
                            searchText: String) {
        setMarker(location, searchText, R.drawable.ic_map_marker)
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                location,
                15f
            )
        )
    }

    private fun goToAddress(addresses: MutableList<Address>,
                            view: View,
                            searchText: String) {
        val location = LatLng(
            addresses[0].latitude,
            addresses[0].longitude
        )

        view.post {
            setMarker(location, searchText, R.drawable.ic_map_marker)
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    15f
                )
            )
        }
    }

    private fun getAddressAsync(location: LatLng) {
        context?.let {
            val geoCoder = Geocoder(it)

            Thread {
                try {
                    val addresses =
                        geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    textAddress.post { textAddress.text = addresses[0].getAddressLine(0)}

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()

        }
    }

    private fun activateMyLocation(googleMap: GoogleMap) {
        context?.let {
            val isPermissionGranted =
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED

            googleMap.isMyLocationEnabled = isPermissionGranted
            googleMap.uiSettings.isMyLocationButtonEnabled = isPermissionGranted
        }
    }

    private fun addMarkerToArray(location: LatLng) {
        val marker = setMarker(location, markers.size.toString(), R.drawable.ic_map_pin)
        markers.add(marker)
    }

    private fun setMarker(location: LatLng, searchText: String, resourceId: Int) : Marker {
        return map.addMarker(MarkerOptions()
            .position(location)
            .title(searchText)
            .icon(BitmapDescriptorFactory.fromResource(resourceId)))
    }

    private fun drawLine() {
        val last: Int = markers.size - 1

        if (last >= 1) {
            var previous : LatLng = markers[last - 1].position
            var current : LatLng = markers[last].position

            map.addPolyline(PolylineOptions()
                .add(previous, current)
                .color(Color.RED)
                .width(5f))
        }
    }
}