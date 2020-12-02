package dev.project.ib2d2.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import dev.project.ib2d2.R

class ProfileEditFragment : Fragment() {
    private val TAG = javaClass.name

    private lateinit var rootView: View
    private lateinit var saveEditProfileButton: Button

    // Local persistent storage
    private val PREFS_FILENAME = "dev.project.ib2d2.prefs"
    private var prefs: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.profile_edit_layout, container, false)

        // Load stored preferences (data) on device
        prefs = rootView.context.getSharedPreferences(PREFS_FILENAME, 0)

        saveEditProfileButton = rootView.findViewById(R.id.save_edit_profile)

        // TODO save the edits of the profile page
        saveEditProfileButton.setOnClickListener {
            fragmentManager?.beginTransaction()?.replace(R.id.container, ProfileFragment())?.commit()
        }

        return rootView
    }
}