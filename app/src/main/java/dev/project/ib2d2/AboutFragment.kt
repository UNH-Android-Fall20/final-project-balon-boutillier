package dev.project.ib2d2

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class AboutFragment : Fragment(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        //val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

    }

    companion object {
            fun newInstance(): AboutFragment = AboutFragment()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            val sydney = LatLng(-33.852, 151.211)
            addMarker(
                MarkerOptions()
                    .position(sydney)
                    .title("Marker in Sydney")
            )
            moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }
}