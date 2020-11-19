package dev.project.ib2d2.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.project.ib2d2.R
import dev.project.ib2d2.ExampleActivity


class AboutFragment : Fragment(), OnMapReadyCallback {
    private lateinit var facebookButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.about_tab, container, false)

        // Get the map fragment so we can override it
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
        };

        // Example to handle the "Facebook" button and go to that view
        facebookButton = rootView.findViewById(R.id.facebook)
        facebookButton.setOnClickListener {
            val intent = Intent(rootView.context, ExampleActivity::class.java)
            startActivity(intent)
        }

        return rootView
    }

    /**
     * Overrides Google Map with specified Lat/Lng + Options
     */
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            val backBlazeDC = LatLng(38.595504, -121.272839)
            val marker = addMarker(
                MarkerOptions()
                    .position(backBlazeDC)
                    .title("BackBlaze Datacenter")
            )
            marker.showInfoWindow()
            moveCamera(CameraUpdateFactory.newLatLngZoom(backBlazeDC, 10F))
        }
    }


}