package dev.project.ib2d2.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import dev.project.ib2d2.R
import android.util.Log
import android.widget.BaseAdapter
import android.widget.TextView

data class Team(
    val teamName: String = "",
    val teamCode: String = "",
    val teamMembers: ArrayList<String> = arrayListOf()
)

class TeamAdapter(private val context: Context,
                  private val dataSource: ArrayList<Team>): BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.team_list_item, parent, false)

        val teamName = rowView.findViewById(R.id.team_list_name) as TextView
        val teamCode = rowView.findViewById(R.id.team_list_code) as TextView
        val team = getItem(position) as Team

        teamName.text = team.teamName
        teamCode.text = team.teamCode

        return rowView
    }
}

class ListTeamFragment: Fragment() {
    private val TAG = javaClass.name
    private val db = FirebaseFirestore.getInstance()
    private val teams = ArrayList<Team>()

    private lateinit var rootView: View
    private lateinit var teamList: ListView

    // Local persistent storage
    private val PREFS_FILENAME = "dev.project.ib2d2.prefs"
    private var prefs: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.existing_teams_layout, container, false)

        teamList = rootView.findViewById(R.id.team_list)

        // TODO get all the teams from db and list them in the list view
        db.collection("teams")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val tempTeam = document.toObject(Team::class.java)
                    Log.d(TAG, tempTeam.toString())
                    teams.add(tempTeam)
                }
                teamList.adapter = TeamAdapter(rootView.context, teams)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

        return rootView
    }
}