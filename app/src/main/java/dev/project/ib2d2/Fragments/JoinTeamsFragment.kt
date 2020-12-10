package dev.project.ib2d2.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dev.project.ib2d2.R

class JoinTeamsFragment: Fragment() {
    private val TAG = javaClass.name
    private val db = FirebaseFirestore.getInstance()

    private lateinit var rootView: View
    private lateinit var joinTeamButton: Button
    private lateinit var joinTeamCode: EditText

    // Local persistent storage
    private val PREFS_FILENAME = "dev.project.ib2d2.prefs"
    private var prefs: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.join_teams_layout, container, false)
        joinTeamButton = rootView.findViewById(R.id.join_team_button)
        joinTeamCode = rootView.findViewById(R.id.team_join_code)

        prefs = rootView.context.getSharedPreferences(PREFS_FILENAME, 0)

        joinTeamButton.setOnClickListener {
            val teamCode = joinTeamCode.text.toString()
            // TODO add a check to see if person about to be added is already on team
            if(teamCode !== "") {
                db.collection("teams").document(teamCode).update(
                    "teamMembers", FieldValue.arrayUnion(prefs?.getString("EMAILADDR", "<Username Holder>"))
                )
                    .addOnSuccessListener {
                        Log.d(TAG, "Team member added")
                        fragmentManager?.beginTransaction()?.replace(R.id.container, ProfileFragment())?.commit()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding team member", e)
                        Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        return rootView
    }
}