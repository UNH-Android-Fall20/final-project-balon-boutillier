package dev.project.ib2d2.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.project.ib2d2.MainActivity
import dev.project.ib2d2.R

class SettingsFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.settings_tab, container, false)
        // initiate Firebase Auth
        auth = Firebase.auth
        val currentUser = auth.currentUser

        // handle log out button
        logoutButton = rootView.findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            Toast.makeText(rootView.context, "Signing you out...", Toast.LENGTH_SHORT).show()
            auth.signOut()
            val intent = Intent(rootView.context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        return rootView
    }
}