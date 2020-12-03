package dev.project.ib2d2.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import dev.project.ib2d2.R

class CreateTeamsFragment : Fragment() {
    private val TAG = javaClass.name

    private lateinit var rootView: View
    private lateinit var teamCodeText: TextView

    // Local persistent storage
    private val PREFS_FILENAME = "dev.project.ib2d2.prefs"
    private var prefs: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.team_create_layout, container, false)

        teamCodeText = rootView.findViewById(R.id.team_code)

        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val teamCode = getRandomString()

        teamCodeText.text = teamCode

        return rootView
    }

    private fun getRandomString() : String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..5)
            .map { allowedChars.random() }
            .joinToString("")
    }
}