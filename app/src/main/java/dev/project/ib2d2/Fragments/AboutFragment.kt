package dev.project.ib2d2.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.project.ib2d2.R


class AboutFragment : Fragment(), OnMapReadyCallback {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.about_layout, container, false)
    }

    /*
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        //val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

    }

    companion object {
            fun newInstance(): AboutFragment =
                AboutFragment()
    }
*/
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            val sydney = LatLng(-33.852, 151.211)
            addMarker(
                MarkerOptions()
                    .position(sydney)
                    .title("Example Marker")
            )
            moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }
}