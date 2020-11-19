package dev.project.ib2d2.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import dev.project.ib2d2.R


class ProfileFragment : Fragment() {
    private lateinit var profileName: TextView
    private lateinit var profilePic: ImageView
    private lateinit var editProfileButton: Button
    private lateinit var saveEditProfileButton: Button
    private lateinit var profileTeamsButton: Button
    private lateinit var profileBackButton: Button
    private lateinit var rootView: View

    // Local persistent storage
    private val PREFS_FILENAME = "dev.project.ib2d2.prefs"
    private var prefs: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.tab_profile_layout, container, false)

        prefs = rootView.context.getSharedPreferences(PREFS_FILENAME, 0)

        return rootView
    }

    private fun editProfileScreen() {
        // setContentView(R.layout.profile_edit_layout)
        saveEditProfileButton = rootView.findViewById(R.id.save_edit_profile)

        saveEditProfileButton.setOnClickListener {
            profileScreen()
        }
    }

    private fun profileScreen() {
        // bottomScreenChange(R.layout.tab_profile_layout)
        profileName = rootView.findViewById(R.id.profile_name)
        profileName.setText("TEST")
        //profileName.setText(prefs?.getString("USERNAME", "<Username Holder>"))
        profilePic = rootView.findViewById(R.id.profile_pic)
        editProfileButton = rootView.findViewById(R.id.edit_profile)
        profileTeamsButton = rootView.findViewById(R.id.teams)

        editProfileButton.setOnClickListener {
            editProfileScreen()
        }

        profileTeamsButton.setOnClickListener {
            profileTeamScreen()
        }

        // TODO replace this url with the actual profile pic retrieved from firestore
        // Glide.with(this).load("https://i.ytimg.com/vi/MPV2METPeJU/maxresdefault.jpg").into(profilePic)
    }

    private fun profileTeamScreen() {
        //setContentView(R.layout.profile_teams_layout)
        profileBackButton = rootView.findViewById(R.id.profile_back)

        profileBackButton.setOnClickListener {
            profileScreen()
        }
    }
}