package com.example.projectandroid

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.projectandroid.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_PERMISSION = 1
    lateinit var placesList: ArrayList<String>

    private var mPlacesClient: PlacesClient? = null
    private val M_MAX_ENTRIES = 5
    private lateinit var mLikelyPlaceNames: Array<String>
    private lateinit var mLikelyPLaceAddresses: ArrayList<String>
    private lateinit var mLikelyPlaceLatLngs: ArrayList<LatLng>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        placesList = ArrayList()

        val apikey = getString (R.string.api_key)
        Places.initialize(applicationContext, apikey)
        mPlacesClient = Places.createClient(this)
        mLikelyPlaceNames = arrayOf<String>("","","","","")
        mLikelyPLaceAddresses = ArrayList<String>(5)
        mLikelyPlaceLatLngs = ArrayList<LatLng>(5)
        getCurrentPlaceLikelihoods()
        println("plist: "+placesList)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableMyLocation()
        mMap.uiSettings.setZoomControlsEnabled(true)
        mMap.setTrafficEnabled(true)

    }

    // creating a menu to change map type
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_options, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {


        R.id.gPlaces -> {

            val intent = Intent(this,
                PlacesActivity::class.java).apply {
                putExtra("data",placesList)

            }
            startActivity(intent)
            //  startActivity(Intent(this,PlacesActivity::class.java))

            Toast.makeText(this@MapsActivity, "Places selected", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.email -> {
              startActivity(Intent(this,EmailActivity::class.java))

            Toast.makeText(this@MapsActivity, "Email selected", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.about -> {
            Toast.makeText(this@MapsActivity, "About selected", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun getAddress(loc:LatLng): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(loc!!.latitude, loc!!.longitude, 1)
        } catch (e1: IOException) {
            Log.e("Geocoding", getString(R.string.problem), e1)
        } catch (e2: IllegalArgumentException) {
            Log.e("Geocoding", getString(R.string.invalid)+
                    "Latitude = " + loc!!.latitude +
                    ", Longitude = " +
                    loc!!.longitude, e2)
        }
        // If the reverse geocode returned an address
        if (addresses != null) {
            // Get the first address
            val address = addresses[0]
            val addressText = String.format(
                "%s, %s, %s",
                address.getAddressLine(0),
                address.locality,
                address.countryName)
            return addressText
        }
        else
        {
            Log.e("Geocoding", getString(R.string.noaddress))
            return ""
        }
    }

    // Location services permission
    // this will result in a blue dot being your current location
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()

            }
        }
    }
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener {
                it.apply {

                    var current_location = LatLng(latitude,longitude)
                    var address = getAddress(current_location)
                    mMap.addMarker(MarkerOptions().position(current_location)
                        .title("Lat: "+latitude+", Lon: "+longitude)
                        .snippet(address))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location))

                }
            }
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun getCurrentPlaceLikelihoods() {
        // Use fields to define the data types to return.
        val placeFields = Arrays.asList(
            Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        // Get the likely places - that is, the businesses and other points of interest that
        // are the best match for the device's current location.
        val request = FindCurrentPlaceRequest.builder(placeFields).build()
        val placeResponse: Task<FindCurrentPlaceResponse> =
            mPlacesClient!!.findCurrentPlace(request)
        placeResponse.addOnCompleteListener(this,
            OnCompleteListener<FindCurrentPlaceResponse?> { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    // Set the count, handling cases where less than 5 entries are returned.
                    val count: Int
                    if (response.placeLikelihoods.size < M_MAX_ENTRIES) {
                        count = response.placeLikelihoods.size
                    } else {
                        count = M_MAX_ENTRIES
                    }
                    println("Found a place")
                    var i = 0
                    for (placeLikelihood: PlaceLikelihood in response.placeLikelihoods) {
                        val currPlace = placeLikelihood.place
                        mLikelyPlaceNames[i] = (currPlace.name)
                        placesList.add(currPlace.name)

                        Log.i(TAG,currPlace.name)
                        mLikelyPLaceAddresses.add(currPlace.address)
                        mLikelyPlaceLatLngs.add(currPlace.latLng)
                        val currLatLng =
                            if (mLikelyPlaceLatLngs[i] == null) "" else mLikelyPlaceLatLngs[i].toString()
                        Log.i(
                            TAG, String.format(
                                "Place " + currPlace.name
                                        + " has likelihood: " + placeLikelihood.likelihood
                                        + " at " + currLatLng
                            )
                        )
                        i++
                        if (i > (count - 1)) {
                            break
                        }
                    }

                    Toast.makeText(this@MapsActivity, "Places loaded", Toast.LENGTH_SHORT).show()

//                    recyclerView.adapter = RecyclerAdapter(mLikelyPlaceNames)  // pass in data to be displayed
//                    viewAdapter.notifyDataSetChanged()
                } else {
                    val exception: Exception? = task.getException()
                    if (exception is ApiException) {
                        Log.e(TAG, "Place not found: " + exception.statusCode)
                    }
                }
            })
    }



}
