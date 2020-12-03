package dev.project.ib2d2.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import dev.project.ib2d2.R

class CreateTeamsFragment : Fragment() {
    private val TAG = javaClass.name
    private val db = FirebaseFirestore.getInstance()

    private lateinit var rootView: View
    private lateinit var teamCodeText: TextView
    private lateinit var createTeamButton: Button
    private lateinit var createTeamName: EditText

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
        createTeamButton = rootView.findViewById(R.id.create_team_button)
        createTeamName = rootView.findViewById(R.id.team_create_name)

        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val teamCode = getRandomString()

        prefs = rootView.context.getSharedPreferences(PREFS_FILENAME, 0)

        teamCodeText.text = teamCode

        createTeamButton.setOnClickListener {
            val teamName = createTeamName.text.toString()
            val teamMembers = arrayListOf(prefs?.getString("USERNAME", "<Username Holder>"))
            val team = hashMapOf(
                "teamName" to teamName,
                "teamCode" to teamCode,
                "teamMembers" to teamMembers
            )
            db.collection("teams").document(teamName).set(team)
                .addOnSuccessListener {
                    Log.d(TAG, "Team added")
                    fragmentManager?.beginTransaction()?.replace(R.id.container, ProfileFragment())?.commit()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }
        }

        return rootView
    }

    private fun getRandomString() : String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..5)
            .map { allowedChars.random() }
            .joinToString("")
    }
}