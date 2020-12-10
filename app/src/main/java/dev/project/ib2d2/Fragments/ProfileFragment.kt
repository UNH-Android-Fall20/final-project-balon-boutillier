package dev.project.ib2d2.Fragments

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.project.ib2d2.ExampleActivity
import dev.project.ib2d2.ProfileEdit
import dev.project.ib2d2.R


class ProfileFragment : Fragment() {
    private val TAG = javaClass.name
    private val db = FirebaseFirestore.getInstance()

    private lateinit var profileName: TextView
    private lateinit var backupCount: TextView
    private lateinit var profilePic: ImageView
    private lateinit var editProfileButton: Button
    private lateinit var profileTeamsButton: Button
    private lateinit var rootView: View
    private lateinit var progress: ProgressBar

    // Local persistent storage
    private val PREFS_FILENAME = "dev.project.ib2d2.prefs"
    private var prefs: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.profile_tab, container, false)

        // Load stored preferences (data) on device
        prefs = rootView.context.getSharedPreferences(PREFS_FILENAME, 0)

        profileScreen()
        return rootView
    }

    /**
     * Spawn profileScreen and populate it
     */
    @SuppressLint("ResourceType")
    private fun profileScreen() {
        profileName = rootView.findViewById(R.id.profile_name)
        backupCount = rootView.findViewById(R.id.backup_count)
        profilePic = rootView.findViewById(R.id.profile_pic)
        editProfileButton = rootView.findViewById(R.id.edit_profile)
        profileTeamsButton = rootView.findViewById(R.id.teams)
        progress = rootView.findViewById(R.id.profileProgress)

        profileName.visibility = View.GONE
        backupCount.visibility = View.GONE
        profilePic.visibility = View.GONE

        editProfileButton.visibility = View.GONE
        profileTeamsButton.visibility = View.GONE

        val userID = prefs?.getString("USERID", null)

        if(userID != null){
            db.collection("files")
                .whereEqualTo("createdBy", userID)
                .get()
                .addOnSuccessListener { documents ->
                    backupCount.text = "${documents.size()} files backed up"
                }

            db.collection("users").document(userID)
                .get()
                .addOnSuccessListener { doc ->
                    Log.d(TAG, "Document data: ${doc.data}")

                    if(doc.data!!.get("profilePic") == null){
                        Glide
                            .with(rootView.context)
                            .load("https://i.gyazo.com/b5e21c1d2e211cd1c023a926ee8b8d15.jpg")
                            .into(profilePic)

                        profileName.visibility = View.VISIBLE
                        backupCount.visibility = View.VISIBLE
                        profilePic.visibility = View.VISIBLE

                        editProfileButton.visibility = View.VISIBLE
                        profileTeamsButton.visibility = View.VISIBLE
                        progress.visibility = View.GONE

                        profileName.text = doc.data?.get("displayName") as String
                    } else {
                        // Reference to an image file in Cloud Storage
                        val storageRef = Firebase.storage.getReferenceFromUrl("gs://final-project-9c2ed.appspot.com${doc.data!!.get("profilePic")}")

                        // get the imageUrl and set it to our Glide
                        storageRef.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
                            val imageURL = uri.toString()
                            Log.d(TAG, imageURL)
                            Glide
                                .with(rootView.context)
                                .load(imageURL)
                                .into(profilePic)

                            profileName.visibility = View.VISIBLE
                            backupCount.visibility = View.VISIBLE
                            profilePic.visibility = View.VISIBLE

                            editProfileButton.visibility = View.VISIBLE
                            profileTeamsButton.visibility = View.VISIBLE
                            progress.visibility = View.GONE

                            profileName.text = doc.data?.get("displayName") as String
                        }).addOnFailureListener(OnFailureListener {
                            // Handle any errors
                        })
                    }
                }
                .addOnFailureListener { e -> Log.d(TAG, "Failed to retrieve doc: $e") }
        }


        editProfileButton.setOnClickListener {
            // spawn intent and customize animations
            val intent = Intent(rootView.context, ProfileEdit::class.java)
            val options = ActivityOptions.makeCustomAnimation(rootView.context, R.anim.right_in, R.anim.left_out)
            startActivity(intent, options.toBundle())
        }

        profileTeamsButton.setOnClickListener {
            profileTeamScreen()
        }

        // TODO replace this url with the actual profile pic retrieved from firestore
    }


    // TODO : fix to work with new fragment system -TJ
    private fun profileTeamScreen() {
        //setContentView(R.layout.profile_teams_layout)
        fragmentManager?.beginTransaction()?.replace(R.id.container, ProfileTeamsFragment())?.commit()
    }
}