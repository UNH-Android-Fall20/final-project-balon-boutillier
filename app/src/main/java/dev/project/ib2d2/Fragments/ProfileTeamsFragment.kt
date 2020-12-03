package dev.project.ib2d2.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import dev.project.ib2d2.R
import androidx.fragment.app.Fragment

class ProfileTeamsFragment : Fragment() {
    private val TAG = javaClass.name

    private lateinit var rootView: View
    private lateinit var profileBackButton: Button
    private lateinit var createProfileNavButton: Button

    // Local persistent storage
    private val PREFS_FILENAME = "dev.project.ib2d2.prefs"
    private var prefs: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.profile_teams_layout, container, false)

        // Load stored preferences (data) on device
        prefs = rootView.context.getSharedPreferences(PREFS_FILENAME, 0)

        profileBackButton = rootView.findViewById(R.id.profile_back)
        createProfileNavButton = rootView.findViewById(R.id.profile_create_nav_button)

        profileBackButton.setOnClickListener {
            fragmentManager?.beginTransaction()?.replace(R.id.container, ProfileFragment())?.commit()
        }

        createProfileNavButton.setOnClickListener {
            fragmentManager?.beginTransaction()?.replace(R.id.container, CreateTeamsFragment())?.commit()
        }

        return rootView
    }
}