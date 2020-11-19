package dev.project.ib2d2.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.project.ib2d2.R


class AboutFragment : Fragment(), OnMapReadyCallback {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.about_layout, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
        };

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            val backBlazeDC = LatLng(38.595504, -121.272839)
            addMarker(
                MarkerOptions()
                    .position(backBlazeDC)
                    .title("BackBlaze Datacenter")
            )
            moveCamera(CameraUpdateFactory.newLatLng(backBlazeDC))
        }
    }
}